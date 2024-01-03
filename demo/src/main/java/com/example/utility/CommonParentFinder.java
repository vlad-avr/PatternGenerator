package com.example.utility;

import java.util.List;

import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

public class CommonParentFinder {
    private final Types types;
    private final Elements elements;

    public CommonParentFinder(Types types, Elements elements) {
        this.types = types;
        this.elements = elements;
    }

    public TypeElement findCommonParent(List<TypeElement> typeElements) {
        if (typeElements == null || typeElements.isEmpty()) {
            return null;
        }

        // Get the type mirror for Object class (root of the hierarchy)
        javax.lang.model.type.TypeMirror objectTypeMirror = elements.getTypeElement(Object.class.getCanonicalName()).asType();

        // Find the common parent by iteratively checking the superclass of each class
        javax.lang.model.type.TypeMirror commonParent = typeElements.get(0).asType();
        for (int i = 1; i < typeElements.size(); i++) {
            commonParent = findCommonParent(commonParent, typeElements.get(i).asType());
        }

        // If the common parent is Object, return null (no meaningful common parent)
        if (types.isSameType(commonParent, objectTypeMirror)) {
            return null;
        }

        // Convert the common parent TypeMirror back to TypeElement
        return (TypeElement) types.asElement(commonParent);
    }

    private javax.lang.model.type.TypeMirror findCommonParent(javax.lang.model.type.TypeMirror type1, javax.lang.model.type.TypeMirror type2) {
        while (!types.isSameType(type1, type2)) {
            if (types.isSubtype(type1, type2)) {
                type1 = types.asElement(type1).asType();  // Move up in the hierarchy
            } else {
                type2 = types.asElement(type2).asType();  // Move up in the hierarchy
            }
        }
        return type1;  // type1 and type2 are now the same (common parent)
    }
}
