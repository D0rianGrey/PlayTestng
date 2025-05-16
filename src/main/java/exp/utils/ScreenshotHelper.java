package exp;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Tracing;
import exp.utils.TestLogger;
import org.testng.ITestResult;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ScreenshotHelper {

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