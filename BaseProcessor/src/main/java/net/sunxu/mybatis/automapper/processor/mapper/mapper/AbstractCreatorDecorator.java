package net.sunxu.mybatis.automapper.processor.mapper.mapper;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import net.sunxu.mybatis.automapper.processor.mapper.MapperElementsCreator;
import net.sunxu.mybatis.automapper.processor.mapper.entity.EntityAutoGenerateSqlEmbedded;
import net.sunxu.mybatis.automapper.processor.mapper.template.AbstractXmlMapperElement;
import net.sunxu.mybatis.automapper.processor.mapper.xml.XmlElement;
import net.sunxu.mybatis.automapper.processor.mapper.xml.XmlMapperElement;
import net.sunxu.mybatis.automapper.processor.mapper.xml.XmlNodable;
import net.sunxu.mybatis.automapper.processor.property.Method;
import net.sunxu.mybatis.automapper.processor.property.method.MethodFetchSizeProperty;
import net.sunxu.mybatis.automapper.processor.property.method.MethodStatementTypeProperty;
import net.sunxu.mybatis.automapper.processor.property.method.MethodTimeoutProperty;
import net.sunxu.mybatis.automapper.processor.property.method.MethodUseCacheProperty;
import org.dom4j.DocumentHelper;
import org.javatuples.LabelValue;

import java.util.List;

public abstract class AbstractCreatorDecorator implements MapperElementsCreator {

    @Inject
    @Assisted
    private MapperElementsCreator decorated;

    @Inject
    @Assisted
    protected MapperModel mapperModel;

    protected List<XmlElement> xmlElements;

    @Override
    public final List<XmlElement> getElements() {
        xmlElements = decorated.getElements();
        List<XmlElement> startLocations = getStartLocations();
        xmlElements.addAll(startLocations);
        List<? extends XmlElement> self = getXmlElements();
        xmlElements.addAll(self);
        return xmlElements;
    }

    protected abstract String getDecoratorName();

    protected abstract List<? extends XmlElement> getXmlElements();

    protected List<XmlElement> getStartLocations() {
        return ImmutableList.of((XmlNodable) () ->
                DocumentHelper.createComment("autoMapper for [" + getDecoratorName() + "]"));
    }

    protected void setAttributeWithMapperMethod(AbstractXmlMapperElement mapperElement, MapperMethod method) {
        Method realMethod = method.getMethod();
        if (realMethod.contains(MethodUseCacheProperty.class)) {
            mapperElement.setAttribute("useCache", realMethod.get(MethodUseCacheProperty.class).useCache());
        }
        if (realMethod.contains(MethodTimeoutProperty.class)) {
            mapperElement.setAttribute("timeout", realMethod.get(MethodTimeoutProperty.class).timeout());
        }
        if (realMethod.contains(MethodFetchSizeProperty.class)) {
            mapperElement.setAttribute("fetchSize", realMethod.get(MethodFetchSizeProperty.class).size());
        }
        if (realMethod.contains(MethodStatementTypeProperty.class)) {
            mapperElement.setAttribute("statementType",
                    realMethod.get(MethodStatementTypeProperty.class).statementType());
        }
    }

    protected void setAutoGenerateAttributes(AbstractXmlMapperElement element,
                                             EntityAutoGenerateSqlEmbedded autoGenerateSqlEmbedded) {
        element.addAttribute("useGeneratedKeys", true);
        element.addAttribute("keyColumn", autoGenerateSqlEmbedded.getKeyColumn());
        element.addAttribute("keyProperty", autoGenerateSqlEmbedded.getKeyProperty());
    }

    protected XmlMapperElement getXmlElementFromDecorated(String elementName, String id) {
        for (XmlElement element : xmlElements) {
            if (element instanceof XmlMapperElement) {
                XmlMapperElement mapperElement = (XmlMapperElement) element;
                for (LabelValue<String, String> attr : mapperElement.getAttributes()) {
                    if (attr.getLabel().equals("id") && Strings.nullToEmpty(attr.getValue()).equals(id)) {
                        return mapperElement;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public String getName() {
        return decorated.getName();
    }
}
