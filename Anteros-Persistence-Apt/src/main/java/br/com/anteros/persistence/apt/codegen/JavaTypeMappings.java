/*
 * Copyright 2012, Mysema Ltd
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

import br.com.anteros.persistence.apt.codegen.model.TypeCategory;
import br.com.anteros.persistence.dsl.osql.types.Expression;
import br.com.anteros.persistence.dsl.osql.types.Path;
import br.com.anteros.persistence.dsl.osql.types.expr.BooleanExpression;
import br.com.anteros.persistence.dsl.osql.types.expr.ComparableExpression;
import br.com.anteros.persistence.dsl.osql.types.expr.EnumExpression;
import br.com.anteros.persistence.dsl.osql.types.expr.NumberExpression;
import br.com.anteros.persistence.dsl.osql.types.expr.SimpleExpression;
import br.com.anteros.persistence.dsl.osql.types.expr.StringExpression;
import br.com.anteros.persistence.dsl.osql.types.expr.TemporalExpression;
import br.com.anteros.persistence.dsl.osql.types.path.BooleanPath;
import br.com.anteros.persistence.dsl.osql.types.path.ComparablePath;
import br.com.anteros.persistence.dsl.osql.types.path.DatePath;
import br.com.anteros.persistence.dsl.osql.types.path.DateTimePath;
import br.com.anteros.persistence.dsl.osql.types.path.EnumPath;
import br.com.anteros.persistence.dsl.osql.types.path.NumberPath;
import br.com.anteros.persistence.dsl.osql.types.path.SimplePath;
import br.com.anteros.persistence.dsl.osql.types.path.StringPath;
import br.com.anteros.persistence.dsl.osql.types.path.TimePath;
import br.com.anteros.persistence.dsl.osql.types.template.BooleanTemplate;
import br.com.anteros.persistence.dsl.osql.types.template.ComparableTemplate;
import br.com.anteros.persistence.dsl.osql.types.template.DateTemplate;
import br.com.anteros.persistence.dsl.osql.types.template.DateTimeTemplate;
import br.com.anteros.persistence.dsl.osql.types.template.EnumTemplate;
import br.com.anteros.persistence.dsl.osql.types.template.NumberTemplate;
import br.com.anteros.persistence.dsl.osql.types.template.SimpleTemplate;
import br.com.anteros.persistence.dsl.osql.types.template.StringTemplate;
import br.com.anteros.persistence.dsl.osql.types.template.TimeTemplate;

/**
 * JavaTypeMappings defines mappings from {@link TypeCategory} instances to {@link Expression} types
 *
 * @author tiwe
 *
 */
public class JavaTypeMappings extends TypeMappings {

    public JavaTypeMappings() {
        register(TypeCategory.STRING,     StringExpression.class,     StringPath.class,     StringTemplate.class);
        register(TypeCategory.BOOLEAN,    BooleanExpression.class,    BooleanPath.class,    BooleanTemplate.class);
        register(TypeCategory.COMPARABLE, ComparableExpression.class, ComparablePath.class, ComparableTemplate.class);
        register(TypeCategory.ENUM,       EnumExpression.class,       EnumPath.class,       EnumTemplate.class);
        register(TypeCategory.DATE,       TemporalExpression.class,   DatePath.class,       DateTemplate.class);
        register(TypeCategory.DATETIME,   TemporalExpression.class,   DateTimePath.class,   DateTimeTemplate.class);
        register(TypeCategory.TIME,       TemporalExpression.class,   TimePath.class,       TimeTemplate.class);
        register(TypeCategory.NUMERIC,    NumberExpression.class,     NumberPath.class,     NumberTemplate.class);
        register(TypeCategory.SIMPLE,     SimpleExpression.class,     SimplePath.class,     SimpleTemplate.class);

        register(TypeCategory.ARRAY,      Expression.class,           SimplePath.class,     SimpleTemplate.class);
        register(TypeCategory.COLLECTION, Expression.class,           SimplePath.class,     SimpleTemplate.class);
        register(TypeCategory.SET,        Expression.class,           SimplePath.class,     SimpleTemplate.class);
        register(TypeCategory.LIST,       Expression.class,           SimplePath.class,     SimpleTemplate.class);
        register(TypeCategory.MAP,        Expression.class,           SimplePath.class,     SimpleTemplate.class);

        register(TypeCategory.CUSTOM,     Expression.class,           Path.class,           SimpleTemplate.class);
        register(TypeCategory.ENTITY,     Expression.class,           Path.class,           SimpleTemplate.class);
    }


}
