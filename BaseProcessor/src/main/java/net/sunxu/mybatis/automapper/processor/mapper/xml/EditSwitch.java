package net.sunxu.mybatis.automapper.processor.mapper.xml;

import org.dom4j.Comment;
import org.dom4j.Element;
import org.dom4j.Node;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Strings.isNullOrEmpty;


public enum EditSwitch {
    //by default it will set attributes while the element has no other than an id or a databaseId attribute,
    //and set child element only if there is no other child element.
    DEFAULT,
    //will not modify the element
    NO_EDIT,
    //combination of ADD_MORE_CHILD and ADD_MORE_ATTRIBUTE
    ADD_MORE,
    //only add child to the element, keep the exist children (only resultmap's children is available), in other
    // condition
    // it will only check if the inner elemnts or text is empty
    ADD_MORE_CHILD,
    //only add attribute to the element, keep the exist attributes
    ADD_MORE_ATTRIBUTE,
    //replace the element
    REPLACE;

    public static EditSwitch getSwitch(Node node) {
        assert node != null;
        assert node.getParent() != null;

        Element parent = node.getParent();
        for (int i = parent.indexOf(node) - 1; i >= 0; i--) {
            Node prevNode = parent.node(i);
            if (!(prevNode instanceof Comment)) {
                return DEFAULT;
            }
            String comment = prevNode.getText();
            if (isNullOrEmpty(comment)) {
                continue;
            }

            Matcher matcher = Pattern
                    .compile("^\\s*EDIT_SWITCH\\s*\\[([^\\]]+)\\s*\\]\\s*$", Pattern.CASE_INSENSITIVE)
                    .matcher(comment);
            if (matcher.find()) {
                String type = matcher.group(1).trim().toUpperCase();
                try {
                    return EditSwitch.valueOf(type);
                } catch (IllegalArgumentException | NullPointerException err) {
                    continue;
                }
            }
        }
        return DEFAULT;
    }

    public boolean isAttributeEditable() {
        return this == DEFAULT || this == ADD_MORE || this == ADD_MORE_ATTRIBUTE;
    }

    public boolean isChildEditable() {
        return this == DEFAULT || this == ADD_MORE || this == ADD_MORE_CHILD;
    }
}
