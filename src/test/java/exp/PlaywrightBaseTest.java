package exp;

import com.microsoft.playwright.*;
import org.testng.ITestContext;
import org.testng.annotations.*;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class PlaywrightBaseTest {
    // Не статические поля для изоляции между тестовыми классами
    protected Playwright playwright;
    protected Browser browser;
    protected BrowserContext browserContext;
    protected Page page;

    // Потокобезопасная карта для фабрик страниц
    private static final Map<Class<?>, PageFactory> pageFactories = new ConcurrentHashMap<>();

    @BeforeClass
    public void setUp(ITestContext context) {
        playwright = Playwright.create();

        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions()
                        .setHeadless(false)
                        .setSlowMo(100)
        );

        browserContext = browser.newContext();
        page = browserContext.newPage();
    }

    @AfterClass
    public void tearDown() {
        if (page != null) {
            page.close();
        }
        if (browserContext != null) {
            browserContext.close();
        }
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }

    @DataProvider(name = "pageObjects")
    public Object[][] pageObjectsProvider(Method method) {
        // Проверяем наличие аннотации UsePage на методе
        UsePage usePage = method.getAnnotation(UsePage.class);
        if (usePage == null) {
            // Если нет на методе, проверяем на классе
            usePage = getClass().getAnnotation(UsePage.class);
        }

        Class<? extends PageFactory> factoryClass = DefaultPageFactory.class;
        if (usePage != null) {
            factoryClass = usePage.value();
        }

        PageFactory factory = getPageFactory(factoryClass);
        Parameter[] parameters = method.getParameters();
        Object[] params = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            Parameter param = parameters[i];
            Class<?> paramType = param.getType();

            if (paramType.equals(Playwright.class)) {
                params[i] = playwright;
            } else if (paramType.equals(Browser.class)) {
                params[i] = browser;
            } else if (paramType.equals(BrowserContext.class)) {
                params[i] = browserContext;
            } else if (paramType.equals(Page.class)) {
                params[i] = page;
            } else if (factory.canCreate(paramType)) {
                Object pageObject = factory.createPage(
                        playwright, browser, browserContext, page,
                        getClass(), method
                );

                if (pageObject != null) {
                    params[i] = pageObject;
                }
            }
        }

        return new Object[][]{params};
    }

    private PageFactory getPageFactory(Class<? extends PageFactory> factoryClass) {
        PageFactory factory = pageFactories.get(factoryClass);
        if (factory == null) {
            synchronized (pageFactories) {
                // Повторная проверка внутри синхронизированного блока (паттерн Double-Checked Locking)
                factory = pageFactories.get(factoryClass);
                if (factory == null) {
                    try {
                        factory = factoryClass.getDeclaredConstructor().newInstance();
                    } catch (Exception e) {
                        e.printStackTrace();
                        factory = new DefaultPageFactory();
                    }
                    pageFactories.put(factoryClass, factory);
                }
            }
        }
        return factory;
    }
}