package net.sunxu.mybatis.automapper.processor.property.type;

import com.google.inject.Inject;
import net.sunxu.mybatis.automapper.entity.annotation.DefaultColumnNamingRule;
import net.sunxu.mybatis.automapper.processor.environment.Configuration;
import net.sunxu.mybatis.automapper.processor.property.PropertyForAnnotation;

import javax.annotation.Nullable;

import static net.sunxu.mybatis.automapper.processor.util.AutoMapperException.newException;
import static net.sunxu.mybatis.automapper.processor.util.HelpUtils.convertToLowerCaseSplitByUnderLine;
import static net.sunxu.mybatis.automapper.processor.util.HelpUtils.convertToUpperCaseSplitByUnderLine;


@PropertyForAnnotation(value = DefaultColumnNamingRule.class, alwaysCreate = true)
public class DefaultColumnNamingRuleProperty extends AbstractTypeAnnotationProperty {
    @Inject
    private Configuration configuration;
    @Inject
    @Nullable
    private DefaultColumnNamingRule rule;

    public String getDefaultColumnName(String fieldName) {
        if (rule == null) {
            return getColumnNameByConfiguration(fieldName);
        }
        return getColumnNameByNamingRule(fieldName);
    }

    private String getColumnNameByConfiguration(String fieldName) {
        return configuration.getNameByNamingRule(fieldName, configuration.getDefaultColumnNamingRule());
    }

    private String getColumnNameByNamingRule(String fieldName) {
        switch (rule.value()) {
            case FIELD_NAME:
                return fieldName;
            case LOWER_CASE:
                return fieldName.toLowerCase();
            case LOWER_CASE_SPLIT_BY_UNDER_LINE:
                return convertToLowerCaseSplitByUnderLine(fieldName);
            case UPPER_CASE:
                return fieldName.toUpperCase();
            case UPPER_CASE_SPLIT_BY_UNDER_LINE:
                return convertToUpperCaseSplitByUnderLine(fieldName);
            default:
                throw newException("unexpected defaultColumnNamingRule");
        }
    }
}
