package net.sunxu.mybatis.automapper.processor.mapper.mapper;

import net.sunxu.mybatis.automapper.processor.mapper.entity.EntityIndex;
import net.sunxu.mybatis.automapper.processor.mapper.template.SelectTemplate;
import net.sunxu.mybatis.automapper.processor.mapper.template.SqlBuilder;
import net.sunxu.mybatis.automapper.processor.property.Parameter;
import net.sunxu.mybatis.automapper.processor.property.method.CountByIndexProperty;
import net.sunxu.mybatis.automapper.processor.util.HelpUtils;

import java.util.List;

public class CountByIndexDecorator extends EntityIndexDecorator<CountByIndexProperty, SelectTemplate> {

    @Override
    protected SelectTemplate getXmlElement(MapperMethod mapperMethod, CountByIndexProperty property) {
        String databaseId = configuration.getMybatisDefaultDatabaseId();
        return new SelectTemplate(mapperMethod.getName(), databaseId);
    }

    @Override
    protected void setUpXmlElement(SelectTemplate select, MapperMethod mapperMethod, CountByIndexProperty property) {
        SqlBuilder sb = new SqlBuilder();
        boolean isReturnTypeBoolean = HelpUtils.existIn(mapperMethod.getMethod().getReturnType(),
                "boolean", Boolean.class.getCanonicalName());
        if (isReturnTypeBoolean) {
            sb.append("<![CDATA[")
                    .addIndent().newLine().append("select case count(*) when 0 then 0 else 1 end");
        } else {
            sb.append("select count(*)");
        }
        sb.newLine().append("from ").append(entityModel.getTableName());
        EntityIndex index = entityModel.getEntityIndexes().get(property.indexName());
        List<Parameter> parameters = mapperMethod.getMethod().getParameters();
        if (isParameterEntityValue(parameters.get(0))) {
            setWhereExpression(sb, index, parameters.get(0));
        } else {
            setWhereExpression(sb, index, mapperMethod, 0);
        }
        sb.endWhere();
        if (isReturnTypeBoolean) {
            sb.removeIndent().newLine().append("]]>");
        }
        select.setSql(sb.toString());

        select.addAttribute("resultType", mapperMethod.getMethod().getReturnType());
    }

    @Override
    protected String getDecoratorName() {
        return "CountByIndex";
    }
}
