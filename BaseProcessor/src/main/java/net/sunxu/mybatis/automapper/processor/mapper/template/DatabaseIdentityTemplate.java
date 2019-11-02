package net.sunxu.mybatis.automapper.processor.mapper.template;

abstract class DatabaseIdentityTemplate extends AbstractXmlMapperElement {
    private String id, databaseId;

    public DatabaseIdentityTemplate(String id, String databaseId) {
        this.id = id;
        this.databaseId = databaseId;
        addAttribute("id", id);
        addAttribute("databaseId", databaseId);
    }

    @Override
    public String getIdentityXPath() {
        if (databaseId == null) {
            return String.format("%s[@id=%s and not(@databaseId)]", getElementName(), id);
        } else {
            return String.format("%s[@id=%s and @databaseId=%s]", getElementName(), id, databaseId);
        }
    }

    @Override
    public AbstractXmlMapperElement setAttribute(String name, Object value) {
        if ("id".equals(name)) {
            return this;
        } else if ("databaseId".equals(name)) {
            databaseId = value == null ? null : value.toString();
        }
        return super.setAttribute(name, value);
    }
}
