package com.framework.internal.browser;

import com.framework.api.config.PlaywrightConfig;
import com.framework.internal.logging.TestLogger;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Playwright;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Менеджер для создания и управления экземплярами браузеров.
 * Поддерживает все браузеры, доступные в Playwright: Chromium, Firefox и WebKit.
 * <p>
 * Ключевые возможности:
 * - Создание различных типов браузеров (Chromium, Firefox, WebKit)
 * - Настройка параметров запуска браузера
 * - Создание контекста браузера с различными настройками
 * - Поддержка эмуляции устройств
 * <p>
 * Пример использования:
 * ```
 * Browser browser = BrowserManager.createBrowser(playwright);
 * BrowserContext context = browser.newContext(BrowserManager.createContextOptions());
 * ```
 */
public class BrowserManager {

    /**
     * Создает экземпляр браузера на основе конфигурации.
     * Тип браузера определяется из настроек в PlaywrightConfig.
     *
     * @param playwright экземпляр Playwright
     * @return экземпляр браузера
     */
    public static Browser createBrowser(Playwright playwright) {
        String browserName = PlaywrightConfig.getInstance().getBrowser();
        BrowserType.LaunchOptions options = PlaywrightConfig.getInstance().getLaunchOptions();

        TestLogger.LOGGER.info("Создание браузера: {}, headless: {}",
                browserName, options.headless);

        return switch (browserName.toLowerCase()) {
            case "firefox" -> createFirefoxBrowser(playwright, options);
            case "webkit" -> createWebKitBrowser(playwright, options);
            default -> createChromiumBrowser(playwright, options);
        };
    }

    /**
     * Создает экземпляр Chromium браузера.
     *
     * @param playwright экземпляр Playwright
     * @param options    опции запуска браузера
     * @return экземпляр Chromium браузера
     */
    public static Browser createChromiumBrowser(Playwright playwright, BrowserType.LaunchOptions options) {
        TestLogger.LOGGER.debug("Создание браузера Chromium");
        return playwright.chromium().launch(options);
    }

    /**
     * Создает экземпляр Firefox браузера.
     *
     * @param playwright экземпляр Playwright
     * @param options    опции запуска браузера
     * @return экземпляр Firefox браузера
     */
    public static Browser createFirefoxBrowser(Playwright playwright, BrowserType.LaunchOptions options) {
        TestLogger.LOGGER.debug("Создание браузера Firefox");
        return playwright.firefox().launch(options);
    }

    /**
     * Создает экземпляр WebKit браузера.
     *
     * @param playwright экземпляр Playwright
     * @param options    опции запуска браузера
     * @return экземпляр WebKit браузера
     */
    public static Browser createWebKitBrowser(Playwright playwright, BrowserType.LaunchOptions options) {
        TestLogger.LOGGER.debug("Создание браузера WebKit");
        return playwright.webkit().launch(options);
    }

    /**
     * Создает контекст браузера с настройками из конфигурации.
     * Настройки включают:
     * - Размер вьюпорта
     * - User-agent
     * - Локаль и часовой пояс
     * - Запись видео
     * - Геолокацию
     * - Разрешения
     * - Эмуляцию устройств
     * - Настройки HTTPS
     * - Состояние хранилища
     * - Цветовую схему
     *
     * @return опции для создания контекста браузера
     */
    public static Browser.NewContextOptions createContextOptions() {
        Browser.NewContextOptions options = new Browser.NewContextOptions();

        // Применение настроек из конфигурации
        if (PlaywrightConfig.getInstance().hasViewport()) {
            options.setViewportSize(
                    PlaywrightConfig.getInstance().getViewportWidth(),
                    PlaywrightConfig.getInstance().getViewportHeight()
            );
        }

        if (PlaywrightConfig.getInstance().hasUserAgent()) {
            options.setUserAgent(PlaywrightConfig.getInstance().getUserAgent());
        }

        if (PlaywrightConfig.getInstance().hasLocale()) {
            options.setLocale(PlaywrightConfig.getInstance().getLocale());
        }

        // Для timestamp используем setExtraHTTPHeaders
        if (PlaywrightConfig.getInstance().hasTimezone()) {
            Map<String, String> headers = new HashMap<>();
            headers.put("Accept-Language", PlaywrightConfig.getInstance().getLocale());
            options.setExtraHTTPHeaders(headers);
        }

        if (PlaywrightConfig.getInstance().isRecordVideo()) {
            options.setRecordVideoDir(PlaywrightConfig.getInstance().getVideoDir());
            options.setRecordVideoSize(
                    PlaywrightConfig.getInstance().getVideoWidth(),
                    PlaywrightConfig.getInstance().getVideoHeight()
            );
        }

        if (PlaywrightConfig.getInstance().hasGeolocation()) {
            options.setGeolocation(
                    PlaywrightConfig.getInstance().getLatitude(),
                    PlaywrightConfig.getInstance().getLongitude()
            );
        }

        if (PlaywrightConfig.getInstance().hasPermissions()) {
            options.setPermissions(PlaywrightConfig.getInstance().getPermissions());
        }

        // Добавим настройки для устройства
        if (PlaywrightConfig.getInstance().hasDeviceName()) {
            String deviceName = PlaywrightConfig.getInstance().getDeviceName();
            switch (deviceName.toLowerCase()) {
                case "iphone11":
                    options.setViewportSize(390, 844)
                            .setDeviceScaleFactor(3)
                            .setIsMobile(true)
                            .setUserAgent("Mozilla/5.0 (iPhone; CPU iPhone OS 14_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.0 Mobile/15E148 Safari/604.1");
                    break;
                case "iphone12":
                    options.setViewportSize(390, 844)
                            .setDeviceScaleFactor(3)
                            .setIsMobile(true)
                            .setUserAgent("Mozilla/5.0 (iPhone; CPU iPhone OS 14_4 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.0.3 Mobile/15E148 Safari/604.1");
                    break;
                case "iphone13":
                    options.setViewportSize(390, 844)
                            .setDeviceScaleFactor(3)
                            .setIsMobile(true)
                            .setUserAgent("Mozilla/5.0 (iPhone; CPU iPhone OS 15_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.0 Mobile/15E148 Safari/604.1");
                    break;
                case "ipad":
                    options.setViewportSize(810, 1080)
                            .setDeviceScaleFactor(2)
                            .setIsMobile(true)
                            .setUserAgent("Mozilla/5.0 (iPad; CPU OS 14_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.0 Mobile/15E148 Safari/604.1");
                    break;
                case "pixel5":
                    options.setViewportSize(393, 851)
                            .setDeviceScaleFactor(2.75)
                            .setIsMobile(true)
                            .setUserAgent("Mozilla/5.0 (Linux; Android 11; Pixel 5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.91 Mobile Safari/537.36");
                    break;
                case "samsungs21":
                    options.setViewportSize(360, 800)
                            .setDeviceScaleFactor(3)
                            .setIsMobile(true)
                            .setUserAgent("Mozilla/5.0 (Linux; Android 11; SM-G991U) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.91 Mobile Safari/537.36");
                    break;
                // Можно добавить другие устройства при необходимости
            }
        }

        // Дополнительные настройки контекста
        if (PlaywrightConfig.getInstance().isIgnoreHTTPSErrors()) {
            options.setIgnoreHTTPSErrors(true);
        }

        if (PlaywrightConfig.getInstance().hasStorageState()) {
            options.setStorageStatePath(Paths.get(PlaywrightConfig.getInstance().getStorageStatePath()));
        }

        if (PlaywrightConfig.getInstance().hasColorScheme()) {
            options.setColorScheme(PlaywrightConfig.getInstance().getColorScheme());
        }

        // Возвращаем настроенные опции
        return options;
    }

    /**
     * Создает опции браузера на основе аргументов командной строки.
     * Поддерживаемые аргументы:
     * - --headless - запуск браузера в безголовом режиме
     * - --slow-mo=значение - замедление действий браузера
     * - --timeout=значение - таймаут операций
     *
     * @param args аргументы командной строки
     * @return опции запуска браузера
     */
    public static BrowserType.LaunchOptions createLaunchOptionsFromArgs(String[] args) {
        BrowserType.LaunchOptions options = new BrowserType.LaunchOptions();

        for (String arg : args) {
            if (arg.equals("--headless")) {
                options.setHeadless(true);
            } else if (arg.startsWith("--slow-mo=")) {
                String value = arg.substring("--slow-mo=".length());
                options.setSlowMo(Double.parseDouble(value));
            } else if (arg.startsWith("--timeout=")) {
                String value = arg.substring("--timeout=".length());
                options.setTimeout(Double.parseDouble(value));
            }
            // Можно добавить другие параметры командной строки
        }

        return options;
    }
}