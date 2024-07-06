package com.was.api;

import java.io.IOException;
import java.util.Arrays;
import com.was.api.domain.Server;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.was.util.FileUtil;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigModule {
    private static Logger logger = LoggerFactory.getLogger(ConfigModule.class);
    private static ServerModule servers;
    private static int PORT;
    private static String[] blockedExtension;
    private static ConfigModule instance = null;
    private static RouterModule routers;

    private ConfigModule() throws Exception {
        Config conf = ConfigFactory.load();

        PORT = conf.getInt("port");
        blockedExtension = String.valueOf(conf.getString("blocked_extension")).split(",");
        servers = new ServerModule(conf.getConfigList("servers"));
        servers.setDefaultServer(conf.getString("default"));

        logger.debug(servers.getServer("was").toString());
        logger.debug(servers.getServer("web").toString());

        logger.info("START http://localhost:" + PORT);

        try {
            String fileContent = FileUtil.getFileContent("mapping.json");
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(fileContent);
            JSONObject mapper = (JSONObject) obj;
            routers = new RouterModule();
            routers.buildRouter(mapper);
        } catch (IOException e) {
            logger.warn(e.getMessage(), e);
        }
    }

    public static ConfigModule getInstance() {
        if(instance == null) {
            try {
                instance = new ConfigModule();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        return ConfigModule.instance;
    }

    public int getPort() {
        return PORT;
    }

    public ServerModule getServers() {
        return servers;
    }

    public RouterModule getRouters() {
        return routers;
    }

    public Server getDefaultServer() {
        return this.getServers().getDefaultServer();
    }

    public Boolean checkBlockedExtension(String fileName) throws ArrayIndexOutOfBoundsException {
        if(fileName == null) {
            return false;
        }
        if(fileName.indexOf(".") > -1) {
            String[] fileToken = fileName.split("\\.");
            return Arrays.asList(blockedExtension).contains(fileToken[1]);
        }
        if (fileName.indexOf("../") > -1) {
            return true;
        }
        return false;
    }

    public Boolean checkBlockRootPath(String fileName) {
        if(fileName == null) {
            return false;
        }
        if (fileName.indexOf("../") > -1) {
            return true;
        }
        return false;
    }
}
