package net.sunxu.mybatis.automapper.processor.mapper.xml;

import org.dom4j.Node;

public interface XmlNodable extends XmlElement {
    default String getIdentityXPath() {
        return null;
    }

    Node toNode();
}
