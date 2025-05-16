package com.framework.api.listeners;

import com.framework.internal.logging.TestLogger;
import com.microsoft.playwright.Page;
import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Слушатель TestNG для интеграции с Allure Report.
 * <p>
 * Отвечает за добавление в отчет Allure информации о:
 * - Выполнении тестов (старт/успех/неудача/пропуск)
 * - Прикрепление скриншотов при неудаче
 * - Прикрепление трассировок и логов
 * - Добавление дополнительных меток и параметров
 * <p>
 * Пример регистрации в testng.xml:
 * ```xml
 * <listeners>
 * <listener class-name="com.framework.api.listeners.AllureTestListener"/>
 * </listeners>
 * ```
 */
public class AllureTestListener implements ITestListener {

    /**
     * Вызывается перед выполнением всех тестов.
     *
     * @param context контекст выполнения тестов
     */
    @Override
    public void onStart(ITestContext context) {
        TestLogger.LOGGER.info("Запуск тестового набора: {}", context.getName());
    }

    /**
     * Вызывается после завершения всех тестов.
     *
     * @param context контекст выполнения тестов
     */
    @Override
    public void onFinish(ITestContext context) {
        TestLogger.LOGGER.info("Завершение тестового набора: {}", context.getName());
    }

    /**
     * Вызывается перед выполнением каждого теста.
     *
     * @param result результат выполнения теста
     */
    @Override
    public void onTestStart(ITestResult result) {
        TestLogger.LOGGER.info("Начало теста: {}", result.getName());

        // Добавляем параметры теста в отчет
        Object[] parameters = result.getParameters();
        if (parameters != null && parameters.length > 0) {
            for (int i = 0; i < parameters.length; i++) {
                if (parameters[i] != null && !(parameters[i] instanceof Page)) {
                    Allure.parameter("Параметр " + (i + 1), parameters[i].toString());
                }
            }
        }
    }

    /**
     * Вызывается при успешном выполнении теста.
     *
     * @param result результат выполнения теста
     */
    @Override
    public void onTestSuccess(ITestResult result) {
        TestLogger.LOGGER.info("Тест успешно выполнен: {}", result.getName());
    }

    /**
     * Вызывается при неудачном выполнении теста.
     * Прикрепляет скриншот и трассировку к отчету.
     *
     * @param result результат выполнения теста
     */
    @Override
    public void onTestFailure(ITestResult result) {
        TestLogger.LOGGER.info("Тест не пройден: {}", result.getName());
        TestLogger.LOGGER.info("Причина: {}", result.getThrowable().getMessage());

        // Прикрепляем скриншот, если он был сделан
        attachScreenshotFromResult(result);

        // Прикрепляем трассировку, если она была создана
        attachTraceFromResult(result);

        // Прикрепляем стек-трейс ошибки
        Allure.addAttachment("stack-trace.txt", "text/plain", result.getThrowable().toString());
    }

    /**
     * Вызывается при пропуске теста.
     *
     * @param result результат выполнения теста
     */
    @Override
    public void onTestSkipped(ITestResult result) {
        TestLogger.LOGGER.info("Тест пропущен: {}", result.getName());

        // Если есть причина пропуска, добавляем её в отчет
        if (result.getThrowable() != null) {
            Allure.addAttachment("skip-reason.txt", "text/plain", result.getThrowable().getMessage());
        }
    }

    /**
     * Прикрепляет скриншот к отчету Allure, если он был сделан.
     *
     * @param result результат выполнения теста
     */
    private void attachScreenshotFromResult(ITestResult result) {
        // Проверяем, был ли сделан скриншот
        String screenshotAttribute = (String) result.getAttribute("screenshot");
        if (screenshotAttribute != null) {
            try {
                Path screenshotPath = Paths.get(screenshotAttribute);
                if (Files.exists(screenshotPath)) {
                    byte[] screenshotBytes = Files.readAllBytes(screenshotPath);
                    Allure.addAttachment("screenshot.png", "image/png",
                            new ByteArrayInputStream(screenshotBytes), "png");
                    TestLogger.LOGGER.info("Скриншот прикреплен к отчету Allure: {}", screenshotPath);
                }
            } catch (Exception e) {
                TestLogger.LOGGER.error("Не удалось прикрепить скриншот к отчету: {}", e.getMessage());
            }
        }
    }

    /**
     * Прикрепляет трассировку Playwright к отчету Allure, если она была создана.
     *
     * @param result результат выполнения теста
     */
    private void attachTraceFromResult(ITestResult result) {
        // Проверяем, была ли сделана трассировка
        String traceAttribute = (String) result.getAttribute("trace");
        if (traceAttribute != null) {
            try {
                Path tracePath = Paths.get(traceAttribute);
                if (Files.exists(tracePath)) {
                    byte[] traceBytes = Files.readAllBytes(tracePath);
                    Allure.addAttachment("trace.zip", "application/zip",
                            new ByteArrayInputStream(traceBytes), "zip");
                    TestLogger.LOGGER.info("Трассировка прикреплена к отчету Allure: {}", tracePath);
                }
            } catch (Exception e) {
                TestLogger.LOGGER.error("Не удалось прикрепить трассировку к отчету: {}", e.getMessage());
            }
        }
    }

    /**
     * Создает скриншот и возвращает его в виде массива байтов.
     *
     * @param screenshot массив байтов со скриншотом
     * @return массив байтов для прикрепления к отчету
     */
    @Attachment(value = "Page Screenshot", type = "image/png")
    public static byte[] attachScreenshot(byte[] screenshot) {
        return screenshot;
    }
}