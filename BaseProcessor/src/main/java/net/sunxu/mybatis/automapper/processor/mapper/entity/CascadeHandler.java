package net.sunxu.mybatis.automapper.processor.mapper.entity;

import com.google.inject.Inject;
import net.sunxu.mybatis.automapper.processor.environment.EnvironmentHelper;
import net.sunxu.mybatis.automapper.processor.environment.MessageHelper;
import net.sunxu.mybatis.automapper.processor.property.Field;
import net.sunxu.mybatis.automapper.processor.property.field.CascadeProperty;
import net.sunxu.mybatis.automapper.processor.property.field.ColumnField;
import org.javatuples.LabelValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
class CascadeHandler extends AbstractBuildHandler {
    @Inject
    private MessageHelper messageHelper;
    @Inject
    private EnvironmentHelper env;

    private Map<String, List<LabelValue<String, CascadeProperty>>> uncheckedCascades = new HashMap<>();

    @Override
    protected void build() {
        for (Field field : type.getFields()) {
            if (field.contains(CascadeProperty.class)) {
                CascadeProperty cascade = field.get(CascadeProperty.class);
                validate(field, cascade);
                builder.getCascades().put(cascade.fieldName(), cascade);
            }
        }
        if (uncheckedCascades.containsKey(builder.getName())) {
            checkOtherCascadesReferToThisEntity(uncheckedCascades.get(builder.getName()));
        }
    }

    private void validate(Field field, CascadeProperty cascade) {
        validate(builder.getEntityIndexes().containsKey(cascade.localIndex()),
                "field [%s] local index [%s] not found", field.getName(), cascade.localIndex());


        EntityModel otherEntityModel = builder.getEntityByName(cascade.referEntity());
        if (otherEntityModel == null) {
            addToUncheckedMap(cascade);
        } else {
            checkWithEntityModel(cascade, otherEntityModel);
        }
    }

    private void addToUncheckedMap(CascadeProperty cascade) {
        List<LabelValue<String, CascadeProperty>> unchecks = uncheckedCascades.get(cascade.referEntity());
        if (unchecks == null) {
            unchecks = new ArrayList<>(1);
            uncheckedCascades.put(cascade.referEntity(), unchecks);
        }
        unchecks.add(LabelValue.with(builder.getName(), cascade));
        builder.addRelatedEntityName(cascade.referEntity());
    }

    private void checkWithEntityModel(CascadeProperty cascade, EntityModel referEntityModel) {
        validateCascade(builder.getName(),
                cascade,
                builder.getEntityIndexes().get(cascade.localIndex()),
                referEntityModel.getEntityIndexes().get(cascade.referIndex()));
    }

    private void validateCascade(String localEntityName,
                                 CascadeProperty localCascade,
                                 EntityIndex localIndex,
                                 EntityIndex referIndex) {
        validate(referIndex != null,
                "cascade [%s][%s] referred to index [%s][%s] not exist",
                localEntityName, localCascade.fieldName(),
                localCascade.referEntity(), localCascade.referIndex());

        validate(localIndex.getColumnFields().size() == referIndex.getColumnFields().size(),
                "cascade [%s][%s] local index [%s] and refer index [%s][%s] properties count not match.",
                localEntityName, localCascade.fieldName(), localCascade.localIndex(),
                localCascade.referEntity(), localCascade.referIndex());

        warnOnCascadeJavaTypeNotAssignable(localCascade.fieldName(),
                localIndex.getColumnFields(),
                referIndex.getColumnFields());
    }

    private void warnOnCascadeJavaTypeNotAssignable(String localCascadeName,
                                                    List<ColumnField> localProperties,
                                                    List<ColumnField> referProperties) {
        for (int i = 0; i < localProperties.size(); i++) {
            ColumnField local = localProperties.get(i);
            ColumnField refer = referProperties.get(i);
            if (!local.javaType().equals(refer.javaType()) &&
                    env.isAssignedFrom(refer.javaType(), local.javaType())) {
                messageHelper.warning("cascade [%s] local property java type [%s] " +
                                "is not or super type of the referred property [%s][%s]",
                        localCascadeName, local.javaType(), refer.propertyName(), refer.javaType());
            }
        }
    }

    private void checkOtherCascadesReferToThisEntity(List<LabelValue<String, CascadeProperty>> otherCascades) {
        for (LabelValue<String, CascadeProperty> labelValue : otherCascades) {
            String otherEntityName = labelValue.getLabel();
            CascadeProperty otherCascade = labelValue.getValue();
            EntityModel otherEntityModel = builder.getEntityByName(otherEntityName);
            if (otherEntityModel != null) {
                validateCascade(otherEntityName,
                        otherCascade,
                        otherEntityModel.getEntityIndexes().get(otherCascade.localIndex()),
                        builder.getEntityIndexes().get(otherCascade.referIndex()));
            }
        }
        uncheckedCascades.remove(builder.getName());
    }
}
