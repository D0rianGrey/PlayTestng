package exp;

import com.microsoft.playwright.*;
import exp.DefaultPageFactory;
import exp.PageFactory;
import exp.pages.HomePage;

import java.lang.reflect.Method;

public class CustomPageFactory implements PageFactory {
    @Override
    public Object createPage(Playwright playwright, Browser browser, BrowserContext context,
                             Page page, Class<?> testClass, Method testMethod) {
        if (page == null) {
            return null;
        }

        // Здесь можно добавить кастомную логику для создания страниц
        // Например, предварительно открыть нужный URL или установить куки

        if (HomePage.class.isAssignableFrom(testMethod.getReturnType())) {
            page.navigate("https://example.com");
            return new HomePage(page);
        }

        // Для других типов используем дефолтную логику
        return new DefaultPageFactory().createPage(
                playwright, browser, context, page, testClass, testMethod);
    }

    @Override
    public boolean canCreate(Class<?> pageType) {
        return pageType != null &&
                (HomePage.class.isAssignableFrom(pageType) ||
                        new DefaultPageFactory().canCreate(pageType));
    }
}