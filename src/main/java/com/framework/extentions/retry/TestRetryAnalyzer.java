package com.framework.extentions.retry;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * Анализатор повторных запусков тестов при неудаче.
 * <p>
 * Этот класс реализует интерфейс TestNG IRetryAnalyzer, который позволяет
 * автоматически перезапускать тесты, завершившиеся с ошибкой. Это особенно
 * полезно для нестабильных тестов или тестов, которые могут не пройти из-за
 * внешних факторов (медленная сеть, задержки UI и т.д.).
 * <p>
 * Класс отслеживает количество повторных запусков для каждого теста и
 * ограничивает его заданным максимальным значением.
 * <p>
 * Существует два способа использования этого анализатора:
 * <p>
 * 1. Через аннотацию {@link com.annotations.Retry}:
 * ```java
 *
 * @Test
 * @Retry(maxRetries = 3)
 * public void testUnstableFeature() {
 * // Тест будет повторен до 3 раз при неудаче
 * }
 * ```
 * <p>
 * 2. Через прямое указание в аннотации @Test:
 * ```java
 * @Test(retryAnalyzer = TestRetryAnalyzer.class)
 * public void testUnstableFeature() {
 * // Тест будет повторен до MAX_RETRY_COUNT раз при неудаче
 * }
 * ```
 * <p>
 * Примечание: Стоит использовать повторные запуски только для действительно
 * нестабильных тестов, а не для маскировки реальных проблем в коде.
 */
public class TestRetryAnalyzer implements IRetryAnalyzer {
    /**
     * Счетчик текущего количества повторных запусков.
     */
    private int counter = 0;

    /**
     * Максимальное количество повторных запусков при неудаче.
     * По умолчанию равно 2, что означает до 3 запусков в общей сложности
     * (1 основной + 2 повторных).
     */
    private static final int MAX_RETRY_COUNT = 2;

    /**
     * Метод, вызываемый TestNG для определения, нужно ли повторить тест.
     *
     * @param result результат выполнения теста
     * @return true, если тест нужно повторить, false в противном случае
     */
    @Override
    public boolean retry(ITestResult result) {
        if (!result.isSuccess()) {
            if (counter < MAX_RETRY_COUNT) {
                counter++;
                return true;
            }
        }
        return false;
    }
}