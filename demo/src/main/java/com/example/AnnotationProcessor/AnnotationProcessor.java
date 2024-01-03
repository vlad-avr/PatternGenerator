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

import com.example.annotations.Factory;
import com.example.annotations.Singleton;
import com.example.codeFactory.PatternFactory;

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
            for(List<TypeElement> factoryElems : factories){
                TypeElement parentElem = getCommonParent(factoryElems);
                PatternFactory.makeFactory(parentElem, factoryElems);
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
        System.out.println("CUR :\n" + curParentTree);
        System.out.println("OTHER :\n" + otherClassTree);
        for(int i = 0; i < curParentTree.size(); i++){
            for(int j = 0; j < otherClassTree.size(); j++){
                if(curParentTree.get(i).equals(otherClassTree.get(j))){
                    return curParentTree.subList(i, curParentTree.size());
                }
            }
        }
        System.out.println("DID NOT FIND");
        return Arrays.asList(processingEnv.getElementUtils().getTypeElement(Object.class.getName()).asType());
    }

}
