package net.sunxu.mybatis.automapper.processor.mapper.template;

import net.sunxu.mybatis.automapper.processor.mapper.xml.XmlElement;

import java.util.ArrayList;
import java.util.List;

abstract class SelectKeyableTemplate extends DatabaseIdentityTemplate {

    public SelectKeyableTemplate(String id, String databaseId) {
        super(id, databaseId);
    }

    @Override
    public final List<XmlElement> getChildren() {
        List<XmlElement> res = new ArrayList<>(1);
        if (beforeSelectKey != null) {
            res.add(beforeSelectKey);
        }
        if (sql != null) {
            res.add(new TextTemplate(sql));
        }
        if (afterSelectKey != null) {
            res.add(afterSelectKey);
        }
        return res;
    }

    private SelectKeyTemplate beforeSelectKey;

    private SelectKeyTemplate afterSelectKey;

    private String sql;

    public void setBeforeSelectKey(SelectKeyTemplate beforeSelectKey) {
        this.beforeSelectKey = beforeSelectKey;
    }

    public void setAfterSelectKey(SelectKeyTemplate afterSelectKey) {
        this.afterSelectKey = afterSelectKey;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }
}
