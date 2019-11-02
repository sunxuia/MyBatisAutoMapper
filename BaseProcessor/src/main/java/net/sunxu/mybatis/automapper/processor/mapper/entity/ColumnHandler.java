package net.sunxu.mybatis.automapper.processor.mapper.entity;

import com.google.common.collect.ImmutableList;
import net.sunxu.mybatis.automapper.processor.property.Field;
import net.sunxu.mybatis.automapper.processor.property.field.ColumnProperty;
import net.sunxu.mybatis.automapper.processor.property.field.ColumnField;

import java.util.Map;

class ColumnHandler extends AbstractBuildHandler {
    @Override
    protected void build() {
        if (builder.getColumns().isEmpty()) {
            for (Field field : type.getFields()) {
                if (field.contains(ColumnProperty.class)) {
                    ColumnProperty property = field.get(ColumnProperty.class);
                    String propertyName = property.propertyName();

                    addToColumns(property, propertyName);

                    builder.getFieldPropertyNames().put(field, ImmutableList.of(propertyName));
                    builder.getColumnFields().put(propertyName, property);
                    builder.getColumns().put(propertyName, property);
                }
            }
        }
    }

    private void addToColumns(ColumnField column, String propertyName) {
        Map<String, String> columnNamePropertyNameMap = builder.getColumnNamePropertyNameMap();
        String columnName = column.columnName();
        if (column.isPreferredWhenColumnNameConflict() || !columnNamePropertyNameMap.containsKey(columnName)) {
            columnNamePropertyNameMap.put(columnName, propertyName);
        }
    }
}
