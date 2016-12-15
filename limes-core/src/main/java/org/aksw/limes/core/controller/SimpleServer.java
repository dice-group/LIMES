package org.aksw.limes.core.controller;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.fileupload.MultipartStream;

/**
 * @author Kevin Dreßler
 */

public class SimpleServer {

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/execute", new ExecuteHandler());
        server.createContext("/getresult", new GetResultHandler());
        server.setExecutor(null);
        server.start();
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
                System.out.println("New Job: " + id);
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();
                Controller.main((String[]) Arrays.asList("-f", "xml", "./temp/config_web_" + id + ".xml").toArray());
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

            } else {
                // we only accept GET requests here, anything else gets code "405 - Method Not Allowed"
                t.sendResponseHeaders(405 ,-1);
            }
        }
    }

}
