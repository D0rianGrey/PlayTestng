package com.framework.api.testing;

import com.framework.api.annotations.TestData;
import com.framework.api.annotations.UsePage;
import com.framework.internal.browser.BrowserManager;
import com.framework.internal.factory.DefaultPageFactory;
import com.framework.api.factory.PageFactory;
import com.framework.api.config.PlaywrightConfig;
import com.framework.extentions.screenshots.ScreenshotHelper;
import com.framework.internal.logging.TestLogger;
import com.microsoft.playwright.*;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Базовый класс для всех тестов, использующих Playwright.
 * Обеспечивает инициализацию и закрытие ресурсов Playwright,
 * а также внедрение зависимостей в тестовые методы.
 * <p>
 * Особенности:
 * - Автоматическое создание и управление объектами Playwright, Browser, BrowserContext и Page
 * - Автоматическое внедрение Page Objects в тесты через аннотацию @UsePage
 * - Поддержка параметризации тестов через аннотацию @TestData
 * - Автоматическое создание скриншотов и трассировок при ошибках
 * - Поддержка пользовательских фабрик страниц
 * <p>
 * Пример использования:
 * ```
 *
 * @UsePage public class MyTest extends PlaywrightBaseTest {
 * @Test public void testHomePage(HomePage homePage) {
 * homePage.navigateToHome();
 * assertTrue(homePage.isLoaded());
 * }
 * }
 * ```
 */
@Test(dataProvider = "pageObjects")
public abstract class PlaywrightBaseTest {

    // Не статические поля для изоляции между тестовыми классами
    protected Playwright playwright;
    protected Browser browser;
    protected BrowserContext browserContext;
    protected Page page;

    // Потокобезопасная карта для фабрик страниц
    private static final Map<Class<?>, PageFactory> pageFactories = new ConcurrentHashMap<>();

    // Путь для сохранения трассировок
    private Path tracePath;

    /**
     * Инициализация ресурсов Playwright перед запуском тестового класса.
     * Создает экземпляры Playwright, Browser, BrowserContext и Page.
     *
     * @param context контекст тестирования TestNG
     */
    @BeforeClass
    public void setUp(ITestContext context) {
        TestLogger.LOGGER.info("Инициализация ресурсов Playwright для класса {}", getClass().getSimpleName());

        // Создаем экземпляр Playwright
        playwright = Playwright.create();

        // Используем BrowserManager для создания браузера
        browser = BrowserManager.createBrowser(playwright);

        // Создаем контекст с настройками из конфигурации
        browserContext = browser.newContext(BrowserManager.createContextOptions());

        // Создаем страницу
        page = browserContext.newPage();

        // Если в конфигурации включена трассировка, начинаем её запись
        if (PlaywrightConfig.getInstance().captureTraceOnFailure()) {
            // Создаем директорию для трассировок, если её нет
            try {
                Path tracesDir = Paths.get("traces");
                if (!tracesDir.toFile().exists()) {
                    tracesDir.toFile().mkdirs();
                }
            } catch (Exception e) {
                TestLogger.LOGGER.error("Не удалось создать директорию для трассировок: {}", e.getMessage());
            }

            browserContext.tracing().start(new Tracing.StartOptions()
                    .setScreenshots(true)
                    .setSnapshots(true)
                    .setSources(true));
        }

        // Сохраняем объекты в контексте для доступа из других классов
        context.setAttribute("playwright", playwright);
        context.setAttribute("browser", browser);
        context.setAttribute("browserContext", browserContext);
        context.setAttribute("page", page);

        TestLogger.LOGGER.info("Ресурсы Playwright инициализированы успешно");
    }

    /**
     * Освобождение ресурсов Playwright после завершения всех тестов в классе.
     * Закрывает Page, BrowserContext, Browser и Playwright.
     */
    @AfterClass
    public void tearDown() {
        TestLogger.LOGGER.info("Освобождение ресурсов Playwright для класса {}", getClass().getSimpleName());

        // Останавливаем трассировку, если она была запущена
        if (PlaywrightConfig.getInstance().captureTraceOnFailure() && browserContext != null) {
            try {
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
                tracePath = Paths.get("traces", getClass().getSimpleName() + "_" + timestamp + ".zip");
                browserContext.tracing().stop(new Tracing.StopOptions().setPath(tracePath));
                TestLogger.LOGGER.info("Трассировка сохранена в {}", tracePath);
            } catch (Exception e) {
                TestLogger.LOGGER.error("Не удалось сохранить трассировку: {}", e.getMessage());
            }
        }

        // Закрываем ресурсы в обратном порядке
        if (page != null) {
            try {
                page.close();
                TestLogger.LOGGER.debug("Page закрыт");
            } catch (Exception e) {
                TestLogger.LOGGER.error("Ошибка при закрытии Page: {}", e.getMessage());
            }
        }

        if (browserContext != null) {
            try {
                browserContext.close();
                TestLogger.LOGGER.debug("BrowserContext закрыт");
            } catch (Exception e) {
                TestLogger.LOGGER.error("Ошибка при закрытии BrowserContext: {}", e.getMessage());
            }
        }

        if (browser != null) {
            try {
                browser.close();
                TestLogger.LOGGER.debug("Browser закрыт");
            } catch (Exception e) {
                TestLogger.LOGGER.error("Ошибка при закрытии Browser: {}", e.getMessage());
            }
        }

        if (playwright != null) {
            try {
                playwright.close();
                TestLogger.LOGGER.debug("Playwright закрыт");
            } catch (Exception e) {
                TestLogger.LOGGER.error("Ошибка при закрытии Playwright: {}", e.getMessage());
            }
        }

        TestLogger.LOGGER.info("Ресурсы Playwright освобождены успешно");
    }

    /**
     * Действия после каждого теста.
     * Делает скриншот и сохраняет трассировку в случае ошибки.
     *
     * @param result результат выполнения теста
     */
    @AfterMethod
    public void afterMethod(ITestResult result) {
        // Делаем скриншот в случае ошибки, если это настроено в конфигурации
        if (PlaywrightConfig.getInstance().takeScreenshotOnFailure() && result.getStatus() == ITestResult.FAILURE) {
            ScreenshotHelper.captureScreenshotOnFailure(result, page);
        }

        // Если тест не прошел, и трассировка уже остановлена (в tearDown),
        // добавляем информацию о трассировке в отчет
        if (result.getStatus() == ITestResult.FAILURE && tracePath != null) {
            result.setAttribute("trace", tracePath.toString());
            TestLogger.LOGGER.info("Для теста {} сохранена трассировка: {}",
                    result.getMethod().getMethodName(), tracePath);
        }

        // Очищаем контекст страницы после каждого теста, если это включено в конфигурации
        if (PlaywrightConfig.getInstance().isClearContextAfterTest() && browserContext != null) {
            try {
                // Очищаем cookies и localStorage
                browserContext.clearCookies();
                page.evaluate("localStorage.clear();");
                page.evaluate("sessionStorage.clear();");
                TestLogger.LOGGER.debug("Контекст страницы очищен после теста {}",
                        result.getMethod().getMethodName());
            } catch (Exception e) {
                TestLogger.LOGGER.error("Ошибка при очистке контекста: {}", e.getMessage());
            }
        }
    }

    /**
     * Поставщик данных для тестовых методов.
     * Обеспечивает внедрение объектов Playwright и Page Objects в тестовые методы.
     * Также поддерживает параметризацию тестов с помощью аннотации TestData.
     *
     * @param method тестовый метод
     * @return массив параметров для тестового метода
     */
    @DataProvider(name = "pageObjects")
    public Object[][] pageObjectsProvider(Method method) {
        TestLogger.LOGGER.debug("Подготовка параметров для метода {}", method.getName());

        // Проверяем, есть ли аннотация TestData
        TestData testData = method.getAnnotation(TestData.class);

        if (testData != null && testData.data().length > 0) {
            // Данные из аннотации TestData
            String[] dataValues = testData.data();
            // Количество наборов данных
            int dataCount = dataValues.length;
            // Результирующий массив параметров
            Object[][] result = new Object[dataCount][];

            TestLogger.LOGGER.debug("Обнаружена аннотация TestData с {} наборами данных", dataCount);

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
                        TestLogger.LOGGER.debug("Установлено значение '{}' для параметра {}",
                                dataValues[i], param.getName());
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
                                TestLogger.LOGGER.debug("Создан объект типа {} для параметра {}",
                                        pageObject.getClass().getSimpleName(), param.getName());
                            } else {
                                TestLogger.LOGGER.warn("Не удалось создать объект для параметра {}",
                                        param.getName());
                            }
                        } else {
                            TestLogger.LOGGER.warn("Фабрика не может создать объект типа {} для параметра {}",
                                    paramType.getSimpleName(), param.getName());
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
                        TestLogger.LOGGER.debug("Создан объект типа {} для параметра {}",
                                pageObject.getClass().getSimpleName(), param.getName());
                    } else {
                        TestLogger.LOGGER.warn("Не удалось создать объект для параметра {}",
                                param.getName());
                    }
                } else {
                    TestLogger.LOGGER.warn("Фабрика не может создать объект типа {} для параметра {}",
                            paramType.getSimpleName(), param.getName());
                }
            }
        }

        return new Object[][]{params};
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

    /**
     * Получает класс фабрики страниц на основе аннотаций.
     *
     * @param method тестовый метод
     * @return класс фабрики страниц
     */
    private Class<? extends PageFactory> getPageFactoryClass(Method method) {
        UsePage usePage = method.getAnnotation(UsePage.class);
        if (usePage == null) {
            usePage = getClass().getAnnotation(UsePage.class);
        }

        return usePage != null ? usePage.value() : DefaultPageFactory.class;
    }

    /**
     * Обновляет настройки текущего контекста браузера.
     * Полезно для изменения настроек во время выполнения теста.
     *
     * @param options новые настройки контекста
     */
    protected void updateContextOptions(Browser.NewContextOptions options) {
        if (browserContext != null) {
            TestLogger.LOGGER.info("Обновление настроек контекста браузера");

            // Закрываем текущий контекст и страницу
            if (page != null) {
                page.close();
            }
            browserContext.close();

            // Создаем новый контекст с обновленными настройками
            browserContext = browser.newContext(options);
            page = browserContext.newPage();

            TestLogger.LOGGER.info("Настройки контекста обновлены, создана новая страница");
        } else {
            TestLogger.LOGGER.warn("Невозможно обновить настройки - контекст браузера не инициализирован");
        }
    }

    /**
     * Создает новую страницу в текущем контексте браузера.
     *
     * @return новая страница
     */
    protected Page createNewPage() {
        if (browserContext != null) {
            TestLogger.LOGGER.debug("Создание новой страницы в текущем контексте");
            return browserContext.newPage();
        } else {
            TestLogger.LOGGER.warn("Невозможно создать страницу - контекст браузера не инициализирован");
            return null;
        }
    }

    /**
     * Перезагружает текущую страницу.
     */
    protected void refreshPage() {
        if (page != null) {
            TestLogger.LOGGER.debug("Перезагрузка текущей страницы");
            page.reload();
        } else {
            TestLogger.LOGGER.warn("Невозможно перезагрузить страницу - страница не инициализирована");
        }
    }

    /**
     * Переходит на указанный URL.
     *
     * @param url URL для перехода
     */
    protected void navigateTo(String url) {
        if (page != null) {
            TestLogger.LOGGER.debug("Переход на URL: {}", url);
            page.navigate(url);
        } else {
            TestLogger.LOGGER.warn("Невозможно перейти на URL - страница не инициализирована");
        }
    }

    /**
     * Очищает cookies и localStorage в текущем контексте.
     */
    protected void clearContext() {
        if (browserContext != null && page != null) {
            TestLogger.LOGGER.debug("Очистка контекста (cookies и localStorage)");
            browserContext.clearCookies();
            page.evaluate("localStorage.clear();");
            page.evaluate("sessionStorage.clear();");
        } else {
            TestLogger.LOGGER.warn("Невозможно очистить контекст - контекст или страница не инициализированы");
        }
    }
}