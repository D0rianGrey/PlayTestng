package exp.core;

import com.microsoft.playwright.Page;

public class PageBuilder<T extends PageObject> {
    private final Class<T> pageClass;
    private Page page;

    private PageBuilder(Class<T> pageClass) {
        this.pageClass = pageClass;
    }

    public static <T extends PageObject> PageBuilder<T> forPage(Class<T> pageClass) {
        return new PageBuilder<>(pageClass);
    }

    public PageBuilder<T> withPage(Page page) {
        this.page = page;
        return this;
    }

    public T build() {
        try {
            // Пробуем использовать конструктор с Page
            try {
                return pageClass.getConstructor(Page.class).newInstance(page);
            } catch (NoSuchMethodException e) {
                // Используем пустой конструктор и setPage
                T pageObject = pageClass.getDeclaredConstructor().newInstance();
                pageObject.setPage(page);
                return pageObject;
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to build page object: " + pageClass.getName(), e);
        }
    }
}