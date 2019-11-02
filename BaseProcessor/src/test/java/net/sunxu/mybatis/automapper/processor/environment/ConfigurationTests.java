package net.sunxu.mybatis.automapper.processor.environment;

import net.sunxu.mybatis.automapper.processor.environment.Configuration.NamingRule;
import org.apache.ibatis.type.JdbcType;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


public class ConfigurationTests {

    @Mock
    private SystemHelper sys;

    private Configuration configuration;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        configuration = new Configuration() {};
    }

    @Test
    public void getMybatisConfiguration_noArg_getDefault() {

        String res = configuration.getMybatisConfigurationPath();

        assertNull(res);
    }

    @Test
    public void getDefaultSchemaNamingRule_noArg_getDefault() {

        NamingRule res = configuration.getDefaultSchemaNamingRule();

        assertEquals(NamingRule.DEFAULT, res);
    }

    @Test
    public void getDefaultColumnNamingRule_noArg_getDefault() {

        NamingRule res = configuration.getDefaultColumnNamingRule();

        assertEquals(NamingRule.DEFAULT, res);
    }

    @Test
    public void getDefaultEntityAnnoymousEntityMapper_noArg_getValue() {

        String res = configuration.getDefaultAnnoymousMapper();

        assertNull(res);
    }

    @Test
    public void getNameByNamingRule_nameWithLowerCase_lowerCase() {

        String res = configuration.getNameByNamingRule("testName", NamingRule.LOWER_CASE);

        assertEquals("testname", res);
    }

    @Test
    public void getNameByNamingRule_nameWithLowerCaseSplitByUnderLine_lowerCaseSplitByUnderLine() {

        String res = configuration.getNameByNamingRule("testName", NamingRule.LOWER_CASE);

        assertEquals("testname", res);
    }

    @Test
    public void getNameByNamingRule_nameWithUpperCase_upperCase() {

        String res = configuration.getNameByNamingRule("testName", NamingRule.UPPER_CASE);

        assertEquals("TESTNAME", res);
    }

    @Test
    public void getNameByNamingRule_nameWithUpperCaseSplitByUnderLine_upperCaseSplitByUnderLine() {

        String res = configuration.getNameByNamingRule("testName", NamingRule.UPPER_CASE_SPLIT_BY_UNDER_LINE);

        assertEquals("TEST_NAME", res);
    }

    @Test
    public void getNameByNamingRule_nameWithDefault_inputName() {

        String res = configuration.getNameByNamingRule("testName", NamingRule.DEFAULT);

        assertEquals("testName", res);
    }

    @Test
    public void getDefaultJdbcType_default_getUndefined() {
        JdbcType res = configuration.getJdbcTypeByJavaType("java.util.List");

        assertEquals(JdbcType.UNDEFINED, res);
    }

}
