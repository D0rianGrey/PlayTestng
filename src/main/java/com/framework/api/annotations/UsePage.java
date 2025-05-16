package com.framework.api.annotations;

import com.framework.internal.factory.DefaultPageFactory;
import com.framework.api.factory.PageFactory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация, указывающая, что тестовый метод или класс использует Page Objects.
 * <p>
 * Позволяет определить фабрику для создания объектов страниц, которые будут автоматически
 * внедрены в тестовые методы. Фреймворк будет использовать указанную фабрику для создания
 * и настройки объектов страниц перед выполнением теста.
 * <p>
 * Если аннотация применена к классу, то все методы этого класса будут использовать указанную
 * фабрику страниц. Если аннотация применена к методу, то только этот метод будет использовать
 * указанную фабрику.
 * <p>
 * Пример использования:
 * ```
 * // Использование стандартной фабрики для всего класса
 *
 * @UsePage public class MyTest extends PlaywrightBaseTest {
 * @Test public void testHomePage(HomePage homePage) {
 * // homePage автоматически создан и настроен
 * }
 * }
 * <p>
 * // Использование кастомной фабрики для отдельного метода
 * public class AnotherTest extends PlaywrightBaseTest {
 * @Test
 * @UsePage(CustomPageFactory.class) public void testWithCustomFactory(HomePage homePage) {
 * // homePage создан с помощью CustomPageFactory
 * }
 * }
 * ```
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface UsePage {
    /**
     * Класс фабрики для создания объектов страниц.
     * По умолчанию используется DefaultPageFactory.
     *
     * @return класс фабрики страниц
     */
    Class<? extends PageFactory> value() default DefaultPageFactory.class;
}