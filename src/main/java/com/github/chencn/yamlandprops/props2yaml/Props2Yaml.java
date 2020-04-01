package com.github.chencn.yamlandprops.props2yaml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.invoke.MethodHandles;
import java.nio.file.Path;
import java.util.Properties;

/**
 * @author xqchen
 */
public class Props2Yaml {

    private final static Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final Properties properties = new Properties();

    Props2Yaml(String source) {
        try {
            properties.load(new StringReader(source));
        } catch (IOException e) {
            reportError(e);
        }
    }

    Props2Yaml(File file) {
        try (InputStream input = new FileInputStream(file)) {
            properties.load(input);
        } catch (IOException e) {
            reportError(e);
        }
    }

    public static Props2Yaml fromContent(String content) {
        return new Props2Yaml(content);
    }

    public static Props2Yaml fromFile(File file) {
        return new Props2Yaml(file);
    }

    public static Props2Yaml fromFile(Path path) {
        return new Props2Yaml(path.toFile());
    }

    public String convert(boolean useNumericKeysAsArrayIndexes) {
        PropertyTree tree = new TreeBuilder(properties, useNumericKeysAsArrayIndexes).build();
        tree = new ArrayProcessor(tree).apply();
        return tree.toYaml();
    }

    public String convert() {
        return convert(true);
    }

    private void reportError(IOException e) {
        LOG.error("Conversion failed", e);
    }

}

