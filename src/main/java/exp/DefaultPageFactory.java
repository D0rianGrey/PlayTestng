package exp;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class DefaultPageFactory implements PageFactory {
    @Override
    public Object createPage(Playwright playwright, Browser browser, BrowserContext context,
                             Page page, Class<?> testClass, Method testMethod) {
        // Попытка найти и создать страницу с помощью рефлексии
        try {
            if (page == null) {
                return null;
            }

            // Получаем тип возвращаемого значения из метода тестирования
            Parameter[] parameters = testMethod.getParameters();
            for (Parameter param : parameters) {
                Class<?> paramType = param.getType();
                if (canCreate(paramType)) {
                    // Ищем конструктор, который принимает Page в качестве параметра
                    try {
                        Constructor<?> constructor = paramType.getConstructor(Page.class);
                        return constructor.newInstance(page);
                    } catch (NoSuchMethodException e) {
                        // Если нет конструктора с Page, пробуем пустой конструктор
                        Constructor<?> constructor = paramType.getConstructor();
                        Object pageObj = constructor.newInstance();

                        // Ищем метод initPage или setPage
                        try {
                            Method initMethod = paramType.getMethod("initPage", Page.class);
                            initMethod.invoke(pageObj, page);
                        } catch (NoSuchMethodException ex) {
                            try {
                                Method setMethod = paramType.getMethod("setPage", Page.class);
                                setMethod.invoke(pageObj, page);
                            } catch (NoSuchMethodException ex2) {
                                // Если нет подходящего метода, просто возвращаем объект
                            }
                        }
                        return pageObj;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean canCreate(Class<?> pageType) {
        // Проверяем, является ли класс страницей (можно добавить проверку на интерфейс или базовый класс)
        return pageType != null && !pageType.equals(Page.class)
                && !pageType.isPrimitive() && !pageType.equals(String.class)
                && !pageType.equals(Browser.class) && !pageType.equals(BrowserContext.class)
                && !pageType.equals(Playwright.class);
    }
}