package com.was.handler;

import com.web.servlet.SimpleServlet;
import com.was.api.ConfigModule;
import com.was.api.RouterModule;
import com.was.util.HttpRequest;
import com.was.util.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.net.Socket;
import java.nio.file.Files;

public class HttpHandler implements Runnable {
    private static Logger logger = LoggerFactory.getLogger(HttpResponse.class);
    private Socket connection;
    private static String ROOT_PATH;
    private InputStream input;
    private OutputStream output;
    HttpRequest request = null;
    HttpResponse response = null;

    public HttpHandler(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        // 상대경로 가져오기.
        File path = new File("");
        ROOT_PATH = path.getAbsolutePath();

        File file;
        ConfigModule properties = ConfigModule.getInstance();

        try {
            input = this.connection.getInputStream();
            output = this.connection.getOutputStream();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        request = new HttpRequest(input);
        request.parse();
        logger.info(request.toString());

        response = new HttpResponse(new BufferedOutputStream(output));

        byte[] data = new byte[0];

        try {
            String filePath = request.getUrl();

            if(properties.checkBlockedExtension(filePath)) {
                throw new IOException("Not Access File!!");
            }

            if(properties.checkBlockRootPath(filePath)) {
                throw new IOException("Not Access Root!!");
            }

            if(filePath != null && filePath.indexOf("favicon.ico") > -1) {
                throw new FileNotFoundException("NOT favicon");
            }

            if (filePath.endsWith("/")) filePath += properties.getDefaultServer().getSource().getIndex();
            filePath = ROOT_PATH + properties.getDefaultServer().getHttproot() + filePath;
            file = new File(filePath);

            if (file.canRead()) {
                data = Files.readAllBytes(file.toPath());
                response.setVersion(request.getHeader().getVersion());
                response.setResponseCode("200 OK");
                response.setContentType("text/html");
                response.setLength(data.length);
                response.sendHeader();
                response.getWriter().flush();
                response.send(data);
            } else {
                RouterModule routers = properties.getRouters();
                String packageName = routers.getUriPackage(request.getUrl());

                if(packageName != null) {
                    try {
                        SimpleServlet sServlet = null;
                        String methodName = routers.getRouter(packageName).getRouter(request.getUrl());
                        Class<SimpleServlet> forName = ((Class<SimpleServlet>) Class.forName(packageName + "." + methodName));

                        try {
                            sServlet = forName.newInstance();
                        } catch (InstantiationException e) {
                            logger.error(e.getMessage(), e);
                        } catch (IllegalAccessException e) {
                            logger.error(e.getMessage(), e);
                        }

                        if(sServlet != null) {
                            response.setVersion(request.getHeader().getVersion());
                            response.setResponseCode("200 OK");
                            response.setContentType("text/html");
                            response.setLength(0);
                            response.sendHeader();
                            sServlet.service(request, response);
                            response.getWriter().flush();
                        }
                    } catch (ClassNotFoundException e) {
                        throw new Exception("Not Found Class");
                    }
                } else {
                    throw new FileNotFoundException("404 File Not Found");
                }
            }
            logger.info(response.toString());
        } catch (FileNotFoundException e) {
            logger.warn(e.getMessage(), e);

            try {
                file = new File(ROOT_PATH + properties.getDefaultServer().getPage404());
                data = Files.readAllBytes(file.toPath());
                response.setVersion(request.getHeader().getVersion());
                response.setResponseCode("404 File Not Found");
                response.setContentType("text/html; charset=utf-8");
                response.setLength(data.length);
                response.sendHeader();
                response.send(data);

                logger.info(response.toString());
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }
        } catch (IOException e) {
            logger.warn(e.getMessage(), e);

            try {
                file = new File(ROOT_PATH + properties.getDefaultServer().getPage403());
                data = Files.readAllBytes(file.toPath());
                response.setVersion(request.getHeader().getVersion());
                response.setResponseCode("403 Forbidden");
                response.setContentType("text/html; charset=utf-8");
                response.setLength(data.length);
                response.sendHeader();
                response.send(data);

                logger.info(response.toString());
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);

            try {
                file = new File(ROOT_PATH + properties.getDefaultServer().getPage500());
                data = Files.readAllBytes(file.toPath());
                response.setVersion(request.getHeader().getVersion());
                response.setResponseCode("500 Internal Server Error");
                response.setContentType("text/html; charset=utf-8");
                response.setLength(data.length);
                response.sendHeader();
                response.send(data);

                logger.info(response.toString());
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }
        } finally {
            try {
                connection.close();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }
}
