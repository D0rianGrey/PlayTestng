package com.framework.api.assertions;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.assertions.LocatorAssertions;
import com.microsoft.playwright.assertions.PlaywrightAssertions;

import java.util.regex.Pattern;

/**
 * Утилитный класс с методами для проверки состояний элементов и страниц.
 * <p>
 * Предоставляет удобные обертки над методами assertions из Playwright,
 * упрощая их использование в тестах.
 * <p>
 * Основные категории проверок:
 * - Проверки видимости элементов
 * - Проверки состояния элементов (активен/неактивен, отмечен/не отмечен)
 * - Проверки текста элементов
 * - Проверки URL и заголовка страницы
 * <p>
 * Пример использования:
 * ```
 * public void testLoginForm() {
 * AssertUtil.assertVisible(page.locator("form.login-form"));
 * AssertUtil.assertText(page.locator(".login-header"), "Вход в систему");
 * AssertUtil.assertDisabled(page.locator("button[type='submit']"));
 * <p>
 * // После заполнения формы
 * AssertUtil.assertEnabled(page.locator("button[type='submit']"));
 * page.locator("button[type='submit']").click();
 * <p>
 * // После успешного входа
 * AssertUtil.assertUrlContains(page, "/dashboard");
 * }
 * ```
 */
public class AssertUtil {

    /**
     * Проверяет, что элемент видим на странице.
     *
     * @param locator локатор элемента
     */
    public static void assertVisible(Locator locator) {
        PlaywrightAssertions.assertThat(locator).isVisible();
    }

    /**
     * Проверяет, что элемент не видим на странице.
     *
     * @param locator локатор элемента
     */
    public static void assertNotVisible(Locator locator) {
        PlaywrightAssertions.assertThat(locator).isHidden();
    }

    /**
     * Проверяет, что элемент активен (не имеет атрибута disabled).
     *
     * @param locator локатор элемента
     */
    public static void assertEnabled(Locator locator) {
        PlaywrightAssertions.assertThat(locator).isEnabled();
    }

    /**
     * Проверяет, что элемент неактивен (имеет атрибут disabled).
     *
     * @param locator локатор элемента
     */
    public static void assertDisabled(Locator locator) {
        PlaywrightAssertions.assertThat(locator).isDisabled();
    }

    /**
     * Проверяет, что элемент отмечен (например, checkbox или radio button).
     *
     * @param locator локатор элемента
     */
    public static void assertChecked(Locator locator) {
        PlaywrightAssertions.assertThat(locator).isChecked();
    }

    /**
     * Проверяет, что элемент не отмечен (например, checkbox или radio button).
     *
     * @param locator локатор элемента
     */
    public static void assertNotChecked(Locator locator) {
        PlaywrightAssertions.assertThat(locator).isChecked(new LocatorAssertions.IsCheckedOptions().setChecked(false));
    }

    /**
     * Проверяет, что текст элемента точно соответствует ожидаемому.
     *
     * @param locator      локатор элемента
     * @param expectedText ожидаемый текст
     */
    public static void assertText(Locator locator, String expectedText) {
        PlaywrightAssertions.assertThat(locator).hasText(expectedText);
    }

    /**
     * Проверяет, что текст элемента содержит указанный подстроку.
     *
     * @param locator      локатор элемента
     * @param expectedText ожидаемая подстрока текста
     */
    public static void assertTextContains(Locator locator, String expectedText) {
        PlaywrightAssertions.assertThat(locator).containsText(expectedText);
    }

    /**
     * Проверяет, что текст элемента соответствует указанному регулярному выражению.
     *
     * @param locator локатор элемента
     * @param pattern регулярное выражение
     */
    public static void assertTextMatches(Locator locator, Pattern pattern) {
        PlaywrightAssertions.assertThat(locator).hasText(pattern);
    }

    /**
     * Проверяет, что URL страницы точно соответствует ожидаемому.
     *
     * @param page        объект страницы
     * @param expectedUrl ожидаемый URL
     */
    public static void assertUrl(Page page, String expectedUrl) {
        PlaywrightAssertions.assertThat(page).hasURL(expectedUrl);
    }

    /**
     * Проверяет, что URL страницы содержит указанную подстроку.
     *
     * @param page    объект страницы
     * @param urlPart ожидаемая часть URL
     */
    public static void assertUrlContains(Page page, String urlPart) {
        PlaywrightAssertions.assertThat(page).hasURL(Pattern.compile(".*" + Pattern.quote(urlPart) + ".*"));
    }

    /**
     * Проверяет, что заголовок страницы точно соответствует ожидаемому.
     *
     * @param page          объект страницы
     * @param expectedTitle ожидаемый заголовок
     */
    public static void assertTitle(Page page, String expectedTitle) {
        PlaywrightAssertions.assertThat(page).hasTitle(expectedTitle);
    }

    /**
     * Проверяет, что заголовок страницы содержит указанную подстроку.
     *
     * @param page      объект страницы
     * @param titlePart ожидаемая часть заголовка
     */
    public static void assertTitleContains(Page page, String titlePart) {
        PlaywrightAssertions.assertThat(page).hasTitle(Pattern.compile(".*" + Pattern.quote(titlePart) + ".*"));
    }
}