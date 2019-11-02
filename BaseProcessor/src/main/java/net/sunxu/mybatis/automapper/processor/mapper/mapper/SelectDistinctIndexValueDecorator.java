package net.sunxu.mybatis.automapper.processor.mapper.mapper;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import net.sunxu.mybatis.automapper.processor.environment.EnvironmentHelper;
import net.sunxu.mybatis.automapper.processor.mapper.entity.EntityIndex;
import net.sunxu.mybatis.automapper.processor.mapper.template.*;
import net.sunxu.mybatis.automapper.processor.mapper.xml.XmlElement;
import net.sunxu.mybatis.automapper.processor.property.field.ColumnField;
import net.sunxu.mybatis.automapper.processor.property.method.SelectDistinctIndexValueProperty;
import org.apache.ibatis.type.Alias;

import javax.lang.model.element.TypeElement;
import java.util.Collections;
import java.util.List;

public class SelectDistinctIndexValueDecorator
        extends EntityIndexDecorator<SelectDistinctIndexValueProperty, SelectTemplate> {
    @Inject
    private EnvironmentHelper env;

    @Override
    protected SelectTemplate getXmlElement(MapperMethod mapperMethod, SelectDistinctIndexValueProperty property) {
        String databaseId = configuration.getMybatisDefaultDatabaseId();
        return new SelectTemplate(mapperMethod.getName(), databaseId);
    }

    @Override
    protected void setUpXmlElement(SelectTemplate select, MapperMethod mapperMethod,
                                   SelectDistinctIndexValueProperty property) {
        EntityIndex index = entityModel.getEntityIndexes().get(property.indexName());
        select.setSql(getSql(index));
    }

    private String getSql(EntityIndex index) {
        SqlBuilder sb = new SqlBuilder();
        sb.append("select distinct ");
        for (ColumnField columnField : index.getColumnFields()) {
            sb.addSelectColumnName(columnField).append(", ");
        }
        sb.trimEnd(", ");
        sb.newLine().append("from ").append(entityModel.getTableName());
        return sb.toString();
    }

    @Override
    protected List<? extends XmlElement> getOtherElement(SelectTemplate select,
                                                         MapperMethod mapperMethod,
                                                         SelectDistinctIndexValueProperty property) {
        String returnType = mapperMethod.getReturnType();
        EntityIndex index = entityModel.getEntityIndexes().get(property.indexName());
        if (returnType.equals(entityModel.getName())) {
            select.addAttribute("resultMap", EntityMapperDecorator.RESULT_MAP_FULL_COLUMN);
            return Collections.emptyList();
        } else if (index.getColumnFields().size() == 1) {
            select.addAttribute("resultMap", mapperMethod.getReturnType());
            return Collections.emptyList();
        } else {
            String expectedResultMapName = mapperMethod.getReturnType().replaceAll("\\.", "_");
            TypeElement type = env.getTypeElement(mapperMethod.getReturnType());
            if (type != null && type.getAnnotation(Alias.class) != null) {
                expectedResultMapName = type.getAnnotation(Alias.class).value();
            }
            select.addAttribute("resultMap", expectedResultMapName);
            return ImmutableList.of(createResultMap(expectedResultMapName, mapperMethod.getReturnType(), index));
        }
    }

    private ResultMapTemplate createResultMap(String resultMapName, String typeName, EntityIndex index) {
        ResultMapTemplate resultMap = new ResultMapTemplate(resultMapName, typeName);
        ResultMapTemplate entityResultMap = (ResultMapTemplate) getXmlElementFromDecorated(
                "resultMap", EntityMapperDecorator.RESULT_MAP_FULL_COLUMN);
        if (entityResultMap == null) {
            resultMap.addChild(new CommentTemplate("This resultMap need resultMap [" +
                    EntityMapperDecorator.RESULT_MAP_FULL_COLUMN + "] as a precondition."));
            return resultMap;
        }
        for (XmlElement element : entityResultMap.getChildren()) {
            if (element instanceof ResultMapChildTemplate) {
                String propertyValue = ((ResultMapChildTemplate) element).getPropertyValue();
                if (index.getFields().contains(propertyValue)) {
                    resultMap.addChild(element);
                }
            }
        }
        return resultMap;
    }

    @Override
    protected String getDecoratorName() {
        return "SelectDistinctIndexValue";
    }
}
