package net.sunxu.mybatis.automapper.processor.mapper.mapper;

import net.sunxu.mybatis.automapper.processor.mapper.template.SelectTemplate;
import net.sunxu.mybatis.automapper.processor.mapper.template.SqlBuilder;
import net.sunxu.mybatis.automapper.processor.property.method.SelectAllProperty;

public class SelectAllDecorator extends EntityMethodDecorator<SelectAllProperty, SelectTemplate> {

    @Override
    protected SelectTemplate getXmlElement(MapperMethod mapperMethod, SelectAllProperty property) {
        String databaseId = configuration.getMybatisDefaultDatabaseId();
        return new SelectTemplate(mapperMethod.getName(), databaseId);
    }

    @Override
    protected void setUpXmlElement(SelectTemplate select, MapperMethod mapperMethod, SelectAllProperty property) {
        SqlBuilder sb = new SqlBuilder()
                .append("select ").addIncludeSql(EntityMapperDecorator.SQL_FULL_COLUMN)
                .newLine().append("from ").append(entityModel.getTableName());
        if (property.hasOrderBy()) {
            sb.newLine().append("order by ").append(property.orderBy());
        }

        select.setSql(sb.toString());
        select.addAttribute("resultMap", EntityMapperDecorator.RESULT_MAP_FULL_COLUMN);

        setAttributeWithMapperMethod(select, mapperMethod);
    }

    @Override
    protected String getDecoratorName() {
        return "SelectAll";
    }
}
