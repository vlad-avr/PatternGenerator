package com.example.AnnotationProcessor;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;

public class AnnotationProcessor extends AbstractProcessor{


    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'process'");
    }


}
