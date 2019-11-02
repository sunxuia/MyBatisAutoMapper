package net.sunxu.mybatis.automapper.processor.property.type;

import com.google.inject.Inject;
import net.sunxu.mybatis.automapper.entity.annotation.Entity;
import net.sunxu.mybatis.automapper.processor.environment.Configuration;
import net.sunxu.mybatis.automapper.processor.property.PropertyForAnnotation;
import net.sunxu.mybatis.automapper.processor.property.Type;

import static com.google.common.base.Strings.isNullOrEmpty;


@PropertyForAnnotation(Entity.class)
public class EntityProperty extends AbstractTypeAnnotationProperty {
    @Inject
    private Entity entity;
    @Inject
    private Configuration configuration;
    @Inject
    private Type type;

    private String tableName;

    @Override
    public void initial() {
        tableName = entity.value();
        if (!isNullOrEmpty(tableName)) {
            return;
        }
        tableName = configuration.getNameByNamingRule(
                type.getSimpleName(),
                configuration.getDefaultSchemaNamingRule());
    }

    public String tableName() {
        return tableName;
    }
}
