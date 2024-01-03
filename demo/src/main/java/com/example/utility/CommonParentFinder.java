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
        // Get the erasure of the types to handle generics
        javax.lang.model.type.TypeMirror erasure1 = types.erasure(type1);
        javax.lang.model.type.TypeMirror erasure2 = types.erasure(type2);
    
        // Find the common ancestor of the erasures
        javax.lang.model.type.TypeMirror commonAncestor = findCommonAncestor(erasure1, erasure2);
    
        // Return the common ancestor
        return commonAncestor;
    }
    
    private javax.lang.model.type.TypeMirror findCommonAncestor(javax.lang.model.type.TypeMirror type1, javax.lang.model.type.TypeMirror type2) {
        if (types.isSameType(type1, type2)) {
            return type1;
        }
    
        // Get the direct supertypes of each type
        List<? extends javax.lang.model.type.TypeMirror> supertypes1 = types.directSupertypes(type1);
        List<? extends javax.lang.model.type.TypeMirror> supertypes2 = types.directSupertypes(type2);
    
        // Recursively find the common ancestor in the supertypes
        for (javax.lang.model.type.TypeMirror supertype1 : supertypes1) {
            for (javax.lang.model.type.TypeMirror supertype2 : supertypes2) {
                javax.lang.model.type.TypeMirror commonAncestor = findCommonAncestor(supertype1, supertype2);
                if (commonAncestor != null) {
                    return commonAncestor;
                }
            }
        }
    
        return null; // No common ancestor found
    }
}
