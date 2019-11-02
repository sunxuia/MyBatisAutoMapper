package net.sunxu.mybatis.automapper.processor.mapper.entity;

import net.sunxu.mybatis.automapper.processor.property.Field;
import net.sunxu.mybatis.automapper.processor.property.field.CompositesProperty;
import net.sunxu.mybatis.automapper.processor.property.field.ColumnField;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class CompositeHandler extends AbstractBuildHandler {
    @Override
    protected void build() {
        for (Field field : type.getFields()) {
            if (field.contains(CompositesProperty.class) &&
                    !builder.getFieldPropertyNames().containsKey(field)) {
                CompositesProperty composites = field.get(CompositesProperty.class);
                List<String> propertyNames = new ArrayList<>(composites.components().size());
                for (CompositesProperty.CompositeProperty property : composites.components()) {
                    addToColumns(property);
                    propertyNames.add(property.propertyName());
                    builder.getColumnFields().put(property.propertyName(), property);
                }
                builder.getComposites().put(composites.fieldName(), composites);
                builder.getFieldPropertyNames().put(field, propertyNames);
            }
        }
    }

    private void addToColumns(ColumnField columnField) {
        Map<String, String> columnNamePropertyNameMap = builder.getColumnNamePropertyNameMap();
        if (columnField.isPreferredWhenColumnNameConflict() ||
                !columnNamePropertyNameMap.containsKey(columnField.columnName())) {
            columnNamePropertyNameMap.put(columnField.columnName(), columnField.propertyName());
        }
    }
}
