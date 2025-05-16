package com.framework.tests;

import com.framework.api.testing.PlaywrightBaseTest;
import com.framework.api.annotations.PageParam;
import com.framework.api.annotations.TestData;
import com.framework.api.annotations.UsePage;
import com.framework.api.pages.HomePage;
import com.microsoft.playwright.Page;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertTrue;

/**
 * Пример тестового класса с параметризованными тестами.
 * <p>
 * Демонстрирует:
 * - Использование аннотации TestData для параметризации тестов
 * - Передачу данных в тестовые методы
 * - Комбинирование параметров данных и Page Objects
 * <p>
 * Параметризованные тесты позволяют запускать один и тот же тестовый метод
 * с разными наборами данных, что удобно для проверки различных комбинаций
 * входных данных и граничных случаев.
 */
@UsePage
public class ParameterizedTest extends PlaywrightBaseTest {

    /**
     * Параметризованный тест с несколькими доменами.
     * Использует аннотацию TestData для указания списка доменов,
     * которые нужно посетить и проверить.
     *
     * @param domain доменное имя (из аннотации TestData)
     * @param page   объект Playwright Page
     */
    @Test
    @TestData(data = {"example.com", "google.com", "github.com"})
    public void testDifferentDomains(String domain, Page page) {
        // Переходим на указанный домен
        page.navigate("https://" + domain);

        // Проверяем, что страница загрузилась
        assertTrue(page.title() != null && !page.title().isEmpty());

        // Логируем, какой домен посетили
        System.out.println("Visited domain: " + domain + ", title: " + page.title());
    }

    /**
     * Тест с аннотацией PageParam.
     * Демонстрирует автоматическую навигацию на указанный URL
     * перед выполнением теста.
     *
     * @param homePage объект домашней страницы
     */
    @Test
    public void testSpecificPage(@PageParam(url = "https://example.com", navigate = true) HomePage homePage) {
        // Страница уже открыта по указанному URL благодаря аннотации PageParam
        assertTrue(homePage.isLoaded());
    }
}