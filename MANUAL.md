# Руководство по использованию фреймворка Playwright TestNG

## Содержание

1. [Установка и настройка](#1-установка-и-настройка)
2. [Создание первого теста](#2-создание-первого-теста)
3. [Модель Page Object](#3-модель-page-object)
4. [Компоненты страниц](#4-компоненты-страниц)
5. [Параметризация тестов](#5-параметризация-тестов)
6. [Конфигурация](#6-конфигурация)
7. [Запуск тестов](#7-запуск-тестов)
8. [Отчеты и логи](#8-отчеты-и-логи)
9. [Аннотации фреймворка](#9-аннотации-фреймворка)
10. [Утилитные классы](#10-утилитные-классы)
11. [Лучшие практики](#11-лучшие-практики)

## 1. Установка и настройка

### Предварительные требования

- Java 21 или выше
- Maven
- IntelliJ IDEA или другая IDE

### Добавление зависимостей

Добавьте следующие зависимости в `pom.xml`:

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
    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-api</artifactId>
        <version>2.0.5</version>
    </dependency>
    <dependency>
        <groupId>ch.qos.logback</groupId>
        <artifactId>logback-classic</artifactId>
        <version>1.4.7</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>io.qameta.allure</groupId>
        <artifactId>allure-testng</artifactId>
        <version>2.20.1</version>
    </dependency>
    <dependency>
        <groupId>com.google.code.gson</groupId>
        <artifactId>gson</artifactId>
        <version>2.10.1</version>
    </dependency>
    <dependency>
        <groupId>org.apache.velocity</groupId>
        <artifactId>velocity-engine-core</artifactId>
        <version>2.3</version>
    </dependency>
    <dependency>
        <groupId>org.aspectj</groupId>
        <artifactId>aspectjrt</artifactId>
        <version>1.9.19</version>
    </dependency>
    <dependency>
        <groupId>org.aspectj</groupId>
        <artifactId>aspectjweaver</artifactId>
        <version>1.9.19</version>
    </dependency>
</dependencies>
```

### Установка браузеров Playwright

После добавления зависимостей выполните команду:

```
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install"
```

Эта команда установит все браузеры, необходимые для работы Playwright.

### Структура проекта

Рекомендуемая структура проекта:

```
src/
├── main/
│   ├── java/
│   │   └── ваш.пакет/
│   │       ├── annotations/      # Аннотации фреймворка
│   │       ├── aspects/          # Аспекты для AOP
│   │       ├── components/       # Компоненты страниц
│   │       ├── core/             # Ядро фреймворка
│   │       ├── listeners/        # Слушатели TestNG
│   │       ├── pages/            # Объекты страниц
│   │       └── utils/            # Утилиты
│   └── resources/
│       └── playwright.properties # Настройки
├── test/
│   ├── java/
│   │   └── ваш.пакет/
│   │       └── tests/            # Тестовые классы
│   └── resources/
│       └── testdata/             # Тестовые данные
└── testng.xml                    # Конфигурация TestNG
```

## 2. Создание первого теста

### Базовая структура теста

```java
package ваш.пакет.tests;

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

### Жизненный цикл теста

1. `@BeforeClass` в `PlaywrightBaseTest` инициализирует Playwright, Browser, BrowserContext и Page
2. Система внедрения зависимостей создает объекты страниц для тестовых методов
3. Выполняются тестовые методы с внедренными зависимостями
4. `@AfterMethod` делает скриншоты и сохраняет трассировки в случае ошибок
5. `@AfterClass` закрывает Browser и Playwright, освобождая ресурсы

## 3. Модель Page Object

### Создание класса страницы

```java
package ваш.пакет.pages;

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
package ваш.пакет.pages;

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

### Что такое компоненты

Компоненты - это повторно используемые части пользовательского интерфейса (навигационное меню, таблицы, формы), которые могут встречаться на разных страницах. Использование компонентов позволяет избежать дублирования кода и упрощает поддержку тестов.

### Создание компонента

```java
package ваш.пакет.components;

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
package ваш.пакет.pages;

import com.microsoft.playwright.Page;
import exp.pages.BasePage;
import ваш.пакет.components.NavigationMenu;

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
    String[] parts = userData.split(",");
    String username = parts[0];
    String password = parts[1];
    
    loginPage.navigateToLogin();
    loginPage.login(username, password);
    assertTrue(loginPage.isLoggedIn());
}
```

### Использование DataProvider

Стандартный механизм TestNG DataProvider также поддерживается:

```java
@DataProvider(name = "loginData")
public Object[][] provideLoginData() {
    return new Object[][] {
        {"user1", "pass1"},
        {"user2", "pass2"},
        {"user3", "pass3"}
    };
}

@Test(dataProvider = "loginData")
public void testLogin(String username, String password, LoginPage loginPage) {
    loginPage.navigateToLogin();
    loginPage.login(username, password);
    assertTrue(loginPage.isLoggedIn());
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

# Настройки пользовательского агента
userAgent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.102 Safari/537.36

# Настройки локализации
locale=ru-RU
timezone=Europe/Moscow

# Другие настройки
```

### Поддерживаемые параметры

- `browser` - тип браузера (chromium, firefox, webkit)
- `headless` - запуск в режиме без интерфейса (true/false)
- `slowMo` - замедление действий в мс (полезно при отладке)
- `screenshot.onFailure` - делать скриншот при ошибке (true/false)
- `trace.onFailure` - сохранять трассировку при ошибке (true/false)
- `viewport.width` и `viewport.height` - размеры окна браузера
- `userAgent` - пользовательский агент
- `locale` - локаль (например, ru-RU)
- `timezone` - часовой пояс
- `recordVideo` - записывать видео (true/false)
- `geolocation.latitude` и `geolocation.longitude` - координаты для геолокации
- `permissions` - разрешения (например, geolocation,notifications)

### Программное изменение настроек

```java
import exp.core.PlaywrightConfig;

// В начале теста
PlaywrightConfig.getInstance().setProperty("browser", "firefox");
PlaywrightConfig.getInstance().setProperty("headless", "true");
```

## 7. Запуск тестов

### Через Maven

```
mvn clean test
```

### Запуск конкретного теста

```
mvn clean test -Dtest=HomePageTest
```

### Через TestNG в IDE

1. Создайте конфигурацию запуска TestNG
2. Укажите файл `testng.xml`
3. Запустите конфигурацию

### Настройка testng.xml

```xml
<!DOCTYPE suite SYSTEM "https://testng.org/testng-1.0.dtd">
<suite name="PlaywrightTestSuite" parallel="classes" thread-count="3">
    <!-- Регистрация слушателей -->
    <listeners>
        <listener class-name="exp.listeners.PlaywrightPageInjector"/>
        <listener class-name="exp.listeners.PlaywrightTestFactory"/>
    </listeners>

    <!-- Определение тестов -->
    <test name="HomePage Tests">
        <classes>
            <class name="ваш.пакет.tests.HomePageTest"/>
            <!-- Другие тестовые классы -->
        </classes>
    </test>
</suite>
```

### Параллельное выполнение

Настройте параллельное выполнение в файле `testng.xml`:

```xml
<suite name="PlaywrightTestSuite" parallel="classes" thread-count="3">
    <!-- ... -->
</suite>
```

Параметры параллельного запуска:
- `parallel="classes"` - параллельно запускаются тестовые классы
- `parallel="methods"` - параллельно запускаются тестовые методы
- `thread-count="3"` - количество параллельных потоков

## 8. Отчеты и логи

### Скриншоты и трассировки

При ошибках автоматически создаются:

- Скриншоты в директории `screenshots/`
- Трассировки в директории `traces/`

Пример работы с трассировками:

1. Откройте трассировку через инструмент Playwright:
   ```
   npx playwright show-trace traces/trace_testName_20250516_123456.zip
   ```

2. В открывшемся браузере вы увидите подробную информацию о выполнении теста, включая скриншоты, действия, сетевые запросы и т.д.

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

## 9. Аннотации фреймворка

### @UsePage

Указывает, что тест использует Page Objects:

```java
@UsePage
public class HomePageTest extends PlaywrightBaseTest {
    // тесты...
}
```

Можно указать кастомную фабрику:

```java
@UsePage(CustomPageFactory.class)
public void testWithCustomFactory(HomePage homePage) {
    // код теста...
}
```

### @PageParam

Настраивает параметры страницы:

```java
@Test
public void testLoginPage(@PageParam(url = "https://example.com/login", navigate = true) LoginPage loginPage) {
    // Страница уже открыта по указанному URL
    loginPage.login("admin", "password");
}
```

### @TestData

Для параметризации тестов:

```java
@Test
@TestData(data = {"user1,pass1", "user2,pass2"})
public void testMultipleUsers(String userData, LoginPage loginPage) {
    // код теста...
}
```

### @Retry

Для повторного запуска тестов при неудаче:

```java
@Test
@Retry(maxRetries = 3)
public void testUnstableFeature() {
    // Тест будет повторен до 3 раз в случае неудачи
}
```

## 10. Утилитные классы

### AssertUtil

Утилиты для проверок:

```java
// Проверка видимости элемента
AssertUtil.assertVisible(page.locator(".welcome-message"));

// Проверка текста элемента
AssertUtil.assertText(page.locator(".header"), "Добро пожаловать");

// Проверка URL
AssertUtil.assertUrlContains(page, "/dashboard");
```

### SafeActions

Безопасное выполнение действий с обработкой исключений:

```java
// Действие без возвращаемого значения
SafeActions.execute(() -> page.click(".submit-button"), 
                   "Не удалось нажать на кнопку отправки");

// Действие с возвращаемым значением
String text = SafeActions.execute(() -> page.locator(".message").textContent(),
                                 "Не удалось получить текст сообщения");
```

### WaitUtil

Утилиты для ожидания:

```java
// Ожидание появления элемента
WaitUtil.waitForElementVisible(page, ".dashboard-widget", 5000);

// Ожидание перехода на определенный URL
WaitUtil.waitForNavigation(page, "https://example.com/dashboard", 10000);

// Ожидание пользовательского условия
WaitUtil.waitForCondition(() -> page.locator(".balance").textContent().contains("$"), 5000);
```

### DataManager

Утилиты для работы с тестовыми данными:

```java
// Загрузка объекта из JSON
Map<String, Object> userData = DataManager.loadJsonData("src/test/resources/data/user.json");
String username = (String) userData.get("username");

// Загрузка массива из JSON
List<Map<String, Object>> usersList = DataManager.loadJsonArray("src/test/resources/data/users.json");

// Загрузка текстового файла
String template = DataManager.loadTextData("src/test/resources/templates/email.txt");
```

### TestDataGenerator

Генерация тестовых данных:

```java
// Генерация email
String email = TestDataGenerator.generateRandomEmail();

// Генерация имени пользователя
String username = TestDataGenerator.generateRandomUsername();

// Генерация пароля
String password = TestDataGenerator.generateRandomPassword();
```

## 11. Лучшие практики

### Организация тестов

1. **Один тест - одна проверка**:
   - Каждый тест должен проверять одну функциональность или сценарий
   - Избегайте создания "мега-тестов", проверяющих много функций сразу

2. **Независимость тестов**:
   - Тесты не должны зависеть друг от друга
   - Каждый тест должен сам создавать и настраивать свое окружение

3. **Говорящие имена**:
   - Используйте понятные и информативные имена для тестов
   - Пример: `testSuccessfulLoginWithValidCredentials()`

### Организация Page Objects

1. **Разделение ответственности**:
   - Page Objects отвечают только за взаимодействие с интерфейсом
   - Тесты содержат логику тестирования и проверки

2. **Инкапсуляция локаторов**:
   - Все локаторы элементов должны быть скрыты внутри Page Objects
   - Не используйте локаторы напрямую в тестах

3. **Бизнес-методы**:
   - Создавайте методы, отражающие бизнес-операции пользователя
   - Пример: `login()`, `submitOrder()`, `addToCart()`

### Стабильные тесты

1. **Используйте ожидания**:
   - Применяйте `WaitUtil` для синхронизации с UI
   - Избегайте жестких задержек (Thread.sleep)

2. **Стабильные локаторы**:
   - Используйте id, data-testid или другие стабильные атрибуты
   - Избегайте локаторов на основе индексов или CSS-стилей

3. **Повторные запуски для нестабильных тестов**:
   - Используйте аннотацию `@Retry` для нестабильных тестов
   - Анализируйте причины нестабильности

### Эффективная отладка

1. **Используйте логирование**:
   - Логируйте важные действия и состояния
   - Устанавливайте подходящий уровень логирования

2. **Анализируйте трассировки**:
   - Используйте трассировки Playwright для детального анализа
   - Смотрите снимки экрана в моменты выполнения действий

3. **Скриншоты при ошибках**:
   - Анализируйте скриншоты, созданные при сбоях
   - Добавляйте дополнительные скриншоты в ключевых точках теста

### Поддерживаемость кода

1. **Компоненты для повторяющихся элементов**:
   - Создавайте компоненты для навигационных меню, форм, таблиц и т.д.
   - Переиспользуйте компоненты на разных страницах

2. **Базовые классы**:
   - Наследуйте страницы от `BasePage`
   - Создавайте свои базовые классы для общей функциональности

3. **Конфигурация через свойства**:
   - Выносите настройки в `playwright.properties`
   - Используйте программную конфигурацию для особых случаев
