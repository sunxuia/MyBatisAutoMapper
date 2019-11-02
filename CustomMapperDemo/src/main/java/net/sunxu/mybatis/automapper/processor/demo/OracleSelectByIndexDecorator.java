package net.sunxu.mybatis.automapper.processor.demo;

import net.sunxu.mybatis.automapper.processor.mapper.mapper.MapperMethod;
import net.sunxu.mybatis.automapper.processor.mapper.mapper.SelectByIndexDecorator;
import net.sunxu.mybatis.automapper.processor.mapper.template.SelectTemplate;
import net.sunxu.mybatis.automapper.processor.mapper.template.SqlBuilder;
import net.sunxu.mybatis.automapper.processor.property.method.SelectByIndexProperty;

public class OracleSelectByIndexDecorator extends SelectByIndexDecorator {
    @Override
    protected void setUpXmlElement(SelectTemplate select, MapperMethod mapperMethod, SelectByIndexProperty property) {
        super.setUpXmlElement(select, mapperMethod, property);
        if (!mapperMethod.isReturnTypeArray() && !mapperMethod.isReturnTypeList()) {
            SqlBuilder sqlBuilder = new SqlBuilder(select.getSql());
            sqlBuilder.newLine().append("and rownum = 1");
            select.setSql(sqlBuilder.toString());
        }
    }
}
