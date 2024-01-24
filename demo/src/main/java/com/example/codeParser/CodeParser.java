package com.example.codeParser;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CodeParser {
    public static void main(String[] args) {
        // Specify the path to the Java file you want to parse
        String filePath = "src\\main\\java\\com\\example\\codeFactory\\SnippetLoader.java";

        try {
            // Parse the Java file
            CompilationUnit cu = StaticJavaParser.parse(new File(filePath));

            // Create a visitor to traverse the AST (Abstract Syntax Tree)
            VoidVisitor<Void> classVisitor = new ClassPrinter();
            classVisitor.visit(cu, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // A visitor implementation that prints constructors, methods, their bodies, and
    // fields in the class
    private static class ClassPrinter extends VoidVisitorAdapter<Void> {
        @Override
        public void visit(ConstructorDeclaration n, Void arg) {
            super.visit(n, arg);
            System.out.println("Constructor Name: " + n.getName());

            // Check if the constructor has a body
            if (n.getBody() != null) {
                // Extract and print the constructor body
                System.out.println("Constructor Body:\n" + n.getBody());
            } else {
                System.out.println("Constructor has no body.");
            }

            System.out.println(); // Add a newline for clarity
        }

        @Override
        public void visit(MethodDeclaration n, Void arg) {
            super.visit(n, arg);
            System.out.println("Method Name: " + n.getName());

            // Check if the method has a body
            if (n.getBody().isPresent()) {
                // Extract and print the method body
                System.out.println("Method Body:\n" + n.getBody().get());
            } else {
                System.out.println("Method has no body.");
            }

            System.out.println(); // Add a newline for clarity
        }

        @Override
        public void visit(VariableDeclarator n, Void arg) {
            super.visit(n, arg);

            if (isEnclosedInClass(n)) {
                System.out.println("Field Name: " + n.getName());
                System.out.println("Field Type: " + n.getType());
                System.out.println("Field Initializer: " + n.getInitializer());
                System.out.println(); // Add a newline for clarity
            }
        }

        private boolean isEnclosedInClass(VariableDeclarator variableDeclarator) {
            // Navigate up the AST hierarchy
            Optional<Node> parentNode = variableDeclarator.getParentNode();
            while (parentNode.isPresent()) {
                if (parentNode.get() instanceof ClassOrInterfaceDeclaration) {
                    // VariableDeclarator is enclosed in a class
                    return true;
                } else if (parentNode.get() instanceof MethodDeclaration
                        || parentNode.get() instanceof ConstructorDeclaration) {
                    return false;
                }

                // Move up to the next parent node
                parentNode = parentNode.get().getParentNode();
            }

            // VariableDeclarator is not enclosed in a class
            return false;
        }
    }
}
