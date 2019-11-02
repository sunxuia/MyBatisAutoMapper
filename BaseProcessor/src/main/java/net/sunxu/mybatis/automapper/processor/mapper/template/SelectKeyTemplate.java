package net.sunxu.mybatis.automapper.processor.mapper.template;

import com.google.common.collect.ImmutableList;
import net.sunxu.mybatis.automapper.processor.mapper.entity.EntityAutoGenerateSelectKey;
import net.sunxu.mybatis.automapper.processor.mapper.xml.XmlElement;

import java.util.Collections;
import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;

public class SelectKeyTemplate extends AbstractXmlMapperElement {
    private final String order;

    public SelectKeyTemplate(boolean isBefore) {
        order = isBefore ? "BEFORE" : "AFTER";
        addAttribute("order", order);
    }

    public SelectKeyTemplate(EntityAutoGenerateSelectKey selectKey) {
        order = selectKey.getOrder();
        if (isNullOrEmpty(selectKey.getKeyColumn())) {
            addAttribute("keyColumn", selectKey.getKeyColumn());
        }
        addAttribute("resultType", selectKey.getResultType());
        addAttribute("keyProperty", selectKey.getKeyProperty());
        addAttribute("resultType", selectKey.getResultType());
        addAttribute("order", order);
        setSql(selectKey.getExpression());
    }

    @Override
    public String getElementName() {
        return "selectKey";
    }

    @Override
    public String getIdentityXPath() {
        return String.format("selectKey[@order=%s]", order);
    }

    @Override
    public List<XmlElement> getChildren() {
        if (sql == null) {
            return Collections.emptyList();
        }
        return ImmutableList.of(new TextTemplate(sql));
    }

    private String sql;

    public void setSql(String sql) {
        this.sql = sql;
    }
}
