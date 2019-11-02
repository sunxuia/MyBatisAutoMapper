package net.sunxu.mybatis.automapper.processor.property.method;

import com.google.inject.Inject;
import net.sunxu.mybatis.automapper.mapper.ProcessorRestrict;
import net.sunxu.mybatis.automapper.processor.environment.EnvironmentModule;
import net.sunxu.mybatis.automapper.processor.property.PropertyForAnnotation;

@PropertyForAnnotation(ProcessorRestrict.class)
public class ProcessorRestrictProperty extends AbstractMethodAnnotationProperty {
    @Inject
    private ProcessorRestrict restrict;
    @Inject
    @EnvironmentModule.ProcessorName
    private String processorName;

    public boolean isAllowed() {
        return processorName.equals(restrict.value());
    }
}
