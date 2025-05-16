# Руководство по использованию фреймворка Playwright TestNG

## Введение

Данное руководство поможет вам быстро начать работу с фреймворком Playwright TestNG. Фреймворк предназначен для автоматизации тестирования веб-приложений с использованием Playwright и TestNG.

## Содержание

1. [Установка и настройка](#1-установка-и-настройка)
2. [Создание первого теста](#2-создание-первого-теста)
3. [Модель Page Object](#3-модель-page-object)
4. [Компоненты страниц](#4-компоненты-страниц)
5. [Параметризация тестов](#5-параметризация-тестов)
6. [Конфигурация](#6-конфигурация)
7. [Запуск тестов](#7-запуск-тестов)
8. [Отчеты и логи](#8-отчеты-и-логи)
9. [Лучшие практики](#9-лучшие-практики)

## 1. Установка и настройка

### Предварительные требования

- Java 21 или выше
- Maven
- IntelliJ IDEA или другая IDE

### Добавление зависимостей

Убедитесь, что в файле `pom.xml` присутствуют все необходимые зависимости:

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
    <!-- Другие зависимости... -->
</dependencies>
```

### Установка браузеров Playwright

После добавления зависимостей выполните команду:

```
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install"
```

Эта команда установит все браузеры, необходимые для работы Playwright.

## 2. Создание первого теста

### Базовая структура теста

```java
package org.example.tests;

import exp.annotations.UsePage;
import exp.core.PlaywrightBaseTest;
import exp.pages.HomePage;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

@UsePage
public class MyFirstTest extends PlaywrightBaseTest {
    
    @Test
    public void testHomePage(HomePage homePage) {
        homePage.navigateToHome();
        assertTrue(homePage.isLoaded(), "Домашняя страница должна быть загружена");
    }
}
```

### Элементы теста

- `@UsePage` - аннотация, указывающая, что тест использует Page Objects
- `PlaywrightBaseTest` - базовый класс, от которого наследуются все тесты
- `HomePage` - объект страницы, который будет автоматически внедрен в тест

## 3. Модель Page Object

### Создание класса страницы

```java
package org.example.pages;

import com.microsoft.playwright.Page;
import exp.core.PageObject;

public class LoginPage implements PageObject {
    private Page page;
    
    // Локаторы элементов
    private String usernameInput = "#username";
    private String passwordInput = "#password";
    private String loginButton = "button[type='submit']";
    
    public LoginPage(Page page) {
        this.page = page;
    }
    
    @Override
    public void setPage(Page page) {
        this.page = page;
    }
    
    @Override
    public Page getPage() {
        return page;
    }
    
    // Методы для работы со страницей
    public void navigateToLogin() {
        page.navigate("https://example.com/login");
    }
    
    public void login(String username, String password) {
        page.fill(usernameInput, username);
        page.fill(passwordInput, password);
        page.click(loginButton);
    }
    
    public boolean isLoggedIn() {
        return page.isVisible(".dashboard");
    }
}
```

### Наследование от BasePage

Для упрощения создания страниц рекомендуется наследоваться от `BasePage`:

```java
package org.example.pages;

import com.microsoft.playwright.Page;
import exp.pages.BasePage;

public class DashboardPage extends BasePage {
    
    private String welcomeMessage = ".welcome-message";
    
    public DashboardPage(Page page) {
        super(page);
    }
    
    public void navigateToDashboard() {
        page.navigate("https://example.com/dashboard");
    }
    
    public String getWelcomeMessage() {
        return page.locator(welcomeMessage).textContent();
    }
    
    public boolean isLoaded() {
        return page.url().contains("/dashboard") && page.isVisible(welcomeMessage);
    }
}
```

## 4. Компоненты страниц

### Создание компонента

```java
package org.example.components;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import exp.core.BaseComponent;

public class NavigationMenu extends BaseComponent {
    
    private String homeButton = ".home-btn";
    private String profileButton = ".profile-btn";
    private String logoutButton = ".logout-btn";
    
    public NavigationMenu(Page page) {
        super(page, "nav.main-menu");
    }
    
    public void clickHome() {
        root.locator(homeButton).click();
    }
    
    public void clickProfile() {
        root.locator(profileButton).click();
    }
    
    public void logout() {
        root.locator(logoutButton).click();
    }
}
```

### Использование компонента на странице

```java
package org.example.pages;

import com.microsoft.playwright.Page;
import exp.pages.BasePage;
import org.example.components.NavigationMenu;

public class MainPage extends BasePage {
    
    private NavigationMenu navMenu;
    
    public MainPage(Page page) {
        super(page);
        navMenu = new NavigationMenu(page);
    }
    
    public NavigationMenu getNavMenu() {
        return navMenu;
    }
    
    // Другие методы страницы...
}
```

### Использование компонента в тесте

```java
@Test
public void testNavigation(MainPage mainPage) {
    mainPage.navigateToHome();
    
    // Используем компонент
    mainPage.getNavMenu().clickProfile();
    
    // Проверки...
}
```

## 5. Параметризация тестов

### Использование аннотации TestData

```java
@Test
@TestData(data = {"user1,pass1", "user2,pass2", "user3,pass3"})
public void testMultipleUsers(String userData, LoginPage loginPage) {
    String[] parts = userData.split(",");
    String username = parts[0];
    String password = parts[1];
    
    loginPage.navigateToLogin();
    loginPage.login(username, password);
    assertTrue(loginPage.isLoggedIn(), "Пользователь " + username + " должен войти в систему");
}
```

### Использование файла с данными

```java
@Test
@TestData(dataFile = "src/test/resources/testdata/users.txt")
public void testUsersFromFile(String userData, LoginPage loginPage) {
    // Каждая строка файла будет передана в параметр userData
    // ...
}
```

## 6. Конфигурация

### Настройка файла playwright.properties

Создайте файл `src/main/resources/playwright.properties` с настройками:

```properties
# Настройки браузера
browser=chromium
headless=false
slowMo=100

# Настройки скриншотов и трассировок
screenshot.onFailure=true
trace.onFailure=true

# Настройки вьюпорта
viewport.width=1280
viewport.height=720

# Другие настройки...
```

### Программное изменение настроек

```java
import exp.PlaywrightConfig;

// В начале теста
PlaywrightConfig.getInstance().setProperty("browser", "firefox");
PlaywrightConfig.getInstance().setProperty("headless", "true");
```

## 7. Запуск тестов

### Через Maven

```
mvn clean test
```

### Через TestNG в IDE

1. Создайте конфигурацию запуска TestNG
2. Укажите файл `testng.xml`
3. Запустите конфигурацию

### Параллельное выполнение

Настройте параллельное выполнение в файле `testng.xml`:

```xml
<suite name="PlaywrightTestSuite" parallel="classes" thread-count="3">
    <!-- ... -->
</suite>
```

## 8. Отчеты и логи

### Скриншоты и трассировки

При ошибках автоматически создаются:

- Скриншоты в директории `screenshots/`
- Трассировки в директории `traces/`

### Логирование

Используйте `TestLogger` для логирования:

```java
import exp.utils.TestLogger;

// В коде теста или страницы
TestLogger.LOGGER.info("Выполняется важное действие");
TestLogger.LOGGER.debug("Значение переменной: {}", someValue);
```

### Allure отчеты

Для генерации отчетов Allure:

1. Запустите тесты с помощью Maven:
   ```
   mvn clean test
   ```

2. Сгенерируйте отчет:
   ```
   mvn allure:report
   ```

3. Отчет будет доступен в директории `target/site/allure-maven-plugin/`

## 9. Лучшие практики

### Структура проекта

```
src/
├── main/
│   ├── java/
│   │   └── org/example/
│   │       ├── components/       # Компоненты страниц
│   │       ├── pages/            # Объекты страниц
│   │       └── utils/            # Утилиты
│   └── resources/
│       └── playwright.properties # Настройки
├── test/
│   ├── java/
│   │   └── org/example/
│   │       └── tests/            # Тестовые классы
│   └── resources/
│       └── testdata/             # Тестовые данные
└── testng.xml                    # Конфигурация TestNG
```

### Советы по написанию тестов

1. **Используйте Page Objects**:
   - Все взаимодействия со страницей должны быть в классах страниц
   - Избегайте прямого доступа к элементам в тестах

2. **Выделяйте компоненты**:
   - Создавайте компоненты для повторяющихся элементов интерфейса
   - Переиспользуйте компоненты на разных страницах

3. **Используйте ожидания**:
   - Применяйте класс `WaitUtil` для ожидания элементов и состояний
   - Избегайте жестких задержек (Thread.sleep)

4. **Безопасное выполнение действий**:
   - Используйте класс `SafeActions` для обработки исключений
   - Предоставляйте информативные сообщения об ошибках

5. **Эффективная параметризация**:
   - Используйте аннотацию `@TestData` для множественных прогонов
   - Храните тестовые данные в отдельных файлах

6. **Проверки и утверждения**:
   - Используйте класс `AssertUtil` для удобных проверок
   - Добавляйте информативные сообщения к проверкам

## Заключение

Данное руководство охватывает основные аспекты работы с фреймворком Playwright TestNG. Для более подробной информации обратитесь к документации классов и примерам в исходном коде.

Удачного тестирования!