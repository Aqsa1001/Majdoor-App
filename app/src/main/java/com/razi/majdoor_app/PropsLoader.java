package com.razi.majdoor_app;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.InputStream;
import java.util.Properties;

public class PropsLoader {
    private static PropsLoader instance;
    private static Properties properties;
    private static Context context;

    private PropsLoader() {
    }

    public static PropsLoader getInstance(Context context) {
        PropsLoader.context = context;
        if (null == instance && null == properties) {
            instance = new PropsLoader();
            PropertyReader propertyReader;
            propertyReader = new PropertyReader(PropsLoader.context);
            properties = propertyReader.getMyProperties("config.properties");
        }
        return instance;
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    private static class PropertyReader {

        private final Context context;
        private final Properties properties;

        public PropertyReader(Context context) {
            this.context = context;
            properties = new Properties();
        }

        public Properties getMyProperties(String file) {
            try {
                AssetManager assetManager = context.getAssets();
                InputStream inputStream = assetManager.open(file);
                properties.load(inputStream);

            } catch (Exception e) {
                System.out.print(e.getMessage());
            }

            return properties;
        }
    }
}



