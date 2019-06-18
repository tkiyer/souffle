package org.souffle.spi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

/**
 * Your class description. <p />
 *
 * @author tuyan
 * @version 1.0.0
 * @date 2018/1/9 上午9:33
 * @see
 * @since JDK1.7
 */
public class QueryConfiguration implements QueryConfigurable {

    public final static String APPLICATION_ID = initApplicationId();

    private final static String QUERY_ETC_CONF_FILE = "query-etc.properties";

    private final static String APPLICATION_ID_FILE = "server.applicationId";

    private Map<String, Object> confMap = new HashMap<>(10);

    private final String configurationId;

    private QueryConfiguration() {
        configurationId = UUID.randomUUID().toString();
    }

    private void loadPropertiesFromClasspath() {
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(QUERY_ETC_CONF_FILE);
        if (null != in) {
            Properties props = new Properties();
            try {
                props.load(in);
            } catch (IOException e) {
                throw new QueryException("Properties not load from file: " + QUERY_ETC_CONF_FILE, e);
            }
            // 转到Map里面去
            Enumeration<?> eum = props.propertyNames();
            while (eum.hasMoreElements()) {
                Object key = eum.nextElement();
                Object obj = props.get(key);
                if (null != obj) {
                    confMap.put(String.valueOf(key), obj);
                }
            }
        }
    }

    public static QueryConfiguration create() {
        return new QueryConfiguration();
    }

    public static QueryConfiguration load() {
        QueryConfiguration configuration = create();
        configuration.loadPropertiesFromClasspath();
        return configuration;
    }

    public String get(String key, String def) {
        Object obj = confMap.get(key);
        if (null == obj) {
            return def;
        }
        return obj.toString();
    }

    public long getLong(String key, long def) {
        Object obj = confMap.get(key);
        if (null == obj) {
            return def;
        }
        try {
            return Long.parseLong("" + obj);
        } catch (NumberFormatException ignore) {
            return def;
        }
    }

    public int getInt(String key, int def) {
        Object obj = confMap.get(key);
        if (null == obj) {
            return def;
        }
        try {
            return Integer.parseInt("" + obj);
        } catch (NumberFormatException ignore ) {
            return def;
        }
    }

    public boolean getBoolean(String key, boolean def) {
        Object obj = confMap.get(key);
        if (null == obj) {
            return def;
        }
        return Boolean.parseBoolean("" + obj);
    }

    public String getConfigurationPath() {
        String path = get(QUERY_PROJECT_PATH, null);
        if (null == path) {
            String codeSourceLocation = QueryConfiguration.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            String systemUserDir = System.getProperty("user.dir");
            //
            return codeSourceLocation.startsWith(systemUserDir) ? systemUserDir : codeSourceLocation;
        }
        return path;
    }

    public String getConfigurationId() {
        return configurationId;
    }

    private static String initApplicationId() {
        // read from file.
        String path = QueryConfiguration.load().getConfigurationPath();
        File f = new File(path, APPLICATION_ID_FILE);
        if (f.exists()) {
            String line = null;
            try (FileInputStream fis = new FileInputStream(f)) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
               line = reader.readLine();
               reader.close();
            } catch (IOException ignored) {
            }
            if (null == line) {
                // empty file.
                return writeApplicationId(f);
            } else {
                return line;
            }
        } else {
            return writeApplicationId(f);
        }
    }

    private static String writeApplicationId(File f) {
        if (f.exists()) {
            if (f.delete()) {
                System.out.println("Clean server.applicationId file: " + f.getAbsolutePath());
            }
        }
        String applicationId = UUID.randomUUID().toString();
        try (FileOutputStream fos = new FileOutputStream(f)) {
            fos.write(applicationId.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new QueryException("Server not write application id.", e);
        }
        return applicationId;
    }
}
