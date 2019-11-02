package net.sunxu.mybatis.automapper.processor.mapper.template;

import net.sunxu.mybatis.automapper.processor.mapper.xml.XmlNodable;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;

public class CommentTemplate implements XmlNodable {
    private final String text;

    public CommentTemplate(String text) {
        this.text = text;
    }

    @Override
    public Node toNode() {
        return DocumentHelper.createComment(text);
    }
}
