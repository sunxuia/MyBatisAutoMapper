package net.sunxu.mybatis.automapper.processor.mapper.mapper;

import net.sunxu.mybatis.automapper.entity.annotation.*;
import net.sunxu.mybatis.automapper.mapper.EntityMapper;
import net.sunxu.mybatis.automapper.mapper.annotation.entity.SelectByIndex;
import net.sunxu.mybatis.automapper.processor.mapper.xml.XmlElement;
import org.apache.ibatis.type.Alias;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.Test;

import java.util.List;

public class SelectByIndexHandlerTests extends TestForDecorateHandler<SelectByIndexHandler> {
    private static class TestData {
        @Entity
        @Alias("Person")
        private static class Person {
            @Column
            @PrimaryKey
            @AutoGenerate
            private Long id;
            @Column
            @AutoGenerate(kind = AutoGenerate.Kind.AFTER_UPDATE, value = "select 1 from dual")
            private String name;

            @Composite(value = "id", column = "id")
            @Composite(value = "name", column = "name")
            @Index
            private PersonCard personCard;

            @Reference(localColumns = @Reference.LocalColumn("department_id"))
            private Department department;
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

    private interface TestIndexMapper extends EntityMapper<TestData.Person> {
        @SelectByIndex("")
        TestData.Person select(long id);
    }


    @Test
    public void get_testMapper_validXmlData() {
        List<XmlElement> res = makeElements(TestIndexMapper.class);
        assertCommentEquals(res.get(0), "autoMapper for [SelectByIndex]");

        Element expect = DocumentHelper.createElement("select")
                .addAttribute("id", "select")
                .addAttribute("resultMap", EntityMapperDecorator.RESULT_MAP_FULL_COLUMN);
        expect.setText("select <include refid=\"" + EntityMapperDecorator.SQL_FULL_COLUMN + "\" /> " +
                "from person where id = #{0}");
        assertElementEquals(res.get(1), expect);
    }
}
