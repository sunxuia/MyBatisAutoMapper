package net.sunxu.mybatis.automapper.processor.mapper.template;

import com.google.common.collect.ImmutableList;
import net.sunxu.mybatis.automapper.processor.mapper.xml.XmlElement;

import java.util.Collections;
import java.util.List;

public class DeleteTemplate extends DatabaseIdentityTemplate {
    public DeleteTemplate(String id, String databaseId) {
        super(id, databaseId);
    }

    @Override
    public String getElementName() {
        return "delete";
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
