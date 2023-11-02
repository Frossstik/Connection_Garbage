package org.example;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ConnectionHandler {

    private static String HTTP_HEADERS_TYPE;
    private static final String HTTP_HEADERS = "HTTP/1.1 200 OK\n" +
            "Date: Mon, 18 Sep 2023 14:08:55 +0200\n" +
            "HttpServer: Simple Webserver\n";

    private static final String HTTP_BODY_HTML = "<!DOCTYPE html>\n" +
            "<html lang=\"en\">\n" +
            "<head>\n" +
            "<meta charset=\"UTF-8\">\n" +
            "<title>Simple Http Server</title>\n" +
            "</head>\n" +
            "<body>\n" +
            "<h1>Hi!</h1>\n" +
            "<p>This is a simple line in html.</p>\n" +
            "</body>\n" +
            "</html>\n";
    private static String HTTP_BODY;
    private static final String HTTP_BODY_TEXT = "TEXT\n";

    private static final String HTTP_BODY_JSON = "{type: JSON}\n";
    private Socket socket;

    public ConnectionHandler(Socket socket) {
        this.socket = socket;
        handle();
    }

    public void handle() {
        try {
            var inputStreamReader = new BufferedReader(new InputStreamReader(socket.getInputStream(),
                    StandardCharsets.UTF_8));
            var outputStreamWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),
                    StandardCharsets.UTF_8));
            switch (parseRequest(inputStreamReader)) {
                case "application/json" -> {
                    HTTP_HEADERS_TYPE = "Content-Length: 12\n" +
                            "Content-Type: application/json\n";
                    HTTP_BODY = HTTP_BODY_JSON;
                }
                case "text/html" -> {
                    HTTP_HEADERS_TYPE = "Content-Length: 180\n" +
                            "Content-Type: text/html\n";
                    HTTP_BODY = HTTP_BODY_HTML;
                }
                default -> {
                    HTTP_HEADERS_TYPE = "Content-Length: 4\n" +
                            "Content-Type: text/plain\n" +
                            "Content-Disposition: attachment; filename=file.txt\n";
                    HTTP_BODY = HTTP_BODY_TEXT;
                }
            }
            writeResponse(outputStreamWriter);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String parseRequest(BufferedReader inputStreamReader) throws IOException {
        String str = "";
        var request = inputStreamReader.readLine();
        while (request != null && !request.isEmpty()) {
            request = inputStreamReader.readLine();
            System.out.println(request);
            if (request.contains("Accept:")) {
                str = request.substring(7).trim();
                if (str.contains(",")) {
                    str = str.split("\\,", 2)[0];
                }
                break;
            }
        }
        return str;
    }

    private void writeResponse(BufferedWriter outputStreamWriter) {
        try {
            outputStreamWriter.write(HTTP_HEADERS);
            outputStreamWriter.write(HTTP_HEADERS_TYPE);
            outputStreamWriter.newLine();
            outputStreamWriter.write(HTTP_BODY);
            outputStreamWriter.newLine();
            outputStreamWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}