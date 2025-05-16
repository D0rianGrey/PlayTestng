package com.framework.extentions.data;

import com.framework.api.annotations.TestData;
import org.testng.ITestContext;
import org.testng.annotations.DataProvider;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Провайдер данных для параметризованных тестов.
 * <p>
 * Этот класс реализует функционал поставщика данных TestNG,
 * который извлекает тестовые данные из различных источников и
 * предоставляет их тестовым методам. Поддерживает извлечение данных:
 * - Непосредственно из аннотации {@link TestData}
 * - Из текстового файла
 * - Из контекста теста по имени набора данных
 * <p>
 * Использование этого класса позволяет:
 * - Запускать тесты с разными наборами входных данных
 * - Отделять тестовые данные от кода тестов
 * - Хранить тестовые данные в отдельных файлах
 * - Генерировать тестовые данные программно
 * <p>
 * Пример использования:
 * ```java
 * // Параметры указаны прямо в аннотации
 *
 * @Test(dataProvider = "testData", dataProviderClass = TestDataProvider.class)
 * @TestData(data = {"user1,pass1", "user2,pass2", "user3,pass3"})
 * public void testWithMultipleData(String credentials) {
 * String[] parts = credentials.split(",");
 * String username = parts[0];
 * String password = parts[1];
 * <p>
 * // Тест будет запущен три раза с разными данными
 * }
 * <p>
 * // Данные загружаются из файла
 * @Test(dataProvider = "testData", dataProviderClass = TestDataProvider.class)
 * @TestData(dataFile = "src/test/resources/testdata/users.txt")
 * public void testWithDataFromFile(String userData) {
 * // Каждая строка файла будет использована как отдельный набор данных
 * }
 * ```
 */
public class TestDataProvider {

    /**
     * Поставщик данных для тестовых методов.
     * <p>
     * Анализирует аннотацию {@link TestData} у метода и извлекает данные
     * из указанного источника. Возвращает двумерный массив объектов,
     * где каждая строка представляет один набор тестовых данных.
     *
     * @param method  метод теста, для которого предоставляются данные
     * @param context контекст теста TestNG
     * @return массив объектов с тестовыми данными или пустой массив, если данные не найдены
     */
    @DataProvider(name = "testData")
    public static Object[][] provideTestData(Method method, ITestContext context) {
        if (method.isAnnotationPresent(TestData.class)) {
            TestData testData = method.getAnnotation(TestData.class);

            // Если указаны прямые данные в аннотации
            if (testData.data().length > 0) {
                Object[][] result = new Object[testData.data().length][1];
                for (int i = 0; i < testData.data().length; i++) {
                    result[i][0] = testData.data()[i];
                }
                return result;
            }

            // Если указан файл с данными
            if (!testData.dataFile().isEmpty()) {
                return readDataFromFile(testData.dataFile());
            }

            // Если указано имя набора данных
            if (!testData.dataSetName().isEmpty()) {
                return getDataSetFromContext(testData.dataSetName(), context);
            }
        }

        // По умолчанию возвращаем пустой массив
        return new Object[0][0];
    }

    /**
     * Читает данные из текстового файла.
     * <p>
     * Каждая строка файла становится отдельным набором тестовых данных.
     * Пустые строки и пробелы в начале и конце строк удаляются.
     *
     * @param fileName путь к файлу с данными
     * @return массив объектов с данными из файла
     * @throws RuntimeException если произошла ошибка при чтении файла
     */
    private static Object[][] readDataFromFile(String fileName) {
        List<String> lines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line.trim());
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка чтения файла с тестовыми данными: " + fileName, e);
        }

        Object[][] result = new Object[lines.size()][1];
        for (int i = 0; i < lines.size(); i++) {
            result[i][0] = lines.get(i);
        }

        return result;
    }

    /**
     * Извлекает набор данных из контекста теста по имени.
     *
     * @param dataSetName имя набора данных в контексте
     * @param context     контекст теста TestNG
     * @return массив объектов с данными из контекста
     * @throws RuntimeException если набор данных не найден в контексте
     */
    private static Object[][] getDataSetFromContext(String dataSetName, ITestContext context) {
        Object dataSet = context.getAttribute(dataSetName);
        if (dataSet instanceof Object[][]) {
            return (Object[][]) dataSet;
        }
        throw new RuntimeException("Набор данных не найден в контексте теста: " + dataSetName);
    }
}