package com.example.annotationProcessor;

import java.util.ArrayList;
import java.util.Arrays;
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
import javax.lang.model.type.TypeMirror;

import com.example.annotations.type.Factory;
import com.example.annotations.type.Singleton;
import com.example.codeFactory.PatternFactory;

public class AnnotationProcessor extends AbstractProcessor {

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new HashSet<>();
        annotations.add("com.example.annotations.type.Singleton");
        annotations.add("com.example.annotations.type.Factory");
        annotations.add("com.example.annotations.type.Decorator");
        annotations.add("com.example.annotations.method.ToOverride");
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

        processSingleton(roundEnv.getElementsAnnotatedWith(Singleton.class));
        processFactory(roundEnv.getElementsAnnotatedWith(Factory.class));

        return true;
    }



    private void processSingleton(Set<? extends Element> annotations){
        for (Element element : annotations) {
            System.out.println("Found class annotated with @Singleton: " + element);
            if (element instanceof TypeElement) {
                // If the element is a class
                TypeElement typeElement = (TypeElement) element;
                boolean threadSafe = element.getAnnotation(Singleton.class).threadSafe();
                String curPackage = element.getAnnotation(Singleton.class).pkg();
                PatternFactory.makeSingleton(typeElement, threadSafe, curPackage);
            } else {
                // Handle the case where the element is a package (or other non-class element)
                System.out.println("Element is not a class. Skipping.");
            }
        }
    }

    private void processFactory(Set<? extends Element> annotations){
        Map<String, Integer> factoryMap = new HashMap<>();
        List<List<TypeElement>> factories = new ArrayList<>();
        String curPackage = null;
        for(Element element : annotations){
            TypeElement typeElement = (TypeElement) element;
            String factoryId = typeElement.getAnnotation(Factory.class).id();
            String tmp = typeElement.getAnnotation(Factory.class).pkg();
            if(!tmp.equals("-")){
                curPackage = tmp;
            }
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
            for(List<TypeElement> factoryElems : factories){
                TypeElement parentElem = getCommonParent(factoryElems);
                PatternFactory.makeFactory(parentElem, factoryElems, curPackage);
            }
        }
    }

    private TypeElement getCommonParent(List<TypeElement> elements){
        if(elements == null || elements.size() == 0){
            return null;
        }
        List<TypeMirror> curParentTree = getParentTree(elements.get(0).asType());
        for(int i = 1; i < elements.size(); i++){
            curParentTree = getCommonParent(curParentTree, elements.get(i).asType());
        }
        return (TypeElement)processingEnv.getTypeUtils().asElement(curParentTree.get(0));
    }

    private List<TypeMirror> getParentTree(TypeMirror clazz){
        List<TypeMirror> tree = new ArrayList<>();
        tree.add(clazz);
        while(!clazz.toString().equals(Object.class.getName())){
            clazz = ((TypeElement)processingEnv.getTypeUtils().asElement(clazz)).getSuperclass();
            tree.add(clazz);
        }
        return tree;
    }

    private List<TypeMirror> getCommonParent(List<TypeMirror> curParentTree, TypeMirror el2){
        List<TypeMirror> otherClassTree = getParentTree(el2);
        for(int i = 0; i < curParentTree.size(); i++){
            for(int j = 0; j < otherClassTree.size(); j++){
                if(curParentTree.get(i).equals(otherClassTree.get(j))){
                    return curParentTree.subList(i, curParentTree.size());
                }
            }
        }
        return Arrays.asList(processingEnv.getElementUtils().getTypeElement(Object.class.getName()).asType());
    }

}
