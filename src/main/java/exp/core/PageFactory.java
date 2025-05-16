package exp.core;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

import java.lang.reflect.Method;

public interface PageFactory {
    /**
     * Создает и настраивает экземпляр страницы для тестирования
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
     * Определяет, может ли фабрика создать страницу указанного типа
     *
     * @param pageType Тип страницы
     * @return true, если фабрика может создать такую страницу
     */
    boolean canCreate(Class<?> pageType);
}