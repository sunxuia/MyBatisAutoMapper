package net.sunxu.mybatis.automapper.processor.demo;

import org.apache.ibatis.type.JdbcType;
import org.junit.Test;

import java.sql.*;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.spy;

public class OracleConfigurationTests {
    @Test
    public void getJdbcTypeByJavaType_allValidType_fullTest() {
        OracleConfiguration conf = spy(OracleConfiguration.class);
        assertJdbcValid(conf, "byte", JdbcType.NUMERIC);
        assertJdbcValid(conf, Byte.class.getCanonicalName(), JdbcType.NUMERIC);
        assertJdbcValid(conf, "int", JdbcType.NUMERIC);
        assertJdbcValid(conf, Integer.class.getCanonicalName(), JdbcType.NUMERIC);
        assertJdbcValid(conf, "float", JdbcType.NUMERIC);
        assertJdbcValid(conf, Float.class.getCanonicalName(), JdbcType.NUMERIC);
        assertJdbcValid(conf, "double", JdbcType.NUMERIC);
        assertJdbcValid(conf, Double.class.getCanonicalName(), JdbcType.NUMERIC);
        assertJdbcValid(conf, "char", JdbcType.CHAR);
        assertJdbcValid(conf, Character.class.getCanonicalName(), JdbcType.CHAR);

        assertJdbcValid(conf, String.class.getCanonicalName(), JdbcType.VARCHAR);
        assertJdbcValid(conf, Date.class.getCanonicalName(), JdbcType.TIME);
        assertJdbcValid(conf, java.sql.Date.class.getCanonicalName(), JdbcType.TIME);

        assertJdbcValid(conf, Blob.class.getCanonicalName(), JdbcType.BLOB);
        assertJdbcValid(conf, Clob.class.getCanonicalName(), JdbcType.CLOB);
        assertJdbcValid(conf, Array.class.getCanonicalName(), JdbcType.ARRAY);
        assertJdbcValid(conf, Time.class.getCanonicalName(), JdbcType.TIME);
        assertJdbcValid(conf, Timestamp.class.getCanonicalName(), JdbcType.TIMESTAMP);

        assertJdbcValid(conf, Object.class.getCanonicalName(), JdbcType.UNDEFINED);
    }

    private void assertJdbcValid(OracleConfiguration configuration, String javaType, JdbcType expect) {
        JdbcType actual = configuration.getJdbcTypeByJavaType(javaType);
        assertEquals(javaType + " fail", expect, actual);
    }
}
