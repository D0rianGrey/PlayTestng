# Фреймворк Playwright TestNG

## Обзор
Playwright TestNG - это мощный фреймворк для автоматизации тестирования веб-приложений, объединяющий возможности Playwright для управления браузерами и TestNG для организации и запуска тестов. Фреймворк предоставляет готовую инфраструктуру для быстрого создания, поддержки и масштабирования автоматизированных тестов.

## Ключевые возможности
- **Подход Page Object Model**: организация кода с четким разделением логики тестов и взаимодействия с UI
- **Встроенная система внедрения зависимостей**: автоматическое управление объектами Page и Page Objects
- **Поддержка всех браузеров Playwright**: Chrome/Chromium, Firefox, WebKit
- **Параметризация тестов**: возможность запуска тестов с разными наборами данных
- **Компонентный подход**: выделение повторяющихся элементов интерфейса в отдельные компоненты
- **Расширенные возможности логирования**: автоматическое логирование действий и ошибок
- **Автоматические скриншоты и трассировки**: при сбоях тестов
- **Интеграция с Allure**: детальные и наглядные отчеты

## Предварительные требования
- Java 21 или выше
- Maven
- IntelliJ IDEA или другая Java IDE

## Быстрый старт

### Установка

1. Добавьте зависимости в `pom.xml`:
```xml
<dependencies>
    <dependency>
        <groupId>com.microsoft.playwright</groupId>
        <artifactId>playwright</artifactId>
        <version>1.51.0</version>
    </dependency>
    <dependency>
        <groupId>org.testng</groupId>
        <artifactId>testng</artifactId>
        <version>7.11.0</version>
    </dependency>
    <!-- Другие зависимости указаны в полном руководстве -->
</dependencies>
```

2. Установите браузеры Playwright:
```
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install"
```

### Создание первого теста

```java
@UsePage
public class FirstTest extends PlaywrightBaseTest {
    
    @Test
    public void testHomePage(HomePage homePage) {
        homePage.navigateToHome();
        assertTrue(homePage.isLoaded(), "Домашняя страница должна быть загружена");
    }
}
```

### Запуск тестов

```
mvn clean test
```

## Документация

Подробное руководство по использованию фреймворка находится в файле [MANUAL.md](MANUAL.md).
