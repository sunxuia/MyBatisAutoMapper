package net.sunxu.mybatis.automapper.processor.mapper.template;

public class UpdateTemplate extends SelectKeyableTemplate {
    public UpdateTemplate(String id, String databaseId) {
        super(id, databaseId);
    }

    @Override
    public String getElementName() {
        return "update";
    }
}
