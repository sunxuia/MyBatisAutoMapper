package net.sunxu.mybatis.automapper.processor.mapper.template;

import net.sunxu.mybatis.automapper.processor.mapper.xml.XmlElement;

import java.util.ArrayList;
import java.util.List;

public class ResultMapChildTemplate extends AbstractXmlMapperElement {
    private String elementName, propertyValue;

    public ResultMapChildTemplate(String elementName, String propertyValue) {
        this.elementName = elementName;
        this.propertyValue = propertyValue;
        addAttribute("property", propertyValue);
    }

    @Override
    public String getElementName() {
        return elementName;
    }

    public String getPropertyValue() {
        return propertyValue;
    }

    @Override
    public String getIdentityXPath() {
        return String.format("%s[@property=%s]", elementName, propertyValue);
    }

    @Override
    public List<XmlElement> getChildren() {
        return children;
    }

    private List<XmlElement> children = new ArrayList<>();

    public void addChild(XmlElement xmlElement) {
        children.add(xmlElement);
    }

    @Override
    public AbstractXmlMapperElement setAttribute(String name, Object value) {
        if ("property".equals(name)) {
            return this;
        }
        return super.setAttribute(name, value);
    }
}
