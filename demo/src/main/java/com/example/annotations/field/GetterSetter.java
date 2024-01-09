package com.example.annotations.field;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**Annotation: marks field or class. If field is marked - getter and/or setter function is generated solely for this field. If class is marked - getters and setters according to its marked and unmarked fields 
 * @author vlad-avr
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface GetterSetter {
    //Set false if you don`t want a getter to be generated for marked field
    boolean makeGetter() default true;
    //Set false if you don`t want a setter to be generated for marked field
    boolean makeSetter() default true;
}
