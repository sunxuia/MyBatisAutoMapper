package net.sunxu.mybatis.automapper.processor.mapper.mapper;

import net.sunxu.mybatis.automapper.processor.mapper.entity.EntityIndex;
import net.sunxu.mybatis.automapper.processor.mapper.template.SelectTemplate;
import net.sunxu.mybatis.automapper.processor.mapper.template.SqlBuilder;
import net.sunxu.mybatis.automapper.processor.property.Parameter;
import net.sunxu.mybatis.automapper.processor.property.method.SelectByIndexProperty;

import java.util.List;

public class SelectByIndexDecorator extends EntityIndexDecorator<SelectByIndexProperty, SelectTemplate> {

    @Override
    protected SelectTemplate getXmlElement(MapperMethod mapperMethod, SelectByIndexProperty property) {
        String databaseId = configuration.getMybatisDefaultDatabaseId();
        return new SelectTemplate(mapperMethod.getName(), databaseId);
    }

    @Override
    protected void setUpXmlElement(SelectTemplate select, MapperMethod mapperMethod, SelectByIndexProperty property) {
        SqlBuilder sb = new SqlBuilder();
        sb.append("select ").addIncludeSql(EntityMapperDecorator.SQL_FULL_COLUMN)
                .newLine().append("from ").append(entityModel.getTableName());
        EntityIndex index = entityModel.getEntityIndexes().get(property.indexName());
        List<Parameter> parameters = mapperMethod.getMethod().getParameters();
        if (isParameterEntityValue(parameters.get(0))) {
            setWhereExpression(sb, index, parameters.get(0));
        } else {
            setWhereExpression(sb, index, mapperMethod, 0);
        }
        sb.endWhere();

        if (property.hasLock()) {
            sb.newLine().append(property.withLockExpression());
        }
        if (property.hasOrderBy()) {
            sb.newLine().append("order by ").append(property.orderByExpression());
        }

        select.addAttribute("resultMap", EntityMapperDecorator.RESULT_MAP_FULL_COLUMN);
        select.setSql(sb.toString());
    }

    @Override
    protected String getDecoratorName() {
        return "SelectByIndex";
    }
}
