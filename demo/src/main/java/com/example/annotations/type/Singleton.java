package com.example.annotations.type;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**Annotation: marks class to generate Singleton pattern from
 * @author vlad-avr
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface Singleton {
    //Set false if you don't need thread safety in your singleton
    boolean threadSafe() default true;
    //Set Custom package for generated class
    String pkg() default "-";
}
