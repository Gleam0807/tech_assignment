package com.web.servlet;

import com.was.util.HttpRequest;
import com.was.util.HttpResponse;

public interface SimpleServlet {
    void service(HttpRequest req, HttpResponse res) throws Exception;
}
