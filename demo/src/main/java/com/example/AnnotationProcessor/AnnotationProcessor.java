package com.example.annotationProcessor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import com.example.annotations.Factory;
import com.example.annotations.Singleton;
import com.example.codeFactory.PatternFactory;
import com.example.utility.CommonParentFinder;


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
            System.out.println("Found class annotated with @Singleton: " + element);
            if (element instanceof TypeElement) {
                // If the element is a class
                TypeElement typeElement = (TypeElement) element;
                boolean threadSafe = element.getAnnotation(Singleton.class).threadSafe();
                PatternFactory.makeSingleton(typeElement, threadSafe);
            } else {
                // Handle the case where the element is a package (or other non-class element)
                System.out.println("Element is not a class. Skipping.");
            }
        }

        processFactory(roundEnv.getElementsAnnotatedWith(Factory.class));

        return true;
    }

    private void processFactory(Set<? extends Element> annotations){
        Map<String, Integer> factoryMap = new HashMap<>();
        List<List<TypeElement>> factories = new ArrayList<>();
        for(Element element : annotations){
            TypeElement typeElement = (TypeElement) element;
            String factoryId = typeElement.getAnnotation(Factory.class).id();
            if(factoryMap.containsKey(factoryId)){
                factories.get(factoryMap.get(factoryId)).add(typeElement);
            }else{
                List<TypeElement> elems = new ArrayList<>();
                elems.add(typeElement);
                factories.add(elems);
                factoryMap.put(factoryId, factories.size()-1);
            }
        }
        if(factories.size() != 0){
            Types types = processingEnv.getTypeUtils();
            Elements elements = processingEnv.getElementUtils();
            CommonParentFinder finder = new CommonParentFinder(types, elements);
            for(List<TypeElement> factoryElems : factories){
                TypeElement parentElem = finder.findCommonParent(factoryElems);
                PatternFactory.makeFactory(parentElem, factoryElems);
            }
        }
    }

}
