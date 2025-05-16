package exp.utils;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class ReportGenerator {
    private static final VelocityEngine velocityEngine;

    static {
        velocityEngine = new VelocityEngine();
        velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        velocityEngine.init();
    }

    public static String generateReportFromTemplate(String templatePath, Map<String, Object> data) {
        VelocityContext context = new VelocityContext();

        // Добавляем все данные в контекст
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            context.put(entry.getKey(), entry.getValue());
        }

        Template template = velocityEngine.getTemplate(templatePath);
        StringWriter writer = new StringWriter();
        template.merge(context, writer);

        return writer.toString();
    }

    public static void saveReportToFile(String reportContent, String outputPath) {
        try {
            Path path = Paths.get(outputPath);

            // Создаем директорию, если она не существует
            Files.createDirectories(path.getParent());

            // Записываем содержимое отчета в файл
            Files.writeString(path, reportContent);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save report to file: " + outputPath, e);
        }
    }

    public static void generateAndSaveReport(String templatePath, Map<String, Object> data, String outputPath) {
        String reportContent = generateReportFromTemplate(templatePath, data);
        saveReportToFile(reportContent, outputPath);
    }
}