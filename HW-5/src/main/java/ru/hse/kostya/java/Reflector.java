package ru.hse.kostya.java;

import org.apache.groovy.io.StringBuilderWriter;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;
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
     * Writes class constructors, methods, fields, nested and inner classes
     *      declared in that class
     * @throws IOException if any problem occurred during writing to file previously named
     */
    public static void printStructure(@NotNull Class<?> someClass) throws IOException {
        try (var writer = new FileWriter(someClass.getSimpleName() + ".java")) {
            printClass(writer, someClass, true);
        }
    }

    /**
     * Writes class with declaration and full content.
     * @param isOuter outermost class modifier should be printed public only,
     *                no matter what they were
     */
    private static void printClass(Writer writer, Class<?> someClass, boolean isOuter)
            throws IOException {
        writeClassDeclaration(writer, someClass, isOuter);
        writer.write(" {" + linebreak + linebreak);
        writeClassContent(writer, someClass);
        writer.write("}" + linebreak);
    }

    /**
     * Writes class name with full List of type parameters.
     */
    private static void writeClassNameWithType(Writer writer, Class<?> someClass)
            throws IOException {
        writer.write(someClass.getSimpleName());
        writeTypesArray(writer, someClass.getTypeParameters(), "<", ", ", ">", true);
    }

    /**
     * Writes class with modifiers, class keyWord, full name with parameters,
     *      superclass and implemented interfaces.
     * @param isOuter outermost class modifier should be printed public only,
     *                no matter what they were
     */
    private static void writeClassDeclaration(Writer writer, Class<?> someClass, boolean isOuter)
            throws IOException {
        if (isOuter) {
            writer.write(Modifier.toString(Modifier.PUBLIC));
        } else {
            writer.write(Modifier.toString(someClass.getModifiers()));
        }
        writer.write(" class ");
        writeClassNameWithType(writer, someClass);

        writer.write(" extends ");
        writeType(writer,someClass.getGenericSuperclass(), true);
        writeTypesArray(writer, someClass.getGenericInterfaces(), " implements ", ", ", "", true);
    }

    /**
     * Writes all fields, methods, constructors, inner and nested classes.
     * Methods and constructors bodies are just
     *      {@code throw new UnsupportedOperationException statement}
     * Fields initialized to there default value
     * All result Stings sorted in natural order for some certainty
     */
    private static void writeClassContent(Writer writer, Class<?> someClass) throws IOException {

        List<String> allClassContent = new ArrayList<>();
        for (Class<?> clazz : someClass.getDeclaredClasses()) {
            var stringWriter = new StringBuilderWriter();
            printClass(stringWriter, clazz, false);
            allClassContent.add(stringWriter.toString());
        }
        for (Constructor<?> constructor : someClass.getDeclaredConstructors()) {
            if (constructor.isSynthetic()) {
                continue;
            }
            var stringWriter = new StringBuilderWriter();
            writeConstructor(stringWriter, constructor);
            stringWriter.write(unsupportedOperation);
            allClassContent.add(stringWriter.toString());
        }
        for (Method method : someClass.getDeclaredMethods()) {
            if (method.isSynthetic()) {
                continue;
            }
            var stringWriter = new StringBuilderWriter();
            writeMethod(stringWriter, method);
            stringWriter.write(unsupportedOperation);
            allClassContent.add(stringWriter.toString());
        }
        for (Field field : someClass.getDeclaredFields()) {
            if (field.isSynthetic()) {
                continue;
            }
            var stringWriter = new StringBuilderWriter();
            writeField(stringWriter, field);
            allClassContent.add(stringWriter.toString());
        }

        allClassContent.sort(Comparator.naturalOrder());
        writeListOfString(writer, allClassContent);
    }

    /**
     *  Removes {@code extends Object} cause it looks ugly.
     */
    private static Type[] filterUpperBounds(Type[] typeParameters) {
        return Arrays.stream(typeParameters).filter(x -> !(Object.class.equals(x)))
                .toArray(Type[]::new);
    }

    /**
     * Writes given class, TypeVariable, ParametrizedType, WildcardType or GenericType object.
     * @param isDeclaration settles if TypeVariable should be written with it bounds
     */
    private static void writeType(Writer writer, Type type, boolean isDeclaration)
            throws IOException {

        if (type instanceof Class<?>) {
            var clazz = (Class<?>)type;
            writer.write(clazz.getSimpleName());
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
            writeType(writer, parametrisedType.getRawType(), false);
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
    private static void writeConstructor(Writer writer, Constructor<?> constructor)
            throws IOException {
        writer.write(indent);
        writer.write(Modifier.toString(constructor.getModifiers()) + " ");
        writeTypesArray(writer, constructor.getTypeParameters(), "<", ", ", "> ", true);
        writer.write(constructor.getDeclaringClass().getSimpleName());
        writeMethodOrConstructorParameters(writer, constructor.getGenericParameterTypes(),
                constructor.getGenericExceptionTypes());
    }

    /**
     * Writes constructor with modifiers, typeParameters, generic return type, name and parameters.
     */
    private static void writeMethod(Writer writer, Method method) throws IOException {
        writer.write(indent);
        writer.write(Modifier.toString(method.getModifiers()) + " ");
        writeTypesArray(writer, method.getTypeParameters(), "<", ", ", "> ", true);
        writeType(writer, method.getGenericReturnType(), false);
        writer.write(" ");
        writer.write(method.getName());
        writeMethodOrConstructorParameters(writer, method.getGenericParameterTypes(),
                method.getGenericExceptionTypes());
    }

    /**
     * Writes parameters and exceptions.
     * Names arguments as arg + positionAmongArguments
     */
    private static void writeMethodOrConstructorParameters(Writer writer,
                   Type[] genericParameterTypes, Type[] genericExceptionTypes) throws IOException {
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
        writer.write(indent);
        writer.write(Modifier.toString(field.getModifiers()) + " ");
        writeType(writer, field.getGenericType(), false);
        writer.write(" " + field.getName());
        writer.write(" = " + getDefaultValue(field.getGenericType()) + ";");
    }

    /**
     * Gets String representing default value for given type
     *      as it would appear in source code.
     */
    private static String getDefaultValue(Type type) {
        if (type instanceof Class) {
            var clazz = (Class<?>)type;
            if (clazz.isPrimitive()) {
                if (clazz.equals(char.class)) {
                    //common way prints some unreadable character
                    return "'\\0'";
                }
                if (clazz.equals(float.class)) {
                    //common way prints 0.0 which is taken for double
                    return "0";
                }
                return Array.get(Array.newInstance(clazz, 1), 0).toString();
            } else {
                return "null";
            }
        }
        return null;
    }


    /**
     * Prints to System.out all methods and fields which exists in exactly one class
     *      of the given class.
     */
    public static void diffClasses(@NotNull Class<?> first, @NotNull Class<?> second)
            throws IOException {
        try (Writer writer = new PrintWriter(System.out)) {
            writer.write("Writing methods and fields different in " + first.getSimpleName()
                    + " and " + second.getSimpleName() + "" + linebreak);

            List<String> methodsOfFirstClassOnly = getDifferentMethods(first, second);
            List<String> methodsOfSecondClassOnly = getDifferentMethods(second, first);
            if (methodsOfFirstClassOnly.isEmpty() && methodsOfSecondClassOnly.isEmpty()) {
                writer.write("All methods are the same" + linebreak);
            } else {
                writer.write("Methods of " + first.getSimpleName() + " only:" + linebreak);
                writeListOfString(writer, methodsOfFirstClassOnly);
                writer.write("Methods of " + second.getSimpleName() + " only:" + linebreak);
                writeListOfString(writer, methodsOfSecondClassOnly);
            }

            List<String> fieldsOfFirstClassOnly = getDifferentFields(first, second);
            List<String> fieldsOfSecondClassOnly = getDifferentFields(second, first);
            if (fieldsOfFirstClassOnly.isEmpty() && fieldsOfSecondClassOnly.isEmpty()) {
                writer.write("All fields are the same" + linebreak);
            } else {
                writer.write("Fields of " + first.getSimpleName() + " only:" + linebreak);
                writeListOfString(writer, fieldsOfFirstClassOnly);
                writer.write("Fields of " + second.getSimpleName() + " only:" + linebreak);
                writeListOfString(writer, fieldsOfSecondClassOnly);
            }
        }
    }

    /**
     * Writes given listOfStrings with linebreaks between elements
     *      and one more linebreak at the end.
     */
    private static void writeListOfString(Writer writer, List<String> listOfString)
            throws IOException {
        for (String string : listOfString) {
            writer.write(string);
            writer.write(linebreak);
        }
        writer.write(linebreak);
    }

    /**
     * Returns String representing method as it would appear in source code.
     */
    private static String getStringForMethod(Method method) {
        try {
            Writer writer = new StringBuilderWriter();
            writeMethod(writer, method);
            return writer.toString();
        } catch (IOException e) {
            //no IO exception cannot occur during writing to StringBuilderWriter
            throw new AssertionError(e);
        }
    }

    /**
     * Returns String representing field as it would appear in source code.
     */
    private static String getStringForField(Field field) {
        try {
            Writer writer = new StringBuilderWriter();
            writeField(writer, field);
            return writer.toString();
        } catch (IOException e) {
            //no IO exception cannot occur during writing to StringBuilderWriter
            throw new AssertionError(e);
        }
    }

    /**
     * Return List of all Methods declared in first class,
     *      which has no equal method among methods declared in second class.
     * Equivalence checked according to MethodsAreEqual method
     * Works in a time equals to the product of amount of methods in first and second class
     * List is sorted for sake of certain order
     */
    private static List<String> getDifferentMethods(Class<?> first, Class<?> second) {
        return Arrays.stream(first.getDeclaredMethods()).map(Reflector::getStringForMethod)
                .filter(x -> Arrays.stream(second.getDeclaredMethods())
                        .map(Reflector::getStringForMethod).noneMatch(x::equals))
                .sorted(Comparator.naturalOrder()).collect(Collectors.toList());
    }

    /**
     * Return List of all Fields declared in first class,
     *      which has no equal field among fields declared in second class.
     * Equivalence checked according to MethodsAreEqual method
     * Works in a time equals to the product of amount of fields in first and second class
     * List is sorted for sake of certain order
     */
    private static List<String> getDifferentFields(Class<?> first, Class<?> second) {
        return Arrays.stream(first.getDeclaredFields()).map(Reflector::getStringForField)
                .filter(x -> Arrays.stream(second.getDeclaredFields())
                        .map(Reflector::getStringForField).noneMatch(x::equals))
                .sorted(Comparator.naturalOrder()).collect(Collectors.toList());
    }

}

