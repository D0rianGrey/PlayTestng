package exp.core;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public abstract class BaseComponent implements PageComponent {
    protected Page page;
    protected Locator root;

    public BaseComponent(Page page, String selector) {
        this.page = page;
        this.root = page.locator(selector);
    }

    public BaseComponent(Locator parent, String selector) {
        this.page = parent.page();
        this.root = parent.locator(selector);
    }

    @Override
    public void setPage(Page page) {
        this.page = page;
    }

    @Override
    public void setRoot(Locator root) {
        this.root = root;
    }

    @Override
    public Page getPage() {
        return page;
    }

    @Override
    public Locator getRoot() {
        return root;
    }

    public boolean isVisible() {
        return root.isVisible();
    }

    public void click() {
        root.click();
    }
}