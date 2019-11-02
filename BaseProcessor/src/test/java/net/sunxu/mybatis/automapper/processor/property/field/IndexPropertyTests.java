package net.sunxu.mybatis.automapper.processor.property.field;

import net.sunxu.mybatis.automapper.entity.annotation.*;
import net.sunxu.mybatis.automapper.processor.property.Field;
import net.sunxu.mybatis.automapper.processor.property.TestForAnnotationProperty;
import net.sunxu.mybatis.automapper.processor.util.AutoMapperException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import javax.lang.model.element.VariableElement;

import static net.sunxu.mybatis.automapper.processor.testutil.TestUtils.assertCollectionEqualsUnordered;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;

public class IndexPropertyTests extends TestForAnnotationProperty<IndexProperty> {
    @Mock
    private Indexes indexes;
    @Mock
    private Index index, index2;
    @Mock
    private PrimaryKey primaryKey;
    @Mock
    private Column column;
    @Mock
    private Field field;
    @Mock
    private VariableElement fieldElement;

    @Before
    public void setUp() {
        doReturn(new Index[]{index, index2}).when(indexes).value();
        doReturn(column).when(fieldElement).getAnnotation(Column.class);
        doReturn("fieldName").when(field).getName();
    }

    @Test
    public void getIndexNames_fieldWithIndex_getNames() {
        doReturn(new Index[]{index}).when(fieldElement).getAnnotationsByType(Index.class);
        initialProperty(fieldElement, field);

        assertCollectionEqualsUnordered(property.getIndexNames(), "fieldName");
    }

    @Test
    public void getIndexNames_fieldWithIndexes_getNames() {
        doReturn(indexes.value()).when(fieldElement).getAnnotationsByType(Index.class);
        doReturn("index2").when(index2).value();
        initialProperty(fieldElement, field);

        assertCollectionEqualsUnordered(property.getIndexNames(), "fieldName", "index2");
    }

    @Test
    public void getIndexNames_fieldWithPrimaryKey_getNames() {
        doReturn(new Index[0]).when(fieldElement).getAnnotationsByType(Index.class);
        doReturn(primaryKey).when(fieldElement).getAnnotation(PrimaryKey.class);
        initialProperty(fieldElement, field);

        assertCollectionEqualsUnordered(property.getIndexNames(), "");
    }

    @Test
    public void getOrder_noArg_getAnnoValue() {
        doReturn(new Index[]{index}).when(fieldElement).getAnnotationsByType(Index.class);
        doReturn(101).when(index).order();
        initialProperty(fieldElement, field);

        int res = property.getOrder("fieldName");

        assertEquals(101, res);
    }

    @Test(expected = AutoMapperException.class)
    public void initial_noColumnOrCompositeOrReference_throwException() {
        doReturn(new Index[]{index}).when(fieldElement).getAnnotationsByType(Index.class);
        doReturn(null).when(fieldElement).getAnnotation(Column.class);
        doReturn(null).when(fieldElement).getAnnotation(Reference.class);
        doReturn(new Composite[0]).when(fieldElement).getAnnotationsByType(Composite.class);

        initialProperty(fieldElement, field);
    }

    @Test(expected = AutoMapperException.class)
    public void initial_duplicateIndexName_throwException() {
        doReturn(indexes.value()).when(fieldElement).getAnnotationsByType(Index.class);
        initialProperty(fieldElement, field);
    }
}
