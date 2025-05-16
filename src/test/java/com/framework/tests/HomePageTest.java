package com.framework.tests;

import com.framework.api.annotations.PageParam;
import com.framework.api.annotations.UsePage;
import com.framework.api.pages.GooglePage;
import com.framework.api.pages.HomePage;
import com.framework.api.testing.PlaywrightBaseTest;
import com.framework.internal.factory.CustomPageFactory;
import com.microsoft.playwright.Page;
import io.qameta.allure.*;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

@UsePage
@Epic("UI Tests")
@Feature("Home Page")
public class HomePageTest extends PlaywrightBaseTest {

    @Test
    @Story("Basic Navigation")
    @Description("Проверка загрузки домашней страницы")
    @Severity(SeverityLevel.CRITICAL)
    public void testHomePage(HomePage homePage) {
        // Переходим на домашнюю страницу
        homePage.navigateToHome();

        // Проверяем, что страница загружена
        assertTrue(homePage.isLoaded(), "Домашняя страница должна быть загружена");
    }

    @Test
    @Story("Direct Page Interaction")
    @Description("Проверка заголовка страницы при прямом использовании Playwright")
    @Severity(SeverityLevel.NORMAL)
    public void testDirectPage(Page page) {
        // Переходим на страницу напрямую
        page.navigate("https://example.com");

        // Проверяем заголовок страницы
        assertTrue(page.title().contains("Example Domain"), "Страница должна содержать правильный заголовок");
    }

    @Test
    @Story("Custom Factory")
    @Description("Тест с использованием кастомной фабрики страниц")
    @Severity(SeverityLevel.NORMAL)
    @UsePage(CustomPageFactory.class)
    public void testWithCustomFactory(HomePage homePage) {
        homePage.navigateToHome();
        assertTrue(homePage.isLoaded(), "Домашняя страница должна быть загружена");
    }

    @Test
    @Story("Google Navigation")
    @Description("Проверка навигации на страницу Google")
    @Severity(SeverityLevel.NORMAL)
    public void testWithPageFactory(GooglePage page) {
        page.navigateToHome();
    }

    @Test
    @Story("Annotated Page")
    @Description("Тест с аннотацией PageParam для автоматической навигации")
    @Severity(SeverityLevel.MINOR)
    public void testAnnotatedPage(@PageParam(url = "https://example.com", navigate = true) HomePage homePage) {
        // Страница уже открыта благодаря аннотации PageParam
        assertTrue(homePage.isLoaded(), "Домашняя страница должна быть загружена");
    }
}