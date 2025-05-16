package exp.utils;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;

import java.util.function.Supplier;

public class WaitUtil {

    public static void waitForCondition(Supplier<Boolean> condition, int timeout) {
        long startTime = System.currentTimeMillis();
        long endTime = startTime + timeout;

        while (System.currentTimeMillis() < endTime) {
            if (condition.get()) {
                return;
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Interrupted while waiting for condition", e);
            }
        }

        throw new RuntimeException("Timeout waiting for condition");
    }

    public static void waitForElementVisible(Page page, String selector, int timeout) {
        page.waitForSelector(selector, new Page.WaitForSelectorOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(timeout));
    }

    public static void waitForNavigation(Page page, String url, int timeout) {
        page.waitForURL(url, new Page.WaitForURLOptions().setTimeout(timeout));
    }
}