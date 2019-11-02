package net.sunxu.mybatis.automapper.processor.mapper.template;

import net.sunxu.mybatis.automapper.processor.mapper.xml.XmlElement;

import java.util.ArrayList;
import java.util.List;

public class ResultMapTemplate extends AbstractXmlMapperElement {
    private final String id;

    public ResultMapTemplate(String id, String type) {
        this.id = id;
        addAttribute("id", id);
        addAttribute("type", type);
    }

    @Override
    public String getElementName() {
        return "resultMap";
    }

    @Override
    public String getIdentityXPath() {
        return String.format("resultMap[@id=%s]", id);
    }

    @Override
    public final List<XmlElement> getChildren() {
        return children;
    }

    private List<XmlElement> children = new ArrayList<>();

    public void addChild(XmlElement xmlElement) {
        children.add(xmlElement);
    }

    @Override
    public AbstractXmlMapperElement setAttribute(String name, Object value) {
        if ("id".equals(name)) {
            return this;
        }
        return super.setAttribute(name, value);
    }
}
