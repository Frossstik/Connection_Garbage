package org.example;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ConnectionHandler {
    private static String HTTP_HEADERS_TYPE;

    private static String HTTP_BODY;

    static class JsonMessage{
        public String message;
    }
    private static final String HTTP_HEADERS = "HTTP/1.1 200 OK\n" +
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
            if (parseRequest(inputStreamReader).contains("application/json")) {
                ObjectMapper mapper = new ObjectMapper();
                JsonMessage jsonMessage = new JsonMessage();
                jsonMessage.message = HTTP_BODY_TEXT;
                HTTP_HEADERS_TYPE = ("Content-Type: application/json\n");
                HTTP_BODY = mapper.writeValueAsString(jsonMessage);
                System.out.println(HTTP_HEADERS+HTTP_HEADERS_TYPE+HTTP_BODY);
                writeResponse(outputStreamWriter);
            }
            else if (parseRequest(inputStreamReader).contains("text/plain" +
                    "Content-Disposition: attachment; filename=File.txt\n")){
                HTTP_HEADERS_TYPE = ("Content-Type: text/html\n");
                HTTP_BODY = HTTP_BODY_TEXT;
                System.out.println(HTTP_HEADERS+HTTP_HEADERS_TYPE+HTTP_BODY);
                writeResponse(outputStreamWriter);
            }
            else {
                HTTP_HEADERS_TYPE = ("Content-Type: text/html\n");
                HTTP_BODY = HTTP_BODY_HTML;
                System.out.println(HTTP_HEADERS+HTTP_HEADERS_TYPE+HTTP_BODY);
                writeResponse(outputStreamWriter);
            }
        }
         catch (IOException e) {
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