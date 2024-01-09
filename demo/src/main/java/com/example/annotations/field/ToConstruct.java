package com.example.annotations.field;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**Annotation: marks field to be initialized in the generated constructor. Fields with same id values are included in same constructors. Field can be included in more than 1 constructors
 * @author vlad-avr
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.FIELD)
public @interface ToConstruct {
    //Set ids for constructor in which this field should be included
    int[] id() default {0};
}