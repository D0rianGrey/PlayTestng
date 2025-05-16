package exp.listeners;

import exp.annotations.UsePage;
import org.testng.annotations.ITestAnnotation;
import org.testng.internal.annotations.IAnnotationTransformer;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class PlaywrightTestFactory implements IAnnotationTransformer {

    @Override
    public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
        // Если класс или метод имеет аннотацию UsePage, но не указан dataProvider
        if (testMethod != null &&
                (testMethod.isAnnotationPresent(UsePage.class) || testMethod.getDeclaringClass().isAnnotationPresent(UsePage.class)) &&
                annotation.getDataProvider().isEmpty()) {

            // Установка dataProvider программно
            annotation.setDataProvider("pageObjects");
        }
    }
}