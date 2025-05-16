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
