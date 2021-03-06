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
package br.com.anteros.persistence.apt.codegen;

import java.util.Collection;

import br.com.anteros.persistence.apt.annotation.InjectApt;
import br.com.anteros.persistence.apt.annotation.NamedApt;

/**
 * SupertypeSerializer is a {@link Serializer} implementation for supertypes
 *
 * @author tiwe
 *
 */
public final class SupertypeSerializer extends EntitySerializer{

    /**
     * Create a new SupertypeSerializer instance
     * 
     * @param typeMappings
     * @param keywords
     */
    @InjectApt
    public SupertypeSerializer(TypeMappings typeMappings, @NamedApt("keywords") Collection<String> keywords) {
        super(typeMappings, keywords);
    }

}
