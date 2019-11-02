package net.sunxu.mybatis.automapper.processor.mapper.xml;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import net.sunxu.mybatis.automapper.processor.environment.SystemHelper;
import net.sunxu.mybatis.automapper.processor.util.HelpUtils;
import org.dom4j.*;
import org.dom4j.io.SAXReader;
import org.javatuples.LabelValue;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.List;

import static net.sunxu.mybatis.automapper.processor.util.AutoMapperException.wrapException;

/**
 *
 */
public class XmlHandler {
    private SystemHelper sys;
    private final String filePath;
    private final Document doc;
    private final Element root;

    @Inject
    public XmlHandler(@Assisted String mapperName, SystemHelper sys) {
        this.sys = sys;
        filePath = getClass().getResource("/").getPath() +
                mapperName.replace(".", "/") +
                ".xml";
        File xmlFile = sys.getFile(filePath);
        if (xmlFile.exists()) {
            doc = sys.getDocument(filePath);
            root = doc.getRootElement();
        } else {
            String format = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                    "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis" +
                    ".org/dtd/mybatis-3-mapper.dtd\">\n" +
                    "<mapper />";
            InputStream inputStream = new ByteArrayInputStream(format.getBytes());
            doc = wrapException(() -> new SAXReader().read(inputStream));
            root = doc.getRootElement();
            root.addAttribute("namespace", mapperName);
        }
//        root.addAttribute(QName.get("space", Namespace.XML_NAMESPACE), "preserve");
    }

    public void save() {
        sys.saveXMLFile(doc, filePath);
    }

    public void addElements(List<XmlElement> xmlElements) {
        addElements(root, xmlElements);
    }

    private void addElements(Element parentElement, List<XmlElement> xmlElements) {
        if (xmlElements != null) {
            for (XmlElement xmlElement : xmlElements) {
                String xPath = xmlElement.getIdentityXPath();
                Node node = Strings.isNullOrEmpty(xPath) ? null : parentElement.selectSingleNode(xPath);
                if (node == null) {
                    createNode(parentElement, xmlElement);
                } else {
                    updateNode(node, xmlElement);
                }
            }
        }
    }

    private void createNode(Element parentElement, XmlElement xmlElement) {
        if (xmlElement instanceof XmlNodable) {
            Node node = ((XmlNodable) xmlElement).toNode();
            parentElement.add(node);
        } else { // XmlMapperElement
            XmlMapperElement xmlMapperElement = (XmlMapperElement) xmlElement;
            Element element = parentElement.addElement(xmlMapperElement.getElementName());
            setAttributes(element, xmlMapperElement.getAttributes());
            addElements(element, xmlMapperElement.getChildren());
        }
    }

    private void setAttributes(Element element, List<LabelValue<String, String>> attributes) {
        if (attributes != null) {
            for (LabelValue<String, String> attribute : attributes) {
                String name = attribute.getLabel();
                String value = attribute.getValue();
                if (value != null && element.attribute(name) == null) {
                    element.addAttribute(name, value);
                }
            }
        }
    }

    private void updateNode(Node node, XmlElement xmlElement) {
        EditSwitch editSwitch = EditSwitch.getSwitch(node);
        if (EditSwitch.NO_EDIT.equals(editSwitch)) {
            return;
        }
        if (xmlElement instanceof XmlNodable) {
            updateXmlNodable(node, (XmlNodable) xmlElement, editSwitch);
        } else {
            updateXmlMapperElement(node, (XmlMapperElement) xmlElement, editSwitch);
        }
    }

    private void updateXmlNodable(Node node, XmlNodable xmlNodable, EditSwitch editSwitch) {
        Node updateNode = xmlNodable.toNode();
        switch (editSwitch) {
            case DEFAULT:
                //do nothing if found node
                return;
            case REPLACE:
                int index = node.getParent().indexOf(node);
                node.getParent().content().set(index, updateNode);
                return;
            case ADD_MORE:
            case ADD_MORE_ATTRIBUTE:
                if (updateNode instanceof Element && node instanceof Element) {
                    for (Attribute attribute : (List<Attribute>) ((Element) updateNode).attributes()) {
                        if (((Element) node).attribute(attribute.getName()) == null) {
                            ((Element) node).addAttribute(attribute.getName(), attribute.getValue());
                        }
                    }
                }
                if (editSwitch == EditSwitch.ADD_MORE_ATTRIBUTE) {
                    return;
                }
            case ADD_MORE_CHILD:
                if (updateNode instanceof Element &&
                        node instanceof Element &&
                        ((Element) node).content().isEmpty()) {
                    List<Node> childNodesToAdd = (List<Node>) ((Element) updateNode).content();
                    while (!childNodesToAdd.isEmpty()) {
                        Node childNode = childNodesToAdd.get(0).detach();
                        ((Element) node).add(childNode);
                    }
                }
                return;
        }
    }

    private void updateXmlMapperElement(Node node, XmlMapperElement xmlMapperElement, EditSwitch editSwitch) {
        if (!(node instanceof Element)) {
            return;
        }
        Element element = (Element) node;
        switch (editSwitch) {
            case REPLACE:
                int index = element.getParent().indexOf(element);
                Element newElement = DocumentHelper.createElement(xmlMapperElement.getElementName());
                element.getParent().content().set(index, newElement);
                setAttributes(newElement, xmlMapperElement.getAttributes());
                addElements(newElement, xmlMapperElement.getChildren());
                return;
            case ADD_MORE:
            case ADD_MORE_ATTRIBUTE:
                setAttributes(element, xmlMapperElement.getAttributes());
                if (editSwitch == EditSwitch.ADD_MORE_ATTRIBUTE) {
                    return;
                }
            case ADD_MORE_CHILD:
                addElements(element, xmlMapperElement.getChildren());
                return;
            default:
                if (element.nodeCount() == 0) {
                    addElements(element, xmlMapperElement.getChildren());
                }
                for (Attribute attribute : (List<Attribute>) element.attributes()) {
                    if (!HelpUtils.existIn(attribute.getName(), "id", "databaseId")) {
                        return;
                    }
                }
                setAttributes(element, xmlMapperElement.getAttributes());
        }
    }
}