package exp.utils;

import java.util.UUID;

/**
 * Утилитный класс для генерации тестовых данных.
 * <p>
 * Предоставляет методы для генерации различных типов данных,
 * которые могут потребоваться в тестах:
 * - Email адреса
 * - Имена пользователей
 * - Пароли
 * - Телефонные номера
 * и т.д.
 * <p>
 * Пример использования:
 * ```
 * // Заполнение формы регистрации случайными данными
 * String email = TestDataGenerator.generateRandomEmail();
 * String username = TestDataGenerator.generateRandomUsername();
 * String password = TestDataGenerator.generateRandomPassword();
 * String phone = TestDataGenerator.generateRandomPhoneNumber();
 * <p>
 * // Использование данных в тесте
 * page.fill("#email", email);
 * page.fill("#username", username);
 * page.fill("#password", password);
 * page.fill("#phone", phone);
 * ```
 */
public class TestDataGenerator {

    /**
     * Генерирует случайный email адрес.
     * Формат: test[8 случайных символов]@example.com
     *
     * @return строка со случайным email адресом
     */
    public static String generateRandomEmail() {
        return "test" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";
    }

    /**
     * Генерирует случайное имя пользователя.
     * Формат: user_[8 случайных символов]
     *
     * @return строка со случайным именем пользователя
     */
    public static String generateRandomUsername() {
        return "user_" + UUID.randomUUID().toString().substring(0, 8);
    }

    /**
     * Генерирует случайный пароль.
     * Формат: Pass_[8 случайных символов]!
     *
     * @return строка со случайным паролем
     */
    public static String generateRandomPassword() {
        return "Pass_" + UUID.randomUUID().toString().substring(0, 8) + "!";
    }

    /**
     * Генерирует случайный телефонный номер.
     * Формат: 123[7 случайных цифр]
     *
     * @return строка со случайным телефонным номером
     */
    public static String generateRandomPhoneNumber() {
        StringBuilder sb = new StringBuilder("123");
        for (int i = 0; i < 7; i++) {
            sb.append((int) (Math.random() * 10));
        }
        return sb.toString();
    }
}