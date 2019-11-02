package net.sunxu.mybatis.automapper.processor.mapper.mapper;

import net.sunxu.mybatis.automapper.processor.mapper.entity.EntityIndex;
import net.sunxu.mybatis.automapper.processor.mapper.template.DeleteTemplate;
import net.sunxu.mybatis.automapper.processor.mapper.template.SqlBuilder;
import net.sunxu.mybatis.automapper.processor.property.Parameter;
import net.sunxu.mybatis.automapper.processor.property.method.DeleteByIndexProperty;

import java.util.List;

public class DeleteByIndexDecorator extends EntityIndexDecorator<DeleteByIndexProperty, DeleteTemplate> {

    @Override
    protected DeleteTemplate getXmlElement(MapperMethod mapperMethod, DeleteByIndexProperty property) {
        String databaseId = configuration.getMybatisDefaultDatabaseId();
        return new DeleteTemplate(mapperMethod.getName(), databaseId);
    }

    @Override
    protected void setUpXmlElement(DeleteTemplate delete, MapperMethod mapperMethod, DeleteByIndexProperty property) {
        SqlBuilder sb = new SqlBuilder();
        sb.append("delete from ").append(entityModel.getTableName());
        EntityIndex index = entityModel.getEntityIndexes().get(property.indexName());
        List<Parameter> parameters = mapperMethod.getMethod().getParameters();

        if (isParameterEntityValue(parameters.get(0))) {
            setWhereExpression(sb, index, parameters.get(0));
        } else {
            setWhereExpression(sb, index, mapperMethod, 0);
        }
        sb.endWhere();

        String returnType = mapperMethod.getMethod().getReturnType();
        if (!"void".equals(returnType)) {
            delete.addAttribute("resultType", mapperMethod.getMethod().getReturnType());
        }
        delete.setSql(sb.toString());
    }

    @Override
    protected String getDecoratorName() {
        return "DeleteByIndex";
    }
}
