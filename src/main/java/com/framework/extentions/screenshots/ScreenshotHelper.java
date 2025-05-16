package com.framework.extentions.screenshots;

import com.framework.internal.logging.TestLogger;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Tracing;
import org.testng.ITestResult;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Утилитный класс для создания скриншотов и трассировок при ошибках в тестах.
 * <p>
 * Предоставляет методы для автоматического создания скриншотов и трассировок
 * при неудачном выполнении тестов, что помогает в отладке и анализе проблем.
 * <p>
 * Пример использования:
 * ```
 *
 * @AfterMethod public void afterMethod(ITestResult result) {
 * if (result.getStatus() == ITestResult.FAILURE) {
 * ScreenshotHelper.captureScreenshotOnFailure(result, page);
 * ScreenshotHelper.captureTraceOnFailure(result, browserContext);
 * }
 * }
 * ```
 */
public class ScreenshotHelper {

    /**
     * Создает скриншот страницы при неудачном выполнении теста.
     * Скриншот сохраняется в директории "screenshots" с именем, содержащим
     * название теста и временную метку.
     *
     * @param result результат выполнения теста
     * @param page   объект страницы
     */
    public static void captureScreenshotOnFailure(ITestResult result, Page page) {
        if (page != null && result.getStatus() == ITestResult.FAILURE) {
            try {
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
                String testName = result.getMethod().getMethodName();
                String fileName = String.format("failure_%s_%s.png", testName, timestamp);

                Path screenshotPath = Paths.get("screenshots", fileName);
                page.screenshot(new Page.ScreenshotOptions().setPath(screenshotPath));

                TestLogger.LOGGER.info("Сделан скриншот при ошибке: {}", screenshotPath);
            } catch (Exception e) {
                TestLogger.LOGGER.error("Не удалось сделать скриншот: {}", e.getMessage());
            }
        }
    }

    /**
     * Сохраняет трассировку при неудачном выполнении теста.
     * Трассировка сохраняется в директории "traces" с именем, содержащим
     * название теста и временную метку.
     * <p>
     * Трассировка содержит детальную информацию о выполненных действиях,
     * включая скриншоты, сетевые запросы и время выполнения.
     *
     * @param result  результат выполнения теста
     * @param context контекст браузера
     */
    public static void captureTraceOnFailure(ITestResult result, BrowserContext context) {
        if (context != null && result.getStatus() == ITestResult.FAILURE) {
            try {
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
                String testName = result.getMethod().getMethodName();
                String fileName = String.format("trace_%s_%s.zip", testName, timestamp);

                Path tracePath = Paths.get("traces", fileName);
                context.tracing().stop(new Tracing.StopOptions().setPath(tracePath));

                TestLogger.LOGGER.info("Сохранена трассировка при ошибке: {}", tracePath);
            } catch (Exception e) {
                TestLogger.LOGGER.error("Не удалось сохранить трассировку: {}", e.getMessage());
            }
        }
    }
}