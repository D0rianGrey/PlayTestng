package exp.utils;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;

import java.util.function.Supplier;

/**
 * Утилитный класс для ожидания различных условий в тестах.
 * <p>
 * Предоставляет удобные методы для синхронизации выполнения тестов с состоянием приложения:
 * - Ожидание появления элементов на странице
 * - Ожидание завершения навигации
 * - Ожидание произвольных условий
 * <p>
 * Пример использования:
 * ```
 * // Ожидание появления элемента
 * WaitUtil.waitForElementVisible(page, ".dashboard-widget", 5000);
 * <p>
 * // Ожидание перехода на определенный URL
 * WaitUtil.waitForNavigation(page, "https://example.com/dashboard", 10000);
 * <p>
 * // Ожидание пользовательского условия
 * WaitUtil.waitForCondition(() -> page.locator(".balance").textContent().contains("$"), 5000);
 * ```
 */
public class WaitUtil {

    /**
     * Ожидает выполнения произвольного условия в течение указанного времени.
     *
     * @param condition функция, возвращающая true, когда условие выполнено
     * @param timeout   максимальное время ожидания в миллисекундах
     * @throws RuntimeException если условие не выполнено в течение указанного времени
     */
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
                throw new RuntimeException("Прервано во время ожидания выполнения условия", e);
            }
        }

        throw new RuntimeException("Таймаут при ожидании выполнения условия");
    }

    /**
     * Ожидает появления элемента на странице.
     *
     * @param page     объект страницы
     * @param selector CSS-селектор элемента
     * @param timeout  максимальное время ожидания в миллисекундах
     */
    public static void waitForElementVisible(Page page, String selector, int timeout) {
        page.waitForSelector(selector, new Page.WaitForSelectorOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(timeout));
    }

    /**
     * Ожидает перехода на указанный URL.
     *
     * @param page    объект страницы
     * @param url     ожидаемый URL
     * @param timeout максимальное время ожидания в миллисекундах
     */
    public static void waitForNavigation(Page page, String url, int timeout) {
        page.waitForURL(url, new Page.WaitForURLOptions().setTimeout(timeout));
    }
}