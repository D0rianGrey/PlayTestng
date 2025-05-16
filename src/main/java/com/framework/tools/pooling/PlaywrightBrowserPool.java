package com.framework.tools.pooling;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Playwright;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Пул браузеров для многопоточного выполнения тестов.
 * <p>
 * Данный класс предоставляет механизм пула экземпляров браузеров Playwright,
 * который позволяет повторно использовать уже запущенные браузеры для разных
 * тестов. Это значительно повышает производительность, так как запуск браузера
 * является ресурсоемкой операцией.
 * <p>
 * Класс реализует паттерн Singleton и потокобезопасный механизм доступа к
 * экземплярам браузеров через блокирующую очередь (BlockingQueue).
 * <p>
 * Основные преимущества использования пула браузеров:
 * - Экономия ресурсов за счет повторного использования уже запущенных браузеров
 * - Ускорение выполнения тестов
 * - Ограничение максимального количества одновременно запущенных браузеров
 * - Автоматическое управление жизненным циклом браузеров
 * <p>
 * Пример использования:
 * ```java
 * // Инициализация пула на 3 браузера
 * PlaywrightBrowserPool pool = PlaywrightBrowserPool.getInstance(3);
 * <p>
 * // Получение браузера из пула
 * Browser browser = pool.borrowBrowser();
 * <p>
 * try {
 * // Использование браузера для тестов
 * BrowserContext context = browser.newContext();
 * Page page = context.newPage();
 * page.navigate("https://example.com");
 * // ...
 * } finally {
 * // Возврат браузера в пул
 * pool.returnBrowser(browser);
 * }
 * <p>
 * // Закрытие пула в конце всех тестов
 * pool.shutdown();
 * ```
 */
public class PlaywrightBrowserPool {
    /**
     * Единственный экземпляр класса (Singleton).
     */
    private static PlaywrightBrowserPool instance;

    /**
     * Экземпляр Playwright для создания браузеров.
     */
    private final Playwright playwright;

    /**
     * Очередь доступных браузеров.
     */
    private final BlockingQueue<Browser> browserPool;

    /**
     * Размер пула браузеров.
     */
    private final int poolSize;

    /**
     * Приватный конструктор для Singleton.
     * Инициализирует пул браузеров заданного размера.
     *
     * @param poolSize размер пула (максимальное количество браузеров)
     */
    private PlaywrightBrowserPool(int poolSize) {
        this.poolSize = poolSize;
        this.playwright = Playwright.create();
        this.browserPool = new LinkedBlockingQueue<>(poolSize);

        // Инициализация пула браузеров
        for (int i = 0; i < poolSize; i++) {
            Browser browser = playwright.chromium().launch(
                    new BrowserType.LaunchOptions()
                            .setHeadless(false)
                            .setSlowMo(100)
            );
            browserPool.offer(browser);
        }
    }

    /**
     * Получает экземпляр пула браузеров (Singleton).
     * Если экземпляр еще не создан, создает его с указанным размером пула.
     *
     * @param poolSize размер пула браузеров
     * @return экземпляр PlaywrightBrowserPool
     */
    public static synchronized PlaywrightBrowserPool getInstance(int poolSize) {
        if (instance == null) {
            instance = new PlaywrightBrowserPool(poolSize);
        }
        return instance;
    }

    /**
     * Получает браузер из пула.
     * Если все браузеры заняты, метод блокируется до освобождения
     * или до истечения таймаута (30 секунд).
     *
     * @return экземпляр Browser или null, если превышен таймаут
     * @throws InterruptedException если поток был прерван во время ожидания
     */
    public Browser borrowBrowser() throws InterruptedException {
        return browserPool.poll(30, TimeUnit.SECONDS);
    }

    /**
     * Возвращает браузер в пул.
     *
     * @param browser экземпляр Browser для возврата в пул
     */
    public void returnBrowser(Browser browser) {
        browserPool.offer(browser);
    }

    /**
     * Закрывает все браузеры в пуле и освобождает ресурсы Playwright.
     * Должен вызываться в конце всех тестов.
     */
    public void shutdown() {
        Browser browser;
        while ((browser = browserPool.poll()) != null) {
            browser.close();
        }
        playwright.close();
    }
}