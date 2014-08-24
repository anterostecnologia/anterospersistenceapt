/*
 * Copyright 2010, Mysema Ltd
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package br.com.anteros.persistence.apt.codegen.support;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author tiwe
 * 
 */
public final class ClassUtils {

    private static final Set<String> JAVA_LANG = Collections.singleton("java.lang");
    
    public static String getName(Class<?> cl) {
        return getName(cl, JAVA_LANG, Collections.<String> emptySet());
    }

    public static String getFullName(Class<?> cl) {
        if (cl.isArray()) {
            return getFullName(cl.getComponentType()) + "[]";
        }
        final String name = cl.getName();
        if (name.indexOf('$') > 0) {
            return getFullName(cl.getDeclaringClass()) + "." + cl.getSimpleName();
        }
        return name;
    }

    public static String getPackageName(Class<?> cl) {
        while (cl.isArray()) {
            cl = cl.getComponentType();
        }
        final String name = cl.getName();
        final int i = name.lastIndexOf('.');
        if (i > 0) {
            return name.substring(0, i); 
        } else {
            return "";
        }
    }
    
    public static String getName(Class<?> cl, Set<String> packages, Set<String> classes) {
        if (cl.isArray()) {
            return getName(cl.getComponentType(), packages, classes) + "[]";
        }        
        if (cl.getName().indexOf('$') > 0) {
            return getName(cl.getDeclaringClass(), packages, classes) + "." + cl.getSimpleName();
        }
        final String canonicalName = cl.getName();
        final int i = canonicalName.lastIndexOf('.');
        if (i == -1) {    
            return canonicalName;        
        } else {
            final String packageName = canonicalName.substring(0, i);
            if (packages.contains(packageName) || classes.contains(canonicalName)) {
                return cl.getSimpleName();                        
            }  else {
                return canonicalName;
            }
        }
    }

    public static Class<?> normalize(Class<?> clazz) {
        if (List.class.isAssignableFrom(clazz)) {
            return List.class;
        } else if (Set.class.isAssignableFrom(clazz)) {
            return Set.class;
        } else if (Collection.class.isAssignableFrom(clazz)) {
            return Collection.class;
        } else if (Map.class.isAssignableFrom(clazz)) {
            return Map.class;
        // check for CGLIB generated classes
        } else if (clazz.getName().contains("$$")) {
            Class<?> zuper = clazz.getSuperclass();
            if (zuper != null && !Object.class.equals(zuper)) {
                return zuper;
            }
        }
        return clazz;
    }

    private ClassUtils() {
    }

}
