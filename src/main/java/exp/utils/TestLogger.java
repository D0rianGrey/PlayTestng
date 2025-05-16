package exp.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Централизованный класс для ведения логов в фреймворке.
 * <p>
 * Предоставляет единую точку доступа к логгеру для всех компонентов фреймворка,
 * что обеспечивает согласованность в форматировании и уровнях логирования.
 * <p>
 * Пример использования:
 * ```
 * public void someMethod() {
 * TestLogger.LOGGER.info("Начало выполнения важного действия");
 * <p>
 * try {
 * // Выполнение действия
 * TestLogger.LOGGER.debug("Промежуточное состояние: {}", someValue);
 * } catch (Exception e) {
 * TestLogger.LOGGER.error("Ошибка при выполнении действия: {}", e.getMessage());
 * }
 * <p>
 * TestLogger.LOGGER.info("Завершение действия");
 * }
 * ```
 */
public class TestLogger {
    /**
     * Единый экземпляр логгера для всего фреймворка.
     * Используется всеми компонентами для логирования информации.
     */
    public static final Logger LOGGER = LoggerFactory.getLogger("PlaywrightTests");
}