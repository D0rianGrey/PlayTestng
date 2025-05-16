package exp;

import com.microsoft.playwright.*;
import org.testng.IHookCallBack;
import org.testng.IHookable;
import org.testng.ITestContext;
import org.testng.ITestResult;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

public class PlaywrightPageInjector implements IHookable {
    private static final String PLAYWRIGHT_KEY = "playwright";
    private static final String BROWSER_KEY = "browser";
    private static final String CONTEXT_KEY = "browserContext";
    private static final String PAGE_KEY = "page";

    private static final Map<Class<?>, PageFactory> pageFactories = new HashMap<>();

    protected Playwright playwright;
    protected Browser browser;
    protected BrowserContext browserContext;
    protected Page page;

    @Override
    public void run(IHookCallBack callBack, ITestResult testResult) {
        // Получаем метод из testResult
        Method method = testResult.getMethod().getConstructorOrMethod().getMethod();
        UsePage usePage = method.getAnnotation(UsePage.class);

        if (usePage == null) {
            // Проверим аннотацию на уровне класса
            Class<?> testClass = testResult.getMethod().getTestClass().getRealClass();
            usePage = testClass.getAnnotation(UsePage.class);
        }

        // Если аннотации нет, выполняем метод без изменений
        if (usePage == null) {
            callBack.runTestMethod(testResult);
            return;
        }

        // Получаем или создаем объекты Playwright
        ITestContext context = testResult.getTestContext();
        initPlaywrightObjects(context);

        // Получаем фабрику страниц
        Class<? extends PageFactory> factoryClass = usePage.value();
        PageFactory factory = getPageFactory(factoryClass);

        // Подготавливаем параметры для внедрения
        Object[] paramValues = preparePageObjects(method, testResult, factory);

        // Устанавливаем параметры через рефлексию, так как в новых версиях TestNG нет метода runTestMethod с параметрами
        try {
            // Сначала пробуем получить метод setParameters у ITestResult
            try {
                java.lang.reflect.Method setParamsMethod = ITestResult.class.getDeclaredMethod("setParameters", Object[].class);
                setParamsMethod.setAccessible(true);
                setParamsMethod.invoke(testResult, new Object[]{paramValues});
            } catch (NoSuchMethodException e) {
                // Если этот метод не найден, попробуем другой подход
                // Вариант для более новых версий TestNG
                Object testNGInvocationInfo = testResult.getClass().getMethod("getTestNGInvocationInfo").invoke(testResult);
                Method setParamsMethod = testNGInvocationInfo.getClass().getMethod("setParameterValues", Object[].class);
                setParamsMethod.invoke(testNGInvocationInfo, new Object[]{paramValues});
            }

            // Запускаем тестовый метод (теперь с правильными параметрами)
            callBack.runTestMethod(testResult);
        } catch (Exception e) {
            // Если что-то пошло не так, выводим сообщение об ошибке и запускаем тест как обычно
            System.err.println("Не удалось установить параметры для теста: " + e.getMessage());
            e.printStackTrace();
            callBack.runTestMethod(testResult);
        }
    }

    private void initPlaywrightObjects(ITestContext context) {
        // Получаем или создаем объекты Playwright из контекста
        playwright = (Playwright) context.getAttribute(PLAYWRIGHT_KEY);
        if (playwright == null) {
            playwright = Playwright.create();
            context.setAttribute(PLAYWRIGHT_KEY, playwright);
        }

        browser = (Browser) context.getAttribute(BROWSER_KEY);
        if (browser == null) {
            browser = playwright.chromium().launch(
                    new BrowserType.LaunchOptions()
                            .setHeadless(false)
                            .setSlowMo(100)
            );
            context.setAttribute(BROWSER_KEY, browser);
        }

        browserContext = (BrowserContext) context.getAttribute(CONTEXT_KEY);
        if (browserContext == null) {
            browserContext = browser.newContext();
            context.setAttribute(CONTEXT_KEY, browserContext);
        }

        page = (Page) context.getAttribute(PAGE_KEY);
        if (page == null) {
            page = browserContext.newPage();
            context.setAttribute(PAGE_KEY, page);
        }
    }

    private Object[] preparePageObjects(Method method, ITestResult testResult, PageFactory factory) {
        Object instance = testResult.getInstance();
        Parameter[] parameters = method.getParameters();
        Object[] paramValues = new Object[parameters.length];

        // Заполняем массив параметров соответствующими значениями
        for (int i = 0; i < parameters.length; i++) {
            Parameter param = parameters[i];
            Class<?> paramType = param.getType();

            if (paramType.equals(Playwright.class)) {
                paramValues[i] = playwright;
            } else if (paramType.equals(Browser.class)) {
                paramValues[i] = browser;
            } else if (paramType.equals(BrowserContext.class)) {
                paramValues[i] = browserContext;
            } else if (paramType.equals(Page.class)) {
                paramValues[i] = page;
            } else if (factory.canCreate(paramType)) {
                // Создаем объект страницы с помощью фабрики
                Object pageObject = factory.createPage(
                        playwright, browser, browserContext, page,
                        instance.getClass(), method
                );

                if (pageObject != null) {
                    paramValues[i] = pageObject;
                }
            }
        }

        return paramValues;
    }

    private PageFactory getPageFactory(Class<? extends PageFactory> factoryClass) {
        PageFactory factory = pageFactories.get(factoryClass);
        if (factory == null) {
            try {
                factory = factoryClass.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                e.printStackTrace();
                factory = new DefaultPageFactory();
            }
            pageFactories.put(factoryClass, factory);
        }
        return factory;
    }
}