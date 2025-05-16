
package exp.utils;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.PlaywrightException;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Утилитный класс для безопасного выполнения действий в Playwright.
 * <p>
 * Предоставляет методы для выполнения действий с обработкой исключений,
 * логированием ошибок и возможностью предоставить информативное сообщение
 * при неудаче.
 * <p>
 * Основные категории методов:
 * - Методы для выполнения действий без возвращаемого значения
 * - Методы для выполнения действий с возвращаемым значением
 * - Методы для выполнения действий с объектом Page
 * <p>
 * Пример использования:
 * ```
 * // Выполнение действия без возвращаемого значения
 * SafeActions.execute(() -> page.click(".submit-button"), "Не удалось нажать на кнопку отправки");
 * <p>
 * // Выполнение действия с возвращаемым значением
 * String text = SafeActions.execute(() -> page.locator(".welcome-message").textContent(),
 * "Не удалось получить текст приветствия");
 * <p>
 * // Выполнение действия с объектом Page
 * SafeActions.executeOnPage(page, p -> p.fill("#username", "admin"),
 * "Не удалось заполнить поле имени пользователя");
 * ```
 */
public class SafeActions {

    /**
     * Выполняет действие без возвращаемого значения с обработкой исключений.
     *
     * @param action       действие для выполнения
     * @param errorMessage сообщение об ошибке в случае исключения
     * @throws RuntimeException если действие вызвало исключение PlaywrightException
     */
    public static void execute(Runnable action, String errorMessage) {
        try {
            action.run();
        } catch (PlaywrightException e) {
            TestLogger.LOGGER.error("{}: {}", errorMessage, e.getMessage());
            throw new RuntimeException(errorMessage, e);
        }
    }

    /**
     * Выполняет действие с возвращаемым значением с обработкой исключений.
     *
     * @param <T>          тип возвращаемого значения
     * @param action       действие для выполнения
     * @param errorMessage сообщение об ошибке в случае исключения
     * @return результат выполнения действия
     * @throws RuntimeException если действие вызвало исключение PlaywrightException
     */
    public static <T> T execute(Supplier<T> action, String errorMessage) {
        try {
            return action.get();
        } catch (PlaywrightException e) {
            TestLogger.LOGGER.error("{}: {}", errorMessage, e.getMessage());
            throw new RuntimeException(errorMessage, e);
        }
    }

    /**
     * Выполняет действие с объектом Page без возвращаемого значения.
     *
     * @param page         объект страницы
     * @param action       действие для выполнения
     * @param errorMessage сообщение об ошибке в случае исключения
     * @throws RuntimeException если действие вызвало исключение PlaywrightException
     */
    public static void executeOnPage(Page page, Consumer<Page> action, String errorMessage) {
        try {
            action.accept(page);
        } catch (PlaywrightException e) {
            TestLogger.LOGGER.error("{}: {}", errorMessage, e.getMessage());
            throw new RuntimeException(errorMessage, e);
        }
    }

    /**
     * Выполняет действие с объектом Page с возвращаемым значением.
     *
     * @param <T>          тип возвращаемого значения
     * @param page         объект страницы
     * @param action       действие для выполнения
     * @param errorMessage сообщение об ошибке в случае исключения
     * @return результат выполнения действия
     * @throws RuntimeException если действие вызвало исключение PlaywrightException
     */
    public static <T> T executeOnPage(Page page, Function<Page, T> action, String errorMessage) {
        try {
            return action.apply(page);
        } catch (PlaywrightException e) {
            TestLogger.LOGGER.error("{}: {}", errorMessage, e.getMessage());
            throw new RuntimeException(errorMessage, e);
        }
    }
}