package exp.utils;

import com.microsoft.playwright.*;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class PlaywrightBrowserPool {
    private static PlaywrightBrowserPool instance;
    private final Playwright playwright;
    private final BlockingQueue<Browser> browserPool;
    private final int poolSize;

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

    public static synchronized PlaywrightBrowserPool getInstance(int poolSize) {
        if (instance == null) {
            instance = new PlaywrightBrowserPool(poolSize);
        }
        return instance;
    }

    public Browser borrowBrowser() throws InterruptedException {
        return browserPool.poll(30, TimeUnit.SECONDS);
    }

    public void returnBrowser(Browser browser) {
        browserPool.offer(browser);
    }

    public void shutdown() {
        Browser browser;
        while ((browser = browserPool.poll()) != null) {
            browser.close();
        }
        playwright.close();
    }
}