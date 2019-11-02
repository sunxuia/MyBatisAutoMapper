package net.sunxu.mybatis.automapper.processor.property.type;

import net.sunxu.mybatis.automapper.processor.property.TestForAnnotationProperty;
import net.sunxu.mybatis.automapper.processor.property.Type;
import org.apache.ibatis.type.Alias;
import org.junit.Test;
import org.mockito.Mock;

import javax.lang.model.element.TypeElement;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;


public class AliasAnnotationPropertyTests extends TestForAnnotationProperty<AliasProperty> {

    @Mock
    private TypeElement typeElement;
    @Mock
    private Alias alias;
    @Mock
    private Type type;

    @Test
    public void alias_noArg_getAliasName() {
        doReturn("name").when(alias).value();
        initialProperty(typeElement, type, alias);

        String res = property.alias();

        assertEquals("name", res);
    }
}
