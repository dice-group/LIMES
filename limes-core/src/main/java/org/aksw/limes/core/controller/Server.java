package org.aksw.limes.core.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eu.medsea.mimeutil.MimeType;
import eu.medsea.mimeutil.MimeUtil;
import org.aksw.limes.core.datastrutures.LogicOperator;
import org.aksw.limes.core.io.config.Configuration;
import org.aksw.limes.core.io.config.reader.AConfigurationReader;
import org.aksw.limes.core.io.config.reader.xml.XMLConfigurationReader;
import org.aksw.limes.core.io.preprocessing.APreprocessingFunction;
import org.aksw.limes.core.io.preprocessing.PreprocessingFunctionFactory;
import org.aksw.limes.core.io.serializer.ISerializer;
import org.aksw.limes.core.io.serializer.SerializerFactory;
import org.aksw.limes.core.measures.measure.MeasureType;
import org.aksw.limes.core.ml.algorithm.MLImplementationType;
import org.apache.commons.io.FilenameUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import spark.Request;
import spark.Response;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.http.Part;
import java.io.*;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static spark.Spark.*;

public class Server {

    private static final Logger logger = LoggerFactory.getLogger(Server.class);

    private static final String STORAGE_DIR_PATH = "./.server-storage/";
    private static final String LOG_DIR_PATH = STORAGE_DIR_PATH + "logs/";
    private static final String CONFIG_FILE_PREFIX = "limes_cfg_";
    private static final String CONFIG_FILE_SUFFIX = "xml";

    private static final Gson GSON = new GsonBuilder().create();
    private static final int FILE_SIZE_THRESHOLD = 1024;
    private static final long MAX_REQUEST_SIZE = 1024 * 1024 * 15;
    private static final long MAX_FILE_SIZE = 1024 * 1024 * 5;
    private static Server instance = null;

    private final Map<String, CompletableFuture<Void>> requests = new HashMap<>();
    private final Map<String, AsynchronousServerOracle> oracles = new HashMap<>();
    private Map<String, String> uploadFiles = new HashMap<>();
    private final File uploadDir = new File(STORAGE_DIR_PATH);
    private int port = -1;
    private int limit = -1;

    public static Server getInstance() {
        if (instance == null) {
            instance = new Server();
        }
        return instance;
    }

    public void run(int port, int limit) {
        try {
            Path files = Paths.get(STORAGE_DIR_PATH, "files").toAbsolutePath();
            if (Files.exists(files)) {
                Files.walk(files)
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.limit = limit;
        if (this.port > 0) {
            throw new IllegalStateException("Server already running on port " + port + "!");
        } else {
            this.port = port;
        }
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }
        port(port);
        staticFiles.location("/web-ui");
        staticFiles.expireTime(7200);
        enableCORS("*","GET, POST, OPTIONS","");
        post("/submit", this::handleSubmit);
        post("/activeLearning/:id", this::handleActiveLearning);
        get("/status/:id", this::handleStatus);
        get("/logs/:id", this::handleLogs);
        get("/results/:id", this::handleResults);
        get("/result/:id/:file", this::handleResult);
        get("/list/operators", this::handleOperators);
        get("/list/measures", this::handleMeasures);
        get("/list/preprocessings", this::handlePreprocessings);
        get("/sparql/:endpoint", this::handleSparql);
        get("/uploads/:uploadId/sparql", this::handleLocalSparql);
        post("/upload", this::handleUpload);
        post("/sparql/:endpoint", this::handleSparql);
        options("/sparql/:endpoint", this::handleSparql);
        exception(Exception.class, (e, req, res) -> {
            logger.error("Error in processing request" + req.uri(), e);
            res.status(500);
            res.type("application/json");
            res.body(GSON.toJson(new ServerMessage.ErrorMessage(e)));
        });
        notFound((req, res) -> {
            res.type("application/json");
            res.status(404);
            return GSON.toJson(new ServerMessage.ErrorMessage(-2, "Route not known"));
        });
        init();
        awaitInitialization();
    }

    private Object handleSparql(Request req, Response res) throws IOException {
        String endpointUrl = URLDecoder.decode(req.params("endpoint"), "UTF-8");
        if (req.queryString() != null && !req.queryString().equals("null")) {
            endpointUrl += "?" + req.queryString();
        }
        logger.info("endpointUrl: {}", endpointUrl);
        org.apache.http.client.fluent.Request request;
        switch (req.requestMethod()) {
            case "POST":
                request = org.apache.http.client.fluent.Request.Post(endpointUrl);
                request.bodyByteArray(req.bodyAsBytes());
                break;
            case "OPTIONS":
                request = org.apache.http.client.fluent.Request.Options(endpointUrl);
                break;
            case "GET":
            default:
                request = org.apache.http.client.fluent.Request.Get(endpointUrl);
                break;
        }
        for (String header : req.headers()) {
            if (!header.equals("Content-Length") && !header.equals("Host"))
                request.setHeader(header, req.headers(header));
        }
        HttpResponse response = request.execute().returnResponse();
        for (Header header : response.getAllHeaders()) {
            if (!header.getName().startsWith("Access-Control")) {
                res.header(header.getName(), header.getValue());
            }
        }
        res.status(response.getStatusLine().getStatusCode());
        if (!req.requestMethod().equals("OPTIONS")) {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                entity.writeTo(baos);
                byte[] bytes = baos.toByteArray();
                res.raw().getOutputStream().write(bytes);
                res.raw().getOutputStream().flush();
                res.raw().getOutputStream().close();
            }
        }
        return res.raw();
    }

    private Object handleLocalSparql(Request req, Response res) throws IOException {
        String uploadId = req.params("uploadId");
        String s = uploadFiles.get(uploadId);
        String query = req.queryParams("query");
        String matchQuery = query.toLowerCase().trim();
        Model queryModel = ModelFactory.createDefaultModel().read(s);
        QueryExecution queryExecution = QueryExecutionFactory.create(query, queryModel);
        if (matchQuery.startsWith("select")) {
            ResultSet resultSet = queryExecution.execSelect();
            ResultSetFormatter.outputAsJSON(res.raw().getOutputStream(), resultSet);
        } else if (matchQuery.startsWith("ask")) {
            boolean result = queryExecution.execAsk();
            ResultSetFormatter.outputAsJSON(res.raw().getOutputStream(), result);
        }
        queryExecution.close();
        return "";
    }

    private Object handleUpload(Request req, Response res) throws IOException, ServletException {
        File workingDir = new File(uploadDir.getAbsoluteFile(), "files");
        if (!workingDir.exists() && !workingDir.mkdirs() ||  !workingDir.isDirectory()) {
            throw new IOException("Not able to create directory " + workingDir.getAbsolutePath());
        }
        Map<String, String> partUploads = new HashMap<>();
        req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement(workingDir.getAbsolutePath(), MAX_FILE_SIZE, MAX_REQUEST_SIZE, FILE_SIZE_THRESHOLD));
        if (req.contentType().contains("multipart/form-data")) {
            for (Part part : req.raw().getParts()) {
                try (InputStream is = part.getInputStream()) {
                    String uploadId = UUID.randomUUID().toString();
                    Path partFileName = Paths.get(part.getSubmittedFileName()).getFileName();
                    String extension = FilenameUtils.getExtension(partFileName.toString());
                    Path destinationPath = workingDir.toPath().resolve(uploadId + "." + extension);
                    Files.copy(is, destinationPath, StandardCopyOption.REPLACE_EXISTING);
                    partUploads.put(part.getSubmittedFileName(), uploadId);
                    uploadFiles.put(uploadId, destinationPath.toString());
                    logger.info("Uploaded file '{}' with uploadId '{}'", part.getName(), uploadId);
                }
            }
        } else {
            return GSON.toJson(new ServerMessage.ErrorMessage(1, "Only Requests of type \"multipart/form-data\" are allowed"));
        }
        res.status(200);
        return GSON.toJson(new ServerMessage.UploadMessage(partUploads));
    }

    private Server() { }

    private Object handleOperators(Request req, Response res) {
        ServerMessage.OperatorsMessage result = new ServerMessage.OperatorsMessage(
                Arrays.stream(LogicOperator.values())
                        .map(Enum::name)
                        .collect(Collectors.toList())
        );
        res.status(200);
        return GSON.toJson(result);
    }

    private Object handleMeasures(Request req, Response res) {
        ServerMessage.MeasuresMessage result = new ServerMessage.MeasuresMessage(
                Arrays.stream(MeasureType.values())
                        .map(Enum::name)
                        .map(String::toLowerCase)
                        .collect(Collectors.toList())
        );
        res.status(200);
        return GSON.toJson(result);
    }

    private Object handlePreprocessings(Request req, Response res) {
        ServerMessage.PreprocessingsMessage result = new ServerMessage.PreprocessingsMessage(
                PreprocessingFunctionFactory.listTypes().stream()
                        .map(pp -> {
                            APreprocessingFunction ppf =
                                    PreprocessingFunctionFactory.getPreprocessingFunction(
                                            PreprocessingFunctionFactory.getPreprocessingType(pp)
                                    );
                            return new ServerMessage.PreprocessingsMessage.PPInfo(
                                    pp,
                                    ppf.minNumberOfArguments(),
                                    ppf.maxNumberOfArguments(),
                                    ppf.isComplex());
                        })
                        .filter(ppi -> !ppi.isComplex())
                        .collect(Collectors.toList())
        );
        res.status(200);
        return GSON.toJson(result);
    }

    private Object handleSubmit(Request req, Response res) throws Exception {
        req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));
        Part configFile = req.raw().getPart("config_file");
        String fileName = getFileName(configFile);
        String suffix = FilenameUtils.getExtension(fileName);
        final Path tempFile = Files.createTempFile(uploadDir.toPath(), CONFIG_FILE_PREFIX, "." + (
                suffix.equals("") ? CONFIG_FILE_SUFFIX : suffix));
        try (InputStream is = configFile.getInputStream()) {
            Files.copy(is, tempFile, StandardCopyOption.REPLACE_EXISTING);
        }
        logger.info("Uploaded file '{}' saved as '{}'", fileName, tempFile.toAbsolutePath());
        String id = tempFile.toString();
        id = id.substring(id.indexOf(CONFIG_FILE_PREFIX) + CONFIG_FILE_PREFIX.length(),
                id.lastIndexOf("."));
        final File workingDir = new File(uploadDir.getAbsoluteFile(), id);
        if (!workingDir.mkdir()) {
            throw new RuntimeException("Not able to create directory " + workingDir.getAbsolutePath());
        }
        final String requestId = id;
        AsynchronousServerOracle oracle;
        AConfigurationReader reader = new XMLConfigurationReader(tempFile.toAbsolutePath().toString());
        Configuration config = reader.read();
        if (config.getMlImplementationType() != MLImplementationType.SUPERVISED_ACTIVE) {
            oracle = null;
        } else {
            oracle = new AsynchronousServerOracle();
            oracles.put(requestId, oracle);
        }
        requests.put(requestId, CompletableFuture.completedFuture(null).thenAcceptAsync($->{
            MDC.put("requestId", requestId);
            String sourceEndpoint = config.getSourceInfo().getEndpoint();
            if (uploadFiles.containsKey(sourceEndpoint)) {
                config.getSourceInfo().setEndpoint(uploadFiles.get(sourceEndpoint));
            }
            String targetEndpoint = config.getTargetInfo().getEndpoint();
            if (uploadFiles.containsKey(targetEndpoint)) {
                config.getTargetInfo().setEndpoint(uploadFiles.get(targetEndpoint));
            }
            LimesResult mappings = Controller.getMapping(config, limit, oracle);
            String outputFormat = config.getOutputFormat();
            ISerializer output = SerializerFactory.createSerializer(outputFormat);
            output.setPrefixes(config.getPrefixes());
            File verificationFile = new File(workingDir, config.getVerificationFile());
            File acceptanceFile = new File(workingDir, config.getAcceptanceFile());
            output.writeToFile(mappings.getVerificationMapping(), config.getVerificationRelation(),
                    verificationFile.getAbsolutePath());
            output.writeToFile(mappings.getAcceptanceMapping(), config.getAcceptanceRelation(),
                    acceptanceFile.getAbsolutePath());
        }).exceptionally((e)-> {e.printStackTrace();return null;}));
        res.status(200);

        return GSON.toJson(oracle == null ? new ServerMessage.SubmitMessage(id) : new ServerMessage.ActiveLearningMessage(id, oracle));
    }

    private Object handleActiveLearning(Request req, Response res) throws Exception {
        //todo: catch ids not in active learning currently
        ServerMessage.ScoresMessage scores = GSON.fromJson(req.raw().getReader(), ServerMessage.ScoresMessage.class);
        String id = sanitizeId(req.params("id"));
        AsynchronousServerOracle oracle = oracles.get(id);
        oracle.completeClassification(scores.getExampleScores());
        res.status(200);
        return GSON.toJson(oracle.isStopped() ? new ServerMessage.SubmitMessage(id) : new ServerMessage.ActiveLearningMessage(id, oracle));
    }



    private Object handleStatus(Request req, Response res) {
        String id = sanitizeId(req.params("id"));
        ServerMessage.StatusMessage result;
        // @todo: implement job queuing and status id 0 to limit level of parallelism
        if (!requests.containsKey(id)) {
            result = new ServerMessage.StatusMessage(-1, "Request ID not found");
        } else if (!requests.get(id).isDone()) {
            result = new ServerMessage.StatusMessage(1, "Request is being processed");
        } else {
            result = new ServerMessage.StatusMessage(2, "Request has been processed");
        }
        res.status(200);
        return GSON.toJson(result);
    }

    private Object handleLogs(Request req, Response res) throws Exception {
        String id = sanitizeId(req.params("id"));
        File requestedFile = new File(LOG_DIR_PATH + id + ".log");
        if (requestedFile.exists()) {
            res.type("text/plain");
            res.status(200);
            OutputStream os = res.raw().getOutputStream();
            FileInputStream fs = new FileInputStream(requestedFile);
            final byte[] buffer = new byte[1024];
            int count;
            boolean finish = !requests.containsKey(id) || requests.get(id).isDone();
            while (true) {
                while ((count = fs.read(buffer)) >= 0) {
                    os.write(buffer, 0, count);
                }
                os.flush();
                if (finish) break;
                Thread.sleep(500);
                finish = requests.get(id).isDone();
            }
            fs.close();
            os.close();
            return "";
        } else {
            res.status(404);
            return GSON.toJson(new ServerMessage.ErrorMessage(1, "Logfile not found"));
        }
    }

    private Object handleResult(Request req, Response res) throws Exception {
        String id = sanitizeId(req.params("id"));
        File file = new File(req.params("file"));
        File requestedFile = new File(STORAGE_DIR_PATH + id + "/" + file.getName());
        // is the file available?
        if (requestedFile.exists()) {
            MimeUtil.registerMimeDetector("eu.medsea.mimeutil.detector.MagicMimeMimeDetector");
            Collection mimeTypes = MimeUtil.getMimeTypes(requestedFile, new MimeType("text/plain"));
            res.type(mimeTypes.iterator().next().toString());
            res.header("Content-Disposition", "attachment; filename=" + file.getName());
            res.status(200);
            OutputStream os = res.raw().getOutputStream();
            FileInputStream fs = new FileInputStream(requestedFile);
            final byte[] buffer = new byte[1024];
            int count;
            while ((count = fs.read(buffer)) >= 0) {
                os.write(buffer, 0, count);
            }
            os.flush();
            fs.close();
            os.close();
            return "";
        } else {
            // 404 - Not Found
            res.status(404);
            return GSON.toJson(new ServerMessage.ErrorMessage(1, "Result file not found"));
        }
    }

    private Object handleResults(Request req, Response res) {
        String id = sanitizeId(req.params("id"));
        File dir = new File(STORAGE_DIR_PATH + id);
        if (dir.exists() && dir.isDirectory()) {
            List<String> availableFiles = Arrays
                    .stream(Objects.requireNonNull(dir.listFiles()))
                    .map(File::getName).collect(Collectors.toList());
            return GSON.toJson(new ServerMessage.ResultsMessage(availableFiles));
        } else {
            res.status(404);
            return GSON.toJson(new ServerMessage.ErrorMessage(1, "Request ID not found"));
        }
    }

    private static String sanitizeId(String id) {
        return id.replaceAll("[^\\d]", "");
    }

    private static String getFileName(Part part) {
        for (String cd : part.getHeader("content-disposition").split(";")) {
            if (cd.trim().startsWith("filename")) {
                return cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return "config.ttl";
    }

    private static void enableCORS(final String origin, final String methods, final String headers) {
        options("/*", (request, response) -> {
            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }
            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }
            return "OK";
        });

        before((request, response) -> {
            response.header("Access-Control-Allow-Origin", origin);
            response.header("Access-Control-Request-Method", methods);
            response.header("Access-Control-Allow-Headers", headers);
            response.type("application/json");
        });
    }

}
