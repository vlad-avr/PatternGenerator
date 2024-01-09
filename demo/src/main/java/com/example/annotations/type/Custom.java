package com.example.annotations.type;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**Annotation: marks class to generate Custom code Snippet from that can be then loaded into another class via @Snippet
 * @author vlad-avr
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface Custom {
    //Set true if you want to update generate code snippet on each build
    boolean update() default false;
    //Set true if you want to generate code snippet from this class
    boolean createSnippet() default true;
    //Set code Snippet name
    String name() default "-";
}
