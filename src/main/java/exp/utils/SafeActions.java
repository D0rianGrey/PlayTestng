package exp.utils;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.PlaywrightException;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class SafeActions {

    public static void execute(Runnable action, String errorMessage) {
        try {
            action.run();
        } catch (PlaywrightException e) {
            TestLogger.LOGGER.error("{}: {}", errorMessage, e.getMessage());
            throw new RuntimeException(errorMessage, e);
        }
    }

    public static <T> T execute(Supplier<T> action, String errorMessage) {
        try {
            return action.get();
        } catch (PlaywrightException e) {
            TestLogger.LOGGER.error("{}: {}", errorMessage, e.getMessage());
            throw new RuntimeException(errorMessage, e);
        }
    }

    public static void executeOnPage(Page page, Consumer<Page> action, String errorMessage) {
        try {
            action.accept(page);
        } catch (PlaywrightException e) {
            TestLogger.LOGGER.error("{}: {}", errorMessage, e.getMessage());
            throw new RuntimeException(errorMessage, e);
        }
    }

    public static <T> T executeOnPage(Page page, Function<Page, T> action, String errorMessage) {
        try {
            return action.apply(page);
        } catch (PlaywrightException e) {
            TestLogger.LOGGER.error("{}: {}", errorMessage, e.getMessage());
            throw new RuntimeException(errorMessage, e);
        }
    }
}