package net.sunxu.mybatis.automapper.processor.mapper.mapper;

import net.sunxu.mybatis.automapper.entity.annotation.*;
import net.sunxu.mybatis.automapper.mapper.EntityMapper;
import net.sunxu.mybatis.automapper.processor.mapper.MapperElementsCreator;
import net.sunxu.mybatis.automapper.processor.mapper.xml.XmlElement;
import net.sunxu.mybatis.automapper.processor.testutil.TestEnvRunner;
import org.apache.ibatis.type.Alias;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Map;

@RunWith(TestEnvRunner.class)
public class EntityMapperHandlerTests extends TestForDecorateHandler<EntityMapperHandler> {

    private static class TestData {
        @Entity
        @Alias("Person")
        private static class Person {
            @Column
            @PrimaryKey
            private Long id;
            @Column
            private String name;
            @Composite(value = "id", column = "id")
            @Composite(value = "name", column = "name")
            private PersonCard personCard;
            @Reference(localColumns = @Reference.LocalColumn("department_id"))
            private Department department;
            @Cascade
            private Lesson oneLesson;
            @Cascade
            private List<Lesson> lessons;
        }

        private static class PersonCard {
            private Long id;
            private String name;
        }

        @Entity
        private static class Department {
            @PrimaryKey
            @Column
            private Long id;
        }

        @Entity
        private static class Lesson {
            @PrimaryKey
            @Column
            private Long id;
        }
    }

    private interface TestEntityMapper extends EntityMapper<TestData.Person> {}


    @Test
    public void get_testMapper_validXmlData() {
        Map<String, MapperElementsCreator> providers = getCreator(TestEntityMapper.class);

        List<XmlElement> entityMapper = providers.get(TestEntityMapper.class.getCanonicalName()).getElements();
        assertCommentEquals(entityMapper.get(0), "autoMapper for [entity mapper]");
        assertResultMap(entityMapper.get(1));
        assertSql(entityMapper.get(2));
        assertSelectByIndex(entityMapper.get(3));

        List<XmlElement> annoymousMapper = providers.get(annoymousMapperName).getElements();
        assertCommentEquals(annoymousMapper.get(0), "autoMapper for [entity mapper]");
        int lessonIndex =
                ((Element) toNode(annoymousMapper.get(1))).attribute("id").getValue().contains("Lesson") ? 1 : 4;
        assertAnnoymousResultMapOfLesson(annoymousMapper.get(lessonIndex));
        assertAnnoymousSql(annoymousMapper.get(lessonIndex + 1));
        assertAnnoymousSelectByIndex(annoymousMapper.get(lessonIndex + 2));
    }

    private void assertResultMap(XmlElement actualResultMap) {
        Element expectedResultMap = DocumentHelper.createElement("resultMap");
        expectedResultMap.addAttribute("id", EntityMapperDecorator.RESULT_MAP_FULL_COLUMN)
                .addAttribute("type", TestData.Person.class.getSimpleName());
        //0
        expectedResultMap.addElement("id")
                .addAttribute("property", "id")
                .addAttribute("javaType", Long.class.getCanonicalName())
                .addAttribute("column", "id");
        //1
        expectedResultMap.addElement("result")
                .addAttribute("property", "name")
                .addAttribute("javaType", String.class.getCanonicalName())
                .addAttribute("column", "name");
        //2
        expectedResultMap.addElement("association")
                .addAttribute("property", "personCard")
                .addAttribute("javaType", TestData.PersonCard.class.getCanonicalName())
                .addElement("association")
                .addAttribute("property", "id")
                .addAttribute("column", "id")
                .addAttribute("javaType", Long.class.getCanonicalName())
                .getParent()
                .addElement("association")
                .addAttribute("property", "name")
                .addAttribute("javaType", String.class.getCanonicalName())
                .addAttribute("column", "name");
        //3
        expectedResultMap.addElement("association")
                .addAttribute("property", "department")
                .addAttribute("column", "department_id")
                .addAttribute("javaType", TestData.Department.class.getCanonicalName())
                .addAttribute("select", annoymousMapperName + "." +
                        TestData.Department.class.getCanonicalName().replaceAll("\\.", "_") + "_" +
                        EntityMapperDecorator.SELECT_SELECT_BY_COLUMN_FROM_INDEX_PREFIX);
        //4
        expectedResultMap.addElement("association")
                .addAttribute("property", "oneLesson")
                .addAttribute("column", "id")
                .addAttribute("javaType", TestData.Lesson.class.getCanonicalName())
                .addAttribute("select", annoymousMapperName + "." +
                        TestData.Lesson.class.getCanonicalName().replaceAll("\\.", "_") + "_" +
                        EntityMapperDecorator.SELECT_SELECT_BY_COLUMN_FROM_INDEX_PREFIX);
        //5
        expectedResultMap.addElement("collection")
                .addAttribute("property", "lessons")
                .addAttribute("column", "id")
                .addAttribute("ofType", TestData.Lesson.class.getCanonicalName())
                .addAttribute("select", annoymousMapperName + "." +
                        TestData.Lesson.class.getCanonicalName().replaceAll("\\.", "_") + "_" +
                        EntityMapperDecorator.SELECT_SELECT_BY_COLUMN_FROM_INDEX_PREFIX);

        assertElementEquals(actualResultMap, expectedResultMap);
    }

    private void assertSql(XmlElement actualSql) {
        Element expected = DocumentHelper.createElement("sql")
                .addAttribute("id", EntityMapperDecorator.SQL_FULL_COLUMN);
        expected.setText("department_id, id, name");
        assertElementEquals(actualSql, expected);
    }

    private void assertSelectByIndex(XmlElement actualSelectByIndex) {
        Element expected = DocumentHelper.createElement("select")
                .addAttribute("id", EntityMapperDecorator.SELECT_SELECT_BY_COLUMN_FROM_INDEX_PREFIX)
                .addAttribute("resultMap", EntityMapperDecorator.SQL_FULL_COLUMN);
        expected.setText(String.format("select <include refid=\"%s\" /> from person where id = #{0}",
                EntityMapperDecorator.SQL_FULL_COLUMN));
        assertElementEquals(actualSelectByIndex, expected);
    }

    private void assertAnnoymousResultMapOfLesson(XmlElement actualResultMap) {
        Element expected = DocumentHelper.createElement("resultMap")
                .addAttribute("id", getAnnoymousMapperEntityPrefix(TestData.Lesson.class) +
                        EntityMapperDecorator.RESULT_MAP_FULL_COLUMN)
                .addAttribute("type", TestData.Lesson.class.getCanonicalName());
        expected.addElement("id")
                .addAttribute("property", "id")
                .addAttribute("javaType", Long.class.getCanonicalName())
                .addAttribute("column", "id");
        assertElementEquals(actualResultMap, expected);
    }

    private String getAnnoymousMapperEntityPrefix(Class<?> clazz) {
        return clazz.getCanonicalName().replaceAll("\\.", "_") + "_";
    }

    private void assertAnnoymousSql(XmlElement actualSql) {
        Element expected = DocumentHelper.createElement("sql")
                .addAttribute("id", getAnnoymousMapperEntityPrefix(TestData.Lesson.class) +
                        EntityMapperDecorator.SQL_FULL_COLUMN);
        expected.setText("id");
        assertElementEquals(actualSql, expected);
    }

    private void assertAnnoymousSelectByIndex(XmlElement actualSelectByIndex) {
        Element expected = DocumentHelper.createElement("select")
                .addAttribute("id", getAnnoymousMapperEntityPrefix(TestData.Lesson.class) +
                        EntityMapperDecorator.SELECT_SELECT_BY_COLUMN_FROM_INDEX_PREFIX)
                .addAttribute("resultMap", getAnnoymousMapperEntityPrefix(TestData.Lesson.class) +
                        EntityMapperDecorator.SQL_FULL_COLUMN);
        expected.setText(String.format("select <include refid=\"%s\" /> from lesson where id = #{0}",
                getAnnoymousMapperEntityPrefix(TestData.Lesson.class) + EntityMapperDecorator.SQL_FULL_COLUMN));
        assertElementEquals(actualSelectByIndex, expected);
    }
}
