package org.aksw.limes.core.controller;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.aksw.limes.core.io.config.Configuration;
import org.aksw.limes.core.io.config.reader.AConfigurationReader;
import org.aksw.limes.core.io.config.reader.xml.XMLConfigurationReader;
import org.aksw.limes.core.io.serializer.ISerializer;
import org.aksw.limes.core.io.serializer.SerializerFactory;
import org.apache.commons.fileupload.MultipartStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Kevin Dreßler
 */

// @todo: map output format to standard file suffixes
// @todo: error-prone streaming of potentially large result mappings to http client

public class SimpleServer {

    private static final Logger logger = LoggerFactory.getLogger(SimpleServer.class.getName());

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
        server.setExecutor(null);
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
                String response = id + "\n";
                t.sendResponseHeaders(200, response.length());
                logger.info("New Job: " + id);
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();
                AConfigurationReader reader = new XMLConfigurationReader("./temp/config_web_" + id + ".xml");
                Configuration config = reader.read();
                ResultMappings mappings = Controller.getMapping(config);
                String outputFormat = config.getOutputFormat();
                ISerializer output = SerializerFactory.createSerializer(outputFormat);
                output.setPrefixes(config.getPrefixes());
                File _verificationFile = new File("./temp/_verification_" + id + "." + outputFormat.toLowerCase());
                File _acceptanceFile = new File("./temp/_acceptance_" + id + "." + outputFormat.toLowerCase());
                File verificationFile = new File("./temp/verification_" + id + "." + outputFormat.toLowerCase());
                File acceptanceFile = new File("./temp/acceptance_" + id + "." + outputFormat.toLowerCase());
                output.writeToFile(mappings.getVerificationMapping(), config.getVerificationRelation(),
                        _verificationFile.getAbsolutePath());
                output.writeToFile(mappings.getAcceptanceMapping(), config.getAcceptanceRelation(),
                        _acceptanceFile.getAbsolutePath());
                _verificationFile.renameTo(verificationFile);
                _acceptanceFile.renameTo(acceptanceFile);
            } else {
                // we only accept POST requests here, anything else gets code "405 - Method Not Allowed"
                t.sendResponseHeaders(405 ,-1);
            }
        }

        private String writeConfigFile (InputStream inputStream, String b) throws IOException {
            byte[] boundary = b.getBytes();
            @SuppressWarnings("deprecation")
            MultipartStream multipartStream = new MultipartStream(inputStream, boundary);
            File tempDir = new File("./temp/");
            if (!tempDir.exists()) {
                tempDir.mkdir();
            }
            File file = File.createTempFile("config_web_", ".xml", tempDir);
            FileOutputStream out = new FileOutputStream(file.getAbsolutePath());
            boolean nextPart = multipartStream.skipPreamble();
            if (nextPart) {
                multipartStream.readHeaders();
                multipartStream.readBodyData(out);
            }
            out.close();
            String id = file.getName();
            return id.substring(id.indexOf("web_")+4, id.lastIndexOf(".xml"));
        }
    }

    private static class GetResultHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange t) throws IOException {
            if (t.getRequestMethod().equals("GET")) {
                Map<String, String> params = queryToMap(t.getRequestURI().getRawQuery());
                if (params.containsKey("job_id") && params.containsKey("result_type") &&
                        Arrays.asList("acceptance, verification").contains(params.get("result_type").toLowerCase())) {

                } else {
                    t.sendResponseHeaders(400 ,-1);
                }
            } else {
                // we only accept GET requests here, anything else gets code "405 - Method Not Allowed"
                t.sendResponseHeaders(405 ,-1);
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
