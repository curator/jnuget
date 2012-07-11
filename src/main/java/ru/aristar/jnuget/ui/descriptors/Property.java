package ru.aristar.jnuget.ui.descriptors;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Свойство, подлежащее настройке
 *
 * @author sviridov
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface Property {

    /**
     * Маркер использовать значение по умолчанию
     */
    public static final String DEFAULT_VALUE = "##default";

    /**
     * @return описание свойства
     */
    String description() default DEFAULT_VALUE;
}
