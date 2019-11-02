package net.sunxu.mybatis.automapper.processor.mapper.template;

import com.google.common.collect.ImmutableList;
import net.sunxu.mybatis.automapper.processor.mapper.xml.XmlElement;

import java.util.Collections;
import java.util.List;

public class SqlTemplate extends DatabaseIdentityTemplate {
    public SqlTemplate(String id, String databaseId) {
        super(id, databaseId);
    }

    @Override
    public String getElementName() {
        return "sql";
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
