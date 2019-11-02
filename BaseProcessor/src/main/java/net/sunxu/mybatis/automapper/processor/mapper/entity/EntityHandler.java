package net.sunxu.mybatis.automapper.processor.mapper.entity;

import net.sunxu.mybatis.automapper.processor.property.type.EntityProperty;

import static com.google.common.base.Strings.isNullOrEmpty;

class EntityHandler extends AbstractBuildHandler {

    @Override
    protected void build() {
        if (isNullOrEmpty(builder.getTableName())) {
            validate(type.contains(EntityProperty.class), "annotation @Entity is required");

            String tableName = type.get(EntityProperty.class).tableName();

            builder.setTableName(tableName);
        }
    }
}
