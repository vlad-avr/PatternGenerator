package com.example.annotations.type;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**Annotation: marks classes to generate Factory pattern from
 * @author vlad-avr
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface Factory {
    //Set id of the Factory -> classes that annotated with same ids  
    String id();
    String pkg() default "-";
    String option() default "-";
}
