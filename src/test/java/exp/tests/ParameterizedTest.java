package exp.tests;

import com.microsoft.playwright.Page;
import exp.annotations.PageParam;
import exp.annotations.TestData;
import exp.annotations.UsePage;
import exp.core.PlaywrightBaseTest;
import exp.core.TestDataProvider;
import exp.pages.HomePage;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertTrue;

@UsePage
public class ParameterizedTest extends PlaywrightBaseTest {

    @Test
    @TestData(data = {"example.com", "google.com", "github.com"})
    public void testDifferentDomains(String domain, Page page) {

        page.navigate("https://" + domain);

        // Проверяем, что страница загрузилась
        assertTrue(page.title() != null && !page.title().isEmpty());

        // Логируем, какой домен посетили
        System.out.println("Visited domain: " + domain + ", title: " + page.title());
    }

    @Test
    public void testSpecificPage(@PageParam(url = "https://example.com", navigate = true) HomePage homePage) {
        assertTrue(homePage.isLoaded());
    }
}