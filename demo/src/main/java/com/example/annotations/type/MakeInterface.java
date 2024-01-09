package com.example.annotations.type;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**Annotation: marks class to generate Interface from
 * @author vlad-avr
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface MakeInterface {
    //Set Custom package for generated class
    String pkg() default "-";
    //Set Custom name for generated class
    String name() default "-";
}
