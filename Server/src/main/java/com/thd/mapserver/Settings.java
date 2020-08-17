package com.thd.mapserver;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class Settings {
    private static Settings instance;
    private static final String SETTINGS_FILE_PATH = "settings.json";

    private String dbConString;
    private String baseLink;

    private Settings() {

    }

    public static Settings getInstance() {
        if (instance == null) {
            instance = fromFile(new File(SETTINGS_FILE_PATH));
        }
        return instance;
    }

    private static Settings fromFile(File configFile) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(configFile, Settings.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getDbConString() {
        return dbConString;
    }

    public String getBaseLink() {
        return baseLink;
    }
}
