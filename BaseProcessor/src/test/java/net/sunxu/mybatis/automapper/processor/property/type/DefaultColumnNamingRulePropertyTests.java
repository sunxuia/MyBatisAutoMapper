package net.sunxu.mybatis.automapper.processor.property.type;

import com.google.inject.Inject;
import net.sunxu.mybatis.automapper.entity.annotation.DefaultColumnNamingRule;
import net.sunxu.mybatis.automapper.processor.environment.Configuration;
import net.sunxu.mybatis.automapper.processor.property.TestForAnnotationProperty;
import net.sunxu.mybatis.automapper.processor.property.Type;
import org.junit.Test;
import org.mockito.Mock;

import javax.lang.model.element.TypeElement;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class DefaultColumnNamingRulePropertyTests extends TestForAnnotationProperty<DefaultColumnNamingRuleProperty> {
    @Mock
    private TypeElement element;
    @Mock
    private DefaultColumnNamingRule rule;
    @Mock
    private Type type;
    @Inject
    private Configuration configuration;

    @Test
    public void getDefaultColumnName_noAnnotation_getByConfiguration() {
        doReturn("field_name").when(configuration).getNameByNamingRule(any(), any());
        initialProperty(element, new Class[]{DefaultColumnNamingRule.class}, new Object[]{type});

        String res = property.getDefaultColumnName("fieldName");

        assertEquals("field_name", res);
        verify(configuration, atLeast(1)).getNameByNamingRule(eq("fieldName"), any());
    }


    @Test
    public void getDefaultColumnName_annotationFieldName_getFieldName() {
        doReturn(DefaultColumnNamingRule.NameRule.FIELD_NAME).when(rule).value();
        initialProperty(element, type, rule);

        String res = property.getDefaultColumnName("fieldName");

        assertEquals("fieldName", res);
    }

    @Test
    public void getDefaultColumnName_annotationLowerCase_getFieldNameLowerCase() {
        doReturn(DefaultColumnNamingRule.NameRule.LOWER_CASE).when(rule).value();
        initialProperty(element, type, rule);

        String res = property.getDefaultColumnName("fieldName");

        assertEquals("fieldname", res);
    }

    @Test
    public void getDefaultColumnName_annotationLowerCaseSplitByUnderLine_getFieldNameSplitByUnderLine() {
        doReturn(DefaultColumnNamingRule.NameRule.LOWER_CASE_SPLIT_BY_UNDER_LINE).when(rule).value();
        initialProperty(element, type, rule);

        String res = property.getDefaultColumnName("fieldName");

        assertEquals("field_name", res);
    }

    @Test
    public void getDefaultColumnName_annotationUpperCase_getFieldNameUpperCase() {
        doReturn(DefaultColumnNamingRule.NameRule.UPPER_CASE).when(rule).value();
        initialProperty(element, type, rule);

        String res = property.getDefaultColumnName("fieldName");

        assertEquals("FIELDNAME", res);
    }

    @Test
    public void getDefaultColumnName_annotationUpperCaseSplitByUnderLine_getFieldNameUpperCaseSplitByUnderLine() {
        doReturn(DefaultColumnNamingRule.NameRule.UPPER_CASE_SPLIT_BY_UNDER_LINE).when(rule).value();
        initialProperty(element, type, rule);

        String res = property.getDefaultColumnName("fieldName");

        assertEquals("FIELD_NAME", res);
    }

}
