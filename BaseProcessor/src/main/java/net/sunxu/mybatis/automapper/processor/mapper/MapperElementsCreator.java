package net.sunxu.mybatis.automapper.processor.mapper;

import net.sunxu.mybatis.automapper.processor.mapper.xml.XmlElement;
import net.sunxu.mybatis.automapper.processor.property.Type;

import java.util.List;
import java.util.Map;

public interface MapperElementsCreator {
    List<XmlElement> getElements();

    String getName();
}
