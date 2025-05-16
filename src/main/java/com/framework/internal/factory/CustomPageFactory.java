package com.framework.internal.factory;

import com.framework.api.factory.PageFactory;
import com.framework.api.pages.HomePage;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

import java.lang.reflect.Method;

/**
 * Пользовательская фабрика для создания объектов страниц.
 * <p>
 * Расширяет стандартную логику создания страниц, добавляя возможность
 * предварительной инициализации страниц, например, автоматическую навигацию
 * на определенный URL или установку дополнительных параметров.
 * <p>
 * Используется в тестах через аннотацию @UsePage:
 * ```
 *
 * @Test
 * @UsePage(CustomPageFactory.class) public void testWithCustomFactory(HomePage homePage) {
 * // homePage будет создан этой фабрикой
 * }
 * ```
 * <p>
 * Преимущества использования кастомных фабрик:
 * - Возможность настраивать логику создания страниц для конкретных тестов
 * - Предварительная инициализация состояния (например, переход на URL)
 * - Создание страниц с нестандартными параметрами
 * - Добавление дополнительной логики перед тестом
 */
public class CustomPageFactory implements PageFactory {

    /**
     * Создает объект страницы для тестового метода.
     * <p>
     * Если запрошена страница типа HomePage, выполняет предварительную
     * навигацию на нужный URL. Для других типов страниц использует
     * стандартную фабрику DefaultPageFactory.
     *
     * @param playwright экземпляр Playwright
     * @param browser    экземпляр Browser
     * @param context    экземпляр BrowserContext
     * @param page       экземпляр Page
     * @param testClass  класс теста
     * @param testMethod метод теста
     * @return созданный объект страницы или null
     */
    @Override
    public Object createPage(Playwright playwright, Browser browser, BrowserContext context,
                             Page page, Class<?> testClass, Method testMethod) {
        if (page == null) {
            return null;
        }

        // Здесь можно добавить кастомную логику для создания страниц
        // Например, предварительно открыть нужный URL или установить куки

        if (HomePage.class.isAssignableFrom(testMethod.getReturnType())) {
            // Если запрошена HomePage, выполняем навигацию перед возвратом страницы
            page.navigate("https://example.com");
            return new HomePage(page);
        }

        // Для других типов используем дефолтную логику
        return new DefaultPageFactory().createPage(
                playwright, browser, context, page, testClass, testMethod);
    }

    /**
     * Проверяет, может ли фабрика создать страницу указанного типа.
     *
     * @param pageType тип страницы
     * @return true, если фабрика может создать страницу данного типа, иначе false
     */
    @Override
    public boolean canCreate(Class<?> pageType) {
        return pageType != null &&
                (HomePage.class.isAssignableFrom(pageType) ||
                        new DefaultPageFactory().canCreate(pageType));
    }
}