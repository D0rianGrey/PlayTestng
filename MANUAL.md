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
12. [Работа через консоль](#12-работа-через-консоль)
13. [Решение проблем](#13-решение-проблем)

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

### Настройка плагинов Maven

Добавьте следующие плагины в раздел `build` вашего `pom.xml`:

```xml
<build>
   <plugins>
      <plugin>
         <groupId>org.apache.maven.plugins</groupId>
         <artifactId>maven-compiler-plugin</artifactId>
         <version>3.13.0</version>
         <configuration>
            <release>21</release>
         </configuration>
      </plugin>

      <plugin>
         <groupId>org.apache.maven.plugins</groupId>
         <artifactId>maven-surefire-plugin</artifactId>
         <version>3.2.2</version>
         <configuration>
            <!-- Включение тестов по шаблону -->
            <includes>
               <include>**/*Test.java</include>
            </includes>

            <!-- Системные свойства -->
            <systemPropertyVariables>
               <browser>${browser}</browser>
               <headless>${headless}</headless>
            </systemPropertyVariables>

            <!-- Слушатели TestNG -->
            <properties>
               <property>
                  <name>listener</name>
                  <value>
                     com.framework.api.listeners.PlaywrightPageInjector,com.framework.api.listeners.PlaywrightTestFactory
                  </value>
               </property>
            </properties>
         </configuration>
      </plugin>

      <!-- Плагин для установки Playwright -->
      <plugin>
         <groupId>org.codehaus.mojo</groupId>
         <artifactId>exec-maven-plugin</artifactId>
         <version>3.1.0</version>
         <executions>
            <execution>
               <id>playwright-install</id>
               <phase>none</phase>
               <goals>
                  <goal>java</goal>
               </goals>
               <configuration>
                  <mainClass>com.microsoft.playwright.CLI</mainClass>
                  <arguments>
                     <argument>install</argument>
                  </arguments>
               </configuration>
            </execution>
         </executions>
      </plugin>
   </plugins>
</build>
```

### Установка браузеров Playwright

После добавления зависимостей выполните команду:

```bash
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install"
```

Эта команда установит все браузеры, необходимые для работы Playwright.

### Структура проекта

Рекомендуемая структура проекта:

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── framework/
│   │           ├── api/
│   │           │   ├── annotations/     # Аннотации фреймворка
│   │           │   ├── aspects/         # Аспекты для AOP
│   │           │   ├── components/      # Компоненты страниц
│   │           │   ├── config/          # Конфигурации
│   │           │   ├── factory/         # Фабрики страниц
│   │           │   ├── listeners/       # Слушатели TestNG
│   │           │   ├── pages/           # Объекты страниц
│   │           │   ├── testing/         # Базовые классы тестов
│   │           │   └── utils/           # Утилиты
│   │           ├── extentions/          # Расширения фреймворка
│   │           ├── internal/            # Внутренние компоненты
│   │           └── tools/               # Вспомогательные инструменты
│   └── resources/
│       └── playwright.properties        # Настройки
├── test/
│   ├── java/
│   │   └── com/
│   │       └── framework/
│   │           └── tests/               # Тестовые классы
│   └── resources/
│       ├── testdata/                    # Тестовые данные
│       └── testng.xml                   # Конфигурация TestNG
```

## 8. Отчеты и логи

### 8.1 Интеграция с Allure Report

#### Введение в Allure
Allure - это гибкий и легкий инструмент для создания наглядных отчетов о тестировании. Он преобразует результаты тестов в интерактивный веб-отчет, который помогает:
- Визуализировать ход выполнения тестов
- Анализировать причины неудач с помощью скриншотов, логов и трассировок
- Группировать тесты по эпикам, функциям и пользовательским историям
- Отслеживать тенденции через интеграцию с CI/CD

#### Настройка Allure в проекте

Фреймворк уже содержит все необходимые настройки для работы с Allure:

1. Зависимости в `pom.xml`:
```xml
<dependency>
    <groupId>io.qameta.allure</groupId>
    <artifactId>allure-testng</artifactId>
    <version>${allure.version}</version>
</dependency>
<dependency>
    <groupId>io.qameta.allure</groupId>
    <artifactId>allure-java-commons</artifactId>
    <version>${allure.version}</version>
</dependency>
<dependency>
    <groupId>io.qameta.allure</groupId>
    <artifactId>allure-attachments</artifactId>
    <version>${allure.version}</version>
</dependency>
```

2. Настройка плагина Maven для Allure:
```xml
<plugin>
    <groupId>io.qameta.allure</groupId>
    <artifactId>allure-maven</artifactId>
    <version>2.12.0</version>
    <configuration>
        <reportVersion>${allure.version}</reportVersion>
        <resultsDirectory>${project.build.directory}/allure-results</resultsDirectory>
        <reportDirectory>${project.build.directory}/allure-report</reportDirectory>
    </configuration>
</plugin>
```

3. Конфигурация слушателя TestNG в `testng.xml`:
```xml
<listener class-name="com.framework.api.listeners.AllureTestListener"/>
```

#### Использование аннотаций Allure в тестах

Фреймворк поддерживает все основные аннотации Allure для структурирования отчетов:

```java
@Epic("UI Tests")                        // Определяет эпик для группировки тестов
@Feature("Home Page")                    // Определяет функцию в рамках эпика
@Story("Basic Navigation")               // Определяет пользовательскую историю
@Description("Проверка загрузки домашней страницы") // Подробное описание теста
@Severity(SeverityLevel.CRITICAL)        // Устанавливает критичность теста
@Test
public void testHomePage(HomePage homePage) {
    // Код теста
}
```

#### Добавление шагов и вложений к отчетам

Для добавления информации о шагах в тесте используйте аннотацию `@Step`:

```java
@Step("Переход на домашнюю страницу")
public void navigateToHome() {
    page.navigate("https://example.com");
}
```

Для прикрепления вложений используйте класс `AllureAttachmentHelper`:

```java
// Прикрепление скриншота
AllureAttachmentHelper.attachScreenshot(screenshotPath, "Скриншот после действия");

// Прикрепление HTML страницы
AllureAttachmentHelper.attachPageSource(page.content());

// Прикрепление текстового лога
AllureAttachmentHelper.attachText("Лог события", "Произошло важное событие");

// Прикрепление JSON-данных
AllureAttachmentHelper.attachJson(jsonString, "Ответ API");
```

#### Автоматические скриншоты и трассировки при неудаче

Фреймворк автоматически прикрепляет скриншоты и трассировки Playwright к отчетам при неудачном выполнении тестов. Эта функциональность настраивается в `PlaywrightConfig`:

```java
// Включение/отключение скриншотов при неудаче
PlaywrightConfig.getInstance().setProperty("screenshot.onFailure", "true");

// Включение/отключение трассировки при неудаче
PlaywrightConfig.getInstance().setProperty("trace.onFailure", "true");
```

#### Настройка категорий в Allure

Для группировки тестов по типам ошибок используется файл `allure-categories.json`:

```json
[
  {
    "name": "Неудачные тесты",
    "matchedStatuses": [
      "failed"
    ]
  },
  {
    "name": "Ошибки элементов страницы",
    "messageRegex": ".*Expected condition failed.*",
    "matchedStatuses": [
      "failed"
    ]
  }
]
```

#### Генерация и просмотр отчетов Allure

##### Генерация отчета через Maven:

```bash
# Сначала запустите тесты для создания результатов
mvn clean test

# Затем сгенерируйте отчет
mvn allure:report
```

Отчет будет создан в директории `target/allure-report`.

##### Запуск Allure сервера для просмотра отчета:

```bash
mvn allure:serve
```

Эта команда запустит временный веб-сервер и откроет отчет в браузере.

#### Интеграция с CI/CD системами

Для интеграции с Jenkins:

1. Установите плагин Allure для Jenkins
2. Добавьте шаг публикации отчетов в конфигурацию задачи:
```
allure([
    includeProperties: false,
    jdk: '',
    properties: [],
    reportBuildPolicy: 'ALWAYS',
    results: [[path: 'target/allure-results']]
])
```

### 8.2 Настройка параметров Allure

Дополнительные настройки можно указать в файле `allure.properties` в директории `src/test/resources`:

```properties
# Базовая директория для результатов
allure.results.directory=target/allure-results

# Отображение информации о среде выполнения
allure.link.issue.pattern=https://youtrack.company.com/issue/{}
allure.link.tms.pattern=https://testrail.company.com/browse/{}
```

### 8.3 Практические рекомендации по работе с Allure

1. **Используйте аннотацию @Step для всех значимых действий**:
   ```java
   @Step("Вход пользователя {username}")
   public void login(String username, String password) {
       // код метода
   }
   ```

2. **Прикрепляйте скриншоты в ключевых моментах**, а не только при ошибках:
   ```java
   @Step("Проверка отображения профиля")
   public void verifyProfileDisplayed() {
       // проверка
       AllureAttachmentHelper.attachScreenshot(takeSreenshot(), "Профиль пользователя");
   }
   ```

3. **Используйте методы классификации тестов** для лучшей организации отчетов:
   ```java
   @Epic("Регистрация")
   @Feature("Форма регистрации")
   @Story("Валидация полей")
   @Test
   public void testFieldValidation() {
       // тест
   }
   ```

4. **Структурируйте описания тестов** для понимания их цели даже без кода:
   ```java
   @Description("""
   Проверка валидации поля email:
   1. Пустое значение
   2. Неверный формат email
   3. Уже зарегистрированный email
   """)
   @Test
   public void testEmailValidation() {
       // тест
   }
   ```

### 8.4 Логирование

Фреймворк использует SLF4J с Logback для логирования. Базовый класс `TestLogger` предоставляет централизованную точку доступа к логгеру:

```java
TestLogger.LOGGER.info("Выполнение теста: {}", testName);
TestLogger.LOGGER.error("Ошибка при выполнении: {}", e.getMessage());
```

#### Конфигурация логирования

Создайте файл `logback.xml` в директории `src/test/resources` для настройки логирования:

```xml
<configuration>
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <appender name="FILE" class="ch.qos.logback.core.FileAppender">
        <file>logs/test.log</file>
        <append>false</append>
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <root level="INFO">
        <appender-ref ref="CONSOLE" />
        <appender-ref ref="FILE" />
    </root>
</configuration>
```

Логи будут записываться как в консоль, так и в файл `logs/test.log`.

## 12. Работа через консоль

### Установка браузеров Playwright

```bash
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install"
```

Важно выполнить эту команду один раз перед запуском тестов. Обратите внимание, что в конфигурации `pom.xml` мы установили `<phase>none</phase>` для этого плагина, чтобы отключить автоматическую установку во время сборки, так как она может блокировать выполнение тестов.

### Запуск всех тестов

```bash
mvn clean test
```

### Запуск конкретного тестового класса

```bash
mvn clean test -Dtest=HomePageTest
```

### Запуск конкретного метода

```bash
mvn clean test -Dtest=HomePageTest#testHomePage
```

### Запуск с выбором профиля

Maven-профили позволяют быстро переключаться между разными конфигурациями:

```bash
mvn clean test -P firefox-headless
```

### Запуск с переопределением системных свойств

```bash
mvn clean test -Dbrowser=firefox -Dheadless=true
```

### Запуск с подробным логированием

```bash
mvn clean test -X
```

### Пропуск установки браузеров при запуске

Если браузеры уже установлены:

```bash
mvn clean test -Dskip.playwright.install=true
```

### Установка определенного браузера

```bash
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install chromium"
```

### Генерация и просмотр отчетов Allure

Генерация отчета после выполнения тестов:

```bash
mvn allure:report
```

Запуск веб-сервера с отчетом:

```bash
mvn allure:serve
```

## 13. Решение проблем

### Установка браузеров не завершается

Если команда установки браузеров зависает или не завершается:

1. Убедитесь, что у вас есть права на запись в директорию установки
2. Запустите команду с подробным логированием:
   ```bash
   mvn -X exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install"
   ```
3. Попробуйте установить только один браузер:
   ```bash
   mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install chromium"
   ```
4. Проверьте сетевые настройки и прокси

### Тесты не находятся

Если Maven не находит тесты:

1. Убедитесь, что тестовые классы имеют суффикс `Test.java` и находятся в правильной директории
2. Проверьте настройки в `pom.xml`:
   ```xml
   <includes>
       <include>**/*Test.java</include>
   </includes>
   ```
3. Убедитесь, что тестовые классы находятся в директории `src/test/java`
4. Запустите Maven с опцией `-X` для получения подробного лога:
   ```bash
   mvn -X clean test
   ```

### Ошибки слушателей TestNG

1. Проверьте, что пути к слушателям в `pom.xml` соответствуют реальной структуре пакетов
2. Создайте файл `testng.xml` в директории `src/test/resources/` с корректной конфигурацией слушателей
3. Запустите тесты с указанием файла `testng.xml`:
   ```bash
   mvn clean test -DsuiteXmlFile=src/test/resources/testng.xml
   ```

### Ошибки запуска браузера

1. Убедитесь, что браузеры установлены
2. Запустите тесты в headless режиме:
   ```bash
   mvn clean test -Dheadless=true
   ```
3. Проверьте системные требования для браузеров Playwright
4. Запустите с указанием конкретного браузера:
   ```bash
   mvn clean test -Dbrowser=chromium
   ```

### Конфликты зависимостей

1. Запустите Maven с опцией для анализа зависимостей:
   ```bash
   mvn dependency:tree
   ```
2. Проверьте, нет ли конфликтующих версий библиотек
3. При необходимости, исключите конфликтующие зависимости:
   ```xml
   <dependency>
       <groupId>com.some.lib</groupId>
       <artifactId>lib-name</artifactId>
       <version>1.0.0</version>
       <exclusions>
           <exclusion>
               <groupId>org.conflicting</groupId>
               <artifactId>conflicting-lib</artifactId>
           </exclusion>
       </exclusions>
   </dependency>
   ```

### Отладка тестов при запуске через консоль

1. Добавьте логирование в критических точках теста:
   ```java
   TestLogger.LOGGER.info("Важный шаг выполнен: {}", результат);
   ```
2. Настройте уровень логирования в `logback.xml`:
   ```xml
   <logger name="com.framework" level="DEBUG" />
   ```
3. Включите сохранение скриншотов и трассировок для всех тестов, а не только для упавших:
   ```java
   PlaywrightConfig.getInstance().setProperty("screenshot.always", "true");
   ```
4. Используйте инструмент для анализа трассировок Playwright:
   ```bash
   npx playwright show-trace traces/trace_name.zip
   ```

### Проблемы с параллельным запуском

1. Уменьшите количество параллельных потоков:
   ```xml
   <parallel>classes</parallel>
   <threadCount>1</threadCount>
   ```
2. Проверьте потокобезопасность ваших Page Objects
3. Используйте ThreadLocal для хранения контекстно-зависимых данных
4. Отключите параллельный запуск для отладки:
   ```bash
   mvn clean test -DparallelMode=none
   ```

### Проблемы с отчетами Allure

1. Убедитесь, что директория для результатов существует и доступна для записи:
   ```bash
   mkdir -p target/allure-results
   ```
2. Проверьте наличие конфликтов версий библиотек Allure:
   ```bash
   mvn dependency:tree | grep allure
   ```
3. Очистите кэш Allure:
   ```bash
   rm -rf ~/.allure
   ```
4. Проверьте, что слушатель Allure корректно зарегистрирован:
   ```xml
   <listener class-name="com.framework.api.listeners.AllureTestListener"/>
   ```
5. Убедитесь, что тесты запускаются с аспектами AspectJ:
   ```xml
   <argLine>
     -javaagent:"${settings.localRepository}/org/aspectj/aspectjweaver/${aspectj.version}/aspectjweaver-${aspectj.version}.jar"
   </argLine>
   ```
