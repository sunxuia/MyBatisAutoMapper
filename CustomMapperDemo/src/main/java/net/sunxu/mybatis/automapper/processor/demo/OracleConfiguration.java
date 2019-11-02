package net.sunxu.mybatis.automapper.processor.demo;

import net.sunxu.mybatis.automapper.processor.environment.Configuration;
import org.apache.ibatis.type.JdbcType;


public class OracleConfiguration extends Configuration {
    @Override
    public JdbcType getJdbcTypeByJavaType(String javaType) {
        switch (javaType) {
            case "byte":
            case "java.lang.Byte":
            case "int":
            case "java.lang.Integer":
            case "float":
            case "java.lang.Float":
            case "double":
            case "java.lang.Double":
                return JdbcType.NUMERIC;
            case "boolean":
            case "java.lang.Boolean":
                return JdbcType.NUMERIC;
            case "char":
            case "java.lang.Character":
                return JdbcType.CHAR;
            case "java.lang.String":
                return JdbcType.VARCHAR;
            case "java.sql.Date":
            case "java.util.Date":
                return JdbcType.TIME;
            case "java.sql.Blob":
                return JdbcType.BLOB;
            case "java.sql.Clob":
                return JdbcType.CLOB;
            case "java.sql.Array":
                return JdbcType.ARRAY;
            case "java.sql.Time":
                return JdbcType.TIME;
            case "java.sql.Timestamp":
                return JdbcType.TIMESTAMP;
            default:
                return JdbcType.UNDEFINED;
        }
    }
}
