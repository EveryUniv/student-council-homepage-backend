/*
 *
 *  *
 *  *  *
 *  *  *  * Copyright 2019-2022 the original author or authors.
 *  *  *  *
 *  *  *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  *  * you may not use this file except in compliance with the License.
 *  *  *  * You may obtain a copy of the License at
 *  *  *  *
 *  *  *  *      https://www.apache.org/licenses/LICENSE-2.0
 *  *  *  *
 *  *  *  * Unless required by applicable law or agreed to in writing, software
 *  *  *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  *  *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  *  * See the License for the specific language governing permissions and
 *  *  *  * limitations under the License.
 *  *  *
 *  *
 *
 */

package com.dku.council.global.config.swagger;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.JavaType;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverter;
import io.swagger.v3.core.converter.ModelConverterContext;
import io.swagger.v3.core.util.AnnotationsUtils;
import io.swagger.v3.oas.models.media.Schema;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springdoc.core.providers.JavadocProvider;
import org.springdoc.core.providers.ObjectMapperProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@ConditionalOnExpression("${springdoc.api-docs.enabled:true}")
@RequiredArgsConstructor
public class JavadocPropertyCustomizer implements ModelConverter {

    private final JavadocProvider javadocProvider;
    private final ObjectMapperProvider objectMapperProvider;
	
    @Override
    public Schema resolve(AnnotatedType type, ModelConverterContext context, Iterator<ModelConverter> chain) {
        if (chain.hasNext()) {
            JavaType javaType = objectMapperProvider.jsonMapper().constructType(type.getType());
            if (javaType != null) {
                Class<?> cls = javaType.getRawClass();
                Schema<?> resolvedSchema = chain.next().resolve(type, context, chain);
                List<Field> fields = FieldUtils.getAllFieldsList(cls);
                if (!CollectionUtils.isEmpty(fields)) {
                    if (!type.isSchemaProperty()) {
                        Schema existingSchema = context.resolve(type);
                        setJavadocDescription(cls, fields, existingSchema);
                    } else if (resolvedSchema != null && resolvedSchema.get$ref() != null && resolvedSchema.get$ref().contains(AnnotationsUtils.COMPONENTS_REF)) {
                        String schemaName = resolvedSchema.get$ref().substring(21);
                        Schema existingSchema = context.getDefinedModels().get(schemaName);
                        setJavadocDescription(cls, fields, existingSchema);
                    }
                }
                return resolvedSchema;
            }
        }
        return null;
    }

    private void setJavadocDescription(Class<?> cls, List<Field> fields, Schema existingSchema) {
        if (existingSchema != null) {
            if (StringUtils.isBlank(existingSchema.getDescription())) {
                existingSchema.setDescription(javadocProvider.getClassJavadoc(cls));
            }
            Map<String, Schema> properties = existingSchema.getProperties();
            if (!CollectionUtils.isEmpty(properties))
                properties.entrySet().stream()
                        .filter(stringSchemaEntry -> StringUtils.isBlank(stringSchemaEntry.getValue().getDescription()))
                        .forEach(stringSchemaEntry -> {
                            Optional<Field> optionalField = fields.stream().filter(field1 -> field1.getName().equals(stringSchemaEntry.getKey())).findAny();
                            optionalField.ifPresent(field -> {
                                String fieldJavadoc = javadocProvider.getFieldJavadoc(field);
                                if (StringUtils.isNotBlank(fieldJavadoc))
                                    stringSchemaEntry.getValue().setDescription(fieldJavadoc);
                            });
                        });
            fields.stream().filter(f -> f.isAnnotationPresent(JsonUnwrapped.class))
                    .forEach(f -> setJavadocDescription(f.getType(), FieldUtils.getAllFieldsList(f.getType()), existingSchema));

        }
    }
}
