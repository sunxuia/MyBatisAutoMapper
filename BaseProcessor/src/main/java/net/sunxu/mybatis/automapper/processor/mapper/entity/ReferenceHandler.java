package net.sunxu.mybatis.automapper.processor.mapper.entity;

import net.sunxu.mybatis.automapper.entity.annotation.Reference;
import net.sunxu.mybatis.automapper.processor.property.Field;
import net.sunxu.mybatis.automapper.processor.property.field.ColumnField;
import net.sunxu.mybatis.automapper.processor.property.field.ReferenceProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

/**
 *
 */
public class ReferenceHandler extends AbstractBuildHandler {
    private Map<String, EntityModelBuilder> unFinished = new HashMap<>();
    private ReferenceMap cycleDetector = new ReferenceMap();

    @Override
    protected void build() {
        for (Field field : type.getFields()) {
            if (field.contains(ReferenceProperty.class) &&
                    !builder.getFieldPropertyNames().containsKey(field)) {
                ReferenceProperty reference = field.get(ReferenceProperty.class);
                builder.getReferences().put(reference.fieldName(), reference);

                List<ColumnField> columnFields = getReferenceColumnFields(reference);
                if (columnFields != null) {
                    updateBuilder(field, reference, columnFields);
                }
            }
        }

        if (!builder.isInterrupt()) {
            unFinished.remove(builder);
            for (Field field : type.getFields()) {
                cycleDetector.remove(field.get(ReferenceProperty.class));
            }
        }
    }

    private List<ColumnField> getReferenceColumnFields(ReferenceProperty reference) {
        EntityModel referToEntityModel = builder.getEntityByName(reference.referEntity());
        EntityModelBuilder otherBuilder = unFinished.get(reference.referEntity());
        if (referToEntityModel != null) {
            return getReferredColumnFieldsByEntityModel(reference, referToEntityModel);
        } else if (otherBuilder != null) {
            return getReferredColumnFieldsByOtherBuilder(reference, otherBuilder);
        } else {
            unFinished.put(builder.getName(), builder);
            builder.addRelatedEntityName(reference.referEntity());
            builder.setInterrupt(true);
            return null;
        }
    }

    private List<ColumnField> getReferredColumnFieldsByEntityModel(ReferenceProperty localReference,
                                                                   EntityModel referToEntityModel) {
        EntityIndex entityIndex = referToEntityModel.getEntityIndexes().get(localReference.referIndex());
        validate(entityIndex != null, "reference [%s] referred index [%s][%s] no found.",
                localReference.fieldName(), referToEntityModel.getName(), localReference.referIndex());

        return entityIndex.getColumnFields();
    }

    private List<ColumnField> getReferredColumnFieldsByOtherBuilder(ReferenceProperty localReference,
                                                                    EntityModelBuilder otherBuilder) {
        List<Field> referFields = otherBuilder.getIndexFields().get(localReference.referIndex());
        validate(referFields != null, "reference [%s] referred index [%s][%s] no found.",
                localReference.fieldName(), otherBuilder.getName(), localReference.referIndex());

        List<ColumnField> ans = new ArrayList<>(localReference.getLocalColumns().size());
        for (Field referField : referFields) {
            if (referField.contains(ReferenceProperty.class)) {
                ReferenceProperty property = referField.get(ReferenceProperty.class);
                validate(!cycleDetector.hasCycle(localReference, property),
                        "cycle reference detected : [%s/%s] and [%s/%s]",
                        builder.getName(), localReference.fieldName(), otherBuilder.getName(), property.fieldName());

                List<String> referPropertyNames = otherBuilder.getFieldPropertyNames().get(referField);
                if (referPropertyNames == null) {
                    return null;
                } else {
                    ans.addAll(referPropertyNames.stream()
                            .map(propertyName -> otherBuilder.getColumnFields().get(propertyName))
                            .collect(toList()));
                }
            } else {
                ans.addAll(otherBuilder.getFieldPropertyNames()
                        .get(referField).stream()
                        .map(propertyName -> otherBuilder.getColumnFields().get(propertyName))
                        .collect(toList()));
            }
        }
        return ans;
    }

    private void updateBuilder(Field field, ReferenceProperty localReference, List<ColumnField> referColumnFields) {
        final int length = localReference.getLocalColumns().size();
        validate(length == referColumnFields.size(),
                "reference [%s] referred index fields column count [%d] not match with local columns [%d]",
                localReference.fieldName(), referColumnFields.size(), length);

        List<String> propertyNames = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            String propertyName = localReference.fieldName() + "." + referColumnFields.get(i).propertyName();
            propertyNames.add(propertyName);

            Reference.LocalColumn localColumn = localReference.getLocalColumns().get(i);
            CustomColumnField columnField = new CustomColumnField(referColumnFields.get(i));
            columnField.setPropertyName(propertyName);
            columnField.setColumnName(localColumn.value());
            columnField.setJdbcType(localColumn.jdbcType());
            columnField.setPreferredWhenColumnNameConfilict(localColumn.isPreferredWhenColumnNameConflict());
            columnField.setInsertable(localColumn.insertable());
            columnField.setUpdatable(localColumn.updatable());

            builder.getColumnFields().put(propertyName, columnField);

            if (columnField.isPreferredWhenColumnNameConflict() ||
                    !builder.getColumnNamePropertyNameMap().containsKey(localColumn.value())) {
                builder.getColumnNamePropertyNameMap().put(localColumn.value(), propertyName);
            }
        }
        builder.getFieldPropertyNames().put(field, propertyNames);
    }


}
