package exp.pages;

import com.microsoft.playwright.Page;
import exp.core.PageObject;

/**
 * Базовый класс для всех объектов страниц.
 * Предоставляет общую функциональность для всех страниц в приложении.
 * <p>
 * Рекомендуется создавать свои страницы, наследуясь от этого класса,
 * а не напрямую реализуя интерфейс PageObject.
 * <p>
 * Пример использования:
 * ```
 * public class DashboardPage extends BasePage {
 * public DashboardPage(Page page) {
 * super(page);
 * }
 * <p>
 * public void clickUserProfile() {
 * page.click(".user-profile");
 * }
 * }
 * ```
 */
public abstract class BasePage implements PageObject {
    /**
     * Объект Playwright Page, ассоциированный с этой страницей
     */
    protected Page page;

    /**
     * Конструктор с параметром Page.
     *
     * @param page объект Playwright Page
     */
    public BasePage(Page page) {
        this.page = page;
    }

    /**
     * Пустой конструктор.
     * Используется, когда страница будет установлена позже через метод setPage.
     */
    public BasePage() {
        // Пустой конструктор для случаев, когда страница будет установлена позже
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
     * Получает заголовок текущей страницы.
     *
     * @return строка с заголовком страницы
     */
    public String getTitle() {
        return page.title();
    }

    /**
     * Получает URL текущей страницы.
     *
     * @return строка с URL страницы
     */
    public String getUrl() {
        return page.url();
    }

    /**
     * Обновляет текущую страницу.
     */
    public void refresh() {
        page.reload();
    }
}