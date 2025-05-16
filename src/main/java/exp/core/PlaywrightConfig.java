package exp.core;

import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.options.ColorScheme;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * Класс для управления конфигурацией фреймворка Playwright.
 * <p>
 * Этот класс использует паттерн Singleton для предоставления централизованного
 * доступа к настройкам фреймворка. Загружает настройки из файла playwright.properties
 * и предоставляет методы для их получения и изменения.
 * <p>
 * Поддерживаемые настройки включают:
 * - Тип браузера (chromium, firefox, webkit)
 * - Режим запуска (headless, slowMo)
 * - Размеры окна браузера
 * - Пользовательский агент
 * - Локаль и часовой пояс
 * - Запись видео
 * - Геолокация
 * - Разрешения браузера
 * - Эмуляция устройств
 * - Настройки трассировки и скриншотов
 * - И многие другие
 * <p>
 * Пример использования:
 * ```
 * // Получение экземпляра конфигурации
 * PlaywrightConfig config = PlaywrightConfig.getInstance();
 * <p>
 * // Чтение настроек
 * String browser = config.getBrowser();
 * boolean headless = config.isHeadless();
 * <p>
 * // Изменение настроек программно
 * config.setProperty("browser", "firefox");
 * config.setProperty("headless", "true");
 * ```
 */
public class PlaywrightConfig {
    /**
     * Экземпляр класса для реализации паттерна Singleton.
     */
    private static PlaywrightConfig instance;

    /**
     * Хранилище настроек.
     */
    private final Properties properties;

    /**
     * Приватный конструктор для Singleton.
     * Загружает настройки из файла playwright.properties или устанавливает значения по умолчанию.
     */
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

    /**
     * Получает единственный экземпляр конфигурации.
     *
     * @return экземпляр PlaywrightConfig
     */
    public static PlaywrightConfig getInstance() {
        if (instance == null) {
            instance = new PlaywrightConfig();
        }
        return instance;
    }

    /**
     * Получает тип браузера из конфигурации.
     *
     * @return строка с типом браузера (chromium, firefox, webkit)
     */
    public String getBrowser() {
        return properties.getProperty("browser", "chromium");
    }

    /**
     * Проверяет, должен ли браузер запускаться в безголовом режиме (без GUI).
     *
     * @return true если headless=true, иначе false
     */
    public boolean isHeadless() {
        return Boolean.parseBoolean(properties.getProperty("headless", "false"));
    }

    /**
     * Получает значение задержки между действиями (в миллисекундах).
     * Полезно для замедления тестов для отладки или демонстрации.
     *
     * @return значение задержки в миллисекундах
     */
    public int getSlowMo() {
        return Integer.parseInt(properties.getProperty("slowMo", "100"));
    }

    /**
     * Проверяет, нужно ли делать скриншот при неудачном тесте.
     *
     * @return true если screenshot.onFailure=true, иначе false
     */
    public boolean takeScreenshotOnFailure() {
        return Boolean.parseBoolean(properties.getProperty("screenshot.onFailure", "true"));
    }

    /**
     * Проверяет, нужно ли сохранять трассировку при неудачном тесте.
     *
     * @return true если trace.onFailure=true, иначе false
     */
    public boolean captureTraceOnFailure() {
        return Boolean.parseBoolean(properties.getProperty("trace.onFailure", "true"));
    }

    /**
     * Проверяет, нужно ли очищать контекст браузера после каждого теста.
     * Очистка включает cookies, localStorage, sessionStorage.
     *
     * @return true если clearContext.afterTest=true, иначе false
     */
    public boolean isClearContextAfterTest() {
        return Boolean.parseBoolean(properties.getProperty("clearContext.afterTest", "false"));
    }

    /**
     * Проверяет, заданы ли размеры вьюпорта в конфигурации.
     *
     * @return true если заданы оба параметра viewport.width и viewport.height
     */
    public boolean hasViewport() {
        return properties.containsKey("viewport.width") && properties.containsKey("viewport.height");
    }

    /**
     * Получает ширину вьюпорта из конфигурации.
     *
     * @return ширина вьюпорта в пикселях
     */
    public int getViewportWidth() {
        return Integer.parseInt(properties.getProperty("viewport.width", "1280"));
    }

    /**
     * Получает высоту вьюпорта из конфигурации.
     *
     * @return высота вьюпорта в пикселях
     */
    public int getViewportHeight() {
        return Integer.parseInt(properties.getProperty("viewport.height", "720"));
    }

    /**
     * Проверяет, задан ли пользовательский агент в конфигурации.
     *
     * @return true если задан параметр userAgent
     */
    public boolean hasUserAgent() {
        return properties.containsKey("userAgent");
    }

    /**
     * Получает пользовательский агент из конфигурации.
     *
     * @return строка с пользовательским агентом
     */
    public String getUserAgent() {
        return properties.getProperty("userAgent", "");
    }

    /**
     * Проверяет, задана ли локаль в конфигурации.
     *
     * @return true если задан параметр locale
     */
    public boolean hasLocale() {
        return properties.containsKey("locale");
    }

    /**
     * Получает локаль из конфигурации.
     *
     * @return строка с локалью (например, "ru-RU")
     */
    public String getLocale() {
        return properties.getProperty("locale", "en-US");
    }

    /**
     * Проверяет, задан ли часовой пояс в конфигурации.
     *
     * @return true если задан параметр timezone
     */
    public boolean hasTimezone() {
        return properties.containsKey("timezone");
    }

    /**
     * Получает часовой пояс из конфигурации.
     *
     * @return строка с часовым поясом (например, "Europe/Moscow")
     */
    public String getTimezone() {
        return properties.getProperty("timezone", "UTC");
    }

    /**
     * Проверяет, включена ли запись видео в конфигурации.
     *
     * @return true если recordVideo=true, иначе false
     */
    public boolean isRecordVideo() {
        return Boolean.parseBoolean(properties.getProperty("recordVideo", "false"));
    }

    /**
     * Получает путь к директории для сохранения видео.
     *
     * @return объект Path с путем к директории для видео
     */
    public Path getVideoDir() {
        return Paths.get(properties.getProperty("video.dir", "videos"));
    }

    /**
     * Получает ширину видео из конфигурации.
     *
     * @return ширина видео в пикселях
     */
    public int getVideoWidth() {
        return Integer.parseInt(properties.getProperty("video.width", "1280"));
    }

    /**
     * Получает высоту видео из конфигурации.
     *
     * @return высота видео в пикселях
     */
    public int getVideoHeight() {
        return Integer.parseInt(properties.getProperty("video.height", "720"));
    }

    /**
     * Проверяет, задана ли геолокация в конфигурации.
     *
     * @return true если заданы оба параметра geolocation.latitude и geolocation.longitude
     */
    public boolean hasGeolocation() {
        return properties.containsKey("geolocation.latitude") && properties.containsKey("geolocation.longitude");
    }

    /**
     * Получает широту для геолокации из конфигурации.
     *
     * @return широта в градусах
     */
    public double getLatitude() {
        return Double.parseDouble(properties.getProperty("geolocation.latitude", "0"));
    }

    /**
     * Получает долготу для геолокации из конфигурации.
     *
     * @return долгота в градусах
     */
    public double getLongitude() {
        return Double.parseDouble(properties.getProperty("geolocation.longitude", "0"));
    }

    /**
     * Проверяет, заданы ли разрешения браузера в конфигурации.
     *
     * @return true если задан параметр permissions
     */
    public boolean hasPermissions() {
        return properties.containsKey("permissions");
    }

    /**
     * Получает список разрешений браузера из конфигурации.
     *
     * @return список строк с разрешениями (например, ["geolocation", "notifications"])
     */
    public List<String> getPermissions() {
        String permissionsStr = properties.getProperty("permissions", "");
        return Arrays.asList(permissionsStr.split(","));
    }

    /**
     * Проверяет, задано ли имя устройства для эмуляции в конфигурации.
     *
     * @return true если задан параметр device
     */
    public boolean hasDeviceName() {
        return properties.containsKey("device");
    }

    /**
     * Получает имя устройства для эмуляции из конфигурации.
     *
     * @return строка с именем устройства (например, "iPhone11")
     */
    public String getDeviceName() {
        return properties.getProperty("device", "");
    }

    /**
     * Проверяет, нужно ли игнорировать ошибки HTTPS.
     *
     * @return true если ignoreHTTPSErrors=true, иначе false
     */
    public boolean isIgnoreHTTPSErrors() {
        return Boolean.parseBoolean(properties.getProperty("ignoreHTTPSErrors", "false"));
    }

    /**
     * Проверяет, задано ли состояние хранилища в конфигурации.
     *
     * @return true если задан параметр storageState.path
     */
    public boolean hasStorageState() {
        return properties.containsKey("storageState.path");
    }

    /**
     * Получает путь к файлу с состоянием хранилища.
     *
     * @return строка с путем к файлу состояния хранилища
     */
    public String getStorageStatePath() {
        return properties.getProperty("storageState.path", "");
    }

    /**
     * Проверяет, задана ли цветовая схема в конфигурации.
     *
     * @return true если задан параметр colorScheme
     */
    public boolean hasColorScheme() {
        return properties.containsKey("colorScheme");
    }

    /**
     * Получает цветовую схему из конфигурации.
     *
     * @return объект ColorScheme (DARK, LIGHT или NO_PREFERENCE)
     */
    public ColorScheme getColorScheme() {
        String scheme = properties.getProperty("colorScheme", "no-preference");
        return switch (scheme.toLowerCase()) {
            case "dark" -> ColorScheme.DARK;
            case "light" -> ColorScheme.LIGHT;
            default -> ColorScheme.NO_PREFERENCE;
        };
    }

    /**
     * Получает опции запуска браузера на основе конфигурации.
     *
     * @return объект LaunchOptions с настройками запуска браузера
     */
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

    /**
     * Загружает конфигурацию из указанного файла.
     *
     * @param filePath путь к файлу конфигурации
     * @return новый объект PlaywrightConfig с загруженными настройками
     * @throws RuntimeException если не удалось загрузить файл
     */
    public static PlaywrightConfig loadFromFile(String filePath) {
        PlaywrightConfig config = new PlaywrightConfig();
        try {
            config.properties.load(new java.io.FileReader(filePath));
        } catch (IOException e) {
            throw new RuntimeException("Не удалось загрузить конфигурацию из файла: " + filePath, e);
        }
        return config;
    }

    /**
     * Устанавливает значение параметра конфигурации.
     * Позволяет программно изменять настройки во время выполнения.
     *
     * @param key   ключ параметра
     * @param value значение параметра
     */
    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }
}