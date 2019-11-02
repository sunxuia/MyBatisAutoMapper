package net.sunxu.mybatis.automapper.processor.property.field;

import com.google.inject.Inject;
import net.sunxu.mybatis.automapper.entity.annotation.AutoGenerate;
import net.sunxu.mybatis.automapper.entity.annotation.Cascade;
import net.sunxu.mybatis.automapper.entity.annotation.Index;
import net.sunxu.mybatis.automapper.entity.annotation.PrimaryKey;
import net.sunxu.mybatis.automapper.processor.property.Field;
import net.sunxu.mybatis.automapper.processor.property.PropertyForAnnotation;
import net.sunxu.mybatis.automapper.processor.util.HelpUtils;
import org.apache.ibatis.mapping.FetchType;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ExecutableElement;
import java.util.List;

import static net.sunxu.mybatis.automapper.processor.util.HelpUtils.getAnnotationTypeValue;
import static net.sunxu.mybatis.automapper.processor.util.HelpUtils.visitAnnotationValue;

@PropertyForAnnotation(Cascade.class)
public class CascadeProperty extends AbstractFieldAnnotationProperty implements Fetchable {
    @Inject
    private Cascade cascade;
    @Inject
    private Field field;

    private String referToEntityName;

    private String byMapper;

    private boolean many;

    @Override
    protected void initial() {
        AnnotationMirror mirror = getAnnotationMirror(Cascade.class);
        referToEntityName = getAnnotationTypeValue(mirror, "referEntity", () -> {
            if (List.class.getName().equals(field.getSimpleType())) {
                List<String> genericArguments = field.getGenericArgumentTypes();
                String type = genericArguments.isEmpty() ? Object.class.getCanonicalName() : genericArguments.get(0);
                if (type.contains("<")) {
                    type = type.substring(0, type.indexOf("<"));
                }
                return type;
            } else if (field.isArray()) {
                String componentType = field.getComponentType();
                if (componentType.contains("<")) {
                    componentType = componentType.substring(0, componentType.indexOf("<"));
                }
                return componentType;
            } else {
                return field.getSimpleType();
            }
        });
        byMapper = getAnnotationTypeValue(mirror, "byMapper", () -> "");

        validate(getAnnotations(Index.class).length == 0 &&
                        getAnnotation(PrimaryKey.class) == null &&
                        getAnnotations(AutoGenerate.class).length == 0,
                "field with @Cascade should not have attribute @Index/@Indexes/@AutoGenerate/@PrimaryKey.");

        many = visitAnnotationValue(mirror,
                "many",
                () -> List.class.getName().equals(field.getSimpleType()) || field.isArray(),
                new HelpUtils.AnnotationVisitor<Boolean>() {
                    @Override
                    public Boolean visitBoolean(boolean b, ExecutableElement method) {
                        return b;
                    }
                });
    }

    public String localIndex() {
        return cascade.localIndex();
    }

    @Override
    public FetchType fetchType() {
        return cascade.fetchType();
    }

    @Override
    public String byMapper() {
        return byMapper;
    }

    @Override
    public String referEntity() {
        return referToEntityName;
    }

    @Override
    public String referIndex() {
        return cascade.value();
    }

    public String fieldName() {
        return field.getName();
    }

    public String javaType() {
        return field.getSimpleType();
    }

    public boolean isMany() {
        return many;
    }
}
