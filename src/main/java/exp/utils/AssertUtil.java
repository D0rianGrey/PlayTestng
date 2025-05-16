package exp.utils;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.assertions.LocatorAssertions;
import com.microsoft.playwright.assertions.PageAssertions;
import com.microsoft.playwright.assertions.PlaywrightAssertions;

import java.util.regex.Pattern;

public class AssertUtil {

    public static void assertVisible(Locator locator) {
        PlaywrightAssertions.assertThat(locator).isVisible();
    }

    public static void assertNotVisible(Locator locator) {
        PlaywrightAssertions.assertThat(locator).isHidden();
    }

    public static void assertEnabled(Locator locator) {
        PlaywrightAssertions.assertThat(locator).isEnabled();
    }

    public static void assertDisabled(Locator locator) {
        PlaywrightAssertions.assertThat(locator).isDisabled();
    }

    public static void assertChecked(Locator locator) {
        PlaywrightAssertions.assertThat(locator).isChecked();
    }

    public static void assertNotChecked(Locator locator) {
        PlaywrightAssertions.assertThat(locator).isChecked(new LocatorAssertions.IsCheckedOptions().setChecked(false));
    }

    public static void assertText(Locator locator, String expectedText) {
        PlaywrightAssertions.assertThat(locator).hasText(expectedText);
    }

    public static void assertTextContains(Locator locator, String expectedText) {
        PlaywrightAssertions.assertThat(locator).containsText(expectedText);
    }

    public static void assertTextMatches(Locator locator, Pattern pattern) {
        PlaywrightAssertions.assertThat(locator).hasText(pattern);
    }

    public static void assertUrl(Page page, String expectedUrl) {
        PlaywrightAssertions.assertThat(page).hasURL(expectedUrl);
    }

    public static void assertUrlContains(Page page, String urlPart) {
        PlaywrightAssertions.assertThat(page).hasURL(Pattern.compile(".*" + Pattern.quote(urlPart) + ".*"));
    }

    public static void assertTitle(Page page, String expectedTitle) {
        PlaywrightAssertions.assertThat(page).hasTitle(expectedTitle);
    }

    public static void assertTitleContains(Page page, String titlePart) {
        PlaywrightAssertions.assertThat(page).hasTitle(Pattern.compile(".*" + Pattern.quote(titlePart) + ".*"));
    }
}