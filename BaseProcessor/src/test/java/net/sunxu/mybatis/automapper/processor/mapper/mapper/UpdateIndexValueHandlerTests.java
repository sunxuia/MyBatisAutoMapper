package net.sunxu.mybatis.automapper.processor.mapper.mapper;

import net.sunxu.mybatis.automapper.entity.annotation.*;
import net.sunxu.mybatis.automapper.mapper.EntityMapper;
import net.sunxu.mybatis.automapper.mapper.annotation.entity.EntityValue;
import net.sunxu.mybatis.automapper.mapper.annotation.entity.UpdateIndexValue;
import net.sunxu.mybatis.automapper.processor.mapper.xml.XmlElement;
import org.apache.ibatis.type.Alias;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.Test;

import java.util.List;

public class UpdateIndexValueHandlerTests extends TestForDecorateHandler<UpdateIndexValueHandler> {
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
        @UpdateIndexValue(indexNameToUpdate = "", indexNameToRestrict = "")
        int updateByIndex(@EntityValue TestData.Person person, long id, String name);
    }


    @Test
    public void get_testMapper_validXmlData() {
        List<XmlElement> res = makeElements(TestIndexMapper.class);
        assertCommentEquals(res.get(0), "autoMapper for [UpdateIndexValue]");


        Element expect = DocumentHelper.createElement("update")
                .addAttribute("id", "updateByIndex")
                .addAttribute("resultType", "int");
        expect.setText("update person set id = #{0.id}, name = #{0.name} where id = #{1} and name = #{2}");

        assertElementEquals(res.get(1), expect);
    }
}
