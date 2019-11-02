package net.sunxu.mybatis.automapper.processor.environment;

import com.google.common.base.Strings;
import org.apache.ibatis.type.JdbcType;

import javax.validation.constraints.NotNull;

import static net.sunxu.mybatis.automapper.processor.util.HelpUtils.convertToLowerCaseSplitByUnderLine;
import static net.sunxu.mybatis.automapper.processor.util.HelpUtils.convertToUpperCaseSplitByUnderLine;


public abstract class Configuration {
    public enum NamingRule {
        DEFAULT,
        LOWER_CASE,
        LOWER_CASE_SPLIT_BY_UNDER_LINE,
        UPPER_CASE,
        UPPER_CASE_SPLIT_BY_UNDER_LINE;
    }

    public String getMybatisConfigurationPath() {
        return null;
    }

    public String getMybatisDefaultDatabaseId() {
        return null;
    }

    public @NotNull NamingRule getDefaultSchemaNamingRule() {
        return NamingRule.DEFAULT;
    }

    public @NotNull NamingRule getDefaultColumnNamingRule() {
        return NamingRule.DEFAULT;
    }

    public String getDefaultAnnoymousMapper() {
        return null;
    }

    public String getNameByNamingRule(String name, NamingRule rule) {
        name = Strings.nullToEmpty(name);
        if (rule != null) {
            switch (rule) {
                case LOWER_CASE:
                    return name.toLowerCase();
                case LOWER_CASE_SPLIT_BY_UNDER_LINE:
                    return convertToLowerCaseSplitByUnderLine(name);
                case UPPER_CASE:
                    return name.toUpperCase();
                case UPPER_CASE_SPLIT_BY_UNDER_LINE:
                    return convertToUpperCaseSplitByUnderLine(name);
            }
        }
        return name;
    }

    public @NotNull JdbcType getJdbcTypeByJavaType(String javaType) {
        return JdbcType.UNDEFINED;
    }
}
