package exp.utils;

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

public class DataManager {
    private static final Map<String, Object> dataCache = new HashMap<>();
    private static final Gson gson = new Gson();

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
            throw new RuntimeException("Failed to load JSON data from " + filePath, e);
        }
    }

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
            throw new RuntimeException("Failed to load JSON array from " + filePath, e);
        }
    }

    public static String loadTextData(String filePath) {
        if (dataCache.containsKey(filePath)) {
            return (String) dataCache.get(filePath);
        }

        try {
            String content = Files.readString(Path.of(filePath));
            dataCache.put(filePath, content);
            return content;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load text data from " + filePath, e);
        }
    }
}