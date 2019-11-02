package net.sunxu.mybatis.automapper.processor.mapper.mapper;

import com.google.common.collect.ImmutableSet;
import com.google.inject.*;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.util.Providers;
import net.sunxu.mybatis.automapper.mapper.AutoMapper;
import net.sunxu.mybatis.automapper.processor.environment.Configuration;
import net.sunxu.mybatis.automapper.processor.environment.EnvironmentHelper;
import net.sunxu.mybatis.automapper.processor.environment.EnvironmentModule;
import net.sunxu.mybatis.automapper.processor.environment.SystemHelper;
import net.sunxu.mybatis.automapper.processor.mapper.MapperElementsCreator;
import net.sunxu.mybatis.automapper.processor.mapper.xml.XmlElement;
import net.sunxu.mybatis.automapper.processor.mapper.xml.XmlMapperElement;
import net.sunxu.mybatis.automapper.processor.mapper.xml.XmlNodable;
import net.sunxu.mybatis.automapper.processor.testutil.TestEnvRunner;
import net.sunxu.mybatis.automapper.processor.testutil.TestHelpUtils;
import org.dom4j.*;
import org.javatuples.LabelValue;
import org.junit.ComparisonFailure;
import org.junit.runner.RunWith;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.sunxu.mybatis.automapper.processor.testutil.TestException.newException;
import static net.sunxu.mybatis.automapper.processor.testutil.TestUtils.assertMapEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;

@RunWith(TestEnvRunner.class)
public abstract class TestForDecorateHandler<H extends AbstractDecorateHandler> {
    private final Class<H> handlerClass;
    private final Class<?> decoratorClass;
    private final TestDecoratorDirector decoratorDirector;

    public TestForDecorateHandler() {
        handlerClass = (Class<H>) TestHelpUtils
                .getPassedTargetClass(getClass(), TestForDecorateHandler.class).get(0);
        decoratorClass = TestHelpUtils.getPassedTargetClass(handlerClass, AbstractDecorateHandler.class).get(0);
        decoratorDirector = new TestDecoratorDirector();
    }

    protected Injector injector;

    @Inject
    private EnvironmentHelper environmentHelper;


    private class TestDecoratorDirector implements DecorateDirector {
        @Inject
        private H handler;

        @Override
        public MapperElementsCreator decorate(MapperElementsCreator provider, MapperModel mapperModel) {
            return handler.decorate(provider, mapperModel);
        }
    }

    @Inject
    private void setInjector(EnvironmentModule.EnvironmentInjector envInjector) {
        injector = envInjector.createChildInjector(binder -> {
            binder.bind(DecorateDirector.class).toProvider(Providers.of(decoratorDirector));
            binder.install(new FactoryModuleBuilder()
                    .build(TypeLiteral.get(new ParameterizedType() {
                        @Override
                        public Type[] getActualTypeArguments() {
                            return new Type[]{decoratorClass};
                        }

                        @Override
                        public Type getRawType() {
                            return DecoratorFactory.class;
                        }

                        @Override
                        public Type getOwnerType() {
                            return null;
                        }
                    })));
            binder.bind(new TypeLiteral<Map<String, MapperElementsCreator>>() {})
                    .toProvider(MapperElementsCreatorsProvider.class)
                    .in(Singleton.class);
        });
        decoratorDirector.handler = injector.getInstance(handlerClass);
    }

    protected final List<XmlElement> makeElements(Class<?> clazz) {
        Map<String, MapperElementsCreator> providerMap = getCreator(clazz);

        MapperElementsCreator provider = providerMap.get(clazz.getCanonicalName());
        if (provider == null) {
            fail("cannot find mapper elements provider for " + clazz.getCanonicalName());
        }
        return provider.getElements();
    }

    protected final String annoymousMapperName = "test.AnnoymousMapper";

    @Inject
    private Configuration configuration;
    @Inject
    private SystemHelper systemHelper;
    @Inject
    private EnvironmentHelper env;

    protected final Map<String, MapperElementsCreator> getCreator(Class<?> clazz) {
        doReturn(ImmutableSet.of(environmentHelper.getTypeElement(clazz.getCanonicalName())))
                .when(environmentHelper)
                .getElementsAnnotatedWith(AutoMapper.class);
        doReturn(annoymousMapperName).when(configuration).getDefaultAnnoymousMapper();
        doNothing().when(systemHelper).saveXMLFile(any(), any());
        doNothing().when(env).saveJavaFile(any());

        Map<String, MapperElementsCreator> providerMap =
                injector.getInstance(new Key<Map<String, MapperElementsCreator>>() {});
        return providerMap;
    }

    protected final <N extends Node> N toNode(XmlElement xmlElement) {
        if (xmlElement instanceof XmlNodable) {
            Node node = ((XmlNodable) xmlElement).toNode();
            return (N) node;
        } else if (xmlElement instanceof XmlMapperElement) {
            XmlMapperElement mapperElement = (XmlMapperElement) xmlElement;
            Element element = DocumentHelper.createElement(mapperElement.getElementName());
            for (LabelValue<String, String> attr : mapperElement.getAttributes()) {
                if (attr.getValue() != null) {
                    element.addAttribute(attr.getLabel(), attr.getValue());
                }
            }
            if (mapperElement.getChildren() != null) {
                for (XmlElement child : mapperElement.getChildren()) {
                    Node node = toNode(child);
                    element.add(node);
                }
            }
            return (N) element;
        } else {
            throw newException("XmlElement not support.");
        }
    }

    protected final void assertTextEqualsIgnoreSpace(String message, String expected, String actual) {
        int expectedIndex = 0, actualIndex = 0;
        while (expectedIndex < expected.length() && actualIndex < actual.length()) {
            if (isSpace(expected, expectedIndex)) {
                expectedIndex++;
            } else if (isSpace(actual, actualIndex)) {
                actualIndex++;
            } else if (expected.charAt(expectedIndex) != actual.charAt(actualIndex) &&
                    Character.toUpperCase(expected.charAt(expectedIndex)) !=
                            Character.toUpperCase(actual.charAt(actualIndex))) {
                throw new ComparisonFailure(String.format("%s text not equals at index[%d][%d]",
                        message, expectedIndex, actualIndex), expected, actual);
            } else {
                expectedIndex++;
                actualIndex++;
            }
        }
        while (expectedIndex < expected.length()) {
            if (!isSpace(expected, expectedIndex++)) {
                throw new ComparisonFailure(message + " text not equals", expected, actual);
            }
        }
        while (actualIndex < actual.length()) {
            if (!isSpace(actual, actualIndex++)) {
                throw new ComparisonFailure(message + " text not equals", expected, actual);
            }
        }
    }

    private boolean isSpace(String str, int index) {
        char c = str.charAt(index);
        return c == ' ' || c == '\r' || c == '\t' || c == '\n';
    }

    protected final void assertCommentEquals(XmlElement xmlElement, String expect) {
        Node node = toNode(xmlElement);
        if (!(node instanceof Comment)) {
            fail("Xml Element is not a Comment");
        }
        String actual = node.getText();
        assertTextEqualsIgnoreSpace("comment not equal", expect, actual);
    }

    protected final void assertElementEquals(XmlElement xmlElement, Node expected) {
        Node actual = toNode(xmlElement);
        assertElementEquals("", 0, actual, expected);
    }

    private final void assertElementEquals(String elementName, int indexInParent, Node actual, Node expected) {
        elementName += "/[" + indexInParent + "] " + expected.getName();
        assertTextEqualsIgnoreSpace(elementName, expected.getText(), actual.getText());
        if (!actual.getClass().isAssignableFrom(expected.getClass())) {
            fail(String.format("[%s] type not equals actual[%s] expected[%s]", elementName,
                    actual.getClass(), expected.getClass()));
        }
        if (expected instanceof Element) {
            Element actualEle = (Element) actual;
            Element expectEle = (Element) expected;
            if (!actualEle.getName().equals(expectEle.getName())) {
                fail(String.format("%s element name not equal expect [%s] actual [%s]",
                        elementName, expectEle.getName(), actualEle.getName()));
            }
            Map<String, String> actualAttrs = getAttributes(actualEle);
            Map<String, String> expectAttrs = getAttributes(expectEle);
            assertMapEquals(elementName + " attribute not equal. ", expectAttrs, actualAttrs);
            assertEquals(elementName + " children count not equals",
                    expectEle.content().size(), actualEle.content().size());
            for (int i = 0; i < actualEle.content().size(); i++) {
                assertElementEquals(elementName, i,
                        (Node) actualEle.content().get(i), (Node) expectEle.content().get(i));
            }
        }
    }

    private Map<String, String> getAttributes(Element element) {
        Map<String, String> res = new HashMap<>();
        for (Attribute attr : (List<Attribute>) element.attributes()) {
            res.put(attr.getName(), attr.getValue());
        }
        return res;
    }

    protected final String getPackageName(Class<?> clazz) {
        String className = clazz.getCanonicalName();
        if (!className.contains(".")) {
            return "";
        }
        return className.substring(0, className.lastIndexOf('.'));
    }
}
