package exp.pages;

import com.microsoft.playwright.Page;

public class GooglePage {
    private final Page page;

    public GooglePage(Page page) {
        this.page = page;
    }

    public void navigateToHome() {
        page.navigate("https://www.gogle.com.ua");
    }
}
