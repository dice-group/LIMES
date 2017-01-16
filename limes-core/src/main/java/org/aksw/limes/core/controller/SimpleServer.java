package org.aksw.limes.core.controller;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import eu.medsea.mimeutil.MimeUtil;
import org.aksw.limes.core.io.config.Configuration;
import org.aksw.limes.core.io.config.reader.AConfigurationReader;
import org.aksw.limes.core.io.config.reader.xml.XMLConfigurationReader;
import org.aksw.limes.core.io.serializer.ISerializer;
import org.aksw.limes.core.io.serializer.SerializerFactory;
import org.apache.commons.fileupload.MultipartStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Kevin Dreßler
 */

public class SimpleServer {

    private static final Logger logger = LoggerFactory.getLogger(SimpleServer.class.getName());
    private static final String STORAGE_DIR_PATH = "./temp/";
    private static final String LOCK_DIR_PATH = "/lock/";
    private static final String QUERY_PARAM_RESULT_TYPE = "result_type";
    private static final String QUERY_PARAM_JOB_ID = "job_id";
    public static final String CONFIG_FILE_PREFIX = "limes_server_cfg_";
    private static ConcurrentMap<Long, Integer> jobs = new ConcurrentHashMap<>();

    public static void startServer(int port) {
        HttpServer server = null;
        logger.info("Attempting to start LIMES server at port " + port + "...");
        try {
            server = HttpServer.create(new InetSocketAddress(port), 0);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        server.createContext("/execute", new ExecuteHandler());
        server.createContext("/get_result", new GetResultHandler());
        server.createContext("/get_status", new GetStatusHandler());
        server.setExecutor(java.util.concurrent.Executors.newCachedThreadPool());
        server.start();
        logger.info("Server has been started! Waiting for requests...");
    }

    private static class ExecuteHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange t) throws IOException {
            if (t.getRequestMethod().equals("POST")) {
                Headers headers = t.getRequestHeaders();
                String boundary = "boundary=";
                for (String s : headers.get("Content-type")) {
                    int i = s.indexOf(boundary);
                    if (i > -1) {
                        boundary = s.substring(i + boundary.length());
                        break;
                    }
                }
                String id = writeConfigFile(t.getRequestBody(), boundary);
                jobs.put(Long.parseLong(id), 0);
                String response = id + "\n";
                t.sendResponseHeaders(200, response.length());
                logger.info("New Job: " + id);
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();
                AConfigurationReader reader = new XMLConfigurationReader(STORAGE_DIR_PATH + CONFIG_FILE_PREFIX + id + ".xml");
                Configuration config = reader.read();
                jobs.put(Long.parseLong(id), 1);
                ResultMappings mappings = Controller.getMapping(config);
                String outputFormat = config.getOutputFormat();
                ISerializer output = SerializerFactory.createSerializer(outputFormat);
                output.setPrefixes(config.getPrefixes());
                File tempDir = new File(STORAGE_DIR_PATH + id + "/");
                File lockDir = new File(tempDir + LOCK_DIR_PATH);
                if (!lockDir.exists()) {
                    lockDir.mkdirs();
                }
                File _verificationFile = new File(lockDir + "/" + config.getVerificationFile());
                File _acceptanceFile = new File(lockDir + "/" + config.getAcceptanceFile());
                File verificationFile = new File(tempDir + "/" + config.getVerificationFile());
                File acceptanceFile = new File(tempDir + "/" + config.getAcceptanceFile());
                output.writeToFile(mappings.getVerificationMapping(), config.getVerificationRelation(),
                        _verificationFile.getAbsolutePath());
                output.writeToFile(mappings.getAcceptanceMapping(), config.getAcceptanceRelation(),
                        _acceptanceFile.getAbsolutePath());
                _verificationFile.renameTo(verificationFile);
                _acceptanceFile.renameTo(acceptanceFile);
                lockDir.delete();
                jobs.put(Long.parseLong(id), 2);
            } else {
                // we only accept POST requests here, anything else gets code "405 - Method Not Allowed"
                t.sendResponseHeaders(405 ,-1);
                logger.info("Bad request: HTTP VERB must be POST for " + t.getRequestURI());
            }
        }

        private String writeConfigFile (InputStream inputStream, String b) throws IOException {
            byte[] boundary = b.getBytes();
            @SuppressWarnings("deprecation")
            MultipartStream multipartStream = new MultipartStream(inputStream, boundary);
            File tempDir = new File(STORAGE_DIR_PATH);
            if (!tempDir.exists()) {
                tempDir.mkdir();
            }
            File file = File.createTempFile(CONFIG_FILE_PREFIX, ".xml", tempDir);
            FileOutputStream out = new FileOutputStream(file.getAbsolutePath());
            boolean nextPart = multipartStream.skipPreamble();
            if (nextPart) {
                multipartStream.readHeaders();
                multipartStream.readBodyData(out);
            }
            out.close();
            String id = file.getName();
            return id.substring(id.indexOf(CONFIG_FILE_PREFIX) + CONFIG_FILE_PREFIX.length(), id.lastIndexOf(".xml"));
        }
    }

    private static class GetResultHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange t) throws IOException {
            if (t.getRequestMethod().equals("GET")) {
                Map<String, String> params = queryToMap(t.getRequestURI().getRawQuery());
                if (params.containsKey(QUERY_PARAM_JOB_ID) && params.containsKey(QUERY_PARAM_RESULT_TYPE) &&
                        Arrays.asList("acceptance", "review").contains(params.get(QUERY_PARAM_RESULT_TYPE).toLowerCase())) {
                    // get data from Config
                    long id = Long.parseLong(params.get(QUERY_PARAM_JOB_ID));
                    if (!new File(STORAGE_DIR_PATH + CONFIG_FILE_PREFIX + id + ".xml").exists()) {
                        // 404 - Not Found
                        t.sendResponseHeaders(404, -1);
                        logger.info("Bad request: " + t.getRequestURI() + "\nResource not found!");
                    } else {
                        AConfigurationReader reader = new XMLConfigurationReader(STORAGE_DIR_PATH + CONFIG_FILE_PREFIX + id + ".xml");
                        Configuration config = reader.read();
                        String requestedFileName = params.get(QUERY_PARAM_RESULT_TYPE).equalsIgnoreCase("acceptance") ?
                                config.getAcceptanceFile() : config.getVerificationFile();
                        File requestedFile = new File(STORAGE_DIR_PATH + id + "/" + requestedFileName);
                        File requestedFileLock = new File(STORAGE_DIR_PATH + id + LOCK_DIR_PATH + requestedFileName);
                        // is the file available yet?
                        if (requestedFile.exists()) {
                            // prepare HTTP headers
                            MimeUtil.registerMimeDetector("eu.medsea.mimeutil.detector.MagicMimeMimeDetector");
                            Collection mimeTypes = MimeUtil.getMimeTypes(requestedFile, new eu.medsea.mimeutil.MimeType("text/plain"));
                            Headers headers = t.getResponseHeaders();
                            headers.add("Content-Type", mimeTypes.iterator().next().toString());
                            headers.add("Content-Disposition", "attachment; filename=" + requestedFileName);
                            t.sendResponseHeaders(200, requestedFile.length());
                            // attempt to open the file
                            // stream the file
                            OutputStream os = t.getResponseBody();
                            FileInputStream fs = new FileInputStream(requestedFile);
                            final byte[] buffer = new byte[1024];
                            int count;
                            while ((count = fs.read(buffer)) >= 0) {
                                os.write(buffer, 0, count);
                            }
                            os.flush();
                            fs.close();
                            os.close();
                        } else if (requestedFileLock.exists()) {
                            // 204 - No Content
                            // Indicates that Job is being processed and output will soon be available
                            t.sendResponseHeaders(204, -1);
                            logger.info("Job not yet finished: " + t.getRequestURI());
                        } else {
                            // 404 - Not Found
                            t.sendResponseHeaders(404, -1);
                            logger.info("Bad request: " + t.getRequestURI() + "\nResource not found!");
                        }
                    }
                } else {
                    // 400 - Bad Request
                    t.sendResponseHeaders(400 ,-1);
                    logger.info("Bad request: " + t.getRequestURI() + "\nPlease specify job_id and result_type query parameters!");
                }
            } else {
                // we only accept GET requests here, anything else gets code "405 - Method Not Allowed"
                t.sendResponseHeaders(405 ,-1);
                logger.info("Bad request: HTTP VERB must be GET for " + t.getRequestURI());
            }
        }
    }

    private static class GetStatusHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange t) throws IOException {
            if (t.getRequestMethod().equals("GET")) {
                Map<String, String> params = queryToMap(t.getRequestURI().getRawQuery());
                if (params.containsKey(QUERY_PARAM_JOB_ID)) {
                    // get data from Config
                    long id = Long.parseLong(params.get(QUERY_PARAM_JOB_ID));
                    int status = -1;
                    if (new File(STORAGE_DIR_PATH + CONFIG_FILE_PREFIX + id + ".xml").exists()) {
                        AConfigurationReader reader = new XMLConfigurationReader(STORAGE_DIR_PATH + CONFIG_FILE_PREFIX + id + ".xml");
                        Configuration config = reader.read();
                        String requestedFileName = config.getAcceptanceFile();
                        File requestedFile = new File(STORAGE_DIR_PATH + id + "/" + requestedFileName);
                        // is the file available yet?
                        if (jobs.containsKey(id)) {
                            status = jobs.get(id);
                        } else if (requestedFile.exists()) {
                            status = 2;
                        }
                    }
                    byte[] response = String.valueOf(status).getBytes();
                    t.sendResponseHeaders(200, response.length);
                    OutputStream os = t.getResponseBody();
                    os.write(response);
                    os.close();
                } else {
                    // 400 - Bad Request
                    t.sendResponseHeaders(400, -1);
                    logger.info("Bad request: " + t.getRequestURI() + "\nPlease specify job_id query parameters!");
                }
            } else {
                // we only accept GET requests here, anything else gets code "405 - Method Not Allowed"
                t.sendResponseHeaders(405, -1);
                logger.info("Bad request: HTTP VERB must be GET for " + t.getRequestURI());
            }
        }
    }

        public static Map<String, String> queryToMap(String query){
            Map<String, String> result = new HashMap<>();
            if (query == null)
                return result;
            for (String param : query.split("&")) {
                try {
                    param = java.net.URLDecoder.decode(param, "UTF-8");
                    String pair[] = param.split("=");
                    if (pair.length > 1) {
                        result.put(pair[0], pair[1]);
                    } else {
                        result.put(pair[0], "");
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            return result;
        }

    }
