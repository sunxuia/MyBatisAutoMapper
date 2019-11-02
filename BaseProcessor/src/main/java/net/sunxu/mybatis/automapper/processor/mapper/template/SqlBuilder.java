package net.sunxu.mybatis.automapper.processor.mapper.template;

import com.google.common.base.Strings;
import net.sunxu.mybatis.automapper.processor.property.Parameter;
import net.sunxu.mybatis.automapper.processor.property.field.ColumnField;
import net.sunxu.mybatis.automapper.processor.property.parameter.ParamProperty;
import net.sunxu.mybatis.automapper.processor.property.parameter.ParamSettingProperty;

import java.util.regex.Pattern;

import static com.google.common.base.Strings.isNullOrEmpty;
import static net.sunxu.mybatis.automapper.processor.util.AutoMapperException.newException;

public class SqlBuilder {
    private StringBuilder sb = new StringBuilder(64);
    private int indents = 0;
    public static final int INDENT_SPACE_SIZE = 2;

    public SqlBuilder() {
        addIndent();
        addIndent();
        sb.append(Strings.repeat(" ", INDENT_SPACE_SIZE));
    }

    public SqlBuilder(String sql) {
        sb.append(sql);
        addIndent();
    }

    public SqlBuilder append(String sql, Object... paras) {
        sb.append(format(sql, paras));
        return this;
    }

    private String format(String sql, Object... paras) {
        if (paras.length > 0) {
            sql = String.format(sql, paras);
        }
        return sql;
    }

    public SqlBuilder append(SqlBuilder otherBuilder) {
        String sql = otherBuilder.toString();
        sql = sql.trim();
        sb.append(sql);
        return this;
    }

    public SqlBuilder removeEnd(int length) {
        sb.delete(sb.length() - length, sb.length());
        return this;
    }

    public SqlBuilder addIncludeSql(String sqlTagName) {
        sb.append(String.format("<include refid=\"%s\" />", sqlTagName));
        return this;
    }

    public SqlBuilder newLine(int spacesCount) {
        sb.append("\n").append(getIndentSpace()).append(Strings.repeat(" ", spacesCount));
        return this;
    }

    public SqlBuilder newLine() {
        return newLine(0);
    }

    public SqlBuilder addIndent() {
        indents += INDENT_SPACE_SIZE; //2 space
        return this;
    }

    public SqlBuilder removeIndent() {
        if (indents >= INDENT_SPACE_SIZE) {
            indents -= INDENT_SPACE_SIZE;
        }
        return this;
    }

    private String getIndentSpace() {
        return Strings.repeat(" ", indents);
    }

    private boolean useAnd = false;

    public SqlBuilder where(String sql, Object... paras) {
        if (useAnd) {
            newLine().append("and ");
        } else {
            newLine().append("where ");
            useAnd = true;
        }
        sb.append(format(sql, paras));
        return this;
    }

    public SqlBuilder endWhere() {
        useAnd = false;
        return this;
    }

    public SqlBuilder trimEnd(String trimStr) {
        int sbLength = sb.length();
        int trimLength = trimStr.length();
        if (sbLength >= trimLength) {
            for (int i = 0; i < trimLength; i++) {
                if (sb.charAt(sbLength - trimLength + i) != trimStr.charAt(i)) {
                    return this;
                }
            }
            sb.delete(sbLength - trimLength, sbLength);
        }
        return this;
    }

    public SqlBuilder addSelectColumnName(ColumnField columnField) {
        if (isNullOrEmpty(columnField.outDbExpression())) {
            sb.append(columnField.columnName());
        } else {
            sb.append(columnField.outDbExpression()).append(" as ").append(columnField.columnName());
        }
        return this;
    }

    public SqlBuilder addParameter(String paramName, ColumnField columnField) {
        StringBuilder paramBuilder = new StringBuilder();
        paramBuilder.append("#{").append(paramName);
        if (columnField.hasJdbcType()) {
            paramBuilder.append(", jdbcType=").append(columnField.jdbcType());
        }
        if (columnField.useJavaType()) {
            paramBuilder.append(", javaType=").append(columnField.javaType());
        }
        if (columnField.hasTypeHandler()) {
            paramBuilder.append(", typeHandler=").append(columnField.typeHandler());
        }
        paramBuilder.append("}");

        if (!isNullOrEmpty(columnField.inDbExpression())) {
            String exp = Pattern.compile("#\\{\\s*value\\s*\\}", Pattern.CASE_INSENSITIVE)
                    .matcher(columnField.inDbExpression())
                    .replaceAll(paramBuilder.toString());
            sb.append(exp);
        } else {
            sb.append(paramBuilder);
        }
        return this;
    }

    public SqlBuilder addParameter(Parameter parameter, ColumnField columnField) {
        String paramName = String.valueOf(parameter.index());
        if (parameter.contains(ParamProperty.class)) {
            paramName = parameter.get(ParamProperty.class).paramName();
        }
        paramName = replaceFieldName(paramName, columnField.propertyName());

        if (parameter.contains(ParamSettingProperty.class)) {
            if (columnField.propertyName().contains(".")) {
                throw newException("[%s][%s] parameter [%s] is cannot have @ParamSetting",
                        parameter.getMethod().getType().getName(), parameter.getMethod().getName(), paramName);
            }
            ParamSettingProperty paramSettingProperty = parameter.get(ParamSettingProperty.class);
            sb.append("#{").append(paramName);
            if (paramSettingProperty.hasJdbcType()) {
                sb.append(", jdbcType=").append(paramSettingProperty.getJdbcType());
            } else if (columnField.useJavaType()) {
                sb.append(", jdbcType=").append(columnField.jdbcType());
            }
            if (paramSettingProperty.hasJavaType()) {
                sb.append(", javaType=").append(paramSettingProperty.getJavaType());
            } else if (columnField.useJavaType()) {
                sb.append(", javaType=").append(columnField.javaType());
            }
            if (paramSettingProperty.hasTypeHandler()) {
                sb.append(", typeHandler=").append(paramSettingProperty.getJavaType());
            } else if (columnField.hasTypeHandler()) {
                sb.append(", typeHandler=").append(columnField.typeHandler());
            }
            sb.append("}");
        } else {
            addParameter(paramName, columnField);
        }
        return this;
    }

    private String replaceFieldName(String paramName, String propertyName) {
        int idx = propertyName.indexOf('.');
        if (idx == -1) {
            return paramName;
        } else {
            return paramName + propertyName.substring(idx, propertyName.length());
        }
    }

    @Override
    public String toString() {
        String sql = sb.toString();
        return sql;
    }
}
