package com.was.api.domain;

import com.typesafe.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server {
    private static Logger logger = LoggerFactory.getLogger(Server.class);

    private String name;
    private String domain;
    private String httproot;
    private Source source;
    private String page403;
    private String page404;
    private String page500;

    public Server(String name){
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getHttproot() {
        return httproot;
    }

    public void setHttproot(String http_root) {
        this.httproot = http_root;
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Config source) {
        this.source = new Source(source);
    }

    public String getPage403() {
        return this.httproot + this.source.get403();
    }

    public String getPage404() {
        return this.httproot + this.source.get404();
    }

    public String getPage500() {
        return this.httproot + this.source.get500();
    }

    @Override
    public String toString() {
        return "Server = {"+
            "name: \"" + name +"\", "+
            "domain: \"" + domain +"\", "+
            "httproot: \"" + httproot +"\", "+
            "source: \"" + source +"\" } ";
    }
}