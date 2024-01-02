package com.example.annotationProcessor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import com.example.annotations.Factory;
import com.example.annotations.Singleton;

@SupportedAnnotationTypes({"com.example.Annotations.Singleton", "com.example.Annotations.Factory"})
public class AnnotationProcessor extends AbstractProcessor{

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Path path = Paths.get("D:\\Java\\PatternGenerator\\testing_project\\src\\main\\java\\com\\example\\dummy");
        String content = "";
        for (Element element : roundEnv.getElementsAnnotatedWith(Singleton.class)) {
            // Process elements annotated with @Singleton
            content += "\nFound class annotated with @Singleton: " + element;
        }

        for (Element element : roundEnv.getElementsAnnotatedWith(Factory.class)) {
            // Process elements annotated with @Factory
            content += "\nFound class annotated with @Factory: " + element;
        }
        try {
            Files.write(path, content.getBytes());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return true;
    }

}
