package net.sunxu.mybatis.automapper.processor.mapper.entity;

import com.google.common.base.Strings;
import net.sunxu.mybatis.automapper.entity.annotation.AutoGenerate.Kind;
import net.sunxu.mybatis.automapper.processor.environment.MessageHelper;
import net.sunxu.mybatis.automapper.processor.property.Field;
import net.sunxu.mybatis.automapper.processor.property.field.*;
import net.sunxu.mybatis.automapper.processor.util.HelpUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.stream.Collectors.toList;
import static net.sunxu.mybatis.automapper.processor.util.AutoMapperException.newException;

public class EntityAutoGenerateSelectKey {
    private final Kind kind;
    private final List<Field> fields;
    private final String entityName;
    private final boolean isColumn, isComposite, isReference;
    private final List<String> keyColumn, keyProperty;
    private final String resultType, order;
    private final String expression;
    private final MessageHelper messageHelper;
    private final Map<Field, List<String>> fieldpropertyNames;
    private final Map<String, ColumnField> properties;

    EntityAutoGenerateSelectKey(MessageHelper messageHelper,
                                String entityName,
                                Kind kind,
                                List<Field> fields,
                                Map<Field, List<String>> fieldPropertyNames,
                                Map<String, ColumnField> properties) {
        this.messageHelper = messageHelper;
        this.entityName = entityName;
        this.kind = kind;
        this.fields = fields;
        this.fieldpropertyNames = fieldPropertyNames;
        this.properties = properties;

        isComposite = makeIsComposite();
        isReference = makeIsReference();
        isColumn = !isComposite && !isReference;
        if (isColumn) {
            validateColumn();
        }
        keyColumn = makeKeyColumn();
        keyProperty = makeKeyProperty();
        resultType = makeResultType();
        order = makeOrder();
        expression = makeExpression();
    }

    private boolean makeIsComposite() {
        boolean isComposite = false;
        for (Field field : fields) {
            if (field.contains(CompositesProperty.class)) {
                isComposite = true;
                if (fields.size() > 1) {
                    throw newException("Entity [%s] AutoGenerate validate fail : duplicated type : [%s]. " +
                            "Fields annotated with @AutoGenerate and @Composite " +
                            "should only exist one field for each autoGenerate kind", entityName, kind);
                }
            }
        }
        return isComposite;
    }

    private boolean makeIsReference() {
        boolean isReference = false;
        for (Field field : fields) {
            if (field.contains(ReferenceProperty.class)) {
                isReference = true;
                if (fields.size() > 1) {
                    throw newException("Entity [%s] AutoGenerate validate fail : duplicated type : [%s]. " +
                            "Fields annotated with @AutoGenerate and @Reference " +
                            "should only exist one field for each autoGenerate kind", entityName, kind);
                }
            }
        }
        return isReference;
    }

    private void validateColumn() {
        String previousExpression = "";
        String previousFieldName = "";
        for (Field field : fields) {
            String expression = field.get(AutoGenerateProperty.class).getExpression(kind);
            if (Strings.isNullOrEmpty(expression) || previousExpression.equals(expression)) {
                continue;
            } else if (Strings.isNullOrEmpty(previousExpression)) {
                previousExpression = expression;
                previousFieldName = field.getName();
            } else {
                throw newException("duplicated @AutoGenerate expression for [%s] and [%s] in [%s].",
                        field.getName(), previousFieldName, entityName);
            }
        }
    }

    private List<String> makeKeyColumn() {
        if (isColumn && fields.size() == 1) {
            //single column
            return Collections.emptyList();
        } else if (isColumn) {
            //multi column
            return fields.stream()
                    .map(f -> f.get(ColumnProperty.class).columnName())
                    .collect(toList());
        } else if (isComposite) {
            //composite
            return fields.get(0)
                    .get(CompositesProperty.class)
                    .components().stream()
                    .map(c -> c.columnName())
                    .collect(toList());
        } else {
            //reference
            return fieldpropertyNames
                    .get(fields.get(0)).stream()
                    .map(propertyName -> properties.get(propertyName).columnName())
                    .collect(toList());
        }
    }

    private List<String> makeKeyProperty() {
        if (isColumn) {
            //column(s)
            return fields.stream()
                    .map(c -> c.get(ColumnProperty.class).propertyName())
                    .collect(toList());
        } else {
            //composite / reference
            return fieldpropertyNames.get(fields.get(0));
        }
    }

    private String makeResultType() {
        if (fields.size() == 1) {
            return fields.get(0).getSimpleType();
        } else {
            return entityName;
        }
    }

    private String makeOrder() {
        switch (kind) {
            case BEFORE_INSERT:
            case BEFORE_UPDATE:
                return "BEFORE";
            case AFTER_INSERT:
            case AFTER_UPDATE:
                return "AFTER";
            default:
                return null;
        }
    }

    private String makeExpression() {
        String lastExpression = "";
        for (Field field : fields) {
            AutoGenerateProperty property = field.get(AutoGenerateProperty.class);
            String expression = property.getExpression(kind);
            if (!isNullOrEmpty(expression)) {
                if (!isNullOrEmpty(lastExpression) && !expression.equals(lastExpression)) {
                    throw newException("duplicated sql expression found for [%s][kind : %s].", entityName, kind);
                }
                lastExpression = expression;
            }
        }
        if (isNullOrEmpty(lastExpression)) {
            throw newException("no auto generate sql expression found for [%s][kind : %s]", entityName, kind);
        }

        return lastExpression;
    }

    public String getKeyColumn() {
        return keyColumn.isEmpty() ? null : HelpUtils.concat(keyColumn, ",");
    }

    public String getKeyProperty() {
        return HelpUtils.concat(keyProperty, ",");
    }

    public String getResultType() {
        return resultType;
    }

    public String getOrder() {
        return order;
    }

    public Kind getKind() {
        return kind;
    }

    public String getExpression() {
        return expression;
    }
}
