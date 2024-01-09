package com.example.annotations.field;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**Annotation: marks field to be included in custom code snippet generated from this class
 * @author vlad-avr
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.FIELD)
public @interface CustomField {
    
}
