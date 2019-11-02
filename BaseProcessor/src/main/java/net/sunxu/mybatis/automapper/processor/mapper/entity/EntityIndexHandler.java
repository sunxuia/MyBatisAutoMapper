package net.sunxu.mybatis.automapper.processor.mapper.entity;

import net.sunxu.mybatis.automapper.processor.property.Field;
import net.sunxu.mybatis.automapper.processor.property.field.ColumnField;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class EntityIndexHandler extends AbstractBuildHandler {
    @Override
    protected void build() {
        Map<String, EntityIndex> entityIndexes = builder.getEntityIndexes();
        if (entityIndexes.size() != builder.getIndexFields().size()) {
            builder.getIndexFields().forEach((indexName, fields) -> {
                EntityIndex entityIndex = getEntityIndex(indexName, fields);
                entityIndexes.put(indexName, entityIndex);
            });
        }
    }

    private EntityIndex getEntityIndex(String indexName, List<Field> fields) {
        List<ColumnField> columnFields = fields.stream()
                .flatMap(f -> builder.getFieldPropertyNames().get(f).stream())
                .map(propertyName -> builder.getColumnFields().get(propertyName))
                .collect(Collectors.toList());
        return new EntityIndex(indexName, columnFields);
    }
}
