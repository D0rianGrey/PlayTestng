package exp.core;

import com.microsoft.playwright.*;
import exp.DefaultPageFactory;
import exp.PlaywrightConfig;
import exp.ScreenshotHelper;
import exp.annotations.TestData;
import exp.annotations.UsePage;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Test(dataProvider = "pageObjects")
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
        // Проверяем, есть ли аннотация TestData
        TestData testData = method.getAnnotation(TestData.class);

        if (testData != null && testData.data().length > 0) {
            // Данные из аннотации TestData
            String[] dataValues = testData.data();
            // Количество наборов данных
            int dataCount = dataValues.length;
            // Результирующий массив параметров
            Object[][] result = new Object[dataCount][];

            // Для каждого набора данных создаем параметры
            for (int i = 0; i < dataCount; i++) {
                // Получаем параметры метода
                Parameter[] parameters = method.getParameters();
                // Создаем массив значений параметров
                Object[] params = new Object[parameters.length];

                // Заполняем массив значений параметров
                boolean dataParameterSet = false;
                for (int j = 0; j < parameters.length; j++) {
                    Parameter param = parameters[j];
                    Class<?> paramType = param.getType();

                    // Если тип параметра String и мы еще не установили значение данных,
                    // устанавливаем значение из TestData
                    if (!dataParameterSet && paramType.equals(String.class)) {
                        params[j] = dataValues[i];
                        dataParameterSet = true;
                    }
                    // Иначе обрабатываем параметр как обычно
                    else if (paramType.equals(Playwright.class)) {
                        params[j] = playwright;
                    } else if (paramType.equals(Browser.class)) {
                        params[j] = browser;
                    } else if (paramType.equals(BrowserContext.class)) {
                        params[j] = browserContext;
                    } else if (paramType.equals(Page.class)) {
                        params[j] = page;
                    } else {
                        // Получаем фабрику страниц
                        PageFactory factory = getPageFactory(getPageFactoryClass(method));

                        if (factory.canCreate(paramType)) {
                            Object pageObject = factory.createPage(
                                    playwright, browser, browserContext, page,
                                    getClass(), method
                            );

                            if (pageObject != null) {
                                params[j] = pageObject;
                            }
                        }
                    }
                }

                result[i] = params;
            }

            return result;
        }

        // Стандартная логика для обычных тестов без TestData
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
            } else {
                // Получаем фабрику страниц
                PageFactory factory = getPageFactory(getPageFactoryClass(method));

                if (factory.canCreate(paramType)) {
                    Object pageObject = factory.createPage(
                            playwright, browser, browserContext, page,
                            getClass(), method
                    );

                    if (pageObject != null) {
                        params[i] = pageObject;
                    }
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

    @AfterMethod
    public void afterMethod(ITestResult result) {
        if (PlaywrightConfig.getInstance().takeScreenshotOnFailure()) {
            ScreenshotHelper.captureScreenshotOnFailure(result, page);
        }

        if (PlaywrightConfig.getInstance().captureTraceOnFailure()) {
            ScreenshotHelper.captureTraceOnFailure(result, browserContext);
        }
    }

    // Вспомогательный метод для получения класса фабрики страниц
    private Class<? extends PageFactory> getPageFactoryClass(Method method) {
        UsePage usePage = method.getAnnotation(UsePage.class);
        if (usePage == null) {
            usePage = getClass().getAnnotation(UsePage.class);
        }

        return usePage != null ? usePage.value() : DefaultPageFactory.class;
    }
}