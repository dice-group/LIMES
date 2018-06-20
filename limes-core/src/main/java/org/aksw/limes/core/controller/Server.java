package org.aksw.limes.core.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eu.medsea.mimeutil.MimeType;
import eu.medsea.mimeutil.MimeUtil;
import org.aksw.limes.core.io.config.Configuration;
import org.aksw.limes.core.io.config.reader.AConfigurationReader;
import org.aksw.limes.core.io.config.reader.xml.XMLConfigurationReader;
import org.aksw.limes.core.io.serializer.ISerializer;
import org.aksw.limes.core.io.serializer.SerializerFactory;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import spark.Request;
import spark.Response;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Part;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static spark.Spark.*;

public class Server {

    private static final Logger logger = LoggerFactory.getLogger(Server.class);

    public static final String STORAGE_DIR_PATH = "./.server-storage/";
    public static final String LOG_DIR_PATH = STORAGE_DIR_PATH + "logs/";
    public static final String CONFIG_FILE_PREFIX = "limes_cfg_";
    public static final String CONFIG_FILE_SUFFIX = "xml";

    private static final Gson GSON = new GsonBuilder().create();
    private static Server instance = null;

    private final Map<String, CompletableFuture<Void>> requests = new HashMap<>();
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
        staticFiles.expireTime(10);
        enableCORS("*","GET, POST, OPTIONS","");
        post("/submit", this::handleSubmit);
        get("/status/:id", this::handleStatus);
        get("/logs/:id", this::handleLogs);
        get("/results/:id", this::handleResults);
        get("/result/:id/:file", this::handleResult);
        exception(Exception.class, (e, req, res) -> {
            logger.error("Error in processing request" + req.uri(), e);
            res.status(500);
            res.type("application/json");
            res.body(GSON.toJson(new ErrorMessage(e)));
        });
        notFound((req, res) -> {
            res.type("application/json");
            res.status(404);
            return GSON.toJson(new ErrorMessage(-2, "Route not known"));
        });
        init();
        awaitInitialization();
    }

    private Server(){
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
        requests.put(requestId, CompletableFuture.completedFuture(null).thenAcceptAsync($->{
            MDC.put("requestId", requestId);
            AConfigurationReader reader = new XMLConfigurationReader(tempFile.toAbsolutePath().toString());
            Configuration config = reader.read();
            ResultMappings mappings = Controller.getMapping(config, limit);
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
        return GSON.toJson(new SubmitMessage(id));
    }

    private Object handleStatus(Request req, Response res) {
        String id = sanitizeId(req.params("id"));
        StatusMessage result;
        // @todo: implement job queuing and status id 0 to limit level of parallelism
        if (!requests.containsKey(id)) {
            result = new StatusMessage(-1, "Request ID not found");
        } else if (!requests.get(id).isDone()) {
            result = new StatusMessage(1, "Request is being processed");
        } else {
            result = new StatusMessage(2, "Request has been processed");
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
            return GSON.toJson(new ErrorMessage(1, "Logfile not found"));
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
            return GSON.toJson(new ErrorMessage(1, "Result file not found"));
        }
    }

    private Object handleResults(Request req, Response res) {
        String id = sanitizeId(req.params("id"));
        File dir = new File(STORAGE_DIR_PATH + id);
        if (dir.exists() && dir.isDirectory()) {
            List<String> availableFiles = Arrays
                    .stream(Objects.requireNonNull(dir.listFiles()))
                    .map(File::getName).collect(Collectors.toList());
            return GSON.toJson(new ResultsMessage(availableFiles));
        } else {
            res.status(404);
            return GSON.toJson(new ErrorMessage(1, "Request ID not found"));
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

    private static class ServerMessage {

        protected boolean success = true;

    }

    private static class ErrorMessage extends ServerMessage {

        private Error error;

        private static class Error {
            private int code;
            private String message;
        }

        ErrorMessage(Throwable e) {
            this(-1, e.getMessage());
        }

        ErrorMessage(int code, String message) {
            this.success = false;
            this.error = new Error();
            this.error.code = code;
            this.error.message = message;
        }

    }

    private static class StatusMessage extends ServerMessage {

        private Status status;

        private static class Status {
            int code;
            String description;
        }

        private StatusMessage(int status, String description) {
            this.status = new Status();
            this.status.code = status;
            this.status.description = description;
        }
    }

    private static class ResultsMessage extends ServerMessage {

        private List<String> availableFiles;

        private ResultsMessage(List<String> availableFiles) {
            this.availableFiles = availableFiles;
        }
    }

    private static class SubmitMessage extends ServerMessage {

        private String requestId;

        private SubmitMessage(String requestId) {
            this.requestId = requestId;
        }
    }

}
