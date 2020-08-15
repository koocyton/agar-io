package com.doopp.agar.server;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.core.io.FileSystemResource;

public class KTApplication {

    public static void main(String[] args) {

        if (args[0] != null && (new FileSystemResource(args[0])).exists()) {

            // get run properties
            System.setProperty("applicationPropertiesConfig", args[0]);

            // init applicationContext
            final AbstractApplicationContext ctx = new FileSystemXmlApplicationContext("classpath:config/spring-undertow.xml");

            // add a shutdown hook for the above context...
            ctx.registerShutdownHook();
        }
        // 缺少配置文件
        else {
            System.out.print("\n Run Example : java -jar application.jar propertiesConfigPath\n");
        }
    }
}
