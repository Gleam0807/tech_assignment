package com.web.service;

import com.web.servlet.SimpleServlet;
import com.was.util.HttpRequest;
import com.was.util.HttpResponse;
import java.io.IOException;
import java.io.Writer;

public class Hello implements SimpleServlet {

    @Override
    public void service(HttpRequest req, HttpResponse res) throws IOException {
        System.out.print(res);
        Writer writer = res.getWriter();
        writer.write("service.Hello, ");
        writer.write(req.getParameter("name"));
        writer.flush();
    }
}
