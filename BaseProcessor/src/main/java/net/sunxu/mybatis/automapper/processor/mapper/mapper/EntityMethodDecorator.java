package net.sunxu.mybatis.automapper.processor.mapper.mapper;

import com.google.inject.Inject;
import net.sunxu.mybatis.automapper.processor.environment.Configuration;
import net.sunxu.mybatis.automapper.processor.mapper.entity.EntityModel;
import net.sunxu.mybatis.automapper.processor.mapper.template.AbstractXmlMapperElement;
import net.sunxu.mybatis.automapper.processor.mapper.xml.XmlElement;
import net.sunxu.mybatis.automapper.processor.property.AbstractAnnotationProperty;
import net.sunxu.mybatis.automapper.processor.util.HelpUtils;

import javax.lang.model.element.ExecutableElement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

abstract class EntityMethodDecorator
        <P extends AbstractAnnotationProperty<ExecutableElement>, E extends AbstractXmlMapperElement>
        extends AbstractCreatorDecorator {

    private Class<P> propertyClass;

    public EntityMethodDecorator() {
        propertyClass = (Class<P>)
                HelpUtils.getGenericArgumentPassedToSuperClass(getClass(), EntityMethodDecorator.class).get(0);
    }

    @Inject
    protected Configuration configuration;

    protected EntityModel entityModel;

    public void setEntityModel(EntityModel entityModel) {
        this.entityModel = entityModel;
    }

    protected List<MapperMethod> mapperMethods;

    public void setMapperMethods(List<MapperMethod> mapperMethods) {
        this.mapperMethods = mapperMethods;
    }

    @Override
    protected List<? extends XmlElement> getXmlElements() {
        List<XmlElement> res = new ArrayList<>(mapperMethods.size());
        for (MapperMethod mapperMethod : mapperMethods) {
            P property = mapperMethod.getMethod().get(propertyClass);
            E element = getXmlElement(mapperMethod, property);
            res.add(element);

            setUpXmlElement(element, mapperMethod, property);
            setAttributeWithMapperMethod(element, mapperMethod);

            res.addAll(getOtherElement(element, mapperMethod, property));
        }
        return res;
    }

    protected abstract E getXmlElement(MapperMethod mapperMethod, P property);

    protected abstract void setUpXmlElement(E element, MapperMethod mapperMethod, P property);

    protected List<? extends XmlElement> getOtherElement(E element, MapperMethod mapperMethod, P property) {
        return Collections.emptyList();
    }
}
