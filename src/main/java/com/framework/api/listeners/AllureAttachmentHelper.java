package com.framework.api.listeners;

import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;
import io.qameta.allure.Step;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Вспомогательный класс для прикрепления файлов к отчетам Allure.
 * <p>
 * Предоставляет методы для добавления скриншотов, трассировок и HTML-контента
 * в отчеты Allure. Это позволяет улучшить визуализацию результатов тестов и
 * упростить анализ ошибок.
 * <p>
 * Allure - это популярный фреймворк для генерации наглядных отчетов о тестировании,
 * который поддерживает добавление различных вложений (скриншоты, логи, HTML и т.д.).
 * <p>
 * Пример использования:
 * ```java
 *
 * @AfterMethod public void afterMethod(ITestResult result) {
 * if (result.getStatus() == ITestResult.FAILURE) {
 * // Сделать скриншот и прикрепить его к отчету
 * Path screenshotPath = page.screenshot(new Page.ScreenshotOptions()
 * .setPath(Paths.get("screenshot.png")));
 * AllureAttachmentHelper.attachScreenshot(screenshotPath, "Скриншот при ошибке");
 * <p>
 * // Прикрепить HTML страницы к отчету
 * AllureAttachmentHelper.attachPageSource(page.content());
 * }
 * }
 * ```
 */
public class AllureAttachmentHelper {

    /**
     * Прикрепляет скриншот к отчету Allure.
     *
     * @param path путь к файлу скриншота
     * @param name название вложения в отчете
     */
    @Step("Прикрепление скриншота: {name}")
    public static void attachScreenshot(Path path, String name) {
        try {
            byte[] bytes = Files.readAllBytes(path);
            Allure.addAttachment(name, "image/png", new ByteArrayInputStream(bytes), ".png");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Прикрепляет файл трассировки Playwright к отчету Allure.
     *
     * @param path путь к файлу трассировки
     * @param name название вложения в отчете
     */
    @Step("Прикрепление трассировки: {name}")
    public static void attachTrace(Path path, String name) {
        try {
            byte[] bytes = Files.readAllBytes(path);
            Allure.addAttachment(name, "application/zip", new ByteArrayInputStream(bytes), ".zip");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Прикрепляет HTML-содержимое страницы к отчету Allure.
     * <p>
     * Метод аннотирован {@link Attachment}, что автоматически
     * преобразует возвращаемое значение во вложение Allure.
     *
     * @param html HTML-содержимое страницы
     * @return массив байтов HTML-содержимого
     */
    @Attachment(value = "Page HTML", type = "text/html")
    public static byte[] attachPageSource(String html) {
        return html.getBytes();
    }

    /**
     * Прикрепляет текстовый лог к отчету Allure.
     *
     * @param name название вложения
     * @param text текстовое содержимое
     */
    @Step("Прикрепление текстового лога: {name}")
    public static void attachText(String name, String text) {
        Allure.addAttachment(name, "text/plain", text);
    }

    /**
     * Прикрепляет CSV-файл к отчету Allure.
     *
     * @param path путь к CSV-файлу
     * @param name название вложения
     */
    @Step("Прикрепление CSV: {name}")
    public static void attachCsv(Path path, String name) {
        try {
            byte[] bytes = Files.readAllBytes(path);
            Allure.addAttachment(name, "text/csv", new ByteArrayInputStream(bytes), ".csv");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Прикрепляет JSON-данные к отчету Allure.
     *
     * @param json JSON-строка
     * @param name название вложения
     */
    @Step("Прикрепление JSON: {name}")
    public static void attachJson(String json, String name) {
        Allure.addAttachment(name, "application/json", json);
    }
}