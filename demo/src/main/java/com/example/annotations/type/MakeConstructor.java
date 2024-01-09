package com.example.annotations.type;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**Annotation: marks class to generate Constructors for (if class is not marked - contructors will not be generated even though fields are marked with @ToConstruct)
 * @author vlad-avr
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface MakeConstructor {
    
}
