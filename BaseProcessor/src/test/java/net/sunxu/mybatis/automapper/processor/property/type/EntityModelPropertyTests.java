package net.sunxu.mybatis.automapper.processor.property.type;

import com.google.inject.Inject;
import net.sunxu.mybatis.automapper.entity.annotation.Entity;
import net.sunxu.mybatis.automapper.processor.environment.Configuration;
import net.sunxu.mybatis.automapper.processor.property.TestForAnnotationProperty;
import net.sunxu.mybatis.automapper.processor.property.Type;
import org.junit.Test;
import org.mockito.Mock;

import javax.lang.model.element.TypeElement;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;


public class EntityModelPropertyTests extends TestForAnnotationProperty<EntityProperty> {
    @Mock
    private TypeElement element;
    @Mock
    private Type type;
    @Mock
    private Entity entity;
    @Inject
    private Configuration configuration;

    @Test
    public void tableName_valueSetted_getValue() {
        doReturn("table_name").when(entity).value();
        initialProperty(element, type, entity);

        String res = property.tableName();

        assertEquals("table_name", res);
    }

    @Test
    public void tableName_valueEmpty_callConfiguration() {
        doReturn(Configuration.NamingRule.DEFAULT).when(configuration).getDefaultSchemaNamingRule();
        doReturn("test").when(type).getSimpleName();
        initialProperty(element, type, entity);

        String res = property.tableName();

        assertEquals("test", res);
        verify(configuration, atLeastOnce()).getNameByNamingRule(any(), eq(Configuration.NamingRule.DEFAULT));
    }
}
