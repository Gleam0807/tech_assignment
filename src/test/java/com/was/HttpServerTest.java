package com.was;

import com.was.HttpServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

public class HttpServerTest {

    private static HttpServer server;

    @BeforeClass
    public static void setUp() throws Exception {
        server = new HttpServer(8090);
        Thread serverThread = new Thread(() -> {
            try {
                server.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        serverThread.setDaemon(true);
        serverThread.start();

    }

    @AfterClass
    public static void tearDown() throws IOException {
        ServerSocket serverSocket = null;
        
        if (serverSocket != null) {
            serverSocket.close();
        }
    }

    @Test
    public void testHello() throws Exception {
        URL url = new URL("http://localhost:8090/Hello?name=World");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        assertEquals(200, responseCode);

        try (InputStream in = connection.getInputStream();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            String responseBody = new String(out.toByteArray(), "UTF-8");
            assertEquals("Hello, World", responseBody);
        }
    }

    @Test
    public void notFound() throws Exception {
        URL url = new URL("http://localhost:8090/notfound");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        assertThat(responseCode).isEqualTo(-1);
    }

    @Test
    public void testDate() throws Exception {
        URL url = new URL("http://localhost:8090/date");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        String responseBody;
        try (InputStream in = connection.getInputStream();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            byte[] buf = new byte[1024 * 8];
            int length = 0;
            while ((length = in.read(buf)) != -1) {
                out.write(buf, 0, length);
            }
            responseBody = new String(out.toByteArray(), "UTF-8");
        }

        //시간까지 비교한다.
        responseBody = responseBody.substring(0, 13);
        assertThat(responseBody).isEqualTo(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH")));
    }
}
