package com.bergerkiller.reflection;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.conversion.Converter;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.reflection.net.minecraft.server.NMSDataWatcherObject;

import net.sf.cglib.asm.Type;
import net.sf.cglib.core.Signature;

import org.objenesis.ObjenesisHelper;
import org.objenesis.instantiator.ObjectInstantiator;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Maintains a full reflection model for a class hierarchy
 */
public class ClassTemplate<T> {
    private Class<T> type;
    private List<SafeField<?>> fields;
    private ObjectInstantiator<T> instantiator;
    private List<Field> typeFields;
    private List<Method> typeMethods;
    private Queue<Field> nextFieldQueue;
    private HashSet<String> imports;

    /**
     * Initializes a new ClassTemplate not pointing to any Class<br>
     * Call setClass before use.
     */
    protected ClassTemplate() {
    }

    /**
     * Initializes this Class Template to represent the Class and fields of the
     * type specified
     *
     * @param type to set the Class to
     * @return this class template
     */
    protected ClassTemplate<T> setClass(Class<T> type) {
        this.type = type;
        this.fields = null;
        this.instantiator = null;
        this.typeFields = null;
        this.imports = new HashSet<String>(4);
        if (type != null) {
            addClassImports(type);
            for (Class<?> i : type.getInterfaces()) {
                addClassImports(i);
            }
        }
        this.imports.add("java.lang.*");
        this.imports.add("java.util.*");
        return this;
    }

    private void addClassImports(Class<?> type) {
        if (type == null) {
            return;
        }
        Package pkg = type.getPackage();
        if (pkg != null) {
            this.imports.add(pkg.getName() + ".*");
        }
        this.imports.add(type.getName() + ".*");
        addClassImports(type.getSuperclass());
    }

    @SuppressWarnings("unchecked")
    private ClassTemplate<T> setClassAndLog(Class<?> type, String mode, String className) {
        if (type == null) {
            String mm = (mode.length() == 0) ? (" ") : (" " + mode + " ");
            Logging.LOGGER_REFLECTION.severe("Failed to find" + mm + "Class '" + className + "'");
        } else {
            setClass((Class<T>) type);
        }
        return this;
    }

    /**
     * Initializes this Class Template to represent the
     * Class name specified
     *
     * @param className to represent
     * @return this class template
     */
    protected ClassTemplate<T> setClass(String className) {
        return setClassAndLog(CommonUtil.getClass(className, false), "", className);
    }

    /**
     * Initializes this net.minecraft.server Class Template to represent the NMS
     * Class name specified
     *
     * @param className to represent
     * @return this class template
     */
    protected ClassTemplate<T> setNMSClass(String className) {
        return setClassAndLog(CommonUtil.getNMSClass(className), "NMS", className);
    }

    /**
     * Initializes this org.bukkit.craftbukkit Class Template to represent the
     * CB Class name specified
     *
     * @param className to represent
     * @return this class template
     */
    protected ClassTemplate<T> setCBClass(String className) {
        return setClassAndLog(CommonUtil.getCBClass(className), "CB", className);
    }

    /**
     * Adds a new import package or class path used during class lookup
     * 
     * @param importPath to add
     * @return this class template
     */
    public ClassTemplate<T> addImport(String importPath) {
        this.imports.add(importPath);
        return this;
    }

    /**
     * Gets the Class type represented by this Template
     *
     * @return Class type
     */
    public Class<T> getType() {
        return this.type;
    }

    /**
     * Gets all the fields declared in this Class
     *
     * @return Declared fields
     */
    public List<SafeField<?>> getFields() {
        if (fields == null) {
            if (type == null) {
                fields = Collections.emptyList();
            } else {
                fields = Collections.unmodifiableList(fillFields(new ArrayList<SafeField<?>>(), type));
            }
        }
        return fields;
    }

    /**
     * Gets the field set at a specific index
     *
     * @param index to get the field at
     * @return field at the index
     * @throws IllegalArgumentException - If no field is at the index
     */
    public SafeField<?> getFieldAt(int index) {
        List<SafeField<?>> fields = getFields();
        if (index < 0 || index >= fields.size()) {
            throw new IllegalArgumentException("No field exists at index " + index);
        }
        return fields.get(index);
    }

    private static List<SafeField<?>> fillFields(List<SafeField<?>> fields, Class<?> clazz) {
        if (clazz == null) {
            return fields;
        }
        Field[] declared = clazz.getDeclaredFields();
        ArrayList<SafeField<?>> newFields = new ArrayList<SafeField<?>>(declared.length);
        for (Field field : declared) {
            if (!Modifier.isStatic(field.getModifiers())) {
                newFields.add(new SafeField<Object>(field));
            }
        }
        fields.addAll(0, newFields);
        return fillFields(fields, clazz.getSuperclass());
    }

    /**
     * Gets a new instance of this Class, using the empty constructor
     *
     * @return A new instance, or null if an error occurred/empty constructor is
     * not available
     * @throws IllegalStateException if this ClassTemplate has no (valid) Class
     * set
     */
    public T newInstance() {
        if (this.type == null) {
            throw new IllegalStateException("Class was not found or is not set");
        }
        try {
            return this.type.newInstance();
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }

    /**
     * Gets a new instance of this Class without calling the Class Constructors.
     * Calling this method will result in a new instance with all fields set to
     * the default. That is, all fields will have 'NULL' values, or for
     * primitives, 0/false/etc.
     *
     * @return a new Class Instance, or null upon failure
     * @throws IllegalStateException if this ClassTemplate has no (valid) Class
     * set
     */
    public T newInstanceNull() {
        if (this.type == null) {
            throw new IllegalStateException("Class was not found or is not set");
        }
        if (this.instantiator == null) {
            this.instantiator = ObjenesisHelper.getInstantiatorOf(type);
            if (this.instantiator == null) {
                throw new IllegalStateException("Class could not be instantiated");
            }
        }
        try {
            return this.instantiator.newInstance();
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }

    /**
     * Checks whether a given object is an instance of the class represented by
     * this Template
     *
     * @param object to check
     * @return True if the object is an instance, False if not
     */
    public boolean isInstance(Object object) {
        return this.type.isInstance(object);
    }

    /**
     * Checks whether the specified clazz object can be assigned to the type
     * represented by this ClassTemplate. This template taken as A, and clazz as
     * B, this is equivalent to:<br>
     * <b>B instanceof A</b>
     *
     * @param clazz to check
     * @return True if the clazz can be assigned to this template type, False if
     * not
     */
    public boolean isAssignableFrom(Class<?> clazz) {
        return this.type.isAssignableFrom(clazz);
    }

    /**
     * Checks whether the object class equals the class represented by this
     * Template
     *
     * @param object to check
     * @return True if the object is a direct instance, False if not
     */
    public boolean isType(Object object) {
        return object != null && isType(object.getClass());
    }

    /**
     * Checks whether a given class instance equals the class represented by
     * this Template
     *
     * @param clazz to check
     * @return True if the clazz is not null and equals the class of this
     * template
     */
    public boolean isType(Class<?> clazz) {
        return clazz != null && this.type.equals(clazz);
    }

    /**
     * Casts the object to the type denoted by this Template.
     * Throws a ClassCastException if the operation fails.
     * 
     * @param obj to cast
     * @return obj cast to T
     */
    public T cast(Object obj) {
        return this.type.cast(obj);
    }

    /**
     * Tries to cast the object to the type denoted by this Template.
     * Returns null if the casting fails.
     * 
     * @param obj to cast
     * @return obj to try and cast to T
     */
    public T tryCast(Object obj) {
        if (isInstance(obj)) {
            return this.type.cast(obj);
        } else {
            return null;
        }
    }

    /**
     * Transfers all the fields from one class instance to the other
     *
     * @param from instance
     * @param to instance
     */
    public void transfer(Object from, Object to) {
        for (FieldAccessor<?> field : this.getFields()) {
            field.transfer(from, to);
        }
    }

    /**
     * Checks whether this Class Template is properly initialized and can be
     * used
     *
     * @return True if this template is valid for use, False if not
     */
    public boolean isValid() {
        return this.type != null;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(500);
        builder.append("Class path: ").append(this.getType().getName()).append('\n');
        builder.append("Fields (").append(this.getFields().size()).append("):");
        for (FieldAccessor<?> field : this.getFields()) {
            builder.append("\n    ").append(field.toString());
        }
        return builder.toString();
    }

    /**
     * Attempts to find the constructor for this Class using the parameter types
     *
     * @param parameterTypes of the constructor
     * @return Constructor
     */
    public SafeConstructor<T> getConstructor(Class<?>... parameterTypes) {
        return new SafeConstructor<T>(this.getType(), parameterTypes);
    }

    /**
     * Attempts to find the field by name
     *
     * @param name of the field
     * @return field
     */
    public <K> SafeField<K> getField(String name, Class<?> type) {
        return new SafeField<K>(this.getType(), name, type);
    }

    /**
     * Attempts to find the field by name
     *
     * @param name of the field
     * @return field
     */
    public <K> SafeField<K> getField(String name) {
        return new SafeField<K>(this.getType(), name, null);
    }

    /**
     * Attempts to find the method by name
     *
     * @param name of the method
     * @param parameterTypes of the method
     * @return method
     */
    public <K> SafeMethod<K> getMethod(String name, Class<?>... parameterTypes) {
        return new SafeMethod<K>(this.getType(), name, parameterTypes);
    }

    /**
     * Gets a statically declared field value
     *
     * @param name of the static field
     * @return Static field value
     */
    public <K> K getStaticFieldValue(String name, Class<K> fieldType) {
        return SafeField.get(getType(), name, fieldType);
    }

    /**
     * Gets a statically declared field value and uses the converter to get the
     * value.
     *
     * @param name of the static field
     * @param converter to use
     * @return Converted static field value
     */
    public <K> K getStaticFieldValue(String name, Converter<K> converter) {
        return converter.convert(getStaticFieldValue(name, (Class<Object>) null));
    }

    /**
     * Sets a statically declared field value
     *
     * @param name of the static field
     * @param value to set the static field to
     */
    public <K> void setStaticFieldValue(String name, K value) {
        SafeField.setStatic(getType(), name, value);
    }

    /**
     * Retrieves the static value declaration for a DataWatcher key
     * 
     * @param fieldName of the declared key
     * @return DataWatcher Key
     */
    public <K> DataWatcher.Key<K> getDataWatcherKey(String fieldName) {
        Object keyHandle = getStaticFieldValue(fieldName, NMSDataWatcherObject.T.getType());
        return new DataWatcher.Key<K>(keyHandle);
    }

    /**
     * Attempts to create a new template for the class at the path specified. If
     * the class is not found, a proper warning is printed.
     *
     * @param path to the class
     * @return a new template, or null if the template could not be made
     */
    public static ClassTemplate<?> create(String path) {
        return new ClassTemplate<Object>().setClass(path);
    }

    /**
     * Creates a new Class Template for the org.bukkit.craftbukkit Class name
     * specified
     *
     * @param path to the class
     * @return a new template, or null if the template could not be made
     */
    public static ClassTemplate<?> createCB(String path) {
        return new ClassTemplate<Object>().setCBClass(path);
    }

    /**
     * Creates a new Class Template for the net.minecraft.server Class name
     * specified
     *
     * @param path to the class
     * @return a new template, or null if the template could not be made
     */
    public static ClassTemplate<?> createNMS(String path) {
        return new ClassTemplate<Object>().setNMSClass(path);
    }

    /**
     * Attempts to create a new template for the class of the class instance
     * specified<br>
     * If something fails, an empty instance is returned
     *
     * @param value of the class to create the template for
     * @return a new template
     */
    @SuppressWarnings("unchecked")
    public static <T> ClassTemplate<T> create(T value) {
        return new ClassTemplate<T>().setClass((Class<T>) value.getClass());
    }

    /**
     * Attempts to create a new template for the class specified<br>
     * If something fails, an empty instance is returned
     *
     * @param clazz to create
     * @return a new template
     */
    public static <T> ClassTemplate<T> create(Class<T> clazz) {
        return new ClassTemplate<T>().setClass(clazz);
    }

    private void logFieldWarning(String declaration, String message) {
        Logging.LOGGER_REFLECTION.warning("Field '" + declaration + "' in class " +
                                   getType().getName() + " " + message);
    }

    private void logMethodWarning(String declaration, String message) {
        Logging.LOGGER_REFLECTION.warning("Method '" + declaration + "' in class " +
                                   getType().getName() + " " + message);
    }

    /**
     * Resolves a class name into a Class by using this templates' imports and strategies.
     * 
     * @param className to look up
     * @return resolved Class name, or null if not found
     */
    public Class<?> resolveClass(String className) {
        return resolveClass(className, true);
    }

    /**
     * Resolves a class name into a Class by using this templates' imports and strategies.
     * 
     * @param className to look up
     * @param logErrors whether to log errors when they occur
     * @return resolved Class name, or null if not found
     */
    public Class<?> resolveClass(String className, boolean logErrors) {
        // Return Object for generic typings (T, K, etc.)
        if (className.length() == 1) {
            return Object.class;
        }

        Class<?> fieldType = CommonUtil.getClass(className);

        if (fieldType == null) {
            for (String imp : this.imports) {
                if (imp.endsWith(".*")) {
                    fieldType = CommonUtil.getClass(imp.substring(0, imp.length() - 1) + className);
                } else if (imp.endsWith(className)) {
                    fieldType = CommonUtil.getClass(imp);
                } else {
                    continue;
                }
                if (fieldType != null) {
                    break;
                }
            }
        }

        if (fieldType == null && logErrors) {
            Logging.LOGGER_REFLECTION.warning("Could not find type: " + className);
        }

        return fieldType;
    }

    /**
     * Resolves the name of a Class type when used within this Template
     * 
     * @param type to resolve
     * @return class name
     */
    private String resolveClassName(Class<?> type) {
        // Null types shouldn't happen, but security and all
        if (type == null) {
            return "NULL";
        }

        // Handle arrays elegantly
        if (type.isArray()) {
            return resolveClassName(type.getComponentType()) + "[]";
        }

        // See if the class type was imported
        String name = type.getName();
        for (String imp : this.imports) {
            if (imp.equals(name)) {
                return type.getSimpleName();
            }
            if (imp.endsWith(".*")) {
                String imp_p = imp.substring(0, imp.length() - 1);
                if (name.startsWith(imp_p)) {
                    return name.substring(imp_p.length());
                }
            }
        }
        return name;
    }

    /// parses method/field modifier lists
    private int parseModifiers(String[] parts, int count) {
        // Read modifiers
        int modifiers = 0;
        for (int i = 0; i < count; i++) {
            switch (parts[i]) {
            case "public":
                modifiers |= Modifier.PUBLIC; break;
            case "private":
                modifiers |= Modifier.PRIVATE; break;
            case "protected":
                modifiers |= Modifier.PROTECTED; break;
            case "final":
                modifiers |= Modifier.FINAL; break;
            case "static":
                modifiers |= Modifier.STATIC; break;
            case "volatile":
                modifiers |= Modifier.VOLATILE; break;
            case "abstract":
                modifiers |= Modifier.ABSTRACT; break;
            }
        }
        return modifiers;
    }

    private boolean compareModifiers(int m1, int m2) {
        return (Modifier.isPrivate(m1) == Modifier.isPrivate(m2) &&
                Modifier.isPublic(m1) == Modifier.isPublic(m2) &&
                Modifier.isProtected(m1) == Modifier.isProtected(m2) &&
                Modifier.isStatic(m1) == Modifier.isStatic(m2) &&
                Modifier.isFinal(m1) == Modifier.isFinal(m2));
    }

    private void loadFields(boolean initNextQueue) {
        // Initialize field queue with fields if needed
        if (typeFields == null) {
            typeFields = new LinkedList<Field>();
            if (this.type != null) {
                Collections.addAll(typeFields, this.type.getDeclaredFields());
            }
        }
        if (initNextQueue || nextFieldQueue == null) {
            nextFieldQueue = new LinkedList<Field>(typeFields);
        }
    }

    private void addMethods(Class<?> type, HashSet<Signature> addedSignatures) {
        // Get sorted array of methods
        Method[] declMethods;
        if (type.equals(this.getType())) {
            declMethods = type.getDeclaredMethods();
        } else {
            declMethods = type.getMethods();
        }

        // = type.getDeclaredMethods();
        Arrays.sort(declMethods, new Comparator<Method>() {
            String paramsStr(Method m) {
                Class<?>[] params = m.getParameterTypes();
                String result = "";
                for (int i = 0; i < params.length; i++) {
                    if (i > 0)
                        result += ",";
                    result += resolveClassName(params[i]);
                }
                return result;
            }

            @Override
            public int compare(Method o1, Method o2) {
                String o1p = paramsStr(o1);
                String o2p = paramsStr(o2);
                if (o1p.equals(o2p)) {
                    String o1r = resolveClassName(o1.getReturnType());
                    String o2r = resolveClassName(o2.getReturnType());
                    if (o1r.equals(o2r)) {
                        return o1.getName().compareTo(o2.getName());
                    } else {
                        return o1r.compareTo(o2r);
                    }
                } else {
                    return o1p.compareTo(o2p);
                }
            }
        });

        // Add while checking for duplicates using the hashset, ignoring those
        for (Method m : declMethods) {
            Signature sig = new Signature(m.getName(), Type.getReturnType(m), Type.getArgumentTypes(m));
            if (addedSignatures.add(sig)) {
                typeMethods.add(m);
            }
        }
    }

    private void loadMethods() {
        // Initialize field queue with fields if needed
        if (typeMethods == null) {
            typeMethods = new LinkedList<Method>();

            // Load all methods top-down and then all abstract methods from the interfaces
            Class<?> currentType = this.getType();
            HashSet<Signature> addedSignatures = new HashSet<Signature>();
            do {
                addMethods(currentType, addedSignatures);
            } while ((currentType = currentType.getSuperclass()) != null);
            for (Class<?> interfaceClass : this.getType().getInterfaces()) {
                addMethods(interfaceClass, addedSignatures);
            }

            /*
            System.out.println(this.getType().getName() + ":");
            for (Method m : typeMethods) {
                System.out.println("  - " + m.toString());
            }
            */
        }
    }

    /**
     * Selects the next field matching the declaration exactly.
     * Future calls to @nextFieldSignature will search from this field.
     */
    public <F> FieldAccessor<F> nextField(String declaration) {
        // No type
        if (this.type == null) {
            logFieldWarning(declaration, "can not be found because class to find it in is null");
            return new SafeField<F>(null);
        }

        // Parse the declaration
        FieldDeclare declare = new FieldDeclare(declaration);
        if (!declare.isValid()) {
            logFieldWarning(declaration, "could not be parsed");
            return new SafeField<F>(null);
        }

        loadFields(true);

        // Find the field exactly
        for (Field field : nextFieldQueue) {
            if (declare.match(field)) {

                // Skip until this field
                while (nextFieldQueue.remove() != field);

                // Done!
                return new SafeField<F>(field);
            }
        }

        // List close matches
        List<Field> similar = new ArrayList<Field>();
        for (Field field : typeFields) {
            if (declare.matchSignature(field)) {
                similar.add(field);
            }
        }

        // Maybe a field with this same name exists?
        if (similar.size() == 0) {
            for (Field field : typeFields) {
                if (declare.name.equals(field.getName())) {
                    similar.add(field);
                }
            }
        }

        if (similar.size() == 0) {
            // No alternative found, uh oh!
            logFieldWarning(declaration, "not found; no alternatives available. (Removed?)");
        } else {
            // Log the close matches
            logFieldWarning(declaration, "not found; there are " + similar.size() + " close matches:");
            for (Field field : similar) {
                Logging.LOGGER_REFLECTION.warning("  - " + new FieldDeclare(field).toString());
            }
        }

        return new SafeField<F>(null);
    }

    public void skipFieldSignature(String declaration) {
        nextFieldSignature(declaration);
    }

    public void skipField(String declaration) {
        nextField(declaration);
    }

    /**
     * Locates the next field matching the signature (modifiers and field type) of the declarations, but not the field names
     * 
     * @param declarations to parse
     * @return Next field
     */
    public <F> FieldAccessor<F> nextFieldSignature(String declaration) {
        loadFields(false);

            // If empty, abort
            if (nextFieldQueue.isEmpty()) {
                logFieldWarning(declaration, "could not be found (no more fields)");
                return new SafeField<F>(null);
            }

            // Parse the declaration
            FieldDeclare declare = new FieldDeclare(declaration);
            if (!declare.isValid()) {
                logFieldWarning(declaration, "could not be parsed");
                return new SafeField<F>(null);
            }

            // Check if the field matches the very next item
            Field next = nextFieldQueue.peek();
            if (declare.match(next)) {
                nextFieldQueue.remove();
            } else {
                // Find similar matches until the field is found
                next = null;
                List<Field> skipped = new ArrayList<Field>();
                while (!nextFieldQueue.isEmpty()) {
                    Field ff = nextFieldQueue.remove();
                    if (declare.matchSignature(ff)) {
                        next = ff;
                        break;
                    } else {
                        skipped.add(ff);
                    }
                }
                if (next == null) {
                    logFieldWarning(declaration, "could not be found (no more fields)");
                    return new SafeField<F>(null);
                }

                if (skipped.size() > 0) {
                    logFieldWarning(declaration, "skipped " + skipped.size() + " fields during lookup:");
                    for (Field f : skipped) {
                        Logging.LOGGER_REFLECTION.warning("  - " + new FieldDeclare(f).toString());
                    }
                }
            }

            // Warn about field name changes
            if (!declare.match(next)) {
                logFieldWarning(declaration, "has an incorrect name. New name: " + next.getName());                
            }


            return new SafeField<F>(next);
    }

    /**
     * Selects a static field that matches the declaration and gets the value
     * 
     * @param declaration of the static field
     * @return static field value, or null on failure
     */
    public <F> F selectStaticValue(String declaration) {
        FieldAccessor<F> field = selectField(declaration);
        return (field.isValid()) ? field.get(null) : null;
    }

    /**
     * Locates the field matching the declaration in the underlying class
     * This is used to identify missing fields or fields changing type
     * 
     * @param declaration of the field
     * @return The next field in the field listing, best matching the declaration description
     */
    public <F> FieldAccessor<F> selectField(String declaration) {
        // Parse the declaration
        FieldDeclare declare = new FieldDeclare(declaration);
        if (!declare.isValid()) {
            logFieldWarning(declaration, "could not be parsed");
            return new SafeField<F>(null);
        }

        loadFields(false);

        // Find the field exactly
        for (Field field : typeFields) {
            if (declare.match(field)) {
                return new SafeField<F>(field);
            }
        }

        // List close matches
        List<Field> similar = new ArrayList<Field>();
        for (Field field : typeFields) {
            if (declare.matchSignature(field)) {
                similar.add(field);
            }
        }

        // Maybe a field with this same name and type exists?
        if (similar.size() == 0) {
            for (Field field : typeFields) {
                if (Modifier.isStatic(field.getModifiers()) != Modifier.isStatic(declare.modifiers))
                    continue;

                if (declare.name.equals(field.getName()) && declare.type.equals(field.getType())) {
                    similar.add(field);
                }
            }
        }

        // Maybe the name changed?
        if (similar.size() == 0) {
            for (Field field : typeFields) {
                if (Modifier.isStatic(field.getModifiers()) != Modifier.isStatic(declare.modifiers))
                    continue;

                if (declare.type.equals(field.getType())) {
                    similar.add(field);
                }
            }
        }

        if (similar.size() == 0) {
            // No alternative found, uh oh!
            logFieldWarning(declaration, "not found; no alternatives available. (Removed?)");
        } else {
            // Log the close matches
            logFieldWarning(declaration, "not found; there are " + similar.size() + " close matches:");
            for (Field field : similar) {
                Logging.LOGGER_REFLECTION.warning("  - " + new FieldDeclare(field).toString());
            }
        }

        return new SafeField<F>(null);
    }

    public <M> MethodAccessor<M> selectMethod(String declaration) {
        return new SafeMethod<M>(selectRawMethod(declaration, true));
    }

    public Method selectRawMethod(String declaration, boolean logErrors) {
        // Parse the declaration
        MethodDeclare declare = new MethodDeclare(declaration, logErrors);
        if (!declare.isValid()) {
            if (logErrors) {
                logMethodWarning(declaration, "could not be parsed");
            }
            return null;
        }

        loadMethods();

        // Find the exact method
        for (Method method : typeMethods) {
            if (declare.match(method)) {
                try {
                    method.setAccessible(true);
                    return method;
                } catch (SecurityException ex) {
                    ex.printStackTrace();
                }
            }
        }

        // Log the close matches
        List<Method> similar = new ArrayList<Method>();
        for (Method method : typeMethods) {
            if (declare.matchSignature(method)) {
                similar.add(method);
            }
        }

        if (logErrors) {
            if (similar.size() == 0) {
                // No alternative found, uh oh!
                logMethodWarning(declaration, "not found; no alternatives available. (Removed?)");
            } else {
                // Log the close matches
                logMethodWarning(declaration, "not found; there are " + similar.size() + " close matches:");
                for (Method method : similar) {
                    Logging.LOGGER_REFLECTION.warning("  - " + new MethodDeclare(method).toString());
                }
            }
        }
        return null;
    }

    /// removes generics from a field/method declaration
    /// example: Map<String, String> stuff -> Map stuff
    private static String filterGenerics(String input) {
        String result = input;
        int genEnd, genStart;
        while ((genStart = result.indexOf('<')) != -1) {
            while (true) {
                int a = result.indexOf('<', genStart+1);
                int b = result.indexOf('>', genStart+1);
                if (a == -1 && b == -1) {
                    break;
                }
                if (a == -1) a = result.length();
                if (b == -1) b = result.length();
                if (a < b) {
                    genStart = a;
                } else {
                    genEnd = b;
                    result = result.substring(0, genStart) + result.substring(genEnd + 1);
                    break;
                }
            }
        }
        return result;
    }

    private class FieldDeclare {
        final String name;
        final Class<?> type;
        final int modifiers;
        final String declaration;

        public FieldDeclare(Field f) {
            this.type = f.getType();
            this.name = f.getName();
            this.modifiers = f.getModifiers();

            String declaration = "";

            declaration += Modifier.toString(f.getModifiers()) + " ";
            declaration += resolveClassName(f.getType()) + " ";
            declaration += f.getName() + ";";
            this.declaration = declaration;
        }

        public FieldDeclare(String declaration) {
            if (declaration.endsWith(";")) {
                declaration = declaration.substring(0, declaration.length() - 1);
            }

            String declarationF = filterGenerics(declaration);

            String[] parts = declarationF.split(" ");

            // Figure out what the field type is from the name
            String fieldName = null;
            Class<?> fieldType = null;
            int fieldModifiers = 0;
            if (parts.length >= 2) {
                fieldName = parts[parts.length - 1];
                fieldType = resolveClass(parts[parts.length - 2], true);
                fieldModifiers = parseModifiers(parts, parts.length - 2);
            }

            if (fieldName == null || fieldType == null)
            {
                this.name = null;
                this.type = null;
                this.modifiers = 0;
                this.declaration = null;
            } else {
                this.name = fieldName;
                this.type = fieldType;
                this.modifiers = fieldModifiers;
                this.declaration = declaration + ";";
            }
        }

        /**
         * Checks whether this declaration was properly parsed
         * 
         * @return True if valid, False if not
         */
        public boolean isValid() {
            return name != null && type != null;
        }

        /**
         * Matches the full field signature and field name
         * 
         * @param f field to match
         * @return True if the field matches, False if not
         */
        public boolean match(Field f) {
            return f.getName().equals(name) && matchSignature(f);
        }

        /**
         * Matches only the signature of the method (modifiers, field type)
         * This is used to find alternative candidates for a field name
         * 
         * @param f field to match
         * @return True if the signature matches, False if not
         */
        public boolean matchSignature(Field f) {
            return (f.getType() == type) && compareModifiers(f.getModifiers(), modifiers);
        }

        @Override
        public String toString() {
            return declaration;
        }
    }

    private class MethodDeclare {
        final String name;
        final Class<?> returnType;
        final Class<?>[] parameterTypes;
        final int modifiers;
        final String declaration;

        public MethodDeclare(Method m) {
            this.returnType = m.getReturnType();
            this.parameterTypes = m.getParameterTypes();
            this.name = m.getName();
            this.modifiers = m.getModifiers();

            String declaration = "";
            declaration += Modifier.toString(m.getModifiers()) + " ";
            declaration += resolveClassName(m.getReturnType()) + " ";
            declaration += m.getName();

            declaration += "(";
            for (int i = 0; i < parameterTypes.length; i++) {
                if (i > 0) {
                    declaration += ", ";
                }
                declaration += resolveClassName(parameterTypes[i]);
            }
            declaration += ");";
            this.declaration = declaration;
        }

        public MethodDeclare(String declaration, boolean logErrors) {
            if (declaration.endsWith(";")) {
                declaration = declaration.substring(0, declaration.length() - 1);
            }

            String declarationF = filterGenerics(declaration);

            String methodName = null;
            Class<?> returnType = null;
            Class<?>[] parameterTypes = null;
            int methodModifiers = 0;

            // Find method body start
            int method_start = declarationF.indexOf('(');
            if (method_start != -1) {
                int method_end = declarationF.indexOf(')', method_start);
                if (method_end == -1) {
                    method_end = declarationF.length();
                }

                String method_header = declarationF.substring(0, method_start).trim();
                String[] method_params = declarationF.substring(method_start+1, method_end).split(",");
                String[] parts = method_header.split(" ");

                // Figure out what the field type is from the name
                if (parts.length >= 2) {
                    methodName = parts[parts.length - 1];
                    returnType = resolveClass(parts[parts.length - 2], logErrors);
                    methodModifiers = parseModifiers(parts, parts.length - 2);

                    List<Class<?>> paramTypes = new ArrayList<Class<?>>();
                    boolean paramFail = false;
                    for (String param : method_params) {
                        param = param.trim();
                        if (param.length() == 0)
                            continue;

                        // Ignore name of the parameter
                        int paramTypeEnd = param.indexOf(' ');
                        if (paramTypeEnd != -1) {
                            param = param.substring(0, paramTypeEnd).trim();
                        }

                        Class<?> paramType = resolveClass(param, logErrors);
                        if (paramType == null) {
                            paramFail = true;
                            break;
                        }
                        paramTypes.add(paramType);
                    }
                    if (!paramFail) {
                        parameterTypes = paramTypes.toArray(new Class<?>[0]);
                    }
                }
            }

            if (methodName == null || returnType == null || parameterTypes == null)
            {
                this.name = null;
                this.returnType = null;
                this.parameterTypes = null;
                this.modifiers = 0;
                this.declaration = null;
            } else {
                this.name = methodName;
                this.returnType = returnType;
                this.parameterTypes = parameterTypes;
                this.modifiers = methodModifiers;
                this.declaration = declaration + ";";
            }
        }

        /**
         * Checks whether this declaration was properly parsed
         * 
         * @return True if valid, False if not
         */
        public boolean isValid() {
            return name != null && returnType != null;
        }

        /**
         * Matches the full method signature and method name
         * 
         * @param m method to match
         * @return True if the method matches, False if not
         */
        public boolean match(Method m) {
            return m.getName().equals(name) && matchSignature(m);
        }

        /**
         * Matches only the signature of the method (modifiers, field type)
         * This is used to find alternative candidates for a method name
         * 
         * @param m method to match
         * @return True if the signature matches, False if not
         */
        public boolean matchSignature(Method m) {
            if (m.getReturnType() != returnType)
                return false;

            if (!compareModifiers(m.getModifiers(), modifiers)) {
                return false;
            }

            Class<?>[] m_params = m.getParameterTypes();
            if (m_params.length != parameterTypes.length) {
                return false;
            }
            for (int i = 0; i < m_params.length; i++) {
                if (m_params[i] != parameterTypes[i]) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public String toString() {
            return declaration;
        }
    }
}
