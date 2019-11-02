package net.sunxu.mybatis.automapper.processor.mapper.mapper;

import net.sunxu.mybatis.automapper.entity.annotation.*;
import net.sunxu.mybatis.automapper.mapper.EntityMapper;
import net.sunxu.mybatis.automapper.mapper.annotation.addition.MethodUseCache;
import net.sunxu.mybatis.automapper.mapper.annotation.entity.SelectAll;
import net.sunxu.mybatis.automapper.processor.mapper.xml.XmlElement;
import org.apache.ibatis.type.Alias;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.Test;

import java.util.List;

public class SelectAllHandlerTests extends TestForDecorateHandler<SelectAllHandler> {

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
            private TestData.PersonCard personCard;
            @Reference(localColumns = @Reference.LocalColumn("department_id"))
            private TestData.Department department;
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
    }

    private interface TestMapper extends EntityMapper<TestData.Person> {
        @MethodUseCache
        @SelectAll(orderBy = "id")
        int listAll();
    }

    @Test
    public void get_testMapper_validXmlData() {
        List<XmlElement> res = makeElements(TestMapper.class);

        assertCommentEquals(res.get(0), "autoMapper for [SelectAll]");
        assertListAll(res.get(1));
    }

    private void assertListAll(XmlElement listAll) {
        Element expect = DocumentHelper.createElement("select")
                .addAttribute("id", "listAll")
                .addAttribute("resultMap", EntityMapperDecorator.RESULT_MAP_FULL_COLUMN)
                .addAttribute("useCache", "true");

        expect.setText("select <include refid=\"" + EntityMapperDecorator.SQL_FULL_COLUMN + "\" /> " +
                "from person order by id");
        assertElementEquals(listAll, expect);
    }
}
