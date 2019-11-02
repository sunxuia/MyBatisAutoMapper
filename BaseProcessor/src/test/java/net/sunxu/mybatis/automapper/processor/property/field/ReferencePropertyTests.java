package net.sunxu.mybatis.automapper.processor.property.field;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import net.sunxu.mybatis.automapper.entity.annotation.Entity;
import net.sunxu.mybatis.automapper.entity.annotation.Index;
import net.sunxu.mybatis.automapper.entity.annotation.PrimaryKey;
import net.sunxu.mybatis.automapper.entity.annotation.Reference;
import net.sunxu.mybatis.automapper.mapper.EntityMapper;
import net.sunxu.mybatis.automapper.processor.environment.GenericHelper;
import net.sunxu.mybatis.automapper.processor.environment.SystemHelper;
import net.sunxu.mybatis.automapper.processor.property.Field;
import net.sunxu.mybatis.automapper.processor.property.FieldFactory;
import net.sunxu.mybatis.automapper.processor.property.TestForAnnotationProperty;
import net.sunxu.mybatis.automapper.processor.property.Type;
import net.sunxu.mybatis.automapper.processor.property.type.DefaultColumnNamingRuleProperty;
import net.sunxu.mybatis.automapper.processor.testutil.TestHelpUtils;
import net.sunxu.mybatis.automapper.processor.util.AutoMapperException;
import org.apache.ibatis.mapping.FetchType;
import org.apache.ibatis.type.JdbcType;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class ReferencePropertyTests extends TestForAnnotationProperty<ReferenceProperty> {
    @Entity
    private static class TestBean {
        @Reference(localColumns = {@Reference.LocalColumn("column1"), @Reference.LocalColumn("column1")})
        private ReferredTestBean referenceWithDuplicatedColumnName;

        @Reference
        private ReferredTestBean defaultReference;

        @Reference(referTo = ReferredTestBean.class,
                referIndex = "otherIndex",
                byMapper = TestBeanMapper.class,
                localColumns = {
                        @Reference.LocalColumn(value = "column1", jdbcType = JdbcType.NUMERIC),
                        @Reference.LocalColumn("column2")},
                fetchType = FetchType.LAZY)
        private ReferredTestBean specificReference;
    }

    @Entity
    private static class ReferredTestBean {
        @PrimaryKey
        private Long pk;

        @Index("otherIndex")
        private Integer indexField;

        @Index(value = "otherIndex", order = 101)
        private String indexField2;
    }

    private interface TestBeanMapper extends EntityMapper<ReferredTestBean> {}

    @Before
    public void setUp() {
        typeElement = getTypeElement(TestBean.class);

        DefaultColumnNamingRuleProperty defaultColumnNamingRuleProperty = mock(DefaultColumnNamingRuleProperty.class);
        doReturn(defaultColumnNamingRuleProperty).when(type).get(DefaultColumnNamingRuleProperty.class);
        doAnswer(invocation -> invocation.getArgumentAt(0, String.class))
                .when(defaultColumnNamingRuleProperty)
                .getDefaultColumnName(any());
    }

    private TypeElement typeElement;

    @Mock
    private Type type;

    @Inject
    private FieldFactory fieldFactory;
    @Inject
    private GenericHelper genericHelper;
    @Inject
    private SystemHelper sys;

    private Field field;
    private VariableElement fieldElement;
    private Reference reference;

    private void setField(String fieldName) {
        fieldElement = TestHelpUtils.getFieldElement(typeElement, fieldName);
        fieldElement = spy(fieldElement);

        reference = fieldElement.getAnnotation(Reference.class);

        doReturn(Collections.emptySet()).when(sys).getClassesInPackage(FieldFactory.PACKAGE_NAME);
        field = fieldFactory.get(fieldElement, genericHelper.newGenericTypes(), ImmutableMap.of(Type.class, type));
        field = spy(field);
    }

    private void initialProperty() {
        initialProperty(fieldElement, type, field, reference);
    }

    @Test(expected = AutoMapperException.class)
    public void initial_duplicatedColumnName_throwException() {
        setField("referenceWithDuplicatedColumnName");
        initialProperty();
        property.getLocalColumns();
    }

    @Test
    public void getLocalColumns_notSpecified_getByField() {
        setField("defaultReference");
        initialProperty();

        List<Reference.LocalColumn> fields = property.getLocalColumns();

        assertEquals(1, fields.size());
        assertEquals("defaultReference", fields.get(0).value());
        assertEquals(JdbcType.UNDEFINED, fields.get(0).jdbcType());
        assertEquals(false, fields.get(0).isPreferredWhenColumnNameConflict());
        assertTrue(fields.get(0).insertable());
        assertTrue(fields.get(0).updatable());
    }

    @Test
    public void getLocalColumns_specified_getList() {
        setField("specificReference");
        initialProperty();

        List<Reference.LocalColumn> fields = property.getLocalColumns();
        assertEquals(2, fields.size());
        assertEquals("column1", fields.get(0).value());
        assertEquals(JdbcType.NUMERIC, fields.get(0).jdbcType());
        assertEquals("column2", fields.get(1).value());
    }


    @Test
    public void fetchType_notSpecified_getDefault() {
        setField("defaultReference");
        initialProperty();

        FetchType res = property.fetchType();

        assertEquals(FetchType.DEFAULT, res);
    }

    @Test
    public void fetchType_specified_getFetchType() {
        setField("specificReference");
        initialProperty();

        FetchType res = property.fetchType();

        assertEquals(FetchType.LAZY, res);
    }

    @Test
    public void byMapper_notSpecified_getEmpty() {
        setField("defaultReference");
        initialProperty();

        String res = property.byMapper();

        assertEquals("", res);
    }

    @Test
    public void byMapper_specified_getByMapperClassName() {
        setField("specificReference");
        initialProperty();

        String res = property.byMapper();

        assertEquals(TestBeanMapper.class.getCanonicalName(), res);
    }

    @Test
    public void referEntity_notSpecified_getByFieldType() {
        setField("defaultReference");
        initialProperty();

        String res = property.referEntity();

        assertEquals(ReferredTestBean.class.getCanonicalName(), res);
    }

    @Test
    public void referEntity_specified_getReferEntityClassName() {
        setField("specificReference");
        initialProperty();

        String res = property.referEntity();

        assertEquals(ReferredTestBean.class.getCanonicalName(), res);
    }

    @Test
    public void referIndex_notSpecified_getPrimaryKey() {
        setField("defaultReference");
        initialProperty();

        String res = property.referIndex();

        assertEquals("", res);
    }

    @Test
    public void referIndex_specified_getIndexName() {
        setField("specificReference");
        initialProperty();

        String res = property.referIndex();

        assertEquals("otherIndex", res);
    }

    @Test
    public void fieldName_noArg_fieldName() {
        setField("defaultReference");
        initialProperty();

        String res = property.fieldName();

        assertEquals("defaultReference", res);
    }

    @Test
    public void javaType_noArg_fieldType() {
        setField("defaultReference");
        initialProperty();

        String res = property.javaType();

        assertEquals(ReferredTestBean.class.getCanonicalName(), res);
    }
}
