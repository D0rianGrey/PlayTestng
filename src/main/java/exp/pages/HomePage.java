package exp.pages;

import com.microsoft.playwright.Page;

public class HomePage {
    private final Page page;

    public HomePage(Page page) {
        this.page = page;
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