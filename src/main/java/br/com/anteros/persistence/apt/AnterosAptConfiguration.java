/*******************************************************************************
 * Copyright 2012 Anteros Tecnologia
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package br.com.anteros.persistence.apt;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

import com.google.common.collect.ImmutableList;

import br.com.anteros.persistence.apt.codegen.Keywords;
import br.com.anteros.persistence.dsl.osql.annotations.PropertyType;
import br.com.anteros.persistence.dsl.osql.annotations.QueryEntities;
import br.com.anteros.persistence.dsl.osql.util.Annotations;
import br.com.anteros.persistence.metadata.annotation.Column;
import br.com.anteros.persistence.metadata.annotation.CompositeId;
import br.com.anteros.persistence.metadata.annotation.Enumerated;
import br.com.anteros.persistence.metadata.annotation.Fetch;
import br.com.anteros.persistence.metadata.annotation.ForeignKey;
import br.com.anteros.persistence.metadata.annotation.GeneratedValue;
import br.com.anteros.persistence.metadata.annotation.Id;
import br.com.anteros.persistence.metadata.annotation.Index;
import br.com.anteros.persistence.metadata.annotation.JoinColumn;
import br.com.anteros.persistence.metadata.annotation.MapKeyEnumerated;
import br.com.anteros.persistence.metadata.annotation.Temporal;
import br.com.anteros.persistence.metadata.annotation.Version;
import br.com.anteros.persistence.metadata.annotation.type.FetchMode;

/**
 * Configuration for {@link AnterosPersistenceAnnotationProcessor}
 *
 * @author tiwe  modified by: Edson Martins
 * 
 * @see AnterosPersistenceAnnotationProcessor
 */
public class AnterosAptConfiguration extends DefaultConfiguration {

	private final List<Class<? extends Annotation>> annotations;

	private final Types types;

	public AnterosAptConfiguration(RoundEnvironment roundEnv, Map<String, String> options, Class<? extends Annotation> entityAnn,
			Class<? extends Annotation> superTypeAnn, Class<? extends Annotation> embeddableAnn, Class<? extends Annotation> embeddedAnn,
			Class<? extends Annotation> skipAnn) {
		super(roundEnv, options, Keywords.JPA, QueryEntities.class, entityAnn, superTypeAnn, embeddableAnn, embeddedAnn, skipAnn);
		this.annotations = getAnnotations();
		this.types = AbstractDslProcessor.TYPES;
	}

	@SuppressWarnings("unchecked")
	protected List<Class<? extends Annotation>> getAnnotations() {
		return ImmutableList.of(Column.class, Enumerated.class, GeneratedValue.class, Id.class, CompositeId.class, JoinColumn.class, Fetch.class,
				MapKeyEnumerated.class, ForeignKey.class, Temporal.class, Version.class, Index.class);
	}

	@Override
	public VisitorConfig getConfig(TypeElement e, List<? extends Element> elements) {
		boolean fields = false, methods = false;
		for (Element element : elements) {
			if (hasRelevantAnnotation(element)) {
				fields |= element.getKind().equals(ElementKind.FIELD);
				methods |= element.getKind().equals(ElementKind.METHOD);
			}
		}
		return VisitorConfig.get(fields, methods);
	}

	@Override
	public TypeMirror getRealType(ExecutableElement method) {
		return getRealElementType(method);
	}

	@Override
	public TypeMirror getRealType(VariableElement field) {
		return getRealElementType(field);
	}

	private TypeMirror getRealElementType(Element element) {
		AnnotationMirror mirror = getFetchAnnotationMirror(element, FetchMode.ONE_TO_MANY);

		if (mirror == null) {
			mirror = getFetchAnnotationMirror(element, FetchMode.MANY_TO_MANY);
		}
		if (mirror != null) {
			TypeMirror typeArg = TypeUtils.getAnnotationValueAsTypeMirror(mirror, "targetEntity");
			TypeMirror erasure = types.erasure(element.asType());
			TypeElement typeElement = (TypeElement) types.asElement(erasure);
			if (typeElement != null && typeArg != null) {
				if (typeElement.getTypeParameters().size() == 1) {
					return types.getDeclaredType(typeElement, typeArg);
				} else if (typeElement.getTypeParameters().size() == 2) {
					if (element.asType() instanceof DeclaredType) {
						TypeMirror first = ((DeclaredType) element.asType()).getTypeArguments().get(0);
						return types.getDeclaredType(typeElement, first, typeArg);
					}
				}
			}
		}

		return null;
	}

	protected AnnotationMirror getFetchAnnotationMirror(Element element, FetchMode mode) {
		Fetch fetch = element.getAnnotation(Fetch.class);
		if (fetch == null)
			return null;
		if (fetch.mode() == mode) {
			return TypeUtils.getAnnotationMirrorOfType(element, Fetch.class);
		}
		return null;
	}

	@Override
	public void inspect(Element element, Annotations annotations) {
		Temporal temporal = element.getAnnotation(Temporal.class);
		Fetch fetch = element.getAnnotation(Fetch.class);
		boolean isElementCollection = false;
		if (fetch != null) {
			isElementCollection = (fetch.mode() == FetchMode.ELEMENT_COLLECTION);
		}
		if (temporal != null && !isElementCollection) {
			PropertyType propertyType = null;
			switch (temporal.value()) {
			case DATE:
				propertyType = PropertyType.DATE;
				break;
			case TIME:
				propertyType = PropertyType.TIME;
				break;
			case DATE_TIME:
				propertyType = PropertyType.DATETIME;
			}
			annotations.addAnnotation(new QueryTypeImpl(propertyType));
		}
	}

	private boolean hasRelevantAnnotation(Element element) {
		for (Class<? extends Annotation> annotation : annotations) {
			if (element.getAnnotation(annotation) != null) {
				return true;
			}
		}
		return false;
	}

}
