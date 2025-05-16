package exp.listeners;

import com.microsoft.playwright.*;
import exp.DefaultPageFactory;
import exp.core.PageFactory;
import exp.core.UsePage;
import org.testng.IHookCallBack;
import org.testng.IHookable;
import org.testng.ITestContext;
import org.testng.ITestResult;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PlaywrightPageInjector implements IHookable {
    private static final String PLAYWRIGHT_KEY = "playwright";
    private static final String BROWSER_KEY = "browser";
    private static final String CONTEXT_KEY = "browserContext";
    private static final String PAGE_KEY = "page";

    // Потокобезопасный map для кэширования фабрик
    private static final Map<Class<?>, PageFactory> pageFactories = new ConcurrentHashMap<>();

    // Используем ThreadLocal для хранения объектов Playwright для каждого потока
    private final ThreadLocal<Playwright> playwrightThreadLocal = new ThreadLocal<>();
    private final ThreadLocal<Browser> browserThreadLocal = new ThreadLocal<>();
    private final ThreadLocal<BrowserContext> contextThreadLocal = new ThreadLocal<>();
    private final ThreadLocal<Page> pageThreadLocal = new ThreadLocal<>();

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

        // Устанавливаем параметры через рефлексию
        try {
            // Сначала пробуем получить метод setParameters у ITestResult
            try {
                java.lang.reflect.Method setParamsMethod = ITestResult.class.getDeclaredMethod("setParameters", Object[].class);
                setParamsMethod.setAccessible(true);
                setParamsMethod.invoke(testResult, new Object[]{paramValues});
            } catch (NoSuchMethodException e) {
                // Если этот метод не найден, попробуем другой подход
                Object testNGInvocationInfo = testResult.getClass().getMethod("getTestNGInvocationInfo").invoke(testResult);
                Method setParamsMethod = testNGInvocationInfo.getClass().getMethod("setParameterValues", Object[].class);
                setParamsMethod.invoke(testNGInvocationInfo, new Object[]{paramValues});
            }

            // Запускаем тестовый метод (теперь с правильными параметрами)
            callBack.runTestMethod(testResult);
        } catch (Exception e) {
            System.err.println("Не удалось установить параметры для теста: " + e.getMessage());
            e.printStackTrace();
            callBack.runTestMethod(testResult);
        }
    }

    private void initPlaywrightObjects(ITestContext context) {
        // Используем ThreadLocal для изоляции объектов между потоками
        Playwright playwright = playwrightThreadLocal.get();
        if (playwright == null) {
            playwright = Playwright.create();
            playwrightThreadLocal.set(playwright);

            Browser browser = playwright.chromium().launch(
                    new BrowserType.LaunchOptions()
                            .setHeadless(false)
                            .setSlowMo(100)
            );
            browserThreadLocal.set(browser);

            BrowserContext browserContext = browser.newContext();
            contextThreadLocal.set(browserContext);

            Page page = browserContext.newPage();
            pageThreadLocal.set(page);
        }
    }

    private Object[] preparePageObjects(Method method, ITestResult testResult, PageFactory factory) {
        Object instance = testResult.getInstance();
        Parameter[] parameters = method.getParameters();
        Object[] paramValues = new Object[parameters.length];

        // Получаем объекты Playwright из ThreadLocal
        Playwright playwright = playwrightThreadLocal.get();
        Browser browser = browserThreadLocal.get();
        BrowserContext browserContext = contextThreadLocal.get();
        Page page = pageThreadLocal.get();

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