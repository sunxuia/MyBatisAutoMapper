package net.sunxu.mybatis.automapper.processor.property.field;

import org.apache.ibatis.mapping.FetchType;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;

public class FetchableTests {
    @Mock(answer = Answers.CALLS_REAL_METHODS)
    private TestClass fetchable;

    private abstract static class TestClass implements Fetchable {}

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void hasFetchType_has_true() {
        doReturn(FetchType.LAZY).when(fetchable).fetchType();

        boolean res = fetchable.hasFetchType();

        assertTrue(res);
    }

    @Test
    public void hasByMapper_has_true() {
        doReturn("mapperName").when(fetchable).byMapper();

        boolean res = fetchable.hasByMapper();

        assertTrue(res);
    }
}
