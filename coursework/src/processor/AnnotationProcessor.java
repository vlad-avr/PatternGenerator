package processor;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

@SupportedAnnotationTypes({"annotations.Singleton", "annotations.Factory"})
public class AnnotationProcessor extends AbstractProcessor{


    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        System.out.println("ANNNNNNNOTATIONS!!!");
        for (TypeElement annotation : annotations) {
            for (Element element : roundEnv.getElementsAnnotatedWith(annotation)) {
                // Process the annotated elements
                String annotationName = annotation.getQualifiedName().toString();
                String className = ((TypeElement) element).getQualifiedName().toString();
                System.out.println("Found class with " + annotationName + ": " + className);
            }
        }
        return true;
    }


}