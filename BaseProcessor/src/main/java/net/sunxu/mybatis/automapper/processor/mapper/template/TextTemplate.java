package net.sunxu.mybatis.automapper.processor.mapper.template;

import net.sunxu.mybatis.automapper.processor.mapper.xml.XmlNodable;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;

public class TextTemplate implements XmlNodable {
    private final String text;

    public TextTemplate(String text) {
        this.text = text;
    }

    @Override
    public Node toNode() {
        return DocumentHelper.createText(text);
    }
}
