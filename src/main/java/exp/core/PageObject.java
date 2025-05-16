package exp.core;

import com.microsoft.playwright.Page;

/**
 * Базовый интерфейс для всех объектов страниц (Page Objects) в фреймворке.
 * <p>
 * Определяет обязательные методы, которые должны быть реализованы каждым
 * классом страницы. Это позволяет фреймворку работать с любыми страницами
 * единообразно и внедрять в них объект Playwright Page.
 * <p>
 * Модель Page Object - это паттерн проектирования для автоматизации тестирования,
 * который представляет каждую страницу (или компонент) веб-приложения как отдельный класс.
 * Это позволяет инкапсулировать детали взаимодействия с элементами страницы,
 * делая тесты более понятными и легкими в поддержке.
 * <p>
 * Преимущества использования PageObject:
 * - Отделение логики тестов от деталей взаимодействия с UI
 * - Повторное использование кода
 * - Улучшение читаемости и поддержки тестов
 * - Упрощение обновления тестов при изменении интерфейса
 * <p>
 * Пример реализации:
 * ```
 * public class LoginPage implements PageObject {
 * private Page page;
 * <p>
 * // Локаторы элементов страницы
 * private String usernameField = "#username";
 * private String passwordField = "#password";
 * private String loginButton = "button[type='submit']";
 * <p>
 * public LoginPage(Page page) {
 * this.page = page;
 * }
 *
 * @Override public void setPage(Page page) {
 * this.page = page;
 * }
 * @Override public Page getPage() {
 * return page;
 * }
 * <p>
 * // Бизнес-методы страницы
 * public void login(String username, String password) {
 * page.fill(usernameField, username);
 * page.fill(passwordField, password);
 * page.click(loginButton);
 * }
 * <p>
 * public boolean isErrorDisplayed() {
 * return page.isVisible(".error-message");
 * }
 * }
 * ```
 * <p>
 * В тесте страница используется так:
 * ```
 * @Test public void testLogin(LoginPage loginPage) {
 * loginPage.login("admin", "password");
 * assertFalse(loginPage.isErrorDisplayed());
 * }
 * ```
 */
public interface PageObject {
    /**
     * Устанавливает объект Playwright Page для этой страницы.
     * <p>
     * Этот метод вызывается фреймворком для передачи объекта Page в страницу.
     * Обычно вызывается при создании страницы через фабрику или при внедрении
     * в тестовый метод.
     *
     * @param page объект Playwright Page для взаимодействия с браузером
     */
    void setPage(Page page);

    /**
     * Возвращает объект Playwright Page, связанный с этой страницей.
     * <p>
     * Этот метод используется фреймворком и другими компонентами, которым
     * требуется доступ к объекту Page для выполнения дополнительных действий
     * с браузером.
     *
     * @return объект Playwright Page, связанный с этой страницей
     */
    Page getPage();
}