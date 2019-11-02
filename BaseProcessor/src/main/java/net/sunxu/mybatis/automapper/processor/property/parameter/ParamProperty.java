package net.sunxu.mybatis.automapper.processor.property.parameter;

import com.google.inject.Inject;
import net.sunxu.mybatis.automapper.processor.property.PropertyForAnnotation;
import org.apache.ibatis.annotations.Param;


@PropertyForAnnotation(Param.class)
public class ParamProperty extends AbstractParameterProperty {
    @Inject
    private Param param;

    public String paramName() {
        return param.value();
    }
}
