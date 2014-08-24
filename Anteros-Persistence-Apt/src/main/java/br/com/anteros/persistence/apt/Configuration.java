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
import java.util.List;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

import br.com.anteros.persistence.apt.codegen.EntityType;
import br.com.anteros.persistence.apt.codegen.QueryTypeFactory;
import br.com.anteros.persistence.apt.codegen.Serializer;
import br.com.anteros.persistence.apt.codegen.SerializerConfig;
import br.com.anteros.persistence.apt.codegen.TypeMappings;
import br.com.anteros.persistence.dsl.osql.util.Annotations;

/**
 * Configuration defines the configuration options for APT based Querydsl code generation
 *
 * @author tiwe
 *
 */
public interface Configuration {

    /**
     * @return
     */
    boolean isUnknownAsEmbedded();

    /**
     * @return
     */
    TypeMappings getTypeMappings();

    /**
     * @param e
     * @param elements
     * @return
     */
    VisitorConfig getConfig(TypeElement e, List<? extends Element> elements);

    /**
     * @return
     */
    Serializer getDTOSerializer();

    /**
     * @return
     */
    
    Class<? extends Annotation> getEntitiesAnnotation();

    /**
     * @return
     */
    
    Class<? extends Annotation> getEmbeddedAnnotation();

    /**
     * @return
     */
    
    Class<? extends Annotation> getEmbeddableAnnotation();

    /**
     * @return
     */
    Serializer getEmbeddableSerializer();

    /**
     * @return
     */
    Class<? extends Annotation> getEntityAnnotation();

    /**
     * @return
     */
    Class<? extends Annotation> getAlternativeEntityAnnotation();

    /**
     * @return
     */
    Set<Class<? extends Annotation>> getEntityAnnotations();

    /**
     * @return
     */
    Serializer getEntitySerializer();

    /**
     * @return
     */
    String getNamePrefix();

    /**
     * @return
     */
    String getNameSuffix();

    /**
     * @param entityType
     * @return
     */
    SerializerConfig getSerializerConfig(EntityType entityType);

    /**
     * @return
     */
    
    Class<? extends Annotation> getSkipAnnotation();

    /**
     * @return
     */
    
    Class<? extends Annotation> getSuperTypeAnnotation();

    /**
     * @return
     */
    Serializer getSupertypeSerializer();

    /**
     * @param field
     * @return
     */
    boolean isBlockedField(VariableElement field);

    /**
     * @param getter
     * @return
     */
    boolean isBlockedGetter(ExecutableElement getter);

    /**
     * @return
     */
    boolean isUseFields();

    /**
     * @return
     */
    boolean isUseGetters();

    /**
     * @param constructor
     * @return
     */
    boolean isValidConstructor(ExecutableElement constructor);

    /**
     * @param field
     * @return
     */
    boolean isValidField(VariableElement field);

    /**
     * @param getter
     * @return
     */
    boolean isValidGetter(ExecutableElement getter);

    /**
     * @return
     */
    Collection<String> getKeywords();

    /**
     * @return
     */
    QueryTypeFactory getQueryTypeFactory();

    /**
     * @param packageName
     */
    void addExcludedPackage(String packageName);

    /**
     * @param className
     */
    void addExcludedClass(String className);

    /**
     * @param method
     * @return
     */
    TypeMirror getRealType(ExecutableElement method);

    /**
     * @param field
     * @return
     */
    TypeMirror getRealType(VariableElement field);

    /**
     * @param packageName
     * @return
     */
    boolean isExcludedPackage(String packageName);

    /**
     * @param className
     * @return
     */
    boolean isExcludedClass(String className);

    /**
     * @param element
     * @param annotations
     */
    void inspect(Element element, Annotations annotations);

}
