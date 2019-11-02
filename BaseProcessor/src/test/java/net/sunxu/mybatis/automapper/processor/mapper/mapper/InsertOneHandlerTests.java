package net.sunxu.mybatis.automapper.processor.mapper.mapper;

import net.sunxu.mybatis.automapper.entity.annotation.*;
import net.sunxu.mybatis.automapper.mapper.EntityMapper;
import net.sunxu.mybatis.automapper.mapper.annotation.addition.MethodUseCache;
import net.sunxu.mybatis.automapper.mapper.annotation.entity.InsertOne;
import net.sunxu.mybatis.automapper.processor.mapper.xml.XmlElement;
import net.sunxu.mybatis.automapper.processor.testutil.TestEnvRunner;
import org.apache.ibatis.type.Alias;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

@RunWith(TestEnvRunner.class)
public class InsertOneHandlerTests extends TestForDecorateHandler<InsertOneHandler> {
    private static class TestData {
        @Entity
        @Alias("Person")
        private static class Person {
            @Column
            @PrimaryKey
            @AutoGenerate
            private Long id;
            @AutoGenerate(kind = AutoGenerate.Kind.AFTER_INSERT, value = "select 1 from dual")
            @AutoGenerate(kind = AutoGenerate.Kind.BEFORE_INSERT, value = "select 1 from dual")
            @Column
            private String name;
            @Composite(value = "id", column = "id")
            @Composite(value = "name", column = "name")
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

    private interface TestMapper extends EntityMapper<TestData.Person> {
        @MethodUseCache
        @InsertOne
        int insert(TestData.Person val);
    }

    @Test
    public void get_testMapper_validXmlData() {
        List<XmlElement> res = makeElements(TestMapper.class);

        assertCommentEquals(res.get(0), "autoMapper for [InsertOne]");
        assertInsert(res.get(1));
    }


    private void assertInsert(XmlElement insert) {
        Element expect = DocumentHelper.createElement("insert")
                .addAttribute("id", "insert")
                .addAttribute("useGeneratedKeys", "true")
                .addAttribute("keyProperty", "id")
                .addAttribute("keyColumn", "id")
                .addAttribute("useCache", "true");

        expect.addElement("selectKey")
                .addAttribute("keyProperty", "name")
                .addAttribute("resultType", String.class.getCanonicalName())
                .addAttribute("order", "BEFORE")
                .setText("select 1 from dual");
        expect.setText("insert into person (department_id, name) values(#{department.id}, #{name})");
        expect.addElement("selectKey")
                .addAttribute("keyProperty", "name")
                .addAttribute("resultType", String.class.getCanonicalName())
                .addAttribute("order", "AFTER")
                .setText("select 1 from dual");
        assertElementEquals(insert, expect);
    }
}
