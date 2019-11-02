package net.sunxu.mybatis.automapper.processor.mapper.template;

import com.google.common.base.Function;
import net.sunxu.mybatis.automapper.processor.mapper.entity.EntityIndex;
import net.sunxu.mybatis.automapper.processor.property.field.*;
import org.apache.ibatis.mapping.FetchType;
import org.apache.ibatis.type.JdbcType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;

public class ResultMapBuilder {
    private final String id, type;
    private List<ResultMapChildTemplate> ids = new ArrayList<>();
    private List<ResultMapChildTemplate> results = new ArrayList<>();
    private List<ResultMapChildTemplate> associates = new ArrayList<>();
    private List<ResultMapChildTemplate> cascades = new ArrayList<>();

    public ResultMapBuilder(String id, String type) {
        this.id = id;
        this.type = type;
    }

    public void addId(ColumnProperty column) {
        ResultMapChildTemplate child = new ResultMapChildTemplate("id", column.propertyName());
        setUpResult(column, child);
        ids.add(child);
    }

    private void setUpResult(ColumnProperty column, ResultMapChildTemplate child) {
        child.addAttribute("javaType", column.javaType());
        child.addAttribute("column", column.columnName());
        if (column.jdbcType() != JdbcType.UNDEFINED) {
            child.addAttribute("jdbcType", column.jdbcType());
        }
        if (!isNullOrEmpty(column.typeHandler())) {
            child.addAttribute("typeHandler", column.typeHandler());
        }
    }

    public void addResult(ColumnProperty column) {
        ResultMapChildTemplate child = new ResultMapChildTemplate("result", column.propertyName());
        setUpResult(column, child);
        results.add(child);
    }

    public void addAssociateionLocal(CompositesProperty composites) {
        ResultMapChildTemplate child = new ResultMapChildTemplate("association", composites.fieldName());
        child.addAttribute("javaType", composites.javaType());
        for (CompositesProperty.CompositeProperty composite : composites.components()) {
            ResultMapChildTemplate subNode =
                    new ResultMapChildTemplate("association", composite.propertyNameWOFieldName());
            subNode.addAttribute("javaType", composite.javaType());
            subNode.addAttribute("column", composite.columnName());
            if (composite.jdbcType() != JdbcType.UNDEFINED) {
                subNode.addAttribute("jdbcType", composite.jdbcType());
            }
            if (!isNullOrEmpty(composite.typeHandler())) {
                child.addAttribute("typeHandler", composite.typeHandler());
            }
            child.addChild(subNode);
        }

        associates.add(child);
    }

    public void addAssociationSelectByAssociate(ReferenceProperty reference, String selectFrom) {
        ResultMapChildTemplate child = new ResultMapChildTemplate("association", reference.fieldName());
        child.addAttribute("javaType", reference.javaType());
        child.addAttribute("column",
                getResultMapColumnExpression(reference.getLocalColumns(), c -> c.value()));
        child.addAttribute("select", selectFrom);
        if (reference.fetchType() != FetchType.DEFAULT) {
            child.addAttribute("fetchType", reference.fetchType());
        }

        associates.add(child);
    }

    private <E> String getResultMapColumnExpression(Collection<E> collection, Function<E, String> convertor) {
        if (collection.size() == 1) {
            return convertor.apply(collection.iterator().next());
        } else {
            StringBuilder sb = new StringBuilder("{");
            for (E item : collection) {
                sb.append(convertor.apply(item)).append(',');
            }
            sb.setCharAt(sb.length() - 1, '}');
            return sb.toString();
        }
    }


    public void addAssociationSelectByCascade(CascadeProperty cascade, EntityIndex localIndex, String selectFrom) {
        ResultMapChildTemplate child = new ResultMapChildTemplate("association", cascade.fieldName());
        child.addAttribute("javaType", cascade.javaType());
        child.addAttribute("column",
                getResultMapColumnExpression(localIndex.getColumnFields(), ColumnField::columnName));
        child.addAttribute("select", selectFrom);
        if (cascade.fetchType() != FetchType.DEFAULT) {
            child.addAttribute("fetchType", cascade.fetchType());
        }

        associates.add(child);
    }

    public void addCollectionSelectByCascade(CascadeProperty cascade,
                                             EntityIndex localIndex,
                                             String selectFrom) {
        ResultMapChildTemplate child = new ResultMapChildTemplate("collection", cascade.fieldName());
        child.addAttribute("ofType", cascade.referEntity());

        child.addAttribute("column",
                getResultMapColumnExpression(localIndex.getColumnFields(), ColumnField::columnName));
        child.addAttribute("select", selectFrom);
        if (cascade.fetchType() != FetchType.DEFAULT) {
            child.addAttribute("fetchType", cascade.fetchType());
        }

        cascades.add(child);
    }


    public ResultMapTemplate getResultMap() {
        ResultMapTemplate resultMap = new ResultMapTemplate(id, type);
        ids.forEach(c -> resultMap.addChild(c));
        results.forEach(c -> resultMap.addChild(c));
        associates.forEach(c -> resultMap.addChild(c));
        cascades.forEach(c -> resultMap.addChild(c));
        return resultMap;
    }
}
