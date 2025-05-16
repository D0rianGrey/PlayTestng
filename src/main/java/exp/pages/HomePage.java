package exp.pages;

import com.microsoft.playwright.Page;
import exp.core.PageObject;

public class HomePage implements PageObject {
    private Page page;

    public HomePage(Page page) {
        this.page = page;
    }

    // Конструктор без параметров для случаев, когда page устанавливается позже
    public HomePage() {
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
        page.navigate("https://example.com");
    }

    public String getTitle() {
        return page.title();
    }

    public boolean isLoaded() {
        return page.title().contains("Example Domain");
    }
}