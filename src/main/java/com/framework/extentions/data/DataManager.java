package com.framework.extentions.data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Менеджер для работы с тестовыми данными.
 * <p>
 * Предоставляет методы для загрузки данных из различных источников:
 * - JSON файлы (отдельные объекты или массивы)
 * - Текстовые файлы
 * <p>
 * Реализует кэширование данных для повышения производительности
 * при повторных обращениях к одним и тем же файлам.
 * <p>
 * Пример использования:
 * ```
 * // Загрузка объекта из JSON
 * Map<String, Object> userData = DataManager.loadJsonData("src/test/resources/data/user.json");
 * String username = (String) userData.get("username");
 * <p>
 * // Загрузка массива из JSON
 * List<Map<String, Object>> usersList = DataManager.loadJsonArray("src/test/resources/data/users.json");
 * for (Map<String, Object> user : usersList) {
 * String email = (String) user.get("email");
 * // Использование данных
 * }
 * <p>
 * // Загрузка текстового файла
 * String template = DataManager.loadTextData("src/test/resources/templates/email.txt");
 * ```
 */
public class DataManager {
    /**
     * Кэш для хранения загруженных данных.
     * Ключ - путь к файлу, значение - загруженные данные.
     */
    private static final Map<String, Object> dataCache = new HashMap<>();

    /**
     * Экземпляр Gson для преобразования JSON.
     */
    private static final Gson gson = new Gson();

    /**
     * Загружает объект из JSON файла.
     *
     * @param filePath путь к JSON файлу
     * @return карта с данными из файла
     * @throws RuntimeException если файл не найден или произошла ошибка при чтении
     */
    public static Map<String, Object> loadJsonData(String filePath) {
        if (dataCache.containsKey(filePath)) {
            return (Map<String, Object>) dataCache.get(filePath);
        }

        try (FileReader reader = new FileReader(filePath)) {
            Type type = new TypeToken<Map<String, Object>>() {
            }.getType();
            Map<String, Object> data = gson.fromJson(reader, type);
            dataCache.put(filePath, data);
            return data;
        } catch (IOException e) {
            throw new RuntimeException("Не удалось загрузить JSON данные из " + filePath, e);
        }
    }

    /**
     * Загружает массив объектов из JSON файла.
     *
     * @param filePath путь к JSON файлу
     * @return список карт с данными из файла
     * @throws RuntimeException если файл не найден или произошла ошибка при чтении
     */
    public static List<Map<String, Object>> loadJsonArray(String filePath) {
        if (dataCache.containsKey(filePath)) {
            return (List<Map<String, Object>>) dataCache.get(filePath);
        }

        try (FileReader reader = new FileReader(filePath)) {
            Type type = new TypeToken<List<Map<String, Object>>>() {
            }.getType();
            List<Map<String, Object>> data = gson.fromJson(reader, type);
            dataCache.put(filePath, data);
            return data;
        } catch (IOException e) {
            throw new RuntimeException("Не удалось загрузить JSON массив из " + filePath, e);
        }
    }

    /**
     * Загружает текстовый файл.
     *
     * @param filePath путь к текстовому файлу
     * @return содержимое файла в виде строки
     * @throws RuntimeException если файл не найден или произошла ошибка при чтении
     */
    public static String loadTextData(String filePath) {
        if (dataCache.containsKey(filePath)) {
            return (String) dataCache.get(filePath);
        }

        try {
            String content = Files.readString(Path.of(filePath));
            dataCache.put(filePath, content);
            return content;
        } catch (IOException e) {
            throw new RuntimeException("Не удалось загрузить текстовые данные из " + filePath, e);
        }
    }
}