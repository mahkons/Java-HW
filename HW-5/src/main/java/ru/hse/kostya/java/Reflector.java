package ru.hse.kostya.java;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;

public class Reflector {

    private static final String indent = "    "; // four spaces
    private static final String unsupportedOperation = " {\n" +
            indent + indent + "throw new UnsupportedOperationException();\n" +
            indent + "}\n";

    public static void printStructure(Class<?> someClass) throws IOException {
        try (var writer = new FileWriter(someClass.getSimpleName() + ".java")) {
            printClass(writer, someClass);
        }
    }

    private static void printClass(FileWriter writer, Class<?> someClass) throws IOException {
        writeClassDeclaration(writer, someClass);
        writer.write(" {\n\n");
        writeClassContent(writer, someClass);
        writer.write("}\n");
    }

    public static void diffClasses(Class<?> a, Class<?> b) {

    }

    private static void writeClassNameWithType(FileWriter writer, Class<?> someClass) throws IOException {
        writer.write(someClass.getSimpleName());
        writeTypesArray(writer, someClass.getTypeParameters(), "<", ", ", ">");
    }

    private static void writeClassDeclaration(FileWriter writer, Class<?> someClass) throws IOException {
        writer.write(Modifier.toString(someClass.getModifiers()));
        writer.write(" class ");
        writeClassNameWithType(writer, someClass);

        writer.write(" extends ");
        writeType(writer,someClass.getGenericSuperclass());
        writeTypesArray(writer, someClass.getGenericInterfaces(), " implements ", ", ", "");
    }

    private static void writeClassContent(FileWriter writer, Class<?> someClass) throws IOException {

        for (Class<?> clazz : someClass.getDeclaredClasses()) {
            printClass(writer, clazz);
            writer.write("\n");
        }
        for (Constructor<?> constructor : someClass.getDeclaredConstructors()) {
            writer.write(indent);
            writeConstructor(writer, constructor);
            writer.write("\n");
        }
        for (Method method : someClass.getDeclaredMethods()) {
            writer.write(indent);
            writeMethod(writer, method);
            writer.write("\n");
        }
        for (Field field : someClass.getDeclaredFields()) {
            writer.write(indent);
            writeField(writer, field);
            writer.write("\n");
        }

    }

    private static Type[] filterUpperBounds(Type[] typeParameters) {
        return Arrays.stream(typeParameters).filter(x -> !(Object.class.equals(x))).toArray(Type[]::new);
    }

    private static void writeType(FileWriter writer, Type type) throws IOException {

        if (type instanceof Class<?>) {
            var clazz = (Class<?>)type;
            writeClassNameWithType(writer, clazz);
        } else if (type instanceof TypeVariable) {
            var typeVariable = (TypeVariable<?>)type;
            writer.write(typeVariable.getName());
            writeTypesArray(writer, filterUpperBounds(typeVariable.getBounds()), " extends ", " & ", "");
        } else if (type instanceof WildcardType) {
            var wildcardType = (WildcardType)type;
            writer.write("?");
            writeTypesArray(writer, filterUpperBounds(wildcardType.getUpperBounds()), " extends ", " & ", "");
            writeTypesArray(writer, wildcardType.getLowerBounds(), " super ", " & ", " ");
        } else if (type instanceof ParameterizedType) {
            var parametrisedType = (ParameterizedType)type;
            writer.write(parametrisedType.getRawType().toString());
            writeTypesArray(writer, parametrisedType.getActualTypeArguments(), "<", ", ", ">");
        } else if (type instanceof GenericArrayType) {
            var genericArrayType = (GenericArrayType)type;
            writeType(writer, genericArrayType.getGenericComponentType());
            writer.write("[]");
        }
    }

    private static void writeTypesArray(FileWriter writer, Type[] typeParameters,
                                        String prefix, String separator, String suffix) throws IOException {
        if (typeParameters.length == 0) {
            return;
        }
        writer.write(prefix);
        for (int i = 0; i < typeParameters.length; i++) {
            writeType(writer, typeParameters[i]);
            if (i != typeParameters.length - 1) {
                writer.write(separator);
            }
        }
        writer.write(suffix);
    }

    private static void writeConstructor(FileWriter writer, Constructor<?> constructor) throws IOException {
        if (constructor.isSynthetic()) {
            return;
        }
        writer.write(Modifier.toString(constructor.getModifiers()) + " ");
        writer.write(constructor.getDeclaringClass().getSimpleName());
        writeMethodOrConstructorParametersAndBody(writer, constructor.getGenericParameterTypes(), constructor.getGenericExceptionTypes());
    }

    private static void writeMethod(FileWriter writer, Method method) throws IOException {
        if (method.isSynthetic()) {
            return;
        }
        writer.write(Modifier.toString(method.getModifiers()) + " ");
        writeType(writer, method.getGenericReturnType());
        writer.write(" ");
        writer.write(method.getName());
        writeMethodOrConstructorParametersAndBody(writer, method.getGenericParameterTypes(), method.getGenericExceptionTypes());
    }

    private static void writeMethodOrConstructorParametersAndBody(FileWriter writer, Type[] genericParameterTypes, Type[] genericExceptionTypes) throws IOException {
        writer.write("(");
        writeTypesArray(writer, genericParameterTypes, "", ", ", "");
        writer.write(")");
        writeTypesArray(writer, genericExceptionTypes, " throws ", ", ", "");
        writer.write(unsupportedOperation);
    }

    private static void writeField(FileWriter writer, Field field) throws IOException {
        if (field.isSynthetic()) {
            return;
        }
        writer.write(Modifier.toString(field.getModifiers()) + " ");
        writeType(writer, field.getGenericType());
        writer.write(" " + field.getName() + ";\n");
    }

}
