package exp.core;

import com.microsoft.playwright.Page;

public interface PageObject {
    void setPage(Page page);

    Page getPage();
}