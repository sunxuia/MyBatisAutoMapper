package net.sunxu.mybatis.automapper.processor.mapper.template;

import net.sunxu.mybatis.automapper.processor.mapper.xml.XmlMapperElement;
import org.javatuples.LabelValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractXmlMapperElement implements XmlMapperElement {

    @Override
    public final List<LabelValue<String, String>> getAttributes() {
        return attributes;
    }

    private List<LabelValue<String, String>> attributes = new ArrayList<>();

    private Map<String, Integer> attributeNames = new HashMap<>();

    public AbstractXmlMapperElement addAttribute(String name, Object value) {
        if (value != null && !attributeNames.containsKey(name)) {
            attributeNames.put(name, attributes.size());
            attributes.add(LabelValue.with(name, value.toString()));
        }
        return this;
    }

    public AbstractXmlMapperElement setAttribute(String name, Object value) {
        if (!attributeNames.containsKey(name)) {
            attributeNames.remove(name);
            for (int i = 0; i < attributes.size(); i++) {
                if (attributes.get(i).getLabel().equals(name)) {
                    attributes.remove(i);
                    break;
                }
            }
        }
        addAttribute(name, value);
        return this;
    }
}
