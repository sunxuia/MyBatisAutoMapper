package net.sunxu.mybatis.automapper.processor.property.parameter;

import com.google.inject.Inject;
import net.sunxu.mybatis.automapper.mapper.ParamSetting;
import net.sunxu.mybatis.automapper.processor.environment.Configuration;
import net.sunxu.mybatis.automapper.processor.property.Parameter;
import net.sunxu.mybatis.automapper.processor.property.PropertyForAnnotation;
import net.sunxu.mybatis.automapper.processor.util.HelpUtils;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.type.JdbcType;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;

import static com.google.common.base.Strings.isNullOrEmpty;

@PropertyForAnnotation(value = ParamSetting.class)
public class ParamSettingProperty extends AbstractParameterProperty {
    @Inject
    private ParamSetting setting;
    @Inject
    private Configuration configuration;
    @Inject
    private Parameter parameter;

    private String javaType, inputJavaType;
    private String typeHandler;
    private JdbcType jdbcType, inputJdbcType;
    private ParameterMode parameterMode, inputParameterMode;

    @Override
    public void initial() {
        AnnotationMirror mirror = getAnnotationMirror(ParamSetting.class);
        inputJavaType = setting == null ? "" : HelpUtils.getAnnotationTypeValue(mirror, "javaType");
        javaType = inputJavaType.isEmpty() ? parameter.getSimpleType() : inputJavaType;

        typeHandler = HelpUtils.getAnnotationTypeValue(mirror, "typeHandler");

        inputJdbcType = setting == null ? null : HelpUtils.visitAnnotationValue(mirror,
                "jdbcType",
                () -> null,
                new HelpUtils.AnnotationVisitor<JdbcType>() {
                    @Override
                    public JdbcType visitEnumConstant(VariableElement c, ExecutableElement method) {
                        return JdbcType.valueOf(c.getSimpleName().toString());
                    }
                });
        jdbcType = inputJdbcType == null ? configuration.getJdbcTypeByJavaType(javaType) : inputJdbcType;

        inputParameterMode = setting == null ? null : HelpUtils.visitAnnotationValue(mirror,
                "mode",
                () -> null,
                new HelpUtils.AnnotationVisitor<ParameterMode>() {
                    @Override
                    public ParameterMode visitEnumConstant(VariableElement c, ExecutableElement method) {
                        return ParameterMode.valueOf(c.getSimpleName().toString());
                    }
                });
        parameterMode = inputParameterMode == null ? ParameterMode.IN : inputParameterMode;
    }

    public boolean hasJavaType() {
        return !inputJavaType.isEmpty();
    }

    public String getJavaType() {
        return javaType;
    }

    public boolean hasTypeHandler() {
        return !isNullOrEmpty(typeHandler);
    }

    public String getTypeHandler() {
        return typeHandler;
    }

    public JdbcType getJdbcType() {
        return jdbcType;
    }

    public boolean hasJdbcType() {
        return inputJdbcType != null;
    }

    public ParameterMode getParameterMode() {
        return parameterMode;
    }

    public boolean hasParameterMode() {
        return inputParameterMode != null;
    }
}
