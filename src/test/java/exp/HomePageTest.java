package exp;

import com.microsoft.playwright.Page;
import exp.pages.GooglePage;
import exp.pages.HomePage;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

@UsePage
public class HomePageTest extends PlaywrightBaseTest {

    @Test(dataProvider = "pageObjects")
    public void testHomePage(HomePage homePage) {
        homePage.navigateToHome();
        assertTrue(homePage.isLoaded(), "Домашняя страница должна быть загружена");
    }

    @Test(dataProvider = "pageObjects")
    public void testDirectPage(Page page) {
        page.navigate("https://example.com");
        assertTrue(page.title().contains("Example Domain"), "Страница должна содержать правильный заголовок");
    }

    @Test(dataProvider = "pageObjects")
    @UsePage(CustomPageFactory.class)
    public void testWithCustomFactory(HomePage homePage) {
        homePage.navigateToHome();
        assertTrue(homePage.isLoaded(), "Домашняя страница должна быть загружена");
    }

    @Test(dataProvider = "pageObjects")
    public void testWithPageFactory(GooglePage page) {
        page.navigateToHome();
    }
}