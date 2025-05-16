package com.framework.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация для параметризации тестовых методов данными.
 * <p>
 * Позволяет указать набор данных для выполнения тестового метода.
 * Метод будет выполнен несколько раз - по одному для каждого элемента данных.
 * <p>
 * Данные могут быть заданы:
 * 1. Непосредственно в аннотации через параметр data()
 * 2. Путем к файлу с данными через параметр dataFile()
 * 3. Именем набора данных в контексте теста через параметр dataSetName()
 * <p>
 * Пример использования:
 * ```
 * // Данные в аннотации
 *
 * @Test
 * @TestData(data = {"user1,pass1", "user2,pass2", "user3,pass3"})
 * public void testMultipleUsers(String userData) {
 * String[] parts = userData.split(",");
 * String username = parts[0];
 * String password = parts[1];
 * // Тест будет выполнен 3 раза с разными наборами данных
 * }
 * <p>
 * // Данные из файла
 * @Test
 * @TestData(dataFile = "src/test/resources/testdata/users.txt")
 * public void testUsersFromFile(String userData) {
 * // Тест будет выполнен для каждой строки из файла
 * }
 * ```
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TestData {
    /**
     * Имя набора данных в контексте тестирования.
     * Используется, если данные уже доступны в контексте теста.
     *
     * @return имя набора данных
     */
    String dataSetName() default "";

    /**
     * Массив строк с данными для параметризации теста.
     * Тест будет выполнен по одному разу для каждого элемента массива.
     *
     * @return массив строк с данными
     */
    String[] data() default {};

    /**
     * Путь к файлу с данными для параметризации теста.
     * Каждая строка файла будет использована как отдельный набор данных.
     *
     * @return путь к файлу с данными
     */
    String dataFile() default "";
}