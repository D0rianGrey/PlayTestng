package exp.pages;

import com.microsoft.playwright.Page;
import exp.core.PageObject;

/**
 * Объект страницы Google для тестирования.
 * <p>
 * Этот класс предоставляет методы для взаимодействия с главной страницей Google
 * и реализует интерфейс {@link PageObject}, что позволяет использовать его
 * с механизмом внедрения зависимостей фреймворка.
 * <p>
 * Пример использования:
 * ```java
 *
 * @Test public void testGoogleSearch(GooglePage googlePage) {
 * // Переход на страницу Google
 * googlePage.navigateToHome();
 * <p>
 * // Выполнение поиска
 * googlePage.search("Playwright testing framework");
 * <p>
 * // Проверка результатов
 * assertTrue(googlePage.hasResults());
 * }
 * ```
 * <p>
 * Класс можно расширить дополнительными методами для взаимодействия
 * с различными элементами страницы Google.
 */
public class GooglePage implements PageObject {
    /**
     * Объект Playwright Page для взаимодействия с браузером.
     */
    private Page page;

    /**
     * Конструктор с параметром Page.
     *
     * @param page объект Playwright Page
     */
    public GooglePage(Page page) {
        this.page = page;
    }

    /**
     * Конструктор без параметров.
     * Используется, когда страница будет установлена позже через метод setPage.
     */
    public GooglePage() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPage(Page page) {
        this.page = page;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page getPage() {
        return page;
    }

    /**
     * Переходит на главную страницу Google.
     * Использует украинскую версию, но можно настроить под нужную локаль.
     */
    public void navigateToHome() {
        page.navigate("https://www.google.com.ua");
    }

    /**
     * Выполняет поиск по указанному запросу.
     *
     * @param query поисковый запрос
     */
    public void search(String query) {
        // Локатор поля поиска Google
        String searchInputSelector = "input[name='q']";

        // Заполняем поле поиска
        page.fill(searchInputSelector, query);

        // Нажимаем Enter для выполнения поиска
        page.press(searchInputSelector, "Enter");

        // Ожидаем загрузки результатов поиска
//        page.waitForNavigation();
    }

    /**
     * Проверяет, есть ли результаты поиска на странице.
     *
     * @return true, если есть хотя бы один результат поиска
     */
    public boolean hasResults() {
        // Локатор для результатов поиска
        String resultsSelector = "#search .g";

        // Проверяем наличие хотя бы одного результата
        return page.locator(resultsSelector).count() > 0;
    }
}