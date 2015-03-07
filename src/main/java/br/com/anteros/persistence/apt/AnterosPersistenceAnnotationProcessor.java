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

import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;

import br.com.anteros.persistence.metadata.annotation.Entity;
import br.com.anteros.persistence.metadata.annotation.MappedSuperclass;
import br.com.anteros.persistence.metadata.annotation.Transient;


@SupportedAnnotationTypes("br.com.anteros.persistence.metadata.annotation.*")
public class AnterosPersistenceAnnotationProcessor extends AbstractDslProcessor {

    @Override
    protected Configuration createConfiguration(RoundEnvironment roundEnv) {
        Class<? extends Annotation> entity = Entity.class;
        Class<? extends Annotation> superType = MappedSuperclass.class;
        Class<? extends Annotation> embeddable = null;
        Class<? extends Annotation> embedded = null;
        Class<? extends Annotation> skip = Transient.class;
        return new AnterosAptConfiguration(roundEnv, processingEnv.getOptions(),
                entity, superType, embeddable, embedded, skip);
    }

}
