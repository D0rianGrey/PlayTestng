package com.framework.api.listeners;

import com.framework.api.annotations.UsePage;
import org.testng.annotations.ITestAnnotation;
import org.testng.internal.annotations.IAnnotationTransformer;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Преобразователь аннотаций TestNG для автоматической настройки тестов.
 * <p>
 * Этот класс реализует интерфейс IAnnotationTransformer, который позволяет
 * программно модифицировать аннотации тестов перед их выполнением. В частности,
 * он устанавливает dataProvider для методов с аннотацией {@link UsePage},
 * если dataProvider не указан.
 * <p>
 * Основная задача этого класса - обеспечить автоматическую конфигурацию тестов,
 * использующих Page Objects, без необходимости вручную указывать dataProvider
 * в каждой аннотации @Test.
 * <p>
 * Преимущества использования этого преобразователя:
 * - Уменьшение шаблонного кода в тестах
 * - Автоматическая настройка тестов с Page Objects
 * - Унификация подхода к внедрению зависимостей
 * <p>
 * Пример регистрации слушателя в testng.xml:
 * ```xml
 * <listeners>
 * <listener class-name="exp.listeners.PlaywrightTestFactory"/>
 * </listeners>
 * ```
 * <p>
 * После регистрации этого слушателя, можно использовать только аннотацию @UsePage
 * без необходимости указывать dataProvider в @Test:
 * ```java
 *
 * @UsePage public class LoginTest extends PlaywrightBaseTest {
 * @Test // dataProvider будет установлен автоматически
 * public void testLogin(LoginPage loginPage) {
 * // Тест с внедренным объектом LoginPage
 * }
 * }
 * ```
 */
public class PlaywrightTestFactory implements IAnnotationTransformer {

    /**
     * Метод, вызываемый TestNG для трансформации аннотаций.
     * <p>
     * Если метод или его класс имеет аннотацию {@link UsePage}, но не указан dataProvider,
     * устанавливает dataProvider равным "pageObjects", который должен быть определен
     * в базовом классе тестов.
     *
     * @param annotation      аннотация @Test для трансформации
     * @param testClass       класс, содержащий тест (может быть null)
     * @param testConstructor конструктор теста (может быть null)
     * @param testMethod      метод теста (может быть null)
     */
    @Override
    public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
        // Если класс или метод имеет аннотацию UsePage, но не указан dataProvider
        if (testMethod != null &&
                (testMethod.isAnnotationPresent(UsePage.class) || testMethod.getDeclaringClass().isAnnotationPresent(UsePage.class)) &&
                annotation.getDataProvider().isEmpty()) {

            // Установка dataProvider программно
            annotation.setDataProvider("pageObjects");
        }
    }
}