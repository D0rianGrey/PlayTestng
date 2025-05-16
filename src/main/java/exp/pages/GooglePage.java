package exp.pages;

import com.microsoft.playwright.Page;
import exp.core.PageObject;

public class GooglePage implements PageObject {
    private Page page;

    public GooglePage(Page page) {
        this.page = page;
    }

    // Конструктор без параметров
    public GooglePage() {
    }

    @Override
    public void setPage(Page page) {
        this.page = page;
    }

    @Override
    public Page getPage() {
        return page;
    }

    public void navigateToHome() {
        page.navigate("https://www.google.com.ua");
    }
}