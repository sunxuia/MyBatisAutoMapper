package net.sunxu.mybatis.automapper.processor.property.field;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import net.sunxu.mybatis.automapper.entity.annotation.Composite;
import net.sunxu.mybatis.automapper.entity.annotation.Composites;
import net.sunxu.mybatis.automapper.processor.environment.Configuration;
import net.sunxu.mybatis.automapper.processor.environment.EnvironmentHelper;
import net.sunxu.mybatis.automapper.processor.environment.MessageHelper;
import net.sunxu.mybatis.automapper.processor.property.Field;
import net.sunxu.mybatis.automapper.processor.property.PropertyForAnnotation;
import net.sunxu.mybatis.automapper.processor.property.Type;
import net.sunxu.mybatis.automapper.processor.property.type.DefaultColumnNamingRuleProperty;
import net.sunxu.mybatis.automapper.processor.util.HelpUtils.AnnotationVisitor;
import org.apache.ibatis.type.JdbcType;

import javax.lang.model.element.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Strings.isNullOrEmpty;
import static net.sunxu.mybatis.automapper.processor.util.HelpUtils.getAnnotationTypeValue;
import static net.sunxu.mybatis.automapper.processor.util.HelpUtils.visitAnnotationValue;


@PropertyForAnnotation({Composite.class, Composites.class})
public class CompositesProperty extends AbstractFieldAnnotationProperty {
    @Inject
    private Field field;
    @Inject
    private EnvironmentHelper env;
    @Inject
    private Type type;
    @Inject
    private Configuration configuration;
    @Inject
    private MessageHelper messageHelper;

    private String javaType;

    private List<CompositeProperty> compositeProperties;

    @Override
    protected void initial() {
        if (getAnnotation(Composites.class) != null) {
            AnnotationMirror mirror = getAnnotationMirror(Composites.class);
            valuesMirror = visitAnnotationValue(
                    mirror,
                    "value",
                    () -> { throw new RuntimeException("should not run to here"); },
                    new AnnotationVisitor<List<AnnotationMirror>>() {
                        @SuppressWarnings("unchecked")
                        @Override
                        public List<AnnotationMirror> visitArray(List<? extends AnnotationValue> vals,
                                                                 ExecutableElement method) {
                            return (List) vals;
                        }
                    });
            javaType = getAnnotationTypeValue(mirror, "javaType");
        } else {
            valuesMirror = ImmutableList.of(getAnnotationMirror(Composite.class));
        }
        if (isNullOrEmpty(javaType)) {
            javaType = field.getSimpleType();
        } else {
            warnOnJavaTypeNotAssignable(field.getName(), javaType, field.getSimpleType());
        }

        referToTypeElement = env.getTypeElement(javaType);

        Composite[] composites = getAnnotations(Composite.class);
        List<CompositeProperty> compositeProperties = new ArrayList<>(composites.length);
        for (Composite composite : composites) {
            compositeProperties.add(new CompositeProperty(composite, getAnnotationMirror(composite)));
        }
        this.compositeProperties = Collections.unmodifiableList(compositeProperties);


    }

    private void warnOnJavaTypeNotAssignable(String fieldName, String javaType, String fieldType) {
        if (!env.isAssignedFrom(javaType, fieldType) &&
                !env.isAssignedFrom(fieldType, javaType)) {
            messageHelper.warning(
                    "Entity [%s] field [%s] javaType [%s] and field type [%s] is not assignable",
                    type.getName(), fieldName, javaType, fieldType);
        }
    }

    private List<AnnotationMirror> valuesMirror;

    private TypeElement referToTypeElement;

    private AnnotationMirror getAnnotationMirror(Composite composite) {
        if (composite.equals(getAnnotation(Composite.class))) {
            return getAnnotationMirror(Composite.class);
        } else {
            Composite[] cps = getAnnotation(Composites.class).value();
            for (int i = 0; i < cps.length; i++) {
                if (composite.equals(cps[i])) {
                    return valuesMirror.get(i);
                }
            }
            throw new RuntimeException("should not run to here");
        }
    }

    public String fieldName() {
        return field.getName();
    }

    public String javaType() {
        return javaType;
    }

    public List<CompositeProperty> components() {
        return compositeProperties;
    }

    public class CompositeProperty implements ColumnField {
        private Composite composite;
        private String typeHandler;
        private String javaType;
        private boolean useJavaType;
        private String columnName;
        private JdbcType jdbcType;
        private String propertyName;

        private CompositeProperty(Composite composite, AnnotationMirror mirror) {
            this.composite = composite;
            typeHandler = getAnnotationTypeValue(mirror, "typeHandler");
            javaType = getAnnotationTypeValue(mirror, "javaType");

            propertyName = fieldName() + "." + composite.value();
            if (isNullOrEmpty(javaType)) {
                useJavaType = false;
                javaType = getField(composite.value()).asType().toString();
            } else {
                useJavaType = true;
            }
            columnName = composite.column();
            if (isNullOrEmpty(columnName)) {
                StringBuilder expectName = new StringBuilder(propertyName.length());
                for (int i = 0; i < propertyName.length(); i++) {
                    char c = propertyName.charAt(i);
                    if (c == '.') {
                        if (i < propertyName.length() - 1) {
                            expectName.append(Character.toUpperCase(propertyName.charAt(++i)));
                        }
                    } else {
                        expectName.append(c);
                    }
                }
                columnName = type.get(DefaultColumnNamingRuleProperty.class)
                        .getDefaultColumnName(expectName.toString());
            }

            jdbcType = composite.jdbcType();
            if (jdbcType == JdbcType.UNDEFINED) {
                jdbcType = configuration.getJdbcTypeByJavaType(javaType);
            }
        }

        private VariableElement getField(String fieldNames) {
            VariableElement field = null;
            TypeElement fieldType = referToTypeElement;
            for (String fieldName : fieldNames.split("\\.")) {
                for (TypeElement typeElement = fieldType;
                     typeElement != null;
                     typeElement = env.asElement(typeElement.getSuperclass())) {
                    Optional<VariableElement> fieldElement = typeElement.getEnclosedElements().stream()
                            .filter(e -> e.getKind() == ElementKind.FIELD &&
                                    e.getSimpleName().toString().equals(fieldName))
                            .map(e -> (VariableElement) e)
                            .findAny();
                    if (fieldElement.isPresent()) {
                        field = fieldElement.get();
                        break;
                    } else {
                        field = null;
                    }
                }
                validate(field != null,
                        "Cannot find fieldName [%s] for @Composite's property.", fieldName);
                fieldType = env.asElement(field.asType());
            }
            return field;
        }

        @Override
        public String typeHandler() {
            return typeHandler;
        }

        @Override
        public boolean isPreferredWhenColumnNameConflict() {
            return composite.isPreferredWhenColumnNameConflict();
        }

        @Override
        public String columnName() {
            return columnName;
        }

        @Override
        public JdbcType jdbcType() {
            return jdbcType;
        }

        @Override
        public String outDbExpression() {
            return composite.outDbExpression().trim();
        }

        @Override
        public String inDbExpression() {
            return composite.inDbExpression().trim();
        }

        @Override
        public boolean insertable() {
            return composite.insertable();
        }

        @Override
        public boolean updatable() {
            return composite.updatable();
        }

        @Override
        public String propertyName() {
            return propertyName;
        }

        public String propertyNameWOFieldName() {
            return composite.value();
        }

        @Override
        public String javaType() {
            return javaType;
        }

        @Override
        public boolean useJavaType() {
            return useJavaType;
        }
    }
}
