package exp.listeners;

import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;
import org.testng.ITestResult;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class AllureAttachmentHelper {

    public static void attachScreenshot(Path path, String name) {
        try {
            byte[] bytes = Files.readAllBytes(path);
            Allure.addAttachment(name, "image/png", new ByteArrayInputStream(bytes), ".png");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void attachTrace(Path path, String name) {
        try {
            byte[] bytes = Files.readAllBytes(path);
            Allure.addAttachment(name, "application/zip", new ByteArrayInputStream(bytes), ".zip");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Attachment(value = "Page HTML", type = "text/html")
    public static byte[] attachPageSource(String html) {
        return html.getBytes();
    }
}