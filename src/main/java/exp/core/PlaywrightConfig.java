package exp;

import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.options.ColorScheme;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
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

    // Существующие методы
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

    public boolean isClearContextAfterTest() {
        return Boolean.parseBoolean(properties.getProperty("clearContext.afterTest", "false"));
    }

    // Viewport
    public boolean hasViewport() {
        return properties.containsKey("viewport.width") && properties.containsKey("viewport.height");
    }

    public int getViewportWidth() {
        return Integer.parseInt(properties.getProperty("viewport.width", "1280"));
    }

    public int getViewportHeight() {
        return Integer.parseInt(properties.getProperty("viewport.height", "720"));
    }

    // User agent
    public boolean hasUserAgent() {
        return properties.containsKey("userAgent");
    }

    public String getUserAgent() {
        return properties.getProperty("userAgent", "");
    }

    // Locale
    public boolean hasLocale() {
        return properties.containsKey("locale");
    }

    public String getLocale() {
        return properties.getProperty("locale", "en-US");
    }

    // Timezone - используется через extra HTTP headers
    public boolean hasTimezone() {
        return properties.containsKey("timezone");
    }

    public String getTimezone() {
        return properties.getProperty("timezone", "UTC");
    }

    // Video recording
    public boolean isRecordVideo() {
        return Boolean.parseBoolean(properties.getProperty("recordVideo", "false"));
    }

    public Path getVideoDir() {
        return Paths.get(properties.getProperty("video.dir", "videos"));
    }

    public int getVideoWidth() {
        return Integer.parseInt(properties.getProperty("video.width", "1280"));
    }

    public int getVideoHeight() {
        return Integer.parseInt(properties.getProperty("video.height", "720"));
    }

    // Geolocation
    public boolean hasGeolocation() {
        return properties.containsKey("geolocation.latitude") && properties.containsKey("geolocation.longitude");
    }

    public double getLatitude() {
        return Double.parseDouble(properties.getProperty("geolocation.latitude", "0"));
    }

    public double getLongitude() {
        return Double.parseDouble(properties.getProperty("geolocation.longitude", "0"));
    }

    // Permissions
    public boolean hasPermissions() {
        return properties.containsKey("permissions");
    }

    public List<String> getPermissions() {
        String permissionsStr = properties.getProperty("permissions", "");
        return Arrays.asList(permissionsStr.split(","));
    }

    // Device emulation
    public boolean hasDeviceName() {
        return properties.containsKey("device");
    }

    public String getDeviceName() {
        return properties.getProperty("device", "");
    }

    // HTTPS Errors
    public boolean isIgnoreHTTPSErrors() {
        return Boolean.parseBoolean(properties.getProperty("ignoreHTTPSErrors", "false"));
    }

    // Storage State
    public boolean hasStorageState() {
        return properties.containsKey("storageState.path");
    }

    public String getStorageStatePath() {
        return properties.getProperty("storageState.path", "");
    }

    // Color Scheme
    public boolean hasColorScheme() {
        return properties.containsKey("colorScheme");
    }

    public ColorScheme getColorScheme() {
        String scheme = properties.getProperty("colorScheme", "no-preference");
        return switch (scheme.toLowerCase()) {
            case "dark" -> ColorScheme.DARK;
            case "light" -> ColorScheme.LIGHT;
            default -> ColorScheme.NO_PREFERENCE;
        };
    }

    // Метод для получения опций запуска браузера
    public BrowserType.LaunchOptions getLaunchOptions() {
        BrowserType.LaunchOptions options = new BrowserType.LaunchOptions()
                .setHeadless(isHeadless())
                .setSlowMo(getSlowMo());

        // Добавляем директорию для трассировок, если она указана
        if (properties.containsKey("traces.dir")) {
            options.setTracesDir(Paths.get(properties.getProperty("traces.dir")));
        }

        return options;
    }

    // Метод для загрузки конфигурации из произвольного файла
    public static PlaywrightConfig loadFromFile(String filePath) {
        PlaywrightConfig config = new PlaywrightConfig();
        try {
            config.properties.load(new java.io.FileReader(filePath));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load configuration from file: " + filePath, e);
        }
        return config;
    }

    // Метод для программного изменения настроек
    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }
}