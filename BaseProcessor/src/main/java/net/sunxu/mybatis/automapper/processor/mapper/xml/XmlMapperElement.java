package net.sunxu.mybatis.automapper.processor.mapper.xml;

import org.javatuples.LabelValue;

import java.util.List;

public interface XmlMapperElement extends XmlElement {
    String getElementName();

    String getIdentityXPath();

    List<LabelValue<String, String>> getAttributes();

    List<XmlElement> getChildren();
}
