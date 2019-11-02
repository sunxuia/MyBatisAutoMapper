package net.sunxu.mybatis.automapper.processor.mapper.template;

public class InsertTemplate extends SelectKeyableTemplate {

    public InsertTemplate(String id, String databaseId) {
        super(id, databaseId);
    }

    @Override
    public String getElementName() {
        return "insert";
    }

}
