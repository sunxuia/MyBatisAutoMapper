package net.sunxu.mybatis.automapper.processor.mapper.entity;

import com.google.inject.Inject;
import net.sunxu.mybatis.automapper.entity.annotation.Cascade;
import net.sunxu.mybatis.automapper.entity.annotation.Column;
import net.sunxu.mybatis.automapper.entity.annotation.Composite;
import net.sunxu.mybatis.automapper.entity.annotation.Reference;
import net.sunxu.mybatis.automapper.processor.environment.EnvironmentHelper;
import net.sunxu.mybatis.automapper.processor.environment.GenericHelper;
import net.sunxu.mybatis.automapper.processor.property.Field;
import net.sunxu.mybatis.automapper.processor.property.Type;
import net.sunxu.mybatis.automapper.processor.property.TypeFactory;
import net.sunxu.mybatis.automapper.processor.property.field.CascadeProperty;
import net.sunxu.mybatis.automapper.processor.property.field.ColumnProperty;
import net.sunxu.mybatis.automapper.processor.property.field.CompositesProperty;
import net.sunxu.mybatis.automapper.processor.property.field.ReferenceProperty;
import net.sunxu.mybatis.automapper.processor.property.type.AliasProperty;

import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static net.sunxu.mybatis.automapper.processor.util.AutoMapperException.newException;

class TypeHandler extends AbstractBuildHandler {
    private TypeFactory typeFactory;

    @Inject
    private void initialTypeFactory(TypeFactory factory) {
        factory.setFieldFilterPrototype(new Filter());
        this.typeFactory = factory;
    }

    private static class Filter extends TypeFactory.Filter<VariableElement> {
        private Set<String> fieldNames = new HashSet<>();

        @Override
        public boolean test(VariableElement element, GenericHelper.GenericTypes genericTypes) {
            String fieldName = element.getSimpleName().toString();
            boolean isNew = fieldNames.add(fieldName);

            boolean hasAnno = !(element.getAnnotation(Column.class) == null &&
                    element.getAnnotationsByType(Composite.class).length == 0 &&
                    element.getAnnotation(Reference.class) == null &&
                    element.getAnnotation(Cascade.class) == null);

            return isNew && hasAnno;
        }

        @Override
        protected TypeFactory.Filter<VariableElement> clone() {
            return new Filter();
        }
    }

    @Inject
    private GenericHelper genericHelper;
    @Inject
    private EnvironmentHelper env;

    @Override
    protected void build() {
        if (type == null) {
            TypeElement typeElement = env.getTypeElement(builder.getName());
            Type type = typeFactory.get(typeElement, genericHelper.newGenericTypes(), Collections.emptyMap());
            validate(type);
            builder.setType(type);

            String alias = type.contains(AliasProperty.class) ? type.get(AliasProperty.class).alias() : type.getName();
            builder.setAlias(alias);
        }
    }

    public void validate(Type type) {
        for (Field field : type.getFields()) {
            int baseAnnotationCount = booleanToInt(field.contains(ColumnProperty.class),
                    field.contains(CompositesProperty.class),
                    field.contains(ReferenceProperty.class),
                    field.contains(CascadeProperty.class));
            if (baseAnnotationCount > 1) {
                throw newException("Annotation @Column, @Composite, @Reference and @Cascade should only exist one " +
                                "in [%s] field [%s].",
                        type.getName(), field.getName());
            }
        }
    }

    private int booleanToInt(boolean... isTrues) {
        int count = 0;
        for (boolean isTrue : isTrues) {
            count += isTrue ? 1 : 0;
        }
        return count;
    }


}
