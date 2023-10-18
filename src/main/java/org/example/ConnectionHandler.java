package org.example;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ConnectionHandler {
    private static String HTTP_HEADERS_TYPE = "";

    private static String HTTP_BODY = "";
    private static final String HTTP_HEADERS = "HTTP/1.1 200OK\n" +
            "Date: Mon, 18 Sep 2023 14:08:55 +0200\n" +
            "HttpServer: Simple Webserver\n" +
            "Content-Length: 180\n";

    private static final String HTTP_BODY_HTML = "<!DOCTYPE html>\n"
            +
            "<html lang=\"en\">\n" +
            "<head>\n" +
            "<meta charset=\"UTF-8\">\n" +
            "<title>Simple Http Server</title>\n" +
            "</head>\n" +
            "<body>\n" +
            "<h1>Hi!</h1>\n" +
            "<p>This is a simple line in html.</p>\n" +
            "</body>\n" +
            "</html>\n" +
            "\n";

    private static final String HTTP_BODY_TEXT = "This is a simple line.";
    private Socket socket;
    public ConnectionHandler(Socket socket) {
        this.socket = socket;
        handle();
    }
    public void handle() {
        try {
            var inputStreamReader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream(),StandardCharsets.US_ASCII));
            var outputStreamWriter = new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.US_ASCII));
            switch (parseRequest(inputStreamReader)) {
                case "application/json": {
                    HTTP_HEADERS_TYPE.equals("Content-Type: application/json\n");
                    HTTP_BODY = HTTP_BODY_TEXT;
                    writeResponse(outputStreamWriter);
                } break;
                case "text/plain": {
                    HTTP_HEADERS_TYPE.equals("Content-Type: text/plain\n" +
                            "Content-Disposition: attachment; filename=File.txt\n");
                    HTTP_BODY = HTTP_BODY_TEXT;
                    writeResponse(outputStreamWriter);
                } break;
                default: {
                    HTTP_HEADERS_TYPE.equals("Content-Type: text/html\n");
                    HTTP_BODY = HTTP_BODY_HTML;
                    writeResponse(outputStreamWriter);
                } break;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private String parseRequest(BufferedReader inputStreamReader) throws IOException {
        String str = "";
        var request = inputStreamReader.readLine();
        while (request != null && !request.isEmpty()) {
            System.out.println(request);
            request = inputStreamReader.readLine();
        }
        return str;
    }
    private void writeResponse(BufferedWriter outputStreamWriter) {
        try {
            outputStreamWriter.write(HTTP_HEADERS + HTTP_HEADERS_TYPE);
            outputStreamWriter.newLine();
            outputStreamWriter.write(HTTP_BODY);
            outputStreamWriter.newLine();
            outputStreamWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}