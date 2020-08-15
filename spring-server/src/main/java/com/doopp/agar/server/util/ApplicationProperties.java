package com.doopp.agar.server.util;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Properties;

public class ApplicationProperties extends Properties {

    public ApplicationProperties() {
        String applicationPropertiesConfig = System.getProperty("applicationPropertiesConfig");
        FileSystemResource cpr = new FileSystemResource(applicationPropertiesConfig);
        try {
            this.load(cpr.getInputStream());
        }
        catch(IOException e) {
            System.out.print("\n ApplicationProperties load " + applicationPropertiesConfig + " failed \n");
        }
    }

    public Resource r(String key) {
        return new FileSystemResource(this.getProperty(key));
    }

    public Long l(String key) {
        return Long.valueOf(this.getProperty(key));
    }


    public String s(String key) {
        return this.getProperty(key);
    }

    public int i(String key) {
        return Integer.valueOf(this.getProperty(key));
    }

    public boolean b(String key) {
        return Boolean.valueOf(this.getProperty(key));
    }
}
