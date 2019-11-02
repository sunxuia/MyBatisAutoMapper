package net.sunxu.mybatis.automapper.processor.mapper.entity;

import com.google.common.base.Strings;
import net.sunxu.mybatis.automapper.processor.environment.MessageHelper;
import net.sunxu.mybatis.automapper.processor.property.Field;
import net.sunxu.mybatis.automapper.processor.property.field.AutoGenerateProperty;
import net.sunxu.mybatis.automapper.processor.property.field.ColumnProperty;
import net.sunxu.mybatis.automapper.processor.util.HelpUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static net.sunxu.mybatis.automapper.entity.annotation.AutoGenerate.Kind;

public class EntityAutoGenerateSqlEmbedded {
    private final Kind kind;
    private final List<Field> fields;
    private final String entityName;
    private final String keyColumn, keyProperty;
    private final Map<String, String> expressions;
    private final MessageHelper messageHelper;

    EntityAutoGenerateSqlEmbedded(MessageHelper messageHelper,
                                         String entityName,
                                         Kind kind,
                                         List<Field> fields) {
        this.messageHelper = messageHelper;
        this.entityName = entityName;
        this.kind = kind;
        this.fields = fields;

        int length = fields.size();
        keyColumn = HelpUtils.concat(fields, ",", f -> f.get(ColumnProperty.class).columnName());
        keyProperty = HelpUtils.concat(fields, ",", f -> f.get(ColumnProperty.class).propertyName());
        expressions = createExpressions(fields);
    }

    private Map<String, String> createExpressions(List<Field> columns) {
        Map<String, String> propertyNames = new HashMap<>(columns.size());
        Map<String, String> expressions = new HashMap<>(columns.size());
        for (Field field : fields) {
            ColumnProperty column = field.get(ColumnProperty.class);
            final String columnName = column.columnName();
            final String propertyName = column.propertyName();

            if (propertyNames.containsKey(columnName)) {
                String otherPropertyName = propertyNames.get(columnName);
                String preferred = column.isPreferredWhenColumnNameConflict() ? propertyName : otherPropertyName;
                messageHelper.warning("Duplicated @AutoGenerate with column name [%s] for property [%s] " +
                                "and [%s] in [%s], [%s] will be chosen as preferred.",
                        columnName, otherPropertyName, propertyName, entityName, preferred);

                if (column.isPreferredWhenColumnNameConflict()) {
                    propertyNames.put(columnName, propertyName);
                    expressions.put(columnName, field.get(AutoGenerateProperty.class).getExpression(kind));
                }
            } else {
                propertyNames.put(columnName, propertyName);
                expressions.put(columnName, field.get(AutoGenerateProperty.class).getExpression(kind));
            }
        }

        return expressions;
    }

    public String getKeyColumn() {
        return keyColumn;
    }

    public String getKeyProperty() {
        return keyProperty;
    }

    public Set<String> getColumnNames() {
        return expressions.keySet();
    }

    public String getExpression(String columnName) {
        return expressions.get(columnName);
    }

    public boolean hasExpression(String columnName) {
        return !Strings.isNullOrEmpty(getExpression(columnName));
    }
}
