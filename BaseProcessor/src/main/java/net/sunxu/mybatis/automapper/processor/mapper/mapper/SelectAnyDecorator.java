package net.sunxu.mybatis.automapper.processor.mapper.mapper;

import net.sunxu.mybatis.automapper.processor.mapper.template.SelectTemplate;
import net.sunxu.mybatis.automapper.processor.mapper.template.SqlBuilder;
import net.sunxu.mybatis.automapper.processor.property.method.SelectAnyProperty;

public class SelectAnyDecorator extends EntityMethodDecorator<SelectAnyProperty, SelectTemplate> {

    @Override
    protected SelectTemplate getXmlElement(MapperMethod mapperMethod, SelectAnyProperty property) {
        String databaseId = configuration.getMybatisDefaultDatabaseId();
        return new SelectTemplate(mapperMethod.getName(), databaseId);
    }

    @Override
    protected void setUpXmlElement(SelectTemplate select, MapperMethod mapperMethod, SelectAnyProperty property) {
        SqlBuilder sb = new SqlBuilder()
                .append("select ").addIncludeSql(EntityMapperDecorator.SQL_FULL_COLUMN)
                .newLine().append("from ").append(entityModel.getTableName());

        select.setSql(sb.toString());
        select.addAttribute("fetchSize", Integer.MIN_VALUE);
        select.addAttribute("resultMap", EntityMapperDecorator.RESULT_MAP_FULL_COLUMN);

        setAttributeWithMapperMethod(select, mapperMethod);
    }

    @Override
    protected String getDecoratorName() {
        return "SelectAny";
    }
}
