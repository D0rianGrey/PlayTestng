package exp.listeners;

import com.microsoft.playwright.*;
import exp.DefaultPageFactory;
import exp.core.BrowserManager;
import exp.core.PageFactory;
import exp.annotations.UsePage;
import exp.utils.TestLogger;
import org.testng.IHookCallBack;
import org.testng.IHookable;
import org.testng.ITestContext;
import org.testng.ITestResult;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Слушатель TestNG для внедрения объектов Playwright и Page Objects в тестовые методы.
 * Реализует интерфейс IHookable для перехвата вызовов тестовых методов.
 */
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

    /**
     * Метод, вызываемый TestNG перед выполнением тестового метода.
     * Внедряет объекты Playwright и Page Objects в тестовый метод.
     *
     * @param callBack   обратный вызов для выполнения тестового метода
     * @param testResult результат выполнения теста
     */
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
            TestLogger.LOGGER.debug("Аннотация UsePage не найдена для метода {}, выполняем без внедрения зависимостей",
                    method.getName());
            callBack.runTestMethod(testResult);
            return;
        }

        TestLogger.LOGGER.debug("Обнаружена аннотация UsePage для метода {}, подготавливаем внедрение зависимостей",
                method.getName());

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
                TestLogger.LOGGER.debug("Параметры успешно установлены через setParameters");
            } catch (NoSuchMethodException e) {
                // Если этот метод не найден, попробуем другой подход
                TestLogger.LOGGER.debug("Метод setParameters не найден, пробуем альтернативный подход");
                Object testNGInvocationInfo = testResult.getClass().getMethod("getTestNGInvocationInfo").invoke(testResult);
                Method setParamsMethod = testNGInvocationInfo.getClass().getMethod("setParameterValues", Object[].class);
                setParamsMethod.invoke(testNGInvocationInfo, new Object[]{paramValues});
                TestLogger.LOGGER.debug("Параметры успешно установлены через TestNGInvocationInfo");
            }

            // Запускаем тестовый метод (теперь с правильными параметрами)
            TestLogger.LOGGER.debug("Запуск тестового метода с внедренными зависимостями");
            callBack.runTestMethod(testResult);
        } catch (Exception e) {
            TestLogger.LOGGER.error("Не удалось установить параметры для теста: {}", e.getMessage());
            e.printStackTrace();
            TestLogger.LOGGER.debug("Запускаем тестовый метод без внедрения зависимостей");
            callBack.runTestMethod(testResult);
        }
    }

    /**
     * Инициализирует объекты Playwright для текущего потока.
     *
     * @param context контекст тестирования TestNG
     */
    private void initPlaywrightObjects(ITestContext context) {
        // Используем ThreadLocal для изоляции объектов между потоками
        Playwright playwright = playwrightThreadLocal.get();
        if (playwright == null) {
            TestLogger.LOGGER.debug("Инициализация объектов Playwright для текущего потока");

            playwright = Playwright.create();
            playwrightThreadLocal.set(playwright);

            // Используем BrowserManager для создания браузера
            Browser browser = BrowserManager.createBrowser(playwright);
            browserThreadLocal.set(browser);

            // Создаем контекст с настройками из конфигурации
            BrowserContext browserContext = browser.newContext(BrowserManager.createContextOptions());
            contextThreadLocal.set(browserContext);

            Page page = browserContext.newPage();
            pageThreadLocal.set(page);

            TestLogger.LOGGER.debug("Объекты Playwright успешно инициализированы для текущего потока");
        }
    }

    /**
     * Подготавливает параметры для внедрения в тестовый метод.
     *
     * @param method     метод теста
     * @param testResult результат выполнения теста
     * @param factory    фабрика страниц
     * @return массив параметров для внедрения
     */
    private Object[] preparePageObjects(Method method, ITestResult testResult, PageFactory factory) {
        TestLogger.LOGGER.debug("Подготовка параметров для внедрения в метод {}", method.getName());

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
                TestLogger.LOGGER.debug("Внедряем Playwright в параметр {}", param.getName());
            } else if (paramType.equals(Browser.class)) {
                paramValues[i] = browser;
                TestLogger.LOGGER.debug("Внедряем Browser в параметр {}", param.getName());
            } else if (paramType.equals(BrowserContext.class)) {
                paramValues[i] = browserContext;
                TestLogger.LOGGER.debug("Внедряем BrowserContext в параметр {}", param.getName());
            } else if (paramType.equals(Page.class)) {
                paramValues[i] = page;
                TestLogger.LOGGER.debug("Внедряем Page в параметр {}", param.getName());
            } else if (factory.canCreate(paramType)) {
                // Создаем объект страницы с помощью фабрики
                Object pageObject = factory.createPage(
                        playwright, browser, browserContext, page,
                        instance.getClass(), method
                );

                if (pageObject != null) {
                    paramValues[i] = pageObject;
                    TestLogger.LOGGER.debug("Внедряем объект {} в параметр {}",
                            pageObject.getClass().getSimpleName(), param.getName());
                } else {
                    TestLogger.LOGGER.warn("Не удалось создать объект для параметра {}", param.getName());
                }
            } else {
                TestLogger.LOGGER.warn("Не удалось найти подходящий объект для параметра {} типа {}",
                        param.getName(), paramType.getSimpleName());
            }
        }

        return paramValues;
    }

    /**
     * Получает фабрику страниц из кэша или создает новую.
     *
     * @param factoryClass класс фабрики страниц
     * @return экземпляр фабрики страниц
     */
    private PageFactory getPageFactory(Class<? extends PageFactory> factoryClass) {
        PageFactory factory = pageFactories.get(factoryClass);
        if (factory == null) {
            synchronized (pageFactories) {
                // Повторная проверка внутри синхронизированного блока (паттерн Double-Checked Locking)
                factory = pageFactories.get(factoryClass);
                if (factory == null) {
                    try {
                        factory = factoryClass.getDeclaredConstructor().newInstance();
                        TestLogger.LOGGER.debug("Создана новая фабрика страниц типа {}",
                                factoryClass.getSimpleName());
                    } catch (Exception e) {
                        TestLogger.LOGGER.error("Ошибка при создании фабрики страниц {}: {}",
                                factoryClass.getSimpleName(), e.getMessage());
                        factory = new DefaultPageFactory();
                    }
                    pageFactories.put(factoryClass, factory);
                }
            }
        }
        return factory;
    }
}