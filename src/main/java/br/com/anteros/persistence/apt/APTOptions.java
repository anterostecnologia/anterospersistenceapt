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

/**
 * APT options supported by Querydsl
 *
 * @author tiwe
 *
 */
public final class APTOptions {

    public static final String DSL_CREATE_DEFAULT_VARIABLE = "dsl.createDefaultVariable";

    public static final String DSL_PREFIX = "dsl.prefix";

    public static final String DSL_SUFFIX = "dsl.suffix";

    public static final String DSL_PACKAGE_SUFFIX = "dsl.packageSuffix";

    public static final String DSL_MAP_ACCESSORS = "dsl.mapAccessors";

    public static final String DSL_LIST_ACCESSORS = "dsl.listAccessors";

    public static final String DSL_ENTITY_ACCESSORS = "dsl.entityAccessors";

    public static final String DSL_EXCLUDED_PACKAGES = "dsl.excludedPackages";

    public static final String DSL_EXCLUDED_CLASSES = "dsl.excludedClasses";

    public static final String DSL_INCLUDED_PACKAGES = "dsl.includedPackages";

    public static final String DSL_INCLUDED_CLASSES = "dsl.includedClasses";

    public static final String DSL_UNKNOWN_AS_EMBEDDABLE = "dsl.unknownAsEmbeddable";

    private APTOptions() {}

}
