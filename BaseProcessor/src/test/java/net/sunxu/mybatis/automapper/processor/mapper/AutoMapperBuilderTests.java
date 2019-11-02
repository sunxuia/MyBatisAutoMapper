package net.sunxu.mybatis.automapper.processor.mapper;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import net.sunxu.mybatis.automapper.entity.annotation.Entity;
import net.sunxu.mybatis.automapper.mapper.AutoMapper;
import net.sunxu.mybatis.automapper.mapper.EntityMapper;
import net.sunxu.mybatis.automapper.processor.environment.EnvironmentHelper;
import net.sunxu.mybatis.automapper.processor.environment.EnvironmentModule;
import net.sunxu.mybatis.automapper.processor.environment.SystemHelper;
import net.sunxu.mybatis.automapper.processor.mapper.mapper.EntityMapperDecorator;
import net.sunxu.mybatis.automapper.processor.mapper.template.CommentTemplate;
import net.sunxu.mybatis.automapper.processor.mapper.xml.XmlElement;
import net.sunxu.mybatis.automapper.processor.testutil.TestEnvRunner;
import org.dom4j.Document;
import org.dom4j.Element;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(TestEnvRunner.class)
public class AutoMapperBuilderTests {
    @Inject
    private EnvironmentModule.EnvironmentInjector injector;
    @Inject
    private SystemHelper sys;
    @Inject
    private EnvironmentHelper env;

    private AutoMapperBuilder autoMapperBuilder;

    private Element root;

    @Before
    public void setUp() {
        autoMapperBuilder = injector.createChildInjector()
                .getParent()
                .getInstance(AutoMapperBuilder.class);
        doNothing().when(sys).saveXMLFile(any(), any());
    }

    @Test
    public void build_withCustomCreators_useCustomCreator() {
        doReturn(ImmutableSet.of(env.getTypeElement(TestInterface.class.getCanonicalName())))
                .when(env)
                .getElementsAnnotatedWith(AutoMapper.class);

        autoMapperBuilder.build(ImmutableSet.of(TestElementsCreator.class));

        setDocumentToSave();
        assertEquals(2, root.nodeCount());
        assertEquals("TestElementsCreator", root.node(1).getText());
    }

    @Entity
    private static class TestData {}

    @AutoMapper
    private interface TestInterface extends EntityMapper<TestData> {}

    private static class TestElementsCreator extends EntityMapperDecorator {
        @Override
        public List<XmlElement> getXmlElements() {
            List<XmlElement> res = new ArrayList<>(1);
            res.add(new CommentTemplate("TestElementsCreator"));
            return res;
        }
    }

    private void setDocumentToSave() {
        ArgumentCaptor<Document> captor = ArgumentCaptor.forClass(Document.class);
        verify(sys, atLeast(1)).saveXMLFile(captor.capture(),
                endsWith(TestInterface.class.getCanonicalName().replaceAll("\\.", "/") + ".xml"));
        Document docToSave = captor.getValue();
        root = docToSave.getRootElement();
    }

}
