package com.example.annotations.type;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface Custom {
    boolean update() default false;
    boolean createSnippet() default true;
    String name() default "-";
}
