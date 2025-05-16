package exp.core;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/**
 * Базовый компонент страницы.
 * <p>
 * Реализует интерфейс PageComponent и предоставляет базовую функциональность
 * для компонентов пользовательского интерфейса, которые могут быть использованы
 * на разных страницах.
 * <p>
 * Компоненты позволяют структурировать код, выделяя повторяющиеся элементы
 * интерфейса (навигационные меню, формы, таблицы и т.д.) в отдельные классы.
 * <p>
 * Пример использования:
 * ```
 * // Определение компонента
 * public class NavigationMenu extends BaseComponent {
 * private String homeButtonSelector = ".home-button";
 * private String profileButtonSelector = ".profile-button";
 * <p>
 * public NavigationMenu(Page page) {
 * super(page, ".navigation-menu");
 * }
 * <p>
 * public void clickHome() {
 * root.locator(homeButtonSelector).click();
 * }
 * <p>
 * public void clickProfile() {
 * root.locator(profileButtonSelector).click();
 * }
 * }
 * <p>
 * // Использование компонента в странице
 * public class DashboardPage extends BasePage {
 * private NavigationMenu navMenu;
 * <p>
 * public DashboardPage(Page page) {
 * super(page);
 * navMenu = new NavigationMenu(page);
 * }
 * <p>
 * public NavigationMenu getNavMenu() {
 * return navMenu;
 * }
 * }
 * <p>
 * // Использование в тесте
 *
 * @Test public void testNavigation(DashboardPage dashboardPage) {
 * dashboardPage.getNavMenu().clickProfile();
 * // Дальнейшие проверки...
 * }
 * ```
 */
public abstract class BaseComponent implements PageComponent {
    /**
     * Объект Playwright Page, на котором находится компонент.
     */
    protected Page page;

    /**
     * Корневой локатор компонента, который определяет контейнер компонента.
     */
    protected Locator root;

    /**
     * Конструктор, принимающий объект Page и селектор корневого элемента.
     *
     * @param page     объект страницы
     * @param selector CSS-селектор корневого элемента компонента
     */
    public BaseComponent(Page page, String selector) {
        this.page = page;
        this.root = page.locator(selector);
    }

    /**
     * Конструктор, принимающий родительский локатор и селектор для поиска внутри него.
     * Позволяет создавать вложенные компоненты.
     *
     * @param parent   родительский локатор
     * @param selector CSS-селектор для поиска внутри родительского элемента
     */
    public BaseComponent(Locator parent, String selector) {
        this.page = parent.page();
        this.root = parent.locator(selector);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPage(Page page) {
        this.page = page;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRoot(Locator root) {
        this.root = root;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page getPage() {
        return page;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Locator getRoot() {
        return root;
    }

    /**
     * Проверяет, видим ли компонент на странице.
     *
     * @return true, если компонент видим, false в противном случае
     */
    public boolean isVisible() {
        return root.isVisible();
    }

    /**
     * Выполняет клик по корневому элементу компонента.
     */
    public void click() {
        root.click();
    }
}