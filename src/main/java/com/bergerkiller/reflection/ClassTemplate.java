package com.bergerkiller.reflection;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.conversion.Converter;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.reflection.declarations.ClassResolver;
import com.bergerkiller.reflection.declarations.FieldDeclaration;
import com.bergerkiller.reflection.declarations.MethodDeclaration;
import com.bergerkiller.reflection.net.minecraft.server.NMSDataWatcherObject;

import net.sf.cglib.asm.Type;
import net.sf.cglib.core.Signature;

import org.objenesis.ObjenesisHelper;
import org.objenesis.instantiator.ObjectInstantiator;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
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
    private List<FieldDeclaration> typeFields;
    private LinkedList<MethodDeclaration> typeMethods;
    private Queue<FieldDeclaration> nextFieldQueue;
    private ClassResolver resolver;

    /**
     * Initializes a new ClassTemplate not pointing to any Class<br>
     * Call setClass before use.
     */
    protected ClassTemplate() {
        this.resolver = new ClassResolver();
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
        this.resolver = new ClassResolver();
        this.resolver.addClassImports(type);
        return this;
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
     * TODO: Gotta get rid of this!!!
     */
    @Deprecated
    public ClassResolver getResolver() {
        return this.resolver;
    }

    /**
     * Adds a new import package or class path used during class lookup
     * 
     * @param importPath to add
     * @return this class template
     */
    public ClassTemplate<T> addImport(String importPath) {
        this.resolver.addImport(importPath);
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
                fields = Collections.unmodifiableList(ReflectionUtil.fillFields(new ArrayList<SafeField<?>>(), type));
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
        Class<?> type = this.resolver.resolveClass(className);
        if (type == null && logErrors) {
            Logging.LOGGER_REFLECTION.warning("Could not find type: " + className);
        }
        return type;
    }

    /**
     * Resolves the name of a Class type when used within this Template
     * 
     * @param type to resolve
     * @return class name
     */
    public String resolveClassName(Class<?> type) {
        return this.resolver.resolveName(type);
    }

    private void loadFields(boolean initNextQueue) {
        // Initialize field queue with fields if needed
        if (typeFields == null) {
            typeFields = new LinkedList<FieldDeclaration>();
            if (this.type != null) {
                for (Field f :  this.type.getDeclaredFields()) {
                    typeFields.add(new FieldDeclaration(resolver, f));
                }
            }
        }
        if (initNextQueue || nextFieldQueue == null) {
            nextFieldQueue = new LinkedList<FieldDeclaration>(typeFields);
        }
    }

    private void addMethods(Class<?> type, HashSet<Signature> addedSignatures) {
        // Get sorted array of methods
        Method[] declMethods = type.getDeclaredMethods();

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
                typeMethods.add(new MethodDeclaration(resolver, m));
            }
        }
    }

    private void loadMethods() {
        // Initialize field queue with fields if needed
        if (typeMethods == null) {
            typeMethods = new LinkedList<MethodDeclaration>();

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
        FieldDeclaration declare = new FieldDeclaration(resolver, declaration);
        if (!declare.isValid()) {
            logFieldWarning(declaration, "could not be parsed");
            return new SafeField<F>(null);
        }
        if (!declare.isResolved()) {
            logFieldWarning(declare.toString(), "has some unresolved types");
            return new SafeField<F>(null);
        }

        loadFields(true);

        // Find the field exactly
        for (FieldDeclaration field : nextFieldQueue) {
            if (declare.match(field)) {

                // Skip until this field
                while (nextFieldQueue.remove() != field);

                // Done!
                return new SafeField<F>(field.field);
            }
        }

        // List close matches
        List<FieldDeclaration> similar = new ArrayList<FieldDeclaration>();
        for (FieldDeclaration field : typeFields) {
            if (declare.matchSignature(field)) {
                similar.add(field);
            }
        }

        // Maybe a field with this same name exists?
        if (similar.size() == 0) {
            for (FieldDeclaration field : typeFields) {
                if (declare.name.match(field.name)) {
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
            for (FieldDeclaration field : similar) {
                Logging.LOGGER_REFLECTION.warning("  - " + field.toString());
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
            FieldDeclaration declare = new FieldDeclaration(resolver, declaration);
            if (!declare.isValid()) {
                logFieldWarning(declaration, "could not be parsed");
                return new SafeField<F>(null);
            }
            if (!declare.isResolved()) {
                logFieldWarning(declare.toString(), "has some unresolved types");
                return new SafeField<F>(null);
            }

            // Check if the field matches the very next item
            FieldDeclaration next = nextFieldQueue.peek();
            if (declare.match(next)) {
                nextFieldQueue.remove();
            } else {
                // Find similar matches until the field is found
                next = null;
                List<FieldDeclaration> skipped = new ArrayList<FieldDeclaration>();
                while (!nextFieldQueue.isEmpty()) {
                    FieldDeclaration ff = nextFieldQueue.remove();
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
                    for (FieldDeclaration f : skipped) {
                        Logging.LOGGER_REFLECTION.warning("  - " + f.toString());
                    }
                }
            }

            // Warn about field name changes
            if (!declare.match(next)) {
                logFieldWarning(declaration, "has an incorrect name. New name: " + next.name.toString());
            }

            return new SafeField<F>(next.field);
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
        FieldDeclaration declare = new FieldDeclaration(resolver, declaration);
        if (!declare.isValid()) {
            logFieldWarning(declaration, "could not be parsed");
            return new SafeField<F>(null);
        }
        if (!declare.isResolved()) {
            logFieldWarning(declare.toString(), "has some unresolved types");
            return new SafeField<F>(null);
        }

        loadFields(false);

        // Find the field exactly
        for (FieldDeclaration field : typeFields) {
            if (declare.match(field)) {
                return new SafeField<F>(field.field);
            }
        }

        // List close matches
        List<FieldDeclaration> similar = new ArrayList<FieldDeclaration>();
        for (FieldDeclaration field : typeFields) {
            if (declare.matchSignature(field)) {
                similar.add(field);
            }
        }

        // Maybe a field with this same name and type exists?
        if (similar.size() == 0) {
            for (FieldDeclaration field : typeFields) {
                if (field.modifiers.isStatic() != declare.modifiers.isStatic())
                    continue;

                if (declare.name.match(field.name) && declare.type.match(field.type)) {
                    similar.add(field);
                }
            }
        }

        // Maybe the name changed?
        if (similar.size() == 0) {
            for (FieldDeclaration field : typeFields) {
                if (field.modifiers.isStatic() != declare.modifiers.isStatic())
                    continue;

                if (declare.type.match(field.type)) {
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
            for (FieldDeclaration field : similar) {
                Logging.LOGGER_REFLECTION.warning("  - " + field.toString());
            }
        }

        return new SafeField<F>(null);
    }

    public <M> MethodAccessor<M> selectMethod(String declaration) {
        return new SafeMethod<M>(selectRawMethod(declaration, true));
    }

    public Method selectRawMethod(String declaration, boolean logErrors) {
        // Parse the declaration
        MethodDeclaration declare = new MethodDeclaration(this.resolver, declaration);
        if (!declare.isValid()) {
            if (logErrors) {
                logMethodWarning(declaration, "could not be parsed");
            }
            return null;
        }
        if (!declare.isResolved()) {
            if (logErrors) {
                logMethodWarning(declare.toString(), "has some unresolved types");
            }
            return null;
        }
        return selectRawMethod(declare, logErrors);
    }

    public Method selectRawMethod(MethodDeclaration declare, boolean logErrors) {
        loadMethods();

        // Find the exact method
        for (MethodDeclaration method : typeMethods) {
            if (declare.match(method)) {
                try {
                    method.method.setAccessible(true);
                    return method.method;
                } catch (SecurityException ex) {
                    ex.printStackTrace();
                }
            }
        }

        // Log the close matches
        List<MethodDeclaration> similar = new ArrayList<MethodDeclaration>();
        for (MethodDeclaration method : typeMethods) {
            if (declare.matchSignature(method)) {
                similar.add(method);
            }
        }

        if (logErrors) {
            if (similar.size() == 0) {
                // No alternative found, uh oh!
                logMethodWarning(declare.toString(), "not found; no alternatives available. (Removed?)");
            } else {
                // Log the close matches
                logMethodWarning(declare.toString(), "not found; there are " + similar.size() + " close matches:");
                for (MethodDeclaration method : similar) {
                    Logging.LOGGER_REFLECTION.warning("  - " + method.toString());
                }
            }
        }
        return null;
    }

}
