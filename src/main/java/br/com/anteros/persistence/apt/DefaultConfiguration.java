/*
 * Copyright 2011, Mysema Ltd
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
package br.com.anteros.persistence.apt;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

import br.com.anteros.persistence.apt.codegen.CodegenModule;
import br.com.anteros.persistence.apt.codegen.EmbeddableSerializer;
import br.com.anteros.persistence.apt.codegen.EntitySerializer;
import br.com.anteros.persistence.apt.codegen.EntityType;
import br.com.anteros.persistence.apt.codegen.ProjectionSerializer;
import br.com.anteros.persistence.apt.codegen.QueryTypeFactory;
import br.com.anteros.persistence.apt.codegen.Serializer;
import br.com.anteros.persistence.apt.codegen.SerializerConfig;
import br.com.anteros.persistence.apt.codegen.SimpleSerializerConfig;
import br.com.anteros.persistence.apt.codegen.SupertypeSerializer;
import br.com.anteros.persistence.apt.codegen.TypeMappings;
import br.com.anteros.persistence.apt.codegen.model.ClassType;
import br.com.anteros.persistence.dsl.osql.annotations.Config;
import br.com.anteros.persistence.dsl.osql.annotations.QueryProjection;
import br.com.anteros.persistence.dsl.osql.annotations.QueryType;
import br.com.anteros.persistence.dsl.osql.types.Expression;
import br.com.anteros.persistence.dsl.osql.util.Annotations;
import static br.com.anteros.persistence.apt.APTOptions.*;

import com.google.common.base.Strings;

/**
 * DefaultConfiguration is a simple implementation of the {@link Configuration} interface
 *
 * @author tiwe
 *
 */
public class DefaultConfiguration implements Configuration {

    private static final String DEFAULT_SEPARATOR = ",";

    private boolean unknownAsEmbedded;

    private final CodegenModule module = new CodegenModule();

    private final SerializerConfig defaultSerializerConfig;

    private final Map<String, SerializerConfig> packageToConfig = new HashMap<String, SerializerConfig>();

    protected final Class<? extends Annotation> entityAnn;

    private final Set<String> excludedPackages, excludedClasses;

    private final Set<String> includedPackages, includedClasses;

    
    protected final Class<? extends Annotation> entitiesAnn, superTypeAnn, embeddedAnn, embeddableAnn, skipAnn;

    
    protected Class<? extends Annotation> altEntityAnn;

    private final Set<Class<? extends Annotation>> entityAnnotations = new HashSet<Class<? extends Annotation>>();

    private final Map<String, SerializerConfig> typeToConfig = new HashMap<String, SerializerConfig>();

    private boolean useFields = true, useGetters = true;

    public DefaultConfiguration(
            RoundEnvironment roundEnv,
            Map<String, String> options,
            Collection<String> keywords,
             Class<? extends Annotation> entitiesAnn,
            Class<? extends Annotation> entityAnn,
             Class<? extends Annotation> superTypeAnn,
             Class<? extends Annotation> embeddableAnn,
             Class<? extends Annotation> embeddedAnn,
             Class<? extends Annotation> skipAnn) {
        this.excludedClasses = new HashSet<String>();
        this.excludedPackages = new HashSet<String>();
        this.includedClasses = new HashSet<String>();
        this.includedPackages = new HashSet<String>();
        module.bind(RoundEnvironment.class, roundEnv);
        module.bind(CodegenModule.KEYWORDS, keywords);
        this.entitiesAnn = entitiesAnn;
        this.entityAnn = entityAnn;
        this.superTypeAnn = superTypeAnn;
        this.embeddableAnn = embeddableAnn;
        this.embeddedAnn = embeddedAnn;
        this.skipAnn = skipAnn;

        entityAnnotations.add(entityAnn);
        if (superTypeAnn != null) {
            entityAnnotations.add(superTypeAnn);
        }
        if (embeddableAnn != null) {
            entityAnnotations.add(embeddableAnn);
        }
        for (Element element : roundEnv.getElementsAnnotatedWith(Config.class)) {
            Config dslConfig = element.getAnnotation(Config.class);
            SerializerConfig config = SimpleSerializerConfig.getConfig(dslConfig);
            if (element instanceof PackageElement) {
                PackageElement packageElement = (PackageElement)element;
                packageToConfig.put(packageElement.getQualifiedName().toString(), config);
            } else if (element instanceof TypeElement) {
                TypeElement typeElement = (TypeElement)element;
                typeToConfig.put(typeElement.getQualifiedName().toString(), config);
            }
        }
        boolean entityAccessors = false;
        boolean listAccessors = false;
        boolean mapAccessors = false;
        boolean createDefaultVariable = true;

        if (options.containsKey(DSL_ENTITY_ACCESSORS)) {
            entityAccessors = Boolean.valueOf(options.get(DSL_ENTITY_ACCESSORS));
        }
        if (options.containsKey(DSL_LIST_ACCESSORS)) {
            listAccessors = Boolean.valueOf(options.get(DSL_LIST_ACCESSORS));
        }
        if (options.containsKey(DSL_MAP_ACCESSORS)) {
            mapAccessors = Boolean.valueOf(options.get(DSL_MAP_ACCESSORS));
        }
        if (options.containsKey(DSL_CREATE_DEFAULT_VARIABLE)) {
            createDefaultVariable = Boolean.valueOf(options.get(DSL_CREATE_DEFAULT_VARIABLE));
        }
        if (options.containsKey(DSL_PACKAGE_SUFFIX)) {
            module.bind(CodegenModule.PACKAGE_SUFFIX, Strings.nullToEmpty(options.get(DSL_PACKAGE_SUFFIX)));
        }
        if (options.containsKey(DSL_PREFIX)) {
            module.bind(CodegenModule.PREFIX, Strings.nullToEmpty(options.get(DSL_PREFIX)));
        }
        if (options.containsKey(DSL_SUFFIX)) {
            module.bind(CodegenModule.SUFFIX, Strings.nullToEmpty(options.get(DSL_SUFFIX)));
        }
        if (options.containsKey(DSL_UNKNOWN_AS_EMBEDDABLE)) {
            unknownAsEmbedded = Boolean.valueOf(options.get(DSL_UNKNOWN_AS_EMBEDDABLE));
        }

        if (options.containsKey(DSL_EXCLUDED_PACKAGES)) {
            String packageString = options.get(DSL_EXCLUDED_PACKAGES);
            if (!Strings.isNullOrEmpty(packageString)) {
                for (String packageName : packageString.split(DEFAULT_SEPARATOR)) {
                    excludedPackages.add(packageName);
                }
            }
        }

        if (options.containsKey(DSL_EXCLUDED_CLASSES)) {
            String classString = options.get(DSL_EXCLUDED_CLASSES);
            if (!Strings.isNullOrEmpty(classString)) {
                for (String className : classString.split(DEFAULT_SEPARATOR)) {
                    excludedClasses.add(className);
                }
            }
        }

        if (options.containsKey(DSL_INCLUDED_PACKAGES)) {
            String packageString = options.get(DSL_INCLUDED_PACKAGES);
            if (!Strings.isNullOrEmpty(packageString)) {
                for (String packageName : packageString.split(DEFAULT_SEPARATOR)) {
                    includedPackages.add(packageName);
                }
            }
        }

        if (options.containsKey(DSL_INCLUDED_CLASSES)) {
            String classString = options.get(DSL_INCLUDED_CLASSES);
            if (!Strings.isNullOrEmpty(classString)) {
                for (String className : classString.split(DEFAULT_SEPARATOR)) {
                    includedClasses.add(className);
                }
            }
        }

        defaultSerializerConfig = new SimpleSerializerConfig(entityAccessors, listAccessors,
                mapAccessors, createDefaultVariable, "");

    }

    @Override
    public void addExcludedClass(String className) {
        excludedClasses.add(className);
    }

    @Override
    public void addExcludedPackage(String packageName) {
        excludedPackages.add(packageName);
    }

    @Override
    public VisitorConfig getConfig(TypeElement e, List<? extends Element> elements) {
        if (useFields) {
            if (useGetters) {
                return VisitorConfig.ALL;
            } else {
                return VisitorConfig.FIELDS_ONLY;
            }
        } else if (useGetters) {
            return VisitorConfig.METHODS_ONLY;
        } else {
            return VisitorConfig.NONE;
        }
    }

    @Override
    public Serializer getDTOSerializer() {
        return module.get(ProjectionSerializer.class);
    }

    @Override
    
    public Class<? extends Annotation> getEntitiesAnnotation() {
        return entitiesAnn;
    }

    @Override
    
    public Class<? extends Annotation> getEmbeddableAnnotation() {
        return embeddableAnn;
    }

    @Override
    public Serializer getEmbeddableSerializer() {
        return module.get(EmbeddableSerializer.class);
    }

    @Override
    public Class<? extends Annotation> getEntityAnnotation() {
        return entityAnn;
    }

    @Override
    public Class<? extends Annotation> getAlternativeEntityAnnotation() {
        return altEntityAnn;
    }

    public void setAlternativeEntityAnnotation(Class<? extends Annotation> ann) {
        altEntityAnn = ann;
        entityAnnotations.add(ann);
    }

    @Override
    
    public Class<? extends Annotation> getEmbeddedAnnotation() {
        return embeddedAnn;
    }

    @Override
    public Set<Class<? extends Annotation>> getEntityAnnotations() {
        return entityAnnotations;
    }

    @Override
    public Serializer getEntitySerializer() {
        return module.get(EntitySerializer.class);
    }

    @Override
    public String getNamePrefix() {
        return module.get(String.class, "prefix");
    }

    @Override
    public SerializerConfig getSerializerConfig(EntityType entityType) {
        if (typeToConfig.containsKey(entityType.getFullName())) {
            return typeToConfig.get(entityType.getFullName());
        } else if (packageToConfig.containsKey(entityType.getPackageName())) {
            return packageToConfig.get(entityType.getPackageName());
        } else {
            return defaultSerializerConfig;
        }
    }

    @Override
    
    public Class<? extends Annotation> getSkipAnnotation() {
        return skipAnn;
    }

    @Override
    
    public Class<? extends Annotation> getSuperTypeAnnotation() {
        return superTypeAnn;
    }

    @Override
    public Serializer getSupertypeSerializer() {
        return module.get(SupertypeSerializer.class);
    }

    @Override
    public void inspect(Element element, Annotations annotations) {
        // do nothing
    }

    @Override
    public boolean isBlockedField(VariableElement field) {
        if (field.getAnnotation(QueryType.class) != null) {
            return false;
        } else {
            return field.getAnnotation(skipAnn) != null
                || field.getModifiers().contains(Modifier.TRANSIENT)
                || field.getModifiers().contains(Modifier.STATIC);
        }
    }

    @Override
    public boolean isBlockedGetter(ExecutableElement getter) {
        if (getter.getAnnotation(QueryType.class) != null) {
            return false;
        } else {
            return getter.getAnnotation(skipAnn) != null
                || getter.getModifiers().contains(Modifier.STATIC);
        }
    }

    @Override
    public boolean isUseFields() {
        return useFields;
    }

    @Override
    public boolean isUseGetters() {
        return useGetters;
    }

    @Override
    public boolean isValidConstructor(ExecutableElement constructor) {
        return constructor.getModifiers().contains(Modifier.PUBLIC)
            && constructor.getAnnotation(QueryProjection.class) != null
            && !constructor.getParameters().isEmpty();
    }

    @Override
    public boolean isValidField(VariableElement field) {
        if (field.getAnnotation(QueryType.class) != null) {
            return true;
        } else {
            return field.getAnnotation(skipAnn) == null
                && !field.getModifiers().contains(Modifier.TRANSIENT)
                && !field.getModifiers().contains(Modifier.STATIC);
        }
    }

    @Override
    public boolean isValidGetter(ExecutableElement getter) {
        if (getter.getAnnotation(QueryType.class) != null) {
            return true;
        } else {
            return getter.getAnnotation(skipAnn) == null
                && !getter.getModifiers().contains(Modifier.STATIC);
        }
    }

    public void setNamePrefix(String namePrefix) {
        module.bind(CodegenModule.PREFIX, namePrefix);
    }

    public void setUseFields(boolean b) {
        this.useFields = b;
    }

    public void setUseGetters(boolean b) {
        this.useGetters = b;
    }

    @Override
    public TypeMappings getTypeMappings() {
        return module.get(TypeMappings.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<String> getKeywords() {
        return module.get(Collection.class, CodegenModule.KEYWORDS);
    }

    @Override
    public String getNameSuffix() {
        return module.get(String.class, CodegenModule.SUFFIX);
    }

    public void setNameSuffix(String nameSuffix) {
        module.bind(CodegenModule.SUFFIX, nameSuffix);
    }

    public <T> void addCustomType(Class<T> type, Class<? extends Expression<T>> queryType) {
        module.get(TypeMappings.class).register(new ClassType(type), new ClassType(queryType));
    }

    @Override
    public QueryTypeFactory getQueryTypeFactory() {
        return module.get(QueryTypeFactory.class);
    }

    @Override
    public boolean isExcludedPackage(String packageName) {
        if (!includedPackages.isEmpty()) {
            boolean included = false;
            for (String includedPackage : includedPackages) {
                if (packageName.startsWith(includedPackage)) {
                    included = true;
                    break;
                }
            }
            if (!included) {
                return true;
            }
        }
        if (!excludedPackages.isEmpty()) {
            for (String excludedPackage : excludedPackages) {
                if (packageName.startsWith(excludedPackage)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean isExcludedClass(String className) {
        if (!includedClasses.isEmpty() && !includedClasses.contains(className)) {
            return true;
        } else {
            return excludedClasses.contains(className);
        }
    }

    @Override
    public TypeMirror getRealType(ExecutableElement method) {
        return null;
    }

    @Override
    public TypeMirror getRealType(VariableElement field) {
        return null;
    }

    @Override
    public boolean isUnknownAsEmbedded() {
        return unknownAsEmbedded;
    }

    public void setUnknownAsEmbedded(boolean unknownAsEmbedded) {
        this.unknownAsEmbedded = unknownAsEmbedded;
    }

}
