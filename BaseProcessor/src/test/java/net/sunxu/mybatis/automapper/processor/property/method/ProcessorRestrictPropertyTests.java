package net.sunxu.mybatis.automapper.processor.property.method;

import net.sunxu.mybatis.automapper.mapper.ProcessorRestrict;
import net.sunxu.mybatis.automapper.processor.environment.EnvironmentModule;
import net.sunxu.mybatis.automapper.processor.property.TestForAnnotationProperty;
import org.junit.Test;
import org.mockito.Mock;

import javax.lang.model.element.ExecutableElement;

import static net.sunxu.mybatis.automapper.processor.environment.TestEnvironmentModule.TestProcessorName;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;

public class ProcessorRestrictPropertyTests extends TestForAnnotationProperty<ProcessorRestrictProperty> {
    @Mock
    private ProcessorRestrict restrict;

    private String processorName = TestProcessorName;

    @Mock
    private ExecutableElement methodElement;

    @Test
    public void isAllowed_sameValue_true() {
        doReturn(TestProcessorName).when(restrict).value();
        initialProperty();

        boolean res = property.isAllowed();

        assertTrue(res);
    }

    private void initialProperty() {
        initialProperty(methodElement, binder -> {
            binder.bind(ProcessorRestrict.class).toInstance(restrict);
            binder.bind(String.class).annotatedWith(EnvironmentModule.ProcessorName.class)
                    .toInstance(processorName);
            binder.bind(ExecutableElement.class).toInstance(methodElement);
        });
    }

    @Test
    public void isAllowed_differentValue_false() {
        doReturn("otherProcessorName").when(restrict).value();
        initialProperty();

        boolean res = property.isAllowed();

        assertFalse(res);
    }
}
