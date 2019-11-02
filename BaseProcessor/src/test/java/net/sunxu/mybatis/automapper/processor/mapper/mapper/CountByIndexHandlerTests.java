package net.sunxu.mybatis.automapper.processor.mapper.mapper;

import net.sunxu.mybatis.automapper.entity.annotation.*;
import net.sunxu.mybatis.automapper.mapper.EntityMapper;
import net.sunxu.mybatis.automapper.mapper.annotation.entity.CountByIndex;
import net.sunxu.mybatis.automapper.processor.mapper.xml.XmlElement;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.type.Alias;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.Test;

import java.util.List;

public class CountByIndexHandlerTests extends TestForDecorateHandler<CountByIndexHandler> {
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
        @CountByIndex("personCard")
        int countByIndex(@Param("person") TestData.PersonCard personCard);
    }


    @Test
    public void get_testMapper_validXmlData() {
        List<XmlElement> res = makeElements(TestIndexMapper.class);
        assertCommentEquals(res.get(0), "autoMapper for [CountByIndex]");


        Element expect = DocumentHelper.createElement("select")
                .addAttribute("id", "countByIndex")
                .addAttribute("resultType", "int");
        expect.setText("select count(*) from person where id = #{person.id} and name = #{person.name} ");
        assertElementEquals(res.get(1), expect);
    }
    private interface TestIndexMapper2 extends EntityMapper<TestData.Person> {
        @CountByIndex("personCard")
        Boolean countByIndex(@Param("person") TestData.PersonCard personCard);
    }

    @Test
    public void get_testMapperReturnBoolean_validXmlData() {
        List<XmlElement> res = makeElements(TestIndexMapper2.class);
        assertCommentEquals(res.get(0), "autoMapper for [CountByIndex]");

        Element expect = DocumentHelper.createElement("select")
                .addAttribute("id", "countByIndex")
                .addAttribute("resultType", Boolean.class.getCanonicalName());
        expect.setText("<![CDATA[select case count(*) when 0 then 0 else 1 end" +
                " from person where id = #{person.id} and name = #{person.name} ]]>");
        assertElementEquals(res.get(1), expect);
    }
}
