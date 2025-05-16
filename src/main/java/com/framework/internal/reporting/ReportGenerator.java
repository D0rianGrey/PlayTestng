package com.framework.internal.reporting;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/**
 * Генератор отчетов на основе шаблонов Velocity.
 * <p>
 * Данный класс предоставляет функционал для генерации отчетов и других
 * текстовых файлов на основе шаблонов Apache Velocity. Это позволяет
 * отделить данные от их представления и создавать различные форматы
 * отчетов (HTML, XML, текст) с одними и теми же данными.
 * <p>
 * Основные возможности:
 * - Генерация отчетов из шаблонов Velocity
 * - Поддержка различных форматов вывода
 * - Сохранение отчетов в файлы
 * - Работа с шаблонами, расположенными в classpath
 * <p>
 * Пример использования:
 * ```java
 * // Подготовка данных для отчета
 * Map<String, Object> data = new HashMap<>();
 * data.put("testName", "Login Test");
 * data.put("status", "PASSED");
 * data.put("executionTime", "2.5 seconds");
 * <p>
 * // Генерация отчета из шаблона
 * String report = ReportGenerator.generateReportFromTemplate(
 * "templates/test-report.html.vm", data);
 * <p>
 * // Сохранение отчета в файл
 * ReportGenerator.saveReportToFile(report, "reports/login-test-report.html");
 * <p>
 * // Или в одну строку
 * ReportGenerator.generateAndSaveReport(
 * "templates/test-report.html.vm",
 * data,
 * "reports/login-test-report.html");
 * ```
 */
public class ReportGenerator {
    /**
     * Движок шаблонизатора Velocity.
     */
    private static final VelocityEngine velocityEngine;

    static {
        // Инициализация движка Velocity
        velocityEngine = new VelocityEngine();
        velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        velocityEngine.init();
    }

    /**
     * Генерирует отчет на основе шаблона Velocity и данных.
     *
     * @param templatePath путь к шаблону в classpath (например, "templates/report.html.vm")
     * @param data         карта с данными для подстановки в шаблон
     * @return сгенерированный отчет в виде строки
     */
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

    /**
     * Сохраняет содержимое отчета в файл.
     * Автоматически создает необходимые директории.
     *
     * @param reportContent содержимое отчета
     * @param outputPath    путь для сохранения файла
     * @throws RuntimeException если произошла ошибка при сохранении файла
     */
    public static void saveReportToFile(String reportContent, String outputPath) {
        try {
            Path path = Paths.get(outputPath);

            // Создаем директорию, если она не существует
            Files.createDirectories(path.getParent());

            // Записываем содержимое отчета в файл
            Files.writeString(path, reportContent);
        } catch (Exception e) {
            throw new RuntimeException("Не удалось сохранить отчет в файл: " + outputPath, e);
        }
    }

    /**
     * Генерирует отчет на основе шаблона и сохраняет его в файл.
     * Это удобный метод, объединяющий generateReportFromTemplate и saveReportToFile.
     *
     * @param templatePath путь к шаблону в classpath
     * @param data         карта с данными для подстановки в шаблон
     * @param outputPath   путь для сохранения файла
     * @throws RuntimeException если произошла ошибка при генерации или сохранении отчета
     */
    public static void generateAndSaveReport(String templatePath, Map<String, Object> data, String outputPath) {
        String reportContent = generateReportFromTemplate(templatePath, data);
        saveReportToFile(reportContent, outputPath);
    }
}