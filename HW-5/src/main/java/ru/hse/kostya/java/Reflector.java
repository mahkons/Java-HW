package ru.hse.kostya.java;

import org.jetbrains.annotations.NotNull;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class that contains functions able to print class structure
 *      and compare contents of classes.
 */
public class Reflector {

    private static final String indent = "    "; // four spaces
    private static final String linebreak = System.lineSeparator();
    private static final String unsupportedOperation = " {" + linebreak +
            indent + indent + "throw new UnsupportedOperationException();" + linebreak +
            indent + "}" + linebreak;

    /**
     * Writes class structure in a new file in current directory.
     * Name of new file equals to class simple name
     *
     * Writes class constructors, methods, fields, nested and inner classes
     *      declared in that class
     *
     * @throws IOException if any problem occurred during writing to file previously named
     */
    public static void printStructure(@NotNull Class<?> someClass) throws IOException {
        try (var writer = new FileWriter(someClass.getSimpleName() + ".java")) {
            printClass(writer, someClass);
        }
    }

    /**
     * Writes class with declaration and full content.
     */
    private static void printClass(Writer writer, Class<?> someClass) throws IOException {
        writeClassDeclaration(writer, someClass);
        writer.write(" {" + linebreak + linebreak);
        writeClassContent(writer, someClass);
        writer.write("}" + linebreak);
    }

    /**
     * Writes class name with full List of type parameters.
     */
    private static void writeClassNameWithType(Writer writer, Class<?> someClass) throws IOException {
        writer.write(someClass.getSimpleName());
        writeTypesArray(writer, someClass.getTypeParameters(), "<", ", ", ">", true);
    }

    /**
     * Writes class with modifiers, class keyWord, full name with parameters, superclass and implemented interfaces.
     */
    private static void writeClassDeclaration(Writer writer, Class<?> someClass) throws IOException {
        writer.write(Modifier.toString(someClass.getModifiers()));
        writer.write(" class ");
        writeClassNameWithType(writer, someClass);

        writer.write(" extends ");
        writeType(writer,someClass.getGenericSuperclass(), true);
        writeTypesArray(writer, someClass.getGenericInterfaces(), " implements ", ", ", "", true);
    }

    /**
     * Writes all fields, methods, constructors, inner and nested classes.
     * Methods and constructors bodies are just {@code throw new UnsupportedOperationException statement}
     * Fields initialized to there default value
     */
    private static void writeClassContent(Writer writer, Class<?> someClass) throws IOException {

        for (Class<?> clazz : someClass.getDeclaredClasses()) {
            printClass(writer, clazz);
            writer.write("" + linebreak);
        }
        for (Constructor<?> constructor : someClass.getDeclaredConstructors()) {
            if (constructor.isSynthetic()) {
                continue;
            }
            writer.write(indent);
            writeConstructor(writer, constructor);
            writer.write(unsupportedOperation);
            writer.write("" + linebreak);
        }
        for (Method method : someClass.getDeclaredMethods()) {
            if (method.isSynthetic()) {
                continue;
            }
            writer.write(indent);
            writeMethod(writer, method);
            writer.write(unsupportedOperation);
            writer.write("" + linebreak);
        }
        for (Field field : someClass.getDeclaredFields()) {
            if (field.isSynthetic()) {
                continue;
            }
            writer.write(indent);
            writeField(writer, field);
            writer.write("" + linebreak);
        }

    }

    /**
     *  Removes {@code extends Object} cause it looks ugly.
     */
    private static Type[] filterUpperBounds(Type[] typeParameters) {
        return Arrays.stream(typeParameters).filter(x -> !(Object.class.equals(x))).toArray(Type[]::new);
    }

    /**
     * Writes given class, TypeVariable, ParametrizedType, WildcardType or GenericType object.
     * @param isDeclaration settles if TypeVariable should be written with it bounds
     */
    private static void writeType(Writer writer, Type type, boolean isDeclaration) throws IOException {

        if (type instanceof Class<?>) {
            var clazz = (Class<?>)type;
            writeClassNameWithType(writer, clazz);
        } else if (type instanceof TypeVariable) {
            var typeVariable = (TypeVariable<?>)type;
            writer.write(typeVariable.getName());
            if (isDeclaration) {
                writeTypesArray(writer, filterUpperBounds(typeVariable.getBounds()),
                        " extends ", " & ", "", false);
            }
        } else if (type instanceof WildcardType) {
            var wildcardType = (WildcardType)type;
            writer.write("?");
            writeTypesArray(writer, filterUpperBounds(wildcardType.getUpperBounds()),
                    " extends ", " & ", "", false);
            writeTypesArray(writer, wildcardType.getLowerBounds(),
                    " super ", " & ", " ", false);
        } else if (type instanceof ParameterizedType) {
            var parametrisedType = (ParameterizedType)type;
            writer.write(parametrisedType.getRawType().toString());
            writeTypesArray(writer, parametrisedType.getActualTypeArguments(),
                    "<", ", ", ">", false);
        } else if (type instanceof GenericArrayType) {
            var genericArrayType = (GenericArrayType)type;
            writeType(writer, genericArrayType.getGenericComponentType(), isDeclaration);
            writer.write("[]");
        }
    }

    /**
     *  Writes array of Types.
     *  Uses prefix, separator and suffix to combine elements
     *  @param isDeclaration settles if TypeVariable should be written with it bounds
     */
    private static void writeTypesArray(Writer writer, Type[] typeParameters,
                                        String prefix, String separator, String suffix,
                                        boolean isDeclaration) throws IOException {
        if (typeParameters.length == 0) {
            return;
        }
        writer.write(prefix);
        for (int i = 0; i < typeParameters.length; i++) {
            writeType(writer, typeParameters[i], isDeclaration);
            if (i != typeParameters.length - 1) {
                writer.write(separator);
            }
        }
        writer.write(suffix);
    }

    /**
     * Writes constructor with modifiers, typeParameters and parameters.
     */
    private static void writeConstructor(Writer writer, Constructor<?> constructor) throws IOException {
        writer.write(Modifier.toString(constructor.getModifiers()) + " ");
        writeTypesArray(writer, constructor.getTypeParameters(), "<", ", ", "> ", true);
        writer.write(constructor.getDeclaringClass().getSimpleName());
        writeMethodOrConstructorParameters(writer, constructor.getGenericParameterTypes(), constructor.getGenericExceptionTypes());
    }

    /**
     * Writes constructor with modifiers, typeParameters, generic return type, name and parameters.
     */
    private static void writeMethod(Writer writer, Method method) throws IOException {
        writer.write(Modifier.toString(method.getModifiers()) + " ");
        writeTypesArray(writer, method.getTypeParameters(), "<", ", ", "> ", true);
        writeType(writer, method.getGenericReturnType(), false);
        writer.write(" ");
        writer.write(method.getName());
        writeMethodOrConstructorParameters(writer, method.getGenericParameterTypes(), method.getGenericExceptionTypes());
    }

    /**
     * Writes parameters and exceptions.
     * Names arguments as arg + positionAmongArguments
     */
    private static void writeMethodOrConstructorParameters(Writer writer, Type[] genericParameterTypes, Type[] genericExceptionTypes) throws IOException {
        writer.write("(");
        for (int i = 0; i < genericParameterTypes.length; i++) {
            writeType(writer, genericParameterTypes[i], false);
            writer.write(" arg" + i);
            if (i + 1 != genericParameterTypes.length) {
                writer.write(", ");
            }
        }
        writer.write(")");
        writeTypesArray(writer, genericExceptionTypes, " throws ", ", ", "", false);
    }

    /**
     * Writes field with modifiers and generic type.
     * Initializes it to default value
     */
    private static void writeField(Writer writer, Field field) throws IOException {
        writer.write(Modifier.toString(field.getModifiers()) + " ");
        writeType(writer, field.getGenericType(), false);
        writer.write(" " + field.getName());
        writer.write(" = " + getDefaultValue(field.getGenericType()) + ";" + linebreak);
    }

    /**
     * Gets String representing default value for given type
     *      as it would appear in source code
     */
    private static String getDefaultValue(Type type) {
        if (type instanceof Class) {
            var clazz = (Class<?>)type;
            if (clazz.isPrimitive()) {
                return Array.get(Array.newInstance(clazz, 1), 0).toString();
            } else {
                return "null";
            }
        }
        return null;
    }


    /**
     * Prints to System.out all methods and fields which exists in exactly one class of the given class.
     */
    public static void diffClasses(@NotNull Class<?> first, @NotNull Class<?> second) throws IOException {
        try(Writer writer = new FileWriter("DiffOutput.java")) {
            writer.write("Writing methods and fields different in " + first.getSimpleName() + " and " + second.getSimpleName() + "" + linebreak);

            List<Method> methodsOfFirstClassOnly = getDifferentMethods(first, second);
            List<Method> methodsOfSecondClassOnly = getDifferentMethods(second, first);
            if (methodsOfFirstClassOnly.isEmpty() && methodsOfSecondClassOnly.isEmpty()) {
                writer.write("All methods are the same" + linebreak);
            } else {
                writer.write("Methods of " + first.getSimpleName() + " only:" + linebreak);
                writeListOfMethods(first, writer, methodsOfFirstClassOnly);
                writer.write("Methods of " + second.getSimpleName() + " only:" + linebreak);
                writeListOfMethods(second, writer, methodsOfSecondClassOnly);
            }

            List<Field> fieldsOfFirstClassOnly = getDifferentFields(first, second);
            List<Field> fieldsOfSecondClassOnly = getDifferentFields(second, first);
            if (fieldsOfFirstClassOnly.isEmpty() && fieldsOfSecondClassOnly.isEmpty()) {
                writer.write("All fields are the same" + linebreak);
            } else {
                writer.write("Fields of " + first.getSimpleName() + " only:" + linebreak);
                WriteListOfFields(writer, first, fieldsOfFirstClassOnly);
                writer.write("Fields of " + second.getSimpleName() + " only:" + linebreak);
                WriteListOfFields(writer, second, fieldsOfSecondClassOnly);
            }
        }
    }

    private static void writeListOfMethods(Class<?> clazz, Writer writer, List<Method> methodsOfThisClassOnly) throws IOException {
        for (Method method : methodsOfThisClassOnly) {
            writeMethod(writer, method);
        }
        writer.write(linebreak);
    }

    private static void WriteListOfFields(Writer writer, Class<?> clazz, List<Field> fieldsOfThisClassOnly) throws IOException {
        for (Field field : fieldsOfThisClassOnly) {
            writeField(writer, field);
        }
        writer.write(linebreak);
    }

    /**
     * Compares methods to equality.
     * Uses there Modifiers, TypeParameters, ReturnType, ParameterTypes,
     *  Exceptions and Names to check equivalence
     */
    private static boolean MethodsAreEqual(Method a, Method b) {
        if (a.getModifiers() != b.getModifiers()) {
            return false;
        }
        if (!Arrays.equals(a.getTypeParameters(), b.getTypeParameters())) {
            return false;
        }
        if (!a.getGenericReturnType().equals(b.getGenericReturnType())) {
            return false;
        }
        if (!Arrays.equals(a.getGenericParameterTypes(), b.getGenericParameterTypes())) {
            return false;
        }
        if (!Arrays.equals(a.getGenericExceptionTypes(), b.getGenericExceptionTypes())) {
            return false;
        }
        return a.getName().equals(b.getName());
    }

    /**
     * Compares fields to equality.
     * Uses there Modifiers, Types
     *  and Names to check equivalence
     */
    private static boolean FieldsAreEqual(Field a, Field b) {
        if (a.getModifiers() != b.getModifiers()) {
            return false;
        }
        if (!a.getGenericType().equals(b.getGenericType())) {
            return false;
        }
        return a.getName().equals(b.getName());
    }

    /**
     * Return List off all Methods declared in first class,
     *      which has no equal method among methods declared in second class.
     * Equivalence checked according to MethodsAreEqual method
     * Works in a time equals to the product of amount of methods in first and second class
     */
    private static List<Method> getDifferentMethods(Class<?> first, Class<?> second) {
        return Arrays.stream(first.getDeclaredMethods()).
                filter(x -> Arrays.stream(second.getDeclaredMethods()).noneMatch(y -> MethodsAreEqual(x, y))).collect(Collectors.toList());
    }

    /**
     * Return List off all Fields declared in first class,
     *      which has no equal field among fields declared in second class.
     * Equivalence checked according to MethodsAreEqual method
     * Works in a time equals to the product of amount of fields in first and second class
     */
    private static List<Field> getDifferentFields(Class<?> first, Class<?> second) {
        return Arrays.stream(first.getDeclaredFields()).
                filter(x -> Arrays.stream(second.getDeclaredFields()).noneMatch(y -> FieldsAreEqual(x, y))).collect(Collectors.toList());
    }

}
