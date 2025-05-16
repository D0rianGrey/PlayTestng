package exp.core;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public interface PageComponent {
    void setPage(Page page);

    void setRoot(Locator root);

    Page getPage();

    Locator getRoot();
}