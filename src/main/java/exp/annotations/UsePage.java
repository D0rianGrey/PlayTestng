package exp.annotations;

import exp.DefaultPageFactory;
import exp.core.PageFactory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface UsePage {
    Class<? extends PageFactory> value() default DefaultPageFactory.class;
}