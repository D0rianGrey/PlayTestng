package com.tests;

import com.framework.api.testing.PlaywrightBaseTest;
import com.framework.api.annotations.PageParam;
import com.framework.api.annotations.UsePage;
import com.framework.internal.factory.CustomPageFactory;
import com.framework.api.pages.GooglePage;
import com.framework.api.pages.HomePage;
import com.microsoft.playwright.Page;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

/**
 * Пример тестового класса для демонстрации возможностей фреймворка.
 * <p>
 * Показывает различные способы использования аннотаций и внедрения зависимостей:
 * - Внедрение объектов Page
 * - Внедрение Page Objects
 * - Использование кастомных фабрик страниц
 * - Настройка параметров страницы
 * <p>
 * Все тесты унаследованы от базового класса PlaywrightBaseTest, который обеспечивает
 * инициализацию ресурсов Playwright и внедрение зависимостей.
 */
@UsePage
public class HomePageTest extends PlaywrightBaseTest {

    /**
     * Тест с внедрением объекта HomePage.
     * Демонстрирует базовое использование внедрения Page Objects.
     *
     * @param homePage объект домашней страницы, созданный фреймворком
     */
    @Test
    public void testHomePage(HomePage homePage) {
        // Переходим на домашнюю страницу
        homePage.navigateToHome();

        // Проверяем, что страница загружена
        assertTrue(homePage.isLoaded(), "Домашняя страница должна быть загружена");
    }

    /**
     * Тест с внедрением объекта Page.
     * Демонстрирует прямое использование Playwright Page без Page Objects.
     *
     * @param page объект Playwright Page
     */
    @Test
    public void testDirectPage(Page page) {
        // Переходим на страницу напрямую
        page.navigate("https://example.com");

        // Проверяем заголовок страницы
        assertTrue(page.title().contains("Example Domain"), "Страница должна содержать правильный заголовок");
    }

    /**
     * Тест с использованием кастомной фабрики страниц.
     * Демонстрирует возможность настройки создания страниц через аннотацию UsePage.
     *
     * @param homePage объект домашней страницы, созданный кастомной фабрикой
     */
    @Test
    @UsePage(CustomPageFactory.class)
    public void testWithCustomFactory(HomePage homePage) {
        homePage.navigateToHome();
        assertTrue(homePage.isLoaded(), "Домашняя страница должна быть загружена");
    }

    /**
     * Тест с внедрением другого типа страницы.
     * Демонстрирует возможность внедрения разных типов страниц в разные тесты.
     *
     * @param page объект страницы Google
     */
    @Test
    public void testWithPageFactory(GooglePage page) {
        page.navigateToHome();
    }

    /**
     * Тест с аннотацией PageParam.
     * Демонстрирует настройку страницы через аннотацию PageParam,
     * которая позволяет указать URL и автоматически перейти на него.
     *
     * @param homePage объект домашней страницы с предустановленными параметрами
     */
    @Test
    public void testAnnotatedPage(@PageParam(url = "https://example.com", navigate = true) HomePage homePage) {
        // Страница уже открыта благодаря аннотации PageParam
        assertTrue(homePage.isLoaded(), "Домашняя страница должна быть загружена");
    }
}