
package exp.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Аспект для автоматического логирования методов страниц и тестов.
 * <p>
 * Использует AspectJ для внедрения логирования в методы без изменения их кода.
 * Это позволяет централизованно управлять логированием и обеспечивает единый
 * подход к записи информации о выполнении методов.
 * <p>
 * Аспект логирует:
 * - Вызовы методов в классах страниц (exp.pages.*)
 * - Запуск тестовых методов (с аннотацией @Test)
 * <p>
 * Для работы аспекта необходимо:
 * 1. Добавить зависимости AspectJ в проект
 * 2. Включить обработку аспектов в конфигурации
 * <p>
 * Пример логирования:
 * ```
 * [INFO] Executing page method: LoginPage.login
 * [INFO] Starting test: LoginTest.testSuccessfulLogin
 * ```
 * <p>
 * Преимущества использования аспектов для логирования:
 * - Отделение логики логирования от бизнес-логики
 * - Единообразный формат логов
 * - Возможность включать/отключать логирование централизованно
 * - Исключение дублирования кода логирования
 */
@Aspect
public class LoggingAspect {
    /**
     * Логгер для записи сообщений.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingAspect.class);

    /**
     * Метод, выполняющийся перед вызовом любого метода в классах пакета exp.pages.
     * Логирует имя класса и метода, который будет выполнен.
     * <p>
     * Выражение "execution(* exp.pages.*.*(..))" означает:
     * - * — любой тип возвращаемого значения
     * - exp.pages.*.* — любой класс в пакете exp.pages и любой его метод
     * - (..) — с любыми параметрами
     *
     * @param joinPoint точка соединения, содержащая информацию о выполняемом методе
     */
    @Before("execution(* exp.pages.*.*(..))")
    public void logPageMethodCall(JoinPoint joinPoint) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        LOGGER.info("Executing page method: {}.{}", className, methodName);
    }

    /**
     * Метод, выполняющийся перед вызовом любого тестового метода (с аннотацией @Test).
     * Логирует имя тестового класса и метода, который будет выполнен.
     * <p>
     * Выражение "execution(@org.testng.annotations.Test * *(..))" означает:
     * - @org.testng.annotations.Test — метод должен иметь эту аннотацию
     * - * * — любой класс и любой его метод
     * - (..) — с любыми параметрами
     *
     * @param joinPoint точка соединения, содержащая информацию о выполняемом методе
     */
    @Before("execution(@org.testng.annotations.Test * *(..))")
    public void logTestMethodStart(JoinPoint joinPoint) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        LOGGER.info("Starting test: {}.{}", className, methodName);
    }
}