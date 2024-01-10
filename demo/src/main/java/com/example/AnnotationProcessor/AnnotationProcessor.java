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

import com.example.annotations.field.GetterSetter;
import com.example.annotations.field.ToBuild;
import com.example.annotations.method.ToOverride;
import com.example.annotations.type.Builder;
import com.example.annotations.type.Custom;
import com.example.annotations.type.Decorator;
import com.example.annotations.type.Factory;
import com.example.annotations.type.MakeConstructor;
import com.example.annotations.type.MakeInterface;
import com.example.annotations.type.Singleton;
import com.example.annotations.type.Snippet;
import com.example.codeFactory.CodeCustomiser;
import com.example.codeFactory.PatternFactory;

/**Processor that processes annotations
 * @author vlad-avr
 */
public class AnnotationProcessor extends AbstractProcessor {
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new HashSet<>();
        //Adding supported annotations
        annotations.add("com.example.annotations.type.Singleton");
        annotations.add("com.example.annotations.type.Factory");
        annotations.add("com.example.annotations.type.Decorator");
        annotations.add("com.example.annotations.type.MakeInterface");
        annotations.add("com.example.annotations.type.Custom");
        annotations.add("com.example.annotations.type.Snippet");
        annotations.add("com.example.annotations.type.CustomEnum");
        annotations.add("com.example.annotations.type.MakeConstructor");
        annotations.add("com.example.annotations.method.ToOverride");
        annotations.add("com.example.annotations.method.CustomMethod");
        annotations.add("com.example.annotations.field.CustomField");
        annotations.add("com.example.annotations.field.ToBuild");
        annotations.add("com.example.annotations.field.ToConstruct");
        annotations.add("com.example.annotations.field.GetterSetter");
        annotations.add("com.example.annotations.constructor.CustomConstructor");
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
        System.out.println("\nAll elements with @Singleton processed\n");
        processFactory(roundEnv.getElementsAnnotatedWith(Factory.class));
        System.out.println("\nAll elements with @Factory processed\n");
        processDecorator(roundEnv.getElementsAnnotatedWith(Decorator.class));
        System.out.println("\nAll elements with @Decorator processed\n");
        processInterfaceMakers(roundEnv.getElementsAnnotatedWith(MakeInterface.class));
        System.out.println("\nAll elements with @MakeInterface processed\n");
        processMakeConstructor(roundEnv.getElementsAnnotatedWith(MakeConstructor.class));
        System.out.println("\nAll elements with @MakeConstructor processed\n");
        processGetterSetters(roundEnv.getElementsAnnotatedWith(GetterSetter.class));
        System.out.println("\nAll elements with @GetterSetter processed\n");
        processBuilders(roundEnv.getElementsAnnotatedWith(Builder.class));
        System.out.println("\nAll elements with @Builder processed\n");
        processCustom(roundEnv.getElementsAnnotatedWith(Custom.class));
        System.out.println("\nAll elements with @Custom processed\n");
        processSnippet(roundEnv.getElementsAnnotatedWith(Snippet.class));
        System.out.println("\nAll elements with @Snippet processed\n");

        return true;
    }

    /**Processes elements annotated with @GetterSetter
     * 
     * @param elements elements annotated with @GetterSetter
     */
    private void processGetterSetters(Set<? extends Element> elements){
        //For each annotated element
        for (Element element : elements) {
            System.out.println("Found element annotated with @GetterSetter: " + element);
            //Check the type of the element
            if (element instanceof TypeElement) {
                // If the element is a TypeElement
                TypeElement typeElement = (TypeElement) element;
                //Skip if element is an interface
                if (element.getKind().isInterface()) {
                    System.out.println(element + " is an Interface -> unable to process");
                    continue;
                }
                //Skip if element is an Enum
                if (element.getKind() == ElementKind.ENUM) {
                    System.out.println(element + " is an Enum -> unable to process");
                    continue;
                }
                //Get path to class file
                String path = getAbsolutePath(typeElement);
                if(path != null){
                    //Generate getter and setter methods
                    CodeCustomiser.makeGetterSetters(typeElement, getFields(typeElement, null), getMethods(typeElement, null), path);
                }else{
                    System.out.println("Unable to acquire path to the .java file for " + element);
                }
            } else if (element instanceof VariableElement){
                //If the element is a Field
                VariableElement fieldElement = (VariableElement)element;
                //Make list that consists of only this element
                List<VariableElement> singleElemList = new ArrayList<>();
                singleElemList.add(fieldElement);
                //Get the class in which it is enclosed
                Element enclosingElem = element.getEnclosingElement();
                while(!(enclosingElem instanceof TypeElement)){
                    enclosingElem = enclosingElem.getEnclosingElement();
                }
                TypeElement typeElement = (TypeElement) enclosingElem;
                //Get path to class file
                String path = getAbsolutePath(typeElement);
                //If enclosing class already has @GetterSetter annotation -> skip
                if(isAnnotationPresent(typeElement, GetterSetter.class)){
                    System.out.println("Class is already annotated with @GetterSetter -> SKIPPED to avoid duplication");
                    continue;
                }
                if(path != null){
                    //Generate getter and setter methods
                    CodeCustomiser.makeGetterSetters(typeElement, singleElemList, getMethods(typeElement, null), path);
                }else{
                    System.out.println("Unable to acquire path to the .java file for " + typeElement);
                }
            } else{
                // Handle the case where the element is a package (or other non-class element)
                System.out.println("Element is not a class. Skipping.");
            }
        }
    }

    
    /**Processes elements annotated with @MakeConstructor
     * 
     * @param elements elements annotated with @MakeConstructor
     */
    private void processMakeConstructor(Set<? extends Element> elements){
        //For each annotated element
        for (Element element : elements) {
            System.out.println("Found class annotated with @MakeConstructor: " + element);
            if (element instanceof TypeElement) {
                // If the element is a class
                TypeElement typeElement = (TypeElement) element;
                //Skip if element is an interface
                if (element.getKind().isInterface()) {
                    System.out.println(element + " is an Interface -> cannot make Constructor");
                    continue;
                }
                //Skip if element is an Enum
                if (element.getKind() == ElementKind.ENUM) {
                    System.out.println(element + " is an Enum -> cannot make Constructor");
                    continue;
                }
                //Get path to class file
                String path = getAbsolutePath(typeElement);
                if(path != null){
                    //Generate constructors
                    CodeCustomiser.makeConstructors(typeElement, path);
                }else{
                    System.out.println("Unable to acquire path to the .java file for " + element);
                }
            } else {
                // Handle the case where the element is a package (or other non-class element)
                System.out.println("Element is not a class. Skipping.");
            }
        }
    }

    
    /**Processes elements annotated with @Builder
     * 
     * @param elements elements annotated with @Builder
     */
    private void processBuilders(Set<? extends Element> elements){
        //For each annotated element
        for (Element element : elements) {
            System.out.println("Found class annotated with @Builder: " + element);
            if (element instanceof TypeElement) {
                // If the element is a class
                TypeElement typeElement = (TypeElement) element;
                // Skip if element is an interface
                if (element.getKind().isInterface()) {
                    System.out.println(element + " is an Interface -> cannot be built with Builder");
                    continue;
                }
                // Skip if element is an Enum
                if (element.getKind() == ElementKind.ENUM) {
                    System.out.println(element + " is an Enum -> cannot be built with Builder");
                    continue;
                }
                // Get qualified package name from annotation
                String curPackage = element.getAnnotation(Builder.class).pkg();
                // Generate Builder pattern
                PatternFactory.makeBuilderInterface(typeElement, getFields(typeElement, ToBuild.class), curPackage);
            } else {
                // Handle the case where the element is a package (or other non-class element)
                System.out.println("Element is not a class. Skipping.");
            }
        }
    }

    
    /**Processes elements annotated with @MakeInterface
     * 
     * @param elements elements annotated with @MakeInterface
     */
    private void processInterfaceMakers(Set<? extends Element> elements){
        //For each annotated element
        for (Element element : elements) {
            System.out.println("Found class annotated with @MakeInterface: " + element);
            if (element instanceof TypeElement) {
                // If the element is a class
                TypeElement typeElement = (TypeElement) element;
                // Skip if element is an interface
                if (element.getKind().isInterface()) {
                    System.out.println(element + " is an Interface -> cannot make interface from Interface");
                    continue;
                }
                // Skip if element is an Enum
                if (element.getKind() == ElementKind.ENUM) {
                    System.out.println(element + " is an Enum -> cannot be turned into Interface");
                    continue;
                }
                String curPackage = element.getAnnotation(MakeInterface.class).pkg();
                PatternFactory.makeInterface(typeElement, getMethods(typeElement, null), curPackage);
            } else {
                // Handle the case where the element is a package (or other non-class element)
                System.out.println("Element is not a class. Skipping.");
            }
        }
    }

    
    /**Processes elements annotated with @Snippet
     * 
     * @param elements elements annotated with @Snippet
     */
    private void processSnippet(Set<? extends Element> elements){
        //For each annotated element
        for (Element element : elements) {
            System.out.println("Found class annotated with @Snippet: " + element);
            if (element instanceof TypeElement) {
                // If the element is a class
                TypeElement typeElement = (TypeElement) element;
                // Skip if element is an interface
                if (element.getKind().isInterface()) {
                    System.out.println(element + " is an Interface -> @Snippet cannot be used with Interfaces");
                    continue;
                }
                // Skip if element is an Enum
                if (element.getKind() == ElementKind.ENUM) {
                    System.out.println(element + " is an Enum -> @Snippet cannot be used with Enums");
                    continue;
                }
                // Skip if class is an inner class -> inner class parsing is not implemented yet
                if (!(element.getEnclosingElement() instanceof PackageElement)){
                    System.out.println(element + " is a local class defined in another class (snippet generation for local classes is not supported yet) -> SKIPPED");
                    continue;
                }
                String path = getAbsolutePath(typeElement);
                if (path != null) {
                    CodeCustomiser.loadSnippet(typeElement, path);
                }else{
                    System.out.println("Unable to acquire path to the .java file for " + element);
                }
            } else {
                // Handle the case where the element is a package (or other non-class element)
                System.out.println("Element is not a class. Skipping.");
            }
        }
    }

    
    /**Processes elements annotated with @Custom
     * 
     * @param elements elements annotated with @Custom
     */
    private void processCustom(Set<? extends Element> elements) {
        //For each annotated element
        for (Element element : elements) {
            System.out.println("Found class annotated with @Custom: " + element);
            if (element instanceof TypeElement) {
                // If the element is a class
                TypeElement typeElement = (TypeElement) element;
                // Skip if element is an interface
                if (element.getKind().isInterface()) {
                    System.out.println("@Custom annotation cannot be applied to Interfaces");
                    continue;
                }
                // Skip if element is an Enum
                if (element.getKind() == ElementKind.ENUM) {
                    System.out.println(element + " is an Enum -> SKIPPED");
                    continue;
                }
                // Skip if class is an inner class -> inner class parsing is not implemented yet
                if (!(element.getEnclosingElement() instanceof PackageElement)){
                    System.out.println(element + " is a local class defined in another class (snippet generation for local classes is not supported yet) -> SKIPPED");
                    continue;
                }
                // Skip if createSnippet parameter of @Custom annotation is marked as false
                if (!element.getAnnotation(Custom.class).createSnippet()) {
                    System.out.println(element
                            + " is not marked for snippet generation -> SKIPPED (Mark @Custom(createSnippet = true) or just @Custom to generate snippet for this class)");
                    continue;
                }
                // Get path to class file
                String path = getAbsolutePath(typeElement);
                if (path != null) {
                    // Generate code snippet
                    CodeCustomiser.saveSnippet(typeElement, path);
                }else{
                    System.out.println("Unable to acquire path to the .java file for " + element);
                }
            } else {
                // Handle the case where the element is a package (or other non-class element)
                System.out.println("Element is not a class. Skipping.");
            }
        }
    }

    
    /**Processes elements annotated with @Decorator
     * 
     * @param elements elements annotated with @Decorator
     */
    private void processDecorator(Set<? extends Element> elements) {
        for (Element element : elements) {
            System.out.println("Found class annotated with @Decorator: " + element);
            if (element instanceof TypeElement) {
                // If the element is a class
                TypeElement typeElement = (TypeElement) element;
                // Get qualified package name from annotation
                String curPackage = element.getAnnotation(Decorator.class).pkg();
                // Generate Decorator pattern
                PatternFactory.makeDecorator(typeElement, getMethods(typeElement, ToOverride.class), curPackage);
            } else {
                // Handle the case where the element is a package (or other non-class element)
                System.out.println("Element is not a class. Skipping.");
            }
        }
    }

    /**Extracts all methods marked with certain annotation from the class (or just all methods if annotation is null)
     * 
     * @param clazz         Class from where methods are extracted
     * @param annotation    Annotation which marks methods for extraction (set to null to extract all methods)
     * @return
     */
    private List<ExecutableElement> getMethods(TypeElement clazz, Class<? extends Annotation> annotation) {
        List<ExecutableElement> markedMethods = new ArrayList<>();

        // Get all methods of the class
        for (Element enclosedElement : clazz.getEnclosedElements()) {
            //Check if extracted element is a method
            if (enclosedElement.getKind() == ElementKind.METHOD) {
                ExecutableElement methodElement = (ExecutableElement) enclosedElement;

                // Check if the method is annotated with the specified annotation
                if (annotation == null || isAnnotationPresent(methodElement, annotation)) {
                    //Add to results list
                    markedMethods.add(methodElement);
                }
            }
        }

        return markedMethods;
    }

    /**Extracts all fields marked with certain annotation from the class (or just all fields if annotation is null)
     * 
     * @param clazz         Class from where fields are extracted
     * @param annotation    Annotation which marks fields for extraction (set to null to extract all fields)
     * @return
     */
    private List<VariableElement> getFields(TypeElement clazz, Class<? extends Annotation> annotation) {
        List<VariableElement> markedFields = new ArrayList<>();

        // Get all methods of the class
        for (Element enclosedElement : clazz.getEnclosedElements()) {
            //Check if extracted element is a field
            if (enclosedElement.getKind() == ElementKind.FIELD) {
                VariableElement fieldElement = (VariableElement) enclosedElement;

                // Check if the method is annotated with the specified annotation
                if (annotation == null || isAnnotationPresent(fieldElement, annotation)) {
                    //Add to results list
                    markedFields.add(fieldElement);
                }
            }
        }
        return markedFields;
    }

    /**Extracts all constructors marked with certain annotation from the class (or just all constructors if annotation is null)
     * @deprecated
     * @param clazz         Class from where constructors are extracted
     * @param annotation    Annotation which marks constructors for extraction (set to null to extract all constructors)
     * @return
     */
    private List<ExecutableElement> getConstructors(TypeElement clazz, Class<? extends Annotation> annotation) {
        List<ExecutableElement> markedConstructors = new ArrayList<>();

        // Get all methods of the class
        for (Element enclosedElement : clazz.getEnclosedElements()) {
            //Check if extracted element is a constructor
            if (enclosedElement.getKind() == ElementKind.CONSTRUCTOR) {
                ExecutableElement constructorElement = (ExecutableElement) enclosedElement;

                // Check if the constructor is annotated with the specified annotation
                if (annotation == null || isAnnotationPresent(constructorElement, annotation)) {
                    // Add to results list
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

    
    /**Processes elements annotated with @Singleton
     * 
     * @param elements elements annotated with @Singleton
     */
    private void processSingleton(Set<? extends Element> elements) {
        for (Element element : elements) {
            System.out.println("Found class annotated with @Singleton: " + element);
            if (element instanceof TypeElement) {
                // If the element is a class
                TypeElement typeElement = (TypeElement) element;
                // Get thread safety checker
                boolean threadSafe = element.getAnnotation(Singleton.class).threadSafe();
                // Get qaulified package name
                String curPackage = element.getAnnotation(Singleton.class).pkg();
                // Generate Singleton pattern
                PatternFactory.makeSingleton(typeElement, threadSafe, curPackage);
            } else {
                // Handle the case where the element is a package (or other non-class element)
                System.out.println("Element is not a class. Skipping.");
            }
        }
    }

    
    /**Processes elements annotated with @Factory
     * 
     * @param elements elements annotated with @Factory
     */
    private void processFactory(Set<? extends Element> elements) {
        //Initialize map for mapping lists of classes according to factory ids in @Factory annotation
        Map<String, Integer> factoryMap = new HashMap<>();
        List<List<TypeElement>> factories = new ArrayList<>();
        //For each annotated element
        for (Element element : elements) {
            System.out.println("Found class annotated with @Factory: " + element);
            TypeElement typeElement = (TypeElement) element;
            // Get factory id
            String factoryId = typeElement.getAnnotation(Factory.class).id();
            // If map contains this key -> add class to list of classes to include in the Factory
            if (factoryMap.containsKey(factoryId)) {
                factories.get(factoryMap.get(factoryId)).add(typeElement);
            } else {
                // Else -> create new list of classes to produce in the Factory
                List<TypeElement> elems = new ArrayList<>();
                elems.add(typeElement);
                factories.add(elems);
                factoryMap.put(factoryId, factories.size() - 1);
            }
        }
        //Check if there are any classes to make Factories from
        if (factories.size() != 0) {
            // Iterate through each class list
            for (List<TypeElement> factoryElems : factories) {
                // Get closest common class for list of classes
                TypeElement parentElem = getCommonParent(factoryElems);
                // Generate Factory for this list of classes
                PatternFactory.makeFactory(parentElem, factoryElems);
            }
        }
    }

    /** Gets closest common parent class for a list of classes
     * 
     * @param elements List of TypeElements containing classes 
     * @return         Closest common parent for a list of classes
     */
    private TypeElement getCommonParent(List<TypeElement> elements) {
        // Check if there are any elements to find parent class for
        if (elements == null || elements.size() == 0) {
            return null;
        }
        // Set first element as current closest common parent
        List<TypeMirror> curParentTree = getParentTree(elements.get(0).asType());
        // Find closest common parent for each pair of class 
        for (int i = 1; i < elements.size(); i++) {
            curParentTree = getCommonParent(curParentTree, elements.get(i).asType());
        }
        return (TypeElement) processingEnv.getTypeUtils().asElement(curParentTree.get(0));
    }

    /** Returns hierarchy of parent classes as a list starting with class passed as a parameter
     * 
     * @param clazz Class to get hierarchy of
     * @return      Hierarchy of parent classes starting with class passed as a parameter
     */
    private List<TypeMirror> getParentTree(TypeMirror clazz) {
        //Initialize list
        List<TypeMirror> tree = new ArrayList<>();
        //Add starting class
        tree.add(clazz);
        //Go up the hierarchy until Object class is reached (or classes are derived from Object)
        while (!clazz.toString().equals(Object.class.getName())) {
            clazz = ((TypeElement) processingEnv.getTypeUtils().asElement(clazz)).getSuperclass();
            tree.add(clazz);
        }
        return tree;
    }

    /** Gets hierarchy of classes ending with current closest common parent class
     * 
     * @param curParentTree Current closest commom parent class hierarchy
     * @param el2           Class to compare current parent class to
     * @return              New closest common parent class hierarchy
     */
    private List<TypeMirror> getCommonParent(List<TypeMirror> curParentTree, TypeMirror el2) {
        // Get class hierarchy of the class to compare
        List<TypeMirror> otherClassTree = getParentTree(el2);
        // Find its relation to the current closest commo parent class hierarchy
        for (int i = 0; i < curParentTree.size(); i++) {
            for (int j = 0; j < otherClassTree.size(); j++) {
                if (curParentTree.get(i).equals(otherClassTree.get(j))) {
                    return curParentTree.subList(i, curParentTree.size());
                }
            }
        }
        return Arrays.asList(processingEnv.getElementUtils().getTypeElement(Object.class.getName()).asType());
    }

    /** Gets absolute path to class file from TypeElement object
     * 
     * @param element TypeELement that contains class
     * @return        Absolute path to class file
     */
    private String getAbsolutePath(TypeElement element) {
        Filer filer = processingEnv.getFiler();

        try {
            // Construct the path to the source file
            Element enclosingElement = element;
            // Go up the enclosing elements hierarchy until PackageElement is reached or enclosing element becomes null
            while ( enclosingElement != null && !(enclosingElement.getEnclosingElement() instanceof PackageElement)) {
                enclosingElement = enclosingElement.getEnclosingElement();
            }
            // Get package name
            String packageName = "";
            if(enclosingElement != null){    
                packageName = ((PackageElement)enclosingElement.getEnclosingElement()).getQualifiedName().toString();
            }
            // Build file path from package name
            String sourceFilePath = packageName.replace('.', '/') + "/" + enclosingElement.getSimpleName() + ".java";

            // Get the source file for the specified element
            FileObject fileObject = filer.getResource(StandardLocation.SOURCE_PATH, "", sourceFilePath);

            // Get the absolute path
            String absolutePath = fileObject.toUri().getPath().replaceFirst("/", "");
            return absolutePath;

        } catch (IOException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
