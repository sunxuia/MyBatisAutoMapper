package net.sunxu.mybatis.automapper.processor.property.field;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import net.sunxu.mybatis.automapper.entity.annotation.Reference;
import net.sunxu.mybatis.automapper.entity.annotation.Reference.LocalColumn;
import net.sunxu.mybatis.automapper.processor.property.Field;
import net.sunxu.mybatis.automapper.processor.property.PropertyForAnnotation;
import net.sunxu.mybatis.automapper.processor.property.Type;
import net.sunxu.mybatis.automapper.processor.property.type.DefaultColumnNamingRuleProperty;
import org.apache.ibatis.mapping.FetchType;
import org.apache.ibatis.type.JdbcType;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static net.sunxu.mybatis.automapper.processor.util.HelpUtils.getAnnotationTypeValue;


@PropertyForAnnotation(Reference.class)
public class ReferenceProperty extends AbstractFieldAnnotationProperty implements Fetchable {
    @Inject
    private Reference reference;
    @Inject
    private Type type;
    @Inject
    private Field field;

    private List<LocalColumn> localColumns;

    private String referEntityName;

    private String byMapper;

    @Override
    protected void initial() {
        referEntityName = getAnnotationTypeValue(getAnnotationMirror(Reference.class), "referTo",
                field::getSimpleType);
        byMapper = getAnnotationTypeValue(getAnnotationMirror(Reference.class), "byMapper", () -> "");

        if (reference.localColumns().length > 0) {
            localColumns = ImmutableList.copyOf(reference.localColumns());
        } else {
            localColumns = ImmutableList.of(new LocalColumn() {
                String columnName = type.get(DefaultColumnNamingRuleProperty.class)
                        .getDefaultColumnName(field.getName());

                @Override
                public Class<? extends Annotation> annotationType() {
                    return LocalColumn.class;
                }

                @Override
                public String value() {
                    return columnName;
                }

                @Override
                public JdbcType jdbcType() {
                    return JdbcType.UNDEFINED;
                }

                @Override
                public boolean isPreferredWhenColumnNameConflict() {
                    return false;
                }

                @Override
                public boolean insertable() {
                    return true;
                }

                @Override
                public boolean updatable() {
                    return true;
                }
            });
        }
        validateLocalColumns();
    }

    private void validateLocalColumns() {
        Set<String> columnNames = new HashSet<>(localColumns.size());
        for (LocalColumn localColumn : localColumns) {
            validate(columnNames.add(localColumn.value()),
                    "duplicated column name [%s]", localColumn.value());
        }
    }

    public List<LocalColumn> getLocalColumns() {
        return localColumns;
    }

    @Override
    public FetchType fetchType() {
        return reference.fetchType();
    }

    @Override
    public String byMapper() {
        return byMapper;
    }

    @Override
    public String referEntity() {
        return referEntityName;
    }

    @Override
    public String referIndex() {
        return reference.referIndex();
    }

    public String fieldName() {
        return field.getName();
    }

    public String javaType() {
        return field.getSimpleType();
    }
}
