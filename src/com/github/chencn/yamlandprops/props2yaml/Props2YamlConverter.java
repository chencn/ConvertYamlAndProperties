package com.github.chencn.yamlandprops.props2yaml;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

class Props2YamlConverter {

    private Props2YamlConverter() {
    }
    
    private static String getFileName(String[] args) {
        if (args.length != 1) {
            throw new IllegalArgumentException("Missing file name");
        }
        return args[0];
    }

    private static String readPropertiesFromFile(String fileName) {
        try {
            return new Scanner(new File(fileName)).useDelimiter("\\A").next();
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("File does not exist: " + fileName);
        }
    }

    private static void handleException(IllegalArgumentException ex) {
        System.err.println(ex.getMessage());
        System.err.println(usage());
        System.exit(1);
    }

    private static String usage() {
        return String.format("Usage: %s fileName", Props2YamlConverter.class.getSimpleName());
    }
}
