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

2. Настройте плагины Maven в `pom.xml`:
```xml
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
            <includes>
                <include>**/*Test.java</include>
            </includes>
            <!-- Другие настройки указаны в полном руководстве -->
        </configuration>
    </plugin>

    <plugin>
        <groupId>org.codehaus.mojo</groupId>
        <artifactId>exec-maven-plugin</artifactId>
        <version>3.1.0</version>
        <executions>
            <execution>
                <id>playwright-install</id>
                <phase>none</phase>
                <!-- Важно: фаза none отключает автоматический запуск -->
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
```

3. Установите браузеры Playwright:
```bash
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

### Запуск тестов через консоль

1. Установите браузеры Playwright, если это еще не сделано:
```bash
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install"
```

2. Запуск всех тестов:
```bash
mvn clean test
```

3. Запуск конкретного тестового класса:
```bash
mvn clean test -Dtest=HomePageTest
```

4. Запуск конкретного метода:
```bash
mvn clean test -Dtest=HomePageTest#testHomePage
```

5. Запуск с определенным профилем:
```bash
mvn clean test -P firefox-headless
```

6. Запуск с переопределением системных свойств:
```bash
mvn clean test -Dbrowser=firefox -Dheadless=true
```

## Документация

Подробное руководство по использованию фреймворка находится в файле [MANUAL.md](MANUAL.md).
