package exp.tests;

import com.microsoft.playwright.Page;
import exp.core.PageParam;
import exp.core.PlaywrightBaseTest;
import exp.core.CustomPageFactory;
import exp.core.UsePage;
import exp.pages.GooglePage;
import exp.pages.HomePage;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

@UsePage
public class HomePageTest extends PlaywrightBaseTest {

    @Test
    public void testHomePage(HomePage homePage) {
        homePage.navigateToHome();
        assertTrue(homePage.isLoaded(), "Домашняя страница должна быть загружена");
    }

    @Test
    public void testDirectPage(Page page) {
        page.navigate("https://example.com");
        assertTrue(page.title().contains("Example Domain"), "Страница должна содержать правильный заголовок");
    }

    @Test
    @UsePage(CustomPageFactory.class)
    public void testWithCustomFactory(HomePage homePage) {
        homePage.navigateToHome();
        assertTrue(homePage.isLoaded(), "Домашняя страница должна быть загружена");
    }

    @Test
    public void testWithPageFactory(GooglePage page) {
        page.navigateToHome();
    }

    @Test
    public void testAnnotatedPage(@PageParam(url = "https://example.com", navigate = true) HomePage homePage) {
        assertTrue(homePage.isLoaded(), "Домашняя страница должна быть загружена");
    }
}