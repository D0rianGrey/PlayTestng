package com.framework.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация для настройки повторного запуска тестов при неудаче.
 * <p>
 * Позволяет указать максимальное количество повторных запусков теста,
 * если он завершился с ошибкой. Это полезно для нестабильных тестов или
 * тестов, зависящих от внешних факторов (сеть, анимации и т.д.).
 * <p>
 * Может быть применена как к отдельному методу, так и к целому классу.
 * Если аннотация применена к классу, то повторный запуск будет применяться
 * ко всем тестовым методам этого класса.
 * <p>
 * Пример использования:
 * ```
 *
 * @Test
 * @Retry(maxRetries = 3)
 * public void testUnstableFeature() {
 * // Тест будет повторен до 3 раз в случае неудачи
 * }
 * @Retry(maxRetries = 5)
 * public class UnstableTests {
 * // Все тесты в этом классе будут повторяться до 5 раз при неудаче
 * }
 * ```
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Retry {
    /**
     * Максимальное количество повторных запусков теста при неудаче.
     *
     * @return количество повторных запусков, по умолчанию 2
     */
    int maxRetries() default 2;
}