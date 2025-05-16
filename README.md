# Playwright TestNG Framework

Фреймворк для тестирования веб-приложений с использованием Playwright и TestNG.

## Особенности

- Внедрение зависимостей через аннотации
- Поддержка многопоточного выполнения тестов
- Автоматическая генерация скриншотов и трассировок при ошибках
- Гибкая конфигурация
- Обширные утилиты для тестирования

## Примеры использования

### Простой тест страницы

```java
@UsePage
public class SimplePageTest extends PlaywrightBaseTest {
    
    @Test
    public void testSimplePage(HomePage homePage) {
        homePage.navigateToHome();
        assertTrue(homePage.isLoaded());
    }
}