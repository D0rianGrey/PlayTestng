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

/**
 * Стандартная реализация фабрики для создания объектов страниц.
 * <p>
 * Отвечает за создание и настройку объектов страниц, которые будут
 * внедрены в тестовые методы. Поддерживает различные способы создания
 * страниц и обрабатывает аннотацию PageParam для настройки параметров.
 * <p>
 * Эта фабрика используется по умолчанию, если не указана другая фабрика
 * в аннотации @UsePage.
 * <p>
 * Поддерживаемые способы создания страниц:
 * - Через конструктор с параметром Page
 * - Через пустой конструктор и последующий вызов setPage
 * - Через методы initPage или setPage
 * <p>
 * Пример использования:
 * ```
 * // Стандартная фабрика используется по умолчанию
 *
 * @UsePage public class MyTest extends PlaywrightBaseTest {
 * @Test public void testHomePage(HomePage homePage) {
 * // homePage создан с помощью DefaultPageFactory
 * }
 * }
 * <p>
 * // Явное указание стандартной фабрики
 * @UsePage(DefaultPageFactory.class) public class AnotherTest extends PlaywrightBaseTest {
 * // тесты...
 * }
 * ```
 */
public class DefaultPageFactory implements PageFactory {

    /**
     * Создает объект страницы для тестового метода.
     * <p>
     * Анализирует параметры метода и создает объекты страниц нужных типов.
     * Обрабатывает аннотацию PageParam для настройки параметров страницы.
     *
     * @param playwright экземпляр Playwright
     * @param browser    экземпляр Browser
     * @param context    экземпляр BrowserContext
     * @param page       экземпляр Page
     * @param testClass  класс теста
     * @param testMethod метод теста
     * @return созданный объект страницы или null, если страница не может быть создана
     */
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
     * Создает объект страницы заданного типа.
     * <p>
     * Пробует различные способы создания:
     * 1. Через конструктор с параметром Page
     * 2. Через пустой конструктор и интерфейс PageObject
     * 3. Через пустой конструктор и метод initPage
     * 4. Через пустой конструктор и метод setPage
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

    /**
     * Проверяет, может ли фабрика создать страницу указанного типа.
     *
     * @param pageType тип страницы
     * @return true, если фабрика может создать страницу этого типа, иначе false
     */
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

    /**
     * Проверяет, имеет ли класс конструктор с параметром Page или методы для установки Page.
     *
     * @param type проверяемый тип
     * @return true, если класс имеет способ принять объект Page, иначе false
     */
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