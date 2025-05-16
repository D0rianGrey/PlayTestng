package exp.utils;

import java.util.UUID;

public class TestDataGenerator {

    public static String generateRandomEmail() {
        return "test" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";
    }

    public static String generateRandomUsername() {
        return "user_" + UUID.randomUUID().toString().substring(0, 8);
    }

    public static String generateRandomPassword() {
        return "Pass_" + UUID.randomUUID().toString().substring(0, 8) + "!";
    }

    public static String generateRandomPhoneNumber() {
        StringBuilder sb = new StringBuilder("123");
        for (int i = 0; i < 7; i++) {
            sb.append((int) (Math.random() * 10));
        }
        return sb.toString();
    }
}