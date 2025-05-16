package exp;

import com.microsoft.playwright.BrowserType;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PlaywrightConfig {
    private static PlaywrightConfig instance;
    private final Properties properties;

    private PlaywrightConfig() {
        properties = new Properties();
        try {
            // Загрузка настроек из файла (если есть)
            properties.load(getClass().getResourceAsStream("/playwright.properties"));
        } catch (Exception e) {
            // Используем значения по умолчанию
            properties.setProperty("browser", "chromium");
            properties.setProperty("headless", "false");
            properties.setProperty("slowMo", "100");
            properties.setProperty("screenshot.onFailure", "true");
            properties.setProperty("trace.onFailure", "true");
        }
    }

    public static PlaywrightConfig getInstance() {
        if (instance == null) {
            instance = new PlaywrightConfig();
        }
        return instance;
    }

    public String getBrowser() {
        return properties.getProperty("browser", "chromium");
    }

    public boolean isHeadless() {
        return Boolean.parseBoolean(properties.getProperty("headless", "false"));
    }

    public int getSlowMo() {
        return Integer.parseInt(properties.getProperty("slowMo", "100"));
    }

    public boolean takeScreenshotOnFailure() {
        return Boolean.parseBoolean(properties.getProperty("screenshot.onFailure", "true"));
    }

    public boolean captureTraceOnFailure() {
        return Boolean.parseBoolean(properties.getProperty("trace.onFailure", "true"));
    }

    public BrowserType.LaunchOptions getLaunchOptions() {
        return new BrowserType.LaunchOptions()
                .setHeadless(isHeadless())
                .setSlowMo(getSlowMo());
    }
}