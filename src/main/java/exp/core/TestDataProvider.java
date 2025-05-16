package exp.core;

import exp.annotations.TestData;
import org.testng.ITestContext;
import org.testng.annotations.DataProvider;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class TestDataProvider {

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

    private static Object[][] readDataFromFile(String fileName) {
        List<String> lines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line.trim());
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading test data file: " + fileName, e);
        }

        Object[][] result = new Object[lines.size()][1];
        for (int i = 0; i < lines.size(); i++) {
            result[i][0] = lines.get(i);
        }

        return result;
    }

    private static Object[][] getDataSetFromContext(String dataSetName, ITestContext context) {
        Object dataSet = context.getAttribute(dataSetName);
        if (dataSet instanceof Object[][]) {
            return (Object[][]) dataSet;
        }
        throw new RuntimeException("Data set not found in test context: " + dataSetName);
    }
}