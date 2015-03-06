package br.com.anteros.persistence.apt.codegen.model;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import br.com.anteros.persistence.apt.codegen.support.ClassUtils;

public class IndexClassType implements Type {

    private final TypeCategory category;

    private final Class<?> javaClass;

    private final String className;

    private final List<Type> parameters;

    private Type arrayType, componentType;

	private String indexName;

    public IndexClassType(Class<?> javaClass,String indexName, Type... parameters) {
        this(TypeCategory.SIMPLE, javaClass,indexName, Arrays.asList(parameters));
    }

    public IndexClassType(TypeCategory category, Class<?> clazz, String indexName, Type... parameters) {
        this(category, clazz, indexName, Arrays.asList(parameters));
    }

    public IndexClassType(TypeCategory category, Class<?> clazz, String indexName, List<Type> parameters) {
        this.category = category;
        this.javaClass = clazz;
        this.parameters = parameters;
        this.className = ClassUtils.getFullName(javaClass);
        this.indexName = indexName;
    }

    
    @Override
    public Type as(TypeCategory c) {
        if (category == c) {
            return this;
        } else {
            return new ClassType(c, javaClass);
        }
    }

    @Override
    public Type asArrayType() {
        if (arrayType == null) {
            String fullName = ClassUtils.getFullName(javaClass) + "[]";
            String simpleName = javaClass.getSimpleName() + "[]";
            arrayType = new SimpleType(TypeCategory.ARRAY, fullName, getPackageName(), simpleName,
                    false, false);
        }
        return arrayType;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof Type) {
            Type t = (Type) o;
            return t.getFullName().equals(className) && t.getParameters().equals(parameters);
        } else {
            return false;
        }
    }

    public TypeCategory getCategory() {
        return category;
    }

    @Override
    public Type getComponentType() {
        Class<?> clazz = javaClass.getComponentType();
        if (clazz != null && componentType == null) {
            componentType = new ClassType(TypeCategory.SIMPLE, clazz);
        }
        return componentType;
    }

    @Override
    public String getFullName() {
        return className;
    }

    @Override
    public String getGenericName(boolean asArgType) {
        return getGenericName(asArgType, Collections.singleton("java.lang"),
                Collections.<String> emptySet());
    }

    @Override
    public String getGenericName(boolean asArgType, Set<String> packages, Set<String> classes) {
        if (parameters.isEmpty()) {
            return ClassUtils.getName(javaClass, packages, classes);
        } else {
            StringBuilder builder = new StringBuilder();
            builder.append(ClassUtils.getName(javaClass, packages, classes));
            builder.append("<");
            boolean first = true;
            for (Type parameter : parameters) {
                if (!first) {
                    builder.append(", ");
                }
                if (parameter == null || parameter.getFullName().equals(getFullName())) {
                    builder.append("?");
                } else {
                    builder.append(parameter.getGenericName(false, packages, classes));
                }
                first = false;
            }
            builder.append(">");
            return builder.toString();
        }
    }

    public Class<?> getJavaClass() {
        return javaClass;
    }

    @Override
    public String getPackageName() {
        return ClassUtils.getPackageName(javaClass);
    }

    @Override
    public List<Type> getParameters() {
        return parameters;
    }

    @Override
    public String getRawName(Set<String> packages, Set<String> classes) {
        return ClassUtils.getName(javaClass, packages, classes);
    }

    @Override
    public String getSimpleName() {
        return javaClass.getSimpleName();
    }

    @Override
    public int hashCode() {
        return className.hashCode();
    }

    @Override
    public boolean isFinal() {
        return Modifier.isFinal(javaClass.getModifiers());
    }

    @Override
    public boolean isPrimitive() {
        return javaClass.isPrimitive();
    }

    @Override
    public String toString() {
        return getGenericName(true);
    }

	public String getIndexName() {
		return indexName;
	}

}
