package net.sunxu.mybatis.automapper.processor.mapper.mapper;

import com.google.inject.Inject;
import net.sunxu.mybatis.automapper.processor.environment.EnvironmentHelper;
import net.sunxu.mybatis.automapper.processor.environment.MessageHelper;
import net.sunxu.mybatis.automapper.processor.mapper.entity.EntityIndex;
import net.sunxu.mybatis.automapper.processor.mapper.entity.EntityModel;
import net.sunxu.mybatis.automapper.processor.property.method.SelectDistinctIndexValueProperty;

import static net.sunxu.mybatis.automapper.processor.util.AutoMapperException.newException;

public class SelectDistinctIndexValueHandler extends EntityMethodHandler<SelectDistinctIndexValueDecorator> {
    @Inject
    private EnvironmentHelper env;
    @Inject
    private MessageHelper messageHelper;

    @Override
    protected void validate(MapperModel mapperModel, MapperMethod mapperMethod, EntityModel entityModel) {
        SelectDistinctIndexValueProperty property =
                mapperMethod.getMethod().get(SelectDistinctIndexValueProperty.class);
        String indexName = property.indexName();
        EntityIndex index = entityModel.getEntityIndexes().get(indexName);
        if (index == null) {
            throw newException("[%s][%s] annotated with @SelectDistinctIndexValue index [%s] not exist in entity [%s]",
                    mapperModel.getName(), mapperMethod.getName(), indexName, entityModel.getName());
        }
        if (mapperMethod.getMethod().getParameters().size() != 0) {
            throw newException("[%s][%s] annotated with @SelectDistinctIndexValue should not have parameter.",
                    mapperModel.getName(), mapperMethod.getName());
        }
    }
}
