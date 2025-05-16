package com.framework.internal.factory;

import com.framework.api.pages.PageObject;
import com.microsoft.playwright.Page;

/**
 * Вспомогательный класс для создания объектов страниц с использованием паттерна Builder.
 * <p>
 * Упрощает процесс создания объектов страниц с нужными параметрами,
 * обеспечивая более читаемый и гибкий синтаксис по сравнению с прямым
 * использованием конструкторов или фабрик.
 * <p>
 * Пример использования:
 * ```
 * // Создание страницы
 * LoginPage loginPage = PageBuilder.forPage(LoginPage.class)
 * .withPage(page)
 * .build();
 * <p>
 * // Использование созданной страницы
 * loginPage.login("user", "password");
 * ```
 * <p>
 * Преимущества использования PageBuilder:
 * - Более читаемый синтаксис
 * - Поддержка различных способов создания страниц
 * - Улучшенная обработка ошибок
 * - Возможность расширения для добавления новых параметров
 *
 * @param <T> тип создаваемой страницы (должен реализовывать интерфейс PageObject)
 */
public class PageBuilder<T extends PageObject> {
    /**
     * Класс страницы, объект которой будет создан.
     */
    private final Class<T> pageClass;

    /**
     * Объект Playwright Page для создаваемой страницы.
     */
    private Page page;

    /**
     * Приватный конструктор, принимающий класс страницы.
     * Использует паттерн Static Factory Method для создания экземпляров.
     *
     * @param pageClass класс создаваемой страницы
     */
    private PageBuilder(Class<T> pageClass) {
        this.pageClass = pageClass;
    }

    /**
     * Статический метод-фабрика для создания объекта PageBuilder.
     *
     * @param <T>       тип создаваемой страницы
     * @param pageClass класс создаваемой страницы
     * @return объект PageBuilder для указанного класса
     */
    public static <T extends PageObject> PageBuilder<T> forPage(Class<T> pageClass) {
        return new PageBuilder<>(pageClass);
    }

    /**
     * Устанавливает объект Page для создаваемой страницы.
     *
     * @param page объект Playwright Page
     * @return текущий объект PageBuilder для цепочки вызовов
     */
    public PageBuilder<T> withPage(Page page) {
        this.page = page;
        return this;
    }

    /**
     * Создает объект страницы с указанными параметрами.
     * <p>
     * Пробует различные способы создания:
     * 1. Через конструктор с параметром Page
     * 2. Через пустой конструктор и метод setPage
     *
     * @return созданный объект страницы
     * @throws RuntimeException если не удается создать объект страницы
     */
    public T build() {
        try {
            // Пробуем использовать конструктор с Page
            try {
                return pageClass.getConstructor(Page.class).newInstance(page);
            } catch (NoSuchMethodException e) {
                // Используем пустой конструктор и setPage
                T pageObject = pageClass.getDeclaredConstructor().newInstance();
                pageObject.setPage(page);
                return pageObject;
            }
        } catch (Exception e) {
            throw new RuntimeException("Не удалось создать объект страницы: " + pageClass.getName(), e);
        }
    }
}