package net.sunxu.mybatis.automapper.processor.mapper.mapper;

import net.sunxu.mybatis.automapper.entity.annotation.*;
import net.sunxu.mybatis.automapper.mapper.EntityMapper;
import net.sunxu.mybatis.automapper.mapper.annotation.entity.UpdateByIndex;
import net.sunxu.mybatis.automapper.processor.mapper.xml.XmlElement;
import org.apache.ibatis.type.Alias;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.Test;

import java.util.List;

public class UpdateByIndexHandlerTests extends TestForDecorateHandler<UpdateByIndexHandler> {
    private static class TestData {
        @Entity
        @Alias("Person")
        private static class Person {
            @Column
            @PrimaryKey
            @AutoGenerate(kind = AutoGenerate.Kind.UPDATE)
            private Long id;
            @Column
            @PrimaryKey(101)
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
        @UpdateByIndex("")
        int updateByIndex(TestData.Person person, long id, String name);
    }


    @Test
    public void get_testMapper_validXmlData() {
        List<XmlElement> res = makeElements(TestIndexMapper.class);
        assertCommentEquals(res.get(0), "autoMapper for [UpdateByIndex]");


        Element expect = DocumentHelper.createElement("update")
                .addAttribute("id", "updateByIndex")
                .addAttribute("resultType", "int")
                .addAttribute("useGeneratedKeys", "true")
                .addAttribute("keyProperty", "id")
                .addAttribute("keyColumn", "id");
        expect.setText("update person set\n" +
                "department_id = #{0.department.id}, name = #{0.name} where id = #{1} and name = #{2}");
        expect.addElement("selectKey")
                .addAttribute("keyProperty", "name")
                .addAttribute("resultType", String.class.getCanonicalName())
                .addAttribute("order", "AFTER")
                .setText("select 1 from dual");

        assertElementEquals(res.get(1), expect);
    }
}
