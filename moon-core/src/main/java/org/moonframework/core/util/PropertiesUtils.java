package org.moonframework.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/6/1
 */
public class PropertiesUtils {

    public static Properties load(Class<?> clazz, String name) {
        try (InputStream inputStream = clazz.getClassLoader().getResourceAsStream(name);
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8")) {
            Properties properties = new Properties();
            properties.load(inputStreamReader);
            return properties;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
