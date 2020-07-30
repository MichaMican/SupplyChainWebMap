package com.thd.mapserver;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Settings {
    private static Settings instance;
    private static final String SETTINGS_FILE_PATH = "settings.json";

    private String dbConString;

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
}
