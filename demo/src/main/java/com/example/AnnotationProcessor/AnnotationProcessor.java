package com.example.annotationProcessor;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

import com.example.annotations.method.ToOverride;
import com.example.annotations.type.Custom;
import com.example.annotations.type.Decorator;
import com.example.annotations.type.Factory;
import com.example.annotations.type.Singleton;
import com.example.annotations.type.Snippet;
import com.example.codeFactory.CustomSnippetManager;
import com.example.codeFactory.PatternFactory;

public class AnnotationProcessor extends AbstractProcessor {

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new HashSet<>();
        annotations.add("com.example.annotations.type.Singleton");
        annotations.add("com.example.annotations.type.Factory");
        annotations.add("com.example.annotations.type.Decorator");
        annotations.add("com.example.annotations.type.Custom");
        annotations.add("com.example.annotations.type.Enum");
        annotations.add("com.example.annotations.method.ToOverride");
        annotations.add("com.example.annotations.method.CustomMethod");
        annotations.add("com.example.annotations.field.CustomField");
        annotations.add("com.example.annotations.constructor.CustomConstructor");
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
        processDecorator(roundEnv.getElementsAnnotatedWith(Decorator.class));
        processCustom(roundEnv.getElementsAnnotatedWith(Custom.class));
        processSnippet(roundEnv.getElementsAnnotatedWith(Snippet.class));

        return true;
    }

    private void processSnippet(Set<? extends Element> annotations){
        for (Element element : annotations) {
            System.out.println("Found class annotated with @Custom: " + element);
            if (element instanceof TypeElement) {
                // If the element is a class
                TypeElement typeElement = (TypeElement) element;
                if (element.getKind().isInterface()) {
                    System.out.println(element + " is an Interface -> @Snippet cannot be used with Interfaces");
                    return;
                }
                if (element.getKind() == ElementKind.ENUM) {
                    System.out.println(element + " is an Enum -> @Snippet cannot be used with Enums");
                    continue;
                }
                if (!(element.getEnclosingElement() instanceof PackageElement)){
                    System.out.println(element + " is a local class defined in another class (snippet generation for local classes is not supported yet) -> SKIPPED");
                    continue;
                }
                String path = getAbsolutePath(typeElement);
                if (path != null) {
                    CustomSnippetManager.loadSnippet(typeElement, path);
                }
            } else {
                // Handle the case where the element is a package (or other non-class element)
                System.out.println("Element is not a class. Skipping.");
            }
        }
    }

    private void processCustom(Set<? extends Element> annotations) {
        for (Element element : annotations) {
            System.out.println("Found class annotated with @Custom: " + element);
            if (element instanceof TypeElement) {
                // If the element is a class
                TypeElement typeElement = (TypeElement) element;
                if (element.getKind().isInterface()) {
                    System.out.println("@Custom annotation cannot be applied to Interfaces");
                    return;
                }
                if (element.getKind() == ElementKind.ENUM) {
                    System.out.println(element + " is an Enum -> SKIPPED");
                    continue;
                }
                if (!(element.getEnclosingElement() instanceof PackageElement)){
                    System.out.println(element + " is a local class defined in another class (snippet generation for local classes is not supported yet) -> SKIPPED");
                    continue;
                }
                if (!element.getAnnotation(Custom.class).createSnippet()) {
                    System.out.println(element
                            + " is not marked for snippet generation -> SKIPPED (Mark @Custom(createSnippet = true) or just @Custom to generate snippet for this class)");
                    continue;
                }
                String path = getAbsolutePath(typeElement);
                if (path != null) {
                    CustomSnippetManager.saveSnippet(typeElement, path);
                }
            } else {
                // Handle the case where the element is a package (or other non-class element)
                System.out.println("Element is not a class. Skipping.");
            }
        }
    }

    private void processDecorator(Set<? extends Element> annotations) {
        for (Element element : annotations) {
            System.out.println("Found class annotated with @Decorator: " + element);
            if (element instanceof TypeElement) {
                // If the element is a class
                TypeElement typeElement = (TypeElement) element;
                String curPackage = element.getAnnotation(Decorator.class).pkg();
                PatternFactory.makeDecorator(typeElement, getMethods(typeElement, ToOverride.class), curPackage);
            } else {
                // Handle the case where the element is a package (or other non-class element)
                System.out.println("Element is not a class. Skipping.");
            }
        }
    }

    private List<ExecutableElement> getMethods(TypeElement clazz, Class<? extends Annotation> annotation) {
        List<ExecutableElement> markedMethods = new ArrayList<>();

        // Get all methods of the class
        for (Element enclosedElement : clazz.getEnclosedElements()) {
            if (enclosedElement.getKind() == ElementKind.METHOD) {
                ExecutableElement methodElement = (ExecutableElement) enclosedElement;

                // Check if the method is annotated with the specified annotation
                if (isAnnotationPresent(methodElement, annotation)) {
                    markedMethods.add(methodElement);
                }
            }
        }

        return markedMethods;
    }

    private List<VariableElement> getFields(TypeElement clazz, Class<? extends Annotation> annotation) {
        List<VariableElement> markedFields = new ArrayList<>();

        // Get all methods of the class
        for (Element enclosedElement : clazz.getEnclosedElements()) {
            if (enclosedElement.getKind() == ElementKind.FIELD) {
                VariableElement fieldElement = (VariableElement) enclosedElement;

                // Check if the method is annotated with the specified annotation
                if (isAnnotationPresent(fieldElement, annotation)) {
                    markedFields.add(fieldElement);
                }
            }
        }
        return markedFields;
    }

    private List<ExecutableElement> getConstructors(TypeElement clazz, Class<? extends Annotation> annotation) {
        List<ExecutableElement> markedConstructors = new ArrayList<>();

        // Get all methods of the class
        for (Element enclosedElement : clazz.getEnclosedElements()) {
            if (enclosedElement.getKind() == ElementKind.CONSTRUCTOR) {
                ExecutableElement constructorElement = (ExecutableElement) enclosedElement;

                // Check if the method is annotated with the specified annotation
                if (isAnnotationPresent(constructorElement, annotation)) {
                    markedConstructors.add(constructorElement);
                }
            }
        }
        return markedConstructors;
    }

    private boolean isAnnotationPresent(Element element, Class<? extends Annotation> annotationClass) {
        // Get the TypeMirror for the annotation
        TypeMirror annotationTypeMirror = processingEnv.getElementUtils()
                .getTypeElement(annotationClass.getCanonicalName()).asType();

        // Check if the annotation is present on the element
        List<? extends AnnotationMirror> annotationMirrors = element.getAnnotationMirrors();
        for (AnnotationMirror annotationMirror : annotationMirrors) {
            if (processingEnv.getTypeUtils().isSameType(annotationMirror.getAnnotationType(), annotationTypeMirror)) {
                return true;
            }
        }

        return false;
    }

    private void processSingleton(Set<? extends Element> annotations) {
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

    private void processFactory(Set<? extends Element> annotations) {
        Map<String, Integer> factoryMap = new HashMap<>();
        List<List<TypeElement>> factories = new ArrayList<>();
        String curPackage = null;
        for (Element element : annotations) {
            TypeElement typeElement = (TypeElement) element;
            String factoryId = typeElement.getAnnotation(Factory.class).id();
            String tmp = typeElement.getAnnotation(Factory.class).pkg();
            if (!tmp.equals("-")) {
                curPackage = tmp;
            }
            if (factoryMap.containsKey(factoryId)) {
                factories.get(factoryMap.get(factoryId)).add(typeElement);
            } else {
                List<TypeElement> elems = new ArrayList<>();
                elems.add(typeElement);
                factories.add(elems);
                factoryMap.put(factoryId, factories.size() - 1);
            }
        }
        if (factories.size() != 0) {
            for (List<TypeElement> factoryElems : factories) {
                TypeElement parentElem = getCommonParent(factoryElems);
                PatternFactory.makeFactory(parentElem, factoryElems, curPackage);
            }
        }
    }

    private TypeElement getCommonParent(List<TypeElement> elements) {
        if (elements == null || elements.size() == 0) {
            return null;
        }
        List<TypeMirror> curParentTree = getParentTree(elements.get(0).asType());
        for (int i = 1; i < elements.size(); i++) {
            curParentTree = getCommonParent(curParentTree, elements.get(i).asType());
        }
        return (TypeElement) processingEnv.getTypeUtils().asElement(curParentTree.get(0));
    }

    private List<TypeMirror> getParentTree(TypeMirror clazz) {
        List<TypeMirror> tree = new ArrayList<>();
        tree.add(clazz);
        while (!clazz.toString().equals(Object.class.getName())) {
            clazz = ((TypeElement) processingEnv.getTypeUtils().asElement(clazz)).getSuperclass();
            tree.add(clazz);
        }
        return tree;
    }

    private List<TypeMirror> getCommonParent(List<TypeMirror> curParentTree, TypeMirror el2) {
        List<TypeMirror> otherClassTree = getParentTree(el2);
        for (int i = 0; i < curParentTree.size(); i++) {
            for (int j = 0; j < otherClassTree.size(); j++) {
                if (curParentTree.get(i).equals(otherClassTree.get(j))) {
                    return curParentTree.subList(i, curParentTree.size());
                }
            }
        }
        return Arrays.asList(processingEnv.getElementUtils().getTypeElement(Object.class.getName()).asType());
    }

    private String getAbsolutePath(TypeElement element) {
        Filer filer = processingEnv.getFiler();

        try {
            // Get the package name
            // String packageName = "";
            // if (element.getEnclosingElement() instanceof PackageElement) {
            //     packageName = ((PackageElement) element.getEnclosingElement()).getQualifiedName().toString();
            // } else if (element.getEnclosingElement() instanceof TypeElement) {
            //     packageName = ((TypeElement) element.getEnclosingElement()).getQualifiedName().toString();
            // }

            // Construct the path to the source file
            Element enclosingElement = element;
            while (!(enclosingElement.getEnclosingElement() instanceof PackageElement)) {
                enclosingElement = enclosingElement.getEnclosingElement();
            }
            String packageName =  ((PackageElement)enclosingElement.getEnclosingElement()).getQualifiedName().toString();
            String sourceFilePath = packageName.replace('.', '/') + "/" + enclosingElement.getSimpleName() + ".java";
            // if (element.getAnnotation(Custom.class).localClass()) {
            //     sourceFilePath = packageName.replace('.', '/') + "/" + element.getSimpleName() + ".java";
            // } else {
            //     sourceFilePath = packageName.replace('.', '/') + "/" +
            //             element.getEnclosingElement().getSimpleName() + ".java";
            // }

            // Get the source file for the specified element
            FileObject fileObject = filer.getResource(StandardLocation.SOURCE_PATH, "", sourceFilePath);

            // Get the absolute path
            String absolutePath = fileObject.toUri().getPath().replaceFirst("/", "");
            return absolutePath;

        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
