package com.framework.api.factory;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

import java.lang.reflect.Method;

/**
 * Интерфейс для фабрик, создающих объекты страниц.
 * Фабрики используются фреймворком для создания объектов страниц и внедрения их в тесты.
 * <p>
 * Каждая реализация PageFactory определяет свою логику создания страниц,
 * что позволяет настраивать процесс создания под конкретные нужды.
 * <p>
 * Пример использования:
 * ```
 *
 * @UsePage(CustomPageFactory.class) public class MyTest extends PlaywrightBaseTest {
 * @Test public void testWithCustomFactory(HomePage homePage) {
 * // homePage создан с помощью CustomPageFactory
 * }
 * }
 * ```
 */
public interface PageFactory {
    /**
     * Создает и настраивает экземпляр страницы для тестирования.
     *
     * @param playwright Экземпляр Playwright
     * @param browser    Экземпляр Browser
     * @param context    Экземпляр BrowserContext
     * @param page       Экземпляр Page
     * @param testClass  Класс теста
     * @param testMethod Метод теста
     * @return Объект страницы или null, если не может быть создан
     */
    Object createPage(Playwright playwright, Browser browser, BrowserContext context,
                      Page page, Class<?> testClass, Method testMethod);

    /**
     * Определяет, может ли фабрика создать страницу указанного типа.
     *
     * @param pageType Тип страницы
     * @return true, если фабрика может создать такую страницу, иначе false
     */
    boolean canCreate(Class<?> pageType);
}