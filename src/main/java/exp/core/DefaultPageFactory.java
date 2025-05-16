package exp;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import exp.core.PageFactory;
import exp.core.PageObject;
import exp.annotations.PageParam;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class DefaultPageFactory implements PageFactory {
    @Override
    public Object createPage(Playwright playwright, Browser browser, BrowserContext context,
                             Page page, Class<?> testClass, Method testMethod) {
        try {
            if (page == null) {
                return null;
            }

            Parameter[] parameters = testMethod.getParameters();
            for (Parameter param : parameters) {
                Class<?> paramType = param.getType();

                // Обработка для аннотации PageParam
                if (param.isAnnotationPresent(PageParam.class)) {
                    PageParam pageParam = param.getAnnotation(PageParam.class);

                    // Создаем объект страницы
                    Object pageObj = createPageObject(paramType, page);

                    // Если нужно, открываем URL
                    if (pageParam.navigate() && !pageParam.url().isEmpty()) {
                        page.navigate(pageParam.url());
                    }

                    return pageObj;
                }

                // Стандартная логика создания страниц
                if (canCreate(paramType)) {
                    return createPageObject(paramType, page);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Создает объект страницы заданного типа
     *
     * @param pageType тип страницы
     * @param page     объект Page из Playwright
     * @return созданный объект страницы или null в случае ошибки
     */
    private Object createPageObject(Class<?> pageType, Page page) {
        try {
            // Сначала пробуем конструктор с Page
            try {
                Constructor<?> constructor = pageType.getConstructor(Page.class);
                return constructor.newInstance(page);
            } catch (NoSuchMethodException e) {
                // Пробуем пустой конструктор и установку страницы
                Object pageObj = pageType.getDeclaredConstructor().newInstance();

                // Если класс реализует PageObject, используем его метод
                if (pageObj instanceof PageObject) {
                    ((PageObject) pageObj).setPage(page);
                    return pageObj;
                }

                // Иначе ищем методы вручную
                try {
                    Method initMethod = pageType.getMethod("initPage", Page.class);
                    initMethod.invoke(pageObj, page);
                    return pageObj;
                } catch (NoSuchMethodException ex) {
                    try {
                        Method setMethod = pageType.getMethod("setPage", Page.class);
                        setMethod.invoke(pageObj, page);
                        return pageObj;
                    } catch (NoSuchMethodException ex2) {
                        // Нет методов для установки Page
                        return pageObj;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Не удалось создать объект страницы: " + pageType.getName());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean canCreate(Class<?> pageType) {
        // Проверяем, является ли класс страницей
        return pageType != null &&
                !pageType.equals(Page.class) &&
                !pageType.isPrimitive() &&
                !pageType.equals(String.class) &&
                !pageType.equals(Browser.class) &&
                !pageType.equals(BrowserContext.class) &&
                !pageType.equals(Playwright.class) &&
                // Предпочтительно использовать класс, реализующий PageObject
                (PageObject.class.isAssignableFrom(pageType) ||
                        // Но также разрешаем классы с конструктором Page или методами setPage/initPage
                        hasPageConstructorOrMethod(pageType));
    }

    private boolean hasPageConstructorOrMethod(Class<?> type) {
        // Проверка наличия конструктора или методов
        try {
            type.getConstructor(Page.class);
            return true;
        } catch (NoSuchMethodException e) {
            try {
                type.getMethod("setPage", Page.class);
                return true;
            } catch (NoSuchMethodException ex) {
                try {
                    type.getMethod("initPage", Page.class);
                    return true;
                } catch (NoSuchMethodException ex2) {
                    return false;
                }
            }
        }
    }
}