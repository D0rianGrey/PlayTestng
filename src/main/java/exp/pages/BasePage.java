package exp.pages;

import com.microsoft.playwright.Page;
import exp.core.PageObject;

public abstract class BasePage implements PageObject {
    protected Page page;

    public BasePage(Page page) {
        this.page = page;
    }

    public BasePage() {
        // Пустой конструктор для случаев, когда страница будет установлена позже
    }

    @Override
    public void setPage(Page page) {
        this.page = page;
    }

    @Override
    public Page getPage() {
        return page;
    }

    // Общие методы для всех страниц
    public String getTitle() {
        return page.title();
    }

    public String getUrl() {
        return page.url();
    }

    public void refresh() {
        page.reload();
    }
}