package net.sunxu.mybatis.automapper.processor.mapper.entity;

import net.sunxu.mybatis.automapper.processor.property.Field;
import net.sunxu.mybatis.automapper.processor.property.field.IndexProperty;

import java.util.*;

class IndexFieldsHandler extends AbstractBuildHandler {
    private Set<String> initialed = new HashSet<>();

    @Override
    protected void build() {
        if (initialed.add(builder.getName())) {
            Map<String, List<Field>> indexFields = builder.getIndexFields();
            indexFields.putAll(getIndexes());
        }
    }

    private Map<String, List<Field>> getIndexes() {
        Map<String, List<Field>> indexFields = new HashMap<>();
        for (Field field : type.getFields()) {
            if (field.contains(IndexProperty.class)) {
                IndexProperty property = field.get(IndexProperty.class);
                for (String indexName : property.getIndexNames()) {
                    if (!indexFields.containsKey(indexName)) {
                        indexFields.put(indexName, new ArrayList<>(1));
                    }
                    indexFields.get(indexName).add(field);
                }
            }
        }
        indexFields.forEach((indexName, fields) -> {
            Collections.sort(fields, Comparator.comparingInt(f -> f.get(IndexProperty.class).getOrder(indexName)));
            for (int i = 1; i < fields.size(); i++) {
                int previousOrder = fields.get(i - 1).get(IndexProperty.class).getOrder(indexName);
                int currentOrder = fields.get(i).get(IndexProperty.class).getOrder(indexName);
                validate(previousOrder < currentOrder,
                        "duplicated index order : [%s][%d]", indexName, previousOrder);
            }
        });
        return indexFields;
    }
}
