package com.framework.api.pages;

import com.microsoft.playwright.Page;

/**
 * Объект домашней страницы для тестирования.
 * <p>
 * Этот класс предоставляет методы для взаимодействия с главной
 * страницей тестируемого приложения и реализует интерфейс {@link PageObject},
 * что позволяет использовать его с механизмом внедрения зависимостей фреймворка.
 * <p>
 * В данном примере используется example.com в качестве тестового домена,
 * но в реальных проектах этот класс должен быть настроен на работу
 * с конкретным тестируемым приложением.
 * <p>
 * Пример использования:
 * ```java
 *
 * @Test public void testHomePage(HomePage homePage) {
 * // Переход на домашнюю страницу
 * homePage.navigateToHome();
 * <p>
 * // Проверка, что страница загружена
 * assertTrue(homePage.isLoaded());
 * <p>
 * // Получение заголовка страницы
 * String title = homePage.getTitle();
 * }
 * ```
 */
public class HomePage implements PageObject {
    /**
     * Объект Playwright Page для взаимодействия с браузером.
     */
    private Page page;

    /**
     * Конструктор с параметром Page.
     *
     * @param page объект Playwright Page
     */
    public HomePage(Page page) {
        this.page = page;
    }

    /**
     * Конструктор без параметров.
     * Используется, когда страница будет установлена позже через метод setPage.
     */
    public HomePage() {
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
     * Переходит на домашнюю страницу.
     * В данном примере используется example.com.
     */
    public void navigateToHome() {
        page.navigate("https://example.com");
    }

    /**
     * Получает заголовок страницы.
     *
     * @return строка с заголовком страницы
     */
    public String getTitle() {
        return page.title();
    }

    /**
     * Проверяет, загружена ли домашняя страница.
     * Проверка выполняется по наличию "Example Domain" в заголовке страницы.
     *
     * @return true, если страница загружена корректно
     */
    public boolean isLoaded() {
        return page.title().contains("Example Domain");
    }

    /**
     * Проверяет, содержит ли страница указанный текст.
     *
     * @param text текст для поиска
     * @return true, если текст найден на странице
     */
    public boolean containsText(String text) {
        // Проверяем весь текст на странице
        String pageContent = page.textContent("body");
        return pageContent != null && pageContent.contains(text);
    }

    /**
     * Переходит по ссылке с указанным текстом.
     *
     * @param linkText текст ссылки
     */
    public void clickLink(String linkText) {
        page.click("text=" + linkText);
    }

    /**
     * Получает URL текущей страницы.
     *
     * @return строка с URL
     */
    public String getCurrentUrl() {
        return page.url();
    }
}