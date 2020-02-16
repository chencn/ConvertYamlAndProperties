package com.github.chencn.yamlandprops.yaml2props;

import org.yaml.snakeyaml.Yaml;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author xqchen
 */
public class Yaml2Props {

    TreeMap<String, Map<String, Object>> config;

    public Yaml2Props(String contents) {
        Yaml yaml = new Yaml();
        this.config = (TreeMap<String, Map<String, Object>>) yaml.loadAs(contents, (Class<TreeMap>) TreeMap.class);
    }

    public static Yaml2Props fromContent(String content) {
        return new Yaml2Props(content);
    }

    public String convert() {
        return toProperties(this.config);
    }

    private static String toProperties(final TreeMap<String, Map<String, Object>> config) {
        StringBuilder sb = new StringBuilder();
        for (final String key : config.keySet()) {
            sb.append(toString(key, config.get(key)));
        }
        return sb.toString();
    }

    private static String toString(final String key, final Object o) {
        StringBuilder sb = new StringBuilder();
        if (o instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) o;
            for (final String mapKey : map.keySet()) {
                if (map.get(mapKey) instanceof Map) {
                    sb.append(toString(String.format("%s.%s", key, mapKey), map.get(mapKey)));
                } else {
                    sb.append(String.format("%s.%s=%s%n", key, mapKey, map.get(mapKey).toString()));
                }
            }
        } else {
            sb.append(String.format("%s=%s%n", key, o));
        }
        return sb.toString();
    }
}
