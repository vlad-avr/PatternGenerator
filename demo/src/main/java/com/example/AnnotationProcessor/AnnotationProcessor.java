package com.example.annotationProcessor;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import com.example.annotations.Factory;
import com.example.annotations.Singleton;

public class AnnotationProcessor extends AbstractProcessor {

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new HashSet<>();
        annotations.add("com.example.annotations.Singleton");
        annotations.add("com.example.annotations.Factory");
        // Add more annotation types as needed
        return annotations;
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        System.out.println("\nPROCESSOR WORKING!");
        for (Element element : roundEnv.getElementsAnnotatedWith(Singleton.class)) {
            // Process elements annotated with @Singleton
            System.out.println("Found class annotated with @Singleton: " + element);
        }

        for (Element element : roundEnv.getElementsAnnotatedWith(Factory.class)) {
            // Process elements annotated with @Factory
            System.out.println("Found class annotated with @Factory: " + element);
        }

        return true;
    }

}
