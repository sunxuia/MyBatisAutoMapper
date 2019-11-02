package net.sunxu.mybatis.automapper.processor.mapper.entity;

import net.sunxu.mybatis.automapper.processor.property.field.ColumnField;
import org.javatuples.LabelValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;

public class EntityIndex {
    private final String indexName;
    private final List<ColumnField> columnFields;
    private final List<LabelValue<String, List<ColumnField>>> fields;

    public EntityIndex(String indexName,
                       List<ColumnField> columnFields) {
        this.indexName = indexName;
        this.columnFields = columnFields;
        this.fields = makeFields();
    }

    private List<LabelValue<String, List<ColumnField>>> makeFields() {
        List<LabelValue<String, List<ColumnField>>> fields = new ArrayList<>();
        String lastFieldName = "";
        for (ColumnField columnField : columnFields) {
            String propertyName = columnField.propertyName();

            int dot = propertyName.indexOf('.');
            String fieldName = dot == -1 ? propertyName : propertyName.substring(0, dot);
            if (!lastFieldName.equals(fieldName)) {
                fields.add(LabelValue.with(fieldName, new ArrayList<>()));
                lastFieldName = fieldName;
            }
            fields.get(fields.size() - 1).getValue().add(columnField);
        }
        return fields;
    }

    public String getName() {
        return indexName;
    }

    public List<ColumnField> getColumnFields() {
        return Collections.unmodifiableList(columnFields);
    }

    public List<LabelValue<String, List<ColumnField>>> getFields() {
        return Collections.unmodifiableList(fields);
    }

    public boolean isPrimaryKey() {
        return isNullOrEmpty(indexName);
    }
}
