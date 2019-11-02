package net.sunxu.mybatis.automapper.processor.mapper.entity;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import net.sunxu.mybatis.automapper.entity.annotation.*;
import net.sunxu.mybatis.automapper.processor.testutil.TestEnvRunner;
import net.sunxu.mybatis.automapper.processor.util.AutoMapperException;
import org.apache.ibatis.type.Alias;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collections;

import static java.util.stream.Collectors.toList;
import static junit.framework.TestCase.*;
import static net.sunxu.mybatis.automapper.processor.testutil.TestUtils.*;

@RunWith(TestEnvRunner.class)
public class EntityModelFactoryTests {
    @Inject
    private EntityModelFactory factory;

    private EntityModel entityModel;

    private static class TestDataWithNoEntityAnnotation {}

    @Test(expected = AutoMapperException.class)
    public void getEntityModel_noEntity_throwException() {
        initialEntityModel(TestDataWithNoEntityAnnotation.class);
    }

    private void initialEntityModel(Class<?> clazz) {
        entityModel = factory.getEntityModel(clazz.getCanonicalName());
    }

    @Entity
    @Alias("test_data_empty")
    private static class TestDataEmpty {}

    @Test
    public void getEntityModel_empty_validEntityModel() {
        initialEntityModel(TestDataEmpty.class);

        assertEquals(TestDataEmpty.class.getCanonicalName(), entityModel.getName());
        assertEquals(TestDataEmpty.class.getSimpleName(), entityModel.getTableName());
        assertEquals("test_data_empty", entityModel.getAlias());

        assertMapEquals(Collections.emptyMap(), entityModel.getEntityIndexes());
        assertMapEquals(Collections.emptyMap(), entityModel.getEntityAutoGenerateSelectKeys());
        assertMapEquals(Collections.emptyMap(), entityModel.getEntityAutoGenerateSqlEmbeddeds());
        assertMapEquals(Collections.emptyMap(), entityModel.getColumnFields());
        assertMapEquals(Collections.emptyMap(), entityModel.getColumnNamePropertyNameMap());
        assertMapEquals(Collections.emptyMap(), entityModel.getCascades());
        assertMapEquals(Collections.emptyMap(), entityModel.getComposites());
        assertMapEquals(Collections.emptyMap(), entityModel.getReferences());
        assertMapEquals(Collections.emptyMap(), entityModel.getFields());
    }


    //column
    @Entity
    private static class TestDataColumn {
        @Column(value = "id")
        private int id;

        @Column(value = "id", isPreferredWhenColumnNameConflict = true)
        private int id2;
    }

    @Test
    public void getEntityModel_column_getColumnData() {
        initialEntityModel(TestDataColumn.class);

        assertCollectionEqualsUnordered(entityModel.getColumns().keySet(), "id", "id2");
        assertCollectionEqualsUnordered(entityModel.getColumnFields().keySet(), "id", "id2");
        assertMapEquals(ImmutableMap.of("id", "id2"), entityModel.getColumnNamePropertyNameMap());
        assertMapEquals(
                ImmutableMap.of("id", "int", "id2", "int"),
                entityModel.getFields());
    }

    //composite
    private static class TestCompositeBean {
        private Long value;
        private TestCompositeBean otherBean;
    }

    @Entity
    private static class TestDataComposite {
        @Composite(value = "value", column = "value")
        @Composite(value = "otherBean.value", column = "value", isPreferredWhenColumnNameConflict = true)
        private TestCompositeBean testBean;
    }

    @Test
    public void getEntityModel_composite_getCompositeData() {
        initialEntityModel(TestDataComposite.class);

        assertCollectionEqualsUnordered(entityModel.getComposites().keySet(), "testBean");
        assertEquals("testBean", entityModel.getComposites().get("testBean").fieldName());
        assertCollectionEqualsUnordered(entityModel.getColumnFields().keySet(),
                "testBean.value", "testBean.otherBean.value");
        assertEquals("testBean.value", entityModel.getColumnFields().get("testBean.value").propertyName());
        assertMapEquals(ImmutableMap.of("value", "testBean.otherBean.value"),
                entityModel.getColumnNamePropertyNameMap());
        assertMapEquals(
                ImmutableMap.of("testBean", TestCompositeBean.class.getCanonicalName()),
                entityModel.getFields());
    }

    //reference
    @Entity
    private static class TestDataReference {
        @Column("column_id")
        @PrimaryKey
        private Long idr;
    }

    @Entity
    private static class TestDataWithReference {
        @Reference(localColumns = {@Reference.LocalColumn("local_column")})
        private TestDataReference id;
    }

    @Test
    public void getEntityModel_simpleReference_getRefernceData() {
        initialEntityModel(TestDataWithReference.class);

        assertCollectionEquals(entityModel.getReferences().keySet(), "id");
        assertEquals("id", entityModel.getReferences().get("id").fieldName());
        assertCollectionEqualsUnordered(entityModel.getColumnFields().keySet(), "id.idr");
        assertEquals("id.idr", entityModel.getColumnFields().get("id.idr").propertyName());
        assertMapEquals(ImmutableMap.of("local_column", "id.idr"), entityModel.getColumnNamePropertyNameMap());
        assertMapEquals(
                ImmutableMap.of("id", TestDataReference.class.getCanonicalName()),
                entityModel.getFields());
    }

    @Entity
    private static class TestDataWithDualReference {
        @PrimaryKey
        @Column
        private Long id;

        @Reference(referIndex = "")
        @Index("INDEX")
        private TestDataWithDualReference primaryKeyReference;

        @Reference(referIndex = "INDEX")
        private TestDataWithDualReference otherReference;
    }

    @Test
    public void getEntityModel_threeReference_getReferenceData() {
        initialEntityModel(TestDataWithDualReference.class);

        assertCollectionEquals(entityModel.getReferences().keySet(),
                "primaryKeyReference", "otherReference");
        assertCollectionEqualsUnordered(entityModel.getColumnFields().keySet(),
                "id", "primaryKeyReference.id", "otherReference.primaryKeyReference.id");
    }

    @Entity
    private static class TestDataWithWrongReferenceWrongLocalColumnsCount {
        @Reference(localColumns = {
                @Reference.LocalColumn("local_column"),
                @Reference.LocalColumn("local_column2"),
        })
        private TestDataReference id;
    }

    @Test(expected = AutoMapperException.class)
    public void getEntityModel_wrongLocalReferenceWithColumnsCount_throwException() {
        initialEntityModel(TestDataWithWrongReferenceWrongLocalColumnsCount.class);
    }

    @Entity
    private static class TestDataWithWrongReferenceWrongIndex {
        @Reference(referIndex = "index_name_not_exist", localColumns = {@Reference.LocalColumn("local_column")})
        private TestDataReference id;
    }

    @Test(expected = AutoMapperException.class)
    public void getEntityModel_wrongReferenceWithIndexNameNotExist_throwException() {
        initialEntityModel(TestDataWithWrongReferenceWrongIndex.class);
    }

    @Entity
    private static class TestDataWithCycleReference {
        @Reference
        @PrimaryKey
        private TestDataWithCycleReference cycleReference;
    }

    @Test(expected = AutoMapperException.class)
    public void getEntityModel_cycleReferenceInEntity_throwException() {
        initialEntityModel(TestDataWithCycleReference.class);
    }

    //cascade
    @Entity
    public static class TestDataCascade {
        @Column
        @PrimaryKey
        private Long idc;
    }

    @Entity
    public static class TestDataWithCascade {
        @Column
        @PrimaryKey
        private Long id;

        @Cascade
        private TestDataCascade testDataCascade;
    }

    @Test
    public void getEntityModel_cascade_getCascadeData() {
        initialEntityModel(TestDataWithCascade.class);

        assertCollectionEqualsUnordered(entityModel.getCascades().keySet(), "testDataCascade");
        assertEquals("testDataCascade", entityModel.getCascades().get("testDataCascade").fieldName());
        assertCollectionEqualsUnordered(entityModel.getColumnFields().keySet(), "id");
        assertMapEquals(
                ImmutableMap.of("id", Long.class.getCanonicalName(),
                        "testDataCascade", TestDataCascade.class.getCanonicalName()),
                entityModel.getFields());
    }

    @Test
    public void getEntityModel_cascadeWIthDifferentReadSequence_getCascadeDada() {
        initialEntityModel(TestDataCascade.class);
        initialEntityModel(TestDataWithCascade.class);

        assertCollectionEqualsUnordered(entityModel.getCascades().keySet(), "testDataCascade");
        assertEquals("testDataCascade", entityModel.getCascades().get("testDataCascade").fieldName());
        assertCollectionEqualsUnordered(entityModel.getColumnFields().keySet(), "id");
    }

    @Entity
    public static class TestDataWithCascadeWrongLocalIndex {
        @Column
        @PrimaryKey
        private Long id;

        @Cascade(value = "", localIndex = "index_not_exist")
        private TestDataCascade testDataCascade;
    }

    @Test(expected = AutoMapperException.class)
    public void getEntityModel_wrongCascadeWithLocalIndexNotExist_throwException() {
        initialEntityModel(TestDataWithCascadeWrongLocalIndex.class);
    }

    @Entity
    public static class TestDataWithCascadeWrongReferenceIndex {
        @Column
        @PrimaryKey
        private Long id;

        @Cascade(value = "index_not_exist")
        private TestDataCascade testDataCascade;
    }

    @Test(expected = AutoMapperException.class)
    public void getEntityModel_wrongCascadeWithReferIndexNotExist_throwException() {
        initialEntityModel(TestDataWithCascadeWrongReferenceIndex.class);
    }

    @Entity
    private static class TestDataWithCascadeWrongIndexCount {
        @Column
        @PrimaryKey(100)
        private Long id;

        @Column
        @PrimaryKey(101)
        private Long id2;

        @Cascade
        private TestDataCascade testDataCascade;
    }

    @Test(expected = AutoMapperException.class)
    public void getEntityModel_wrongCascadeReferCount_throwException() {
        initialEntityModel(TestDataWithCascadeWrongIndexCount.class);
    }

    //index
    @Entity
    private static class TestDataWithPrimaryKey {
        @PrimaryKey(1)
        @Column
        private Long id;

        @PrimaryKey(2)
        @Composite(value = "value", column = "value")
        private TestCompositeBean id2;

        @PrimaryKey(3)
        @Reference
        public TestDataReference id3;
    }

    @Test
    public void getEntityModel_primaryKey_getEntityIndex() {
        initialEntityModel(TestDataWithPrimaryKey.class);

        assertEquals(1, entityModel.getEntityIndexes().size());
        EntityIndex pkIndex = entityModel.getEntityIndexes().get("");
        assertNotNull(pkIndex);
        assertTrue(pkIndex.isPrimaryKey());
        assertEquals("", pkIndex.getName());
        assertCollectionEquals(
                pkIndex.getColumnFields().stream().map(l -> l.propertyName()).collect(toList()),
                "id", "id2.value", "id3.idr");

        assertEquals(3, pkIndex.getFields().size());
        assertCollectionEquals(pkIndex.getFields().stream().map(l -> l.getLabel()).collect(toList()),
                "id", "id2", "id3");
        assertCollectionEquals(pkIndex.getFields().stream().map(l -> l.getValue().size()).collect(toList()),
                1, 1, 1);
        assertCollectionEquals(
                pkIndex.getFields().stream().map(l -> l.getValue().get(0).propertyName()).collect(toList()),
                "id", "id2.value", "id3.idr");
    }

    @Entity
    private static class TestDataWithWrongPrimaryKeyOrder {
        @PrimaryKey
        @Column
        private Long id;
        @PrimaryKey
        @Column
        private Long id2;
    }

    @Test(expected = AutoMapperException.class)
    public void getEntityModel_duplicatePrimaryKeyOrder_throwException() {
        initialEntityModel(TestDataWithWrongPrimaryKeyOrder.class);
    }

    @Entity
    private static class TestDataWithIndex {
        @Index(value = "index_name", order = 1)
        @Column
        private Long id;

        @Index(value = "index_name", order = 2)
        @Composite(value = "value", column = "value")
        private TestCompositeBean id2;

        @Index(value = "index_name", order = 3)
        @Reference
        public TestDataReference id3;
    }

    @Test
    public void getEntityModel_index_getEntityIndex() {
        initialEntityModel(TestDataWithIndex.class);

        assertEquals(1, entityModel.getEntityIndexes().size());
        EntityIndex index = entityModel.getEntityIndexes().get("index_name");
        assertNotNull(index);
        assertFalse(index.isPrimaryKey());
        assertEquals("index_name", index.getName());
        assertCollectionEquals(
                index.getColumnFields().stream().map(l -> l.propertyName()).collect(toList()),
                "id", "id2.value", "id3.idr");

        assertEquals(3, index.getFields().size());
        assertCollectionEquals(
                index.getFields().stream().map(l -> l.getLabel()).collect(toList()),
                "id", "id2", "id3");
        assertCollectionEquals(
                index.getFields().stream().map(l -> l.getValue().size()).collect(toList()),
                1, 1, 1);
        assertCollectionEquals(
                index.getFields().stream().map(l -> l.getValue().get(0).propertyName()).collect(toList()),
                "id", "id2.value", "id3.idr");
    }

    @Entity
    private static class TestDataWithDuplicateIndexOrder {
        @Index(value = "index_name", order = 1)
        @Column
        private Long id;
        @Index(value = "index_name", order = 1)
        @Column
        private Long id2;
    }

    @Test(expected = AutoMapperException.class)
    public void getEntityModel_duplicatedIndexOrder_throwException() {
        initialEntityModel(TestDataWithDuplicateIndexOrder.class);
    }

    //autoGenerate
    @Entity
    private static class TestDataWithColumnAndAutoGenerateSelectKey {
        @AutoGenerate(kind = AutoGenerate.Kind.BEFORE_INSERT, value = "select 1 id,2 id2 from dual")
        @Column("column_id")
        private Long id;

        @AutoGenerate(kind = AutoGenerate.Kind.BEFORE_INSERT)
        @Column("column_id2")
        private Long id2;

        @Column(value = "column_id2", isPreferredWhenColumnNameConflict = true)
        private Long id3;
    }

    @Test
    public void getEntityModel_withAutoGenerateColumn_getAutoGenerateSelectKey() {
        initialEntityModel(TestDataWithColumnAndAutoGenerateSelectKey.class);

        EntityAutoGenerateSelectKey selectKey =
                entityModel.getEntityAutoGenerateSelectKeys().get(AutoGenerate.Kind.BEFORE_INSERT);
        assertNotNull(selectKey);
        String[] columns = selectKey.getKeyColumn().split(",");
        assertEquals(2, columns.length);
        String[] properties = selectKey.getKeyProperty().split(",");
        assertEquals(2, properties.length);
        assertMapEquals(ImmutableMap.of("column_id", "id", "column_id2", "id2"),
                ImmutableMap.of(columns[0], properties[0], columns[1], properties[1]));

        assertEquals(TestDataWithColumnAndAutoGenerateSelectKey.class.getCanonicalName(),
                selectKey.getResultType());
        assertEquals("BEFORE", selectKey.getOrder());
        assertEquals("select 1 id,2 id2 from dual", selectKey.getExpression());
        assertEquals(AutoGenerate.Kind.BEFORE_INSERT, selectKey.getKind());
    }

    @Entity
    private static class TestDataWithCompositeAndAutoGenerateSelectKey {
        @AutoGenerate(kind = AutoGenerate.Kind.BEFORE_INSERT, value = "select 1 from dual")
        @Composite(value = "value", column = "column_1")
        @Composite(value = "otherBean.value", column = "column_2")
        private TestCompositeBean id;
    }

    @Test
    public void getEntityModel_withAutoGenrateComposite_getAutoGenerateSelectKey() {
        initialEntityModel(TestDataWithCompositeAndAutoGenerateSelectKey.class);

        EntityAutoGenerateSelectKey selectKey =
                entityModel.getEntityAutoGenerateSelectKeys().get(AutoGenerate.Kind.BEFORE_INSERT);
        assertNotNull(selectKey);
        String[] columns = selectKey.getKeyColumn().split(",");
        assertEquals(2, columns.length);
        String[] properties = selectKey.getKeyProperty().split(",");
        assertEquals(2, properties.length);
        assertMapEquals(ImmutableMap.of("column_1", "id.value", "column_2", "id.otherBean.value"),
                ImmutableMap.of(columns[0], properties[0], columns[1], properties[1]));

        assertEquals(TestCompositeBean.class.getCanonicalName(), selectKey.getResultType());
        assertEquals("BEFORE", selectKey.getOrder());
        assertEquals("select 1 from dual", selectKey.getExpression());
        assertEquals(AutoGenerate.Kind.BEFORE_INSERT, selectKey.getKind());
    }

    @Entity
    private static class TestDataWithAutoGenerateSelectKeyWrongUse {
        @AutoGenerate(kind = AutoGenerate.Kind.BEFORE_INSERT)
        @Column("id")
        private Long id;

        @AutoGenerate(kind = AutoGenerate.Kind.BEFORE_INSERT)
        @Composite(value = "value", column = "id2")
        private TestCompositeBean id2;
    }

    @Test(expected = AutoMapperException.class)
    public void getEntityModel_withWrongAutoGenrate_throwException() {
        initialEntityModel(TestDataWithAutoGenerateSelectKeyWrongUse.class);
    }

    @Entity
    private static class TestDataWithReferenceAndSqlSelectKey {
        @AutoGenerate(kind = AutoGenerate.Kind.BEFORE_INSERT, value = "select 1 from dual")
        @Reference
        private TestDataReference testDataReference;
    }

    @Test
    public void getEntityModel_withAutoGenerateReference_getAutoGenerateSelectKey() {
        initialEntityModel(TestDataWithReferenceAndSqlSelectKey.class);

        EntityAutoGenerateSelectKey selectKey =
                entityModel.getEntityAutoGenerateSelectKeys().get(AutoGenerate.Kind.BEFORE_INSERT);
        assertNotNull(selectKey);
        assertEquals("testDataReference", selectKey.getKeyColumn());
        assertEquals("testDataReference.idr", selectKey.getKeyProperty());

        assertEquals(TestDataReference.class.getCanonicalName(), selectKey.getResultType());
        assertEquals("BEFORE", selectKey.getOrder());
        assertEquals("select 1 from dual", selectKey.getExpression());
        assertEquals(AutoGenerate.Kind.BEFORE_INSERT, selectKey.getKind());
    }

    @Entity
    private static class TestDataWithAutoGenerateSqlEmbedded {
        @AutoGenerate
        @Column("column_1")
        private Long id;

        @AutoGenerate("id.nextval")
        @Column("column_2")
        private Long id2;
    }

    @Test
    public void getEntityModel_withAutoGenerateColumn_getAutoGenerateSqlEmbedded() {
        initialEntityModel(TestDataWithAutoGenerateSqlEmbedded.class);

        EntityAutoGenerateSqlEmbedded sqlEmbedded =
                entityModel.getEntityAutoGenerateSqlEmbeddeds().get(AutoGenerate.Kind.INSERT);

        String[] columns = sqlEmbedded.getKeyColumn().split(",");
        assertEquals(2, columns.length);
        String[] properties = sqlEmbedded.getKeyProperty().split(",");
        assertEquals(2, properties.length);
        assertMapEquals(ImmutableMap.of("column_1", "id", "column_2", "id2"),
                ImmutableMap.of(columns[0], properties[0], columns[1], properties[1]));
        assertCollectionEqualsUnordered(sqlEmbedded.getColumnNames(), "column_1", "column_2");
        assertFalse(sqlEmbedded.hasExpression("column_1"));
        assertTrue(sqlEmbedded.hasExpression("column_2"));
        assertEquals("id.nextval", sqlEmbedded.getExpression("column_2"));
    }

    //type
    private static class TestDataSuperBean {
        @Column
        private Long id;
    }

    @Entity
    private static class TestDataInheriteBean extends TestDataSuperBean {
        @Column
        private Integer id;
    }

    @Test
    public void getEntityModel_withSameNameOverWrite_getInheritedField() {
        initialEntityModel(TestDataInheriteBean.class);

        assertEquals(1, entityModel.getColumnFields().size());
        assertEquals(Integer.class.getCanonicalName(), entityModel.getColumnFields().get("id").javaType());
    }
}
