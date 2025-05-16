package exp.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация для настройки параметров страницы при внедрении в тестовые методы.
 * <p>
 * Позволяет задать URL страницы и указать, нужно ли выполнять навигацию на этот URL
 * перед выполнением теста.
 * <p>
 * Пример использования:
 * ```
 *
 * @Test public void testLoginPage(@PageParam(url = "https://example.com/login", navigate = true) LoginPage loginPage) {
 * // Страница уже открыта по указанному URL
 * loginPage.enterCredentials("user", "password");
 * assertTrue(loginPage.isLoggedIn());
 * }
 * ```
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface PageParam {
    /**
     * URL страницы, на которую нужно выполнить навигацию.
     *
     * @return строка с URL или пустая строка, если навигация не требуется
     */
    String url() default "";

    /**
     * Флаг, указывающий, нужно ли выполнять навигацию на указанный URL.
     * Если true, то перед выполнением теста будет выполнен переход на URL, указанный в параметре url.
     *
     * @return true, если нужно выполнить навигацию, false в противном случае
     */
    boolean navigate() default false;
}