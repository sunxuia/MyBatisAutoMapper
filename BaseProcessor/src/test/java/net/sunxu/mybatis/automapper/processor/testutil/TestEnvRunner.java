package net.sunxu.mybatis.automapper.processor.testutil;

import com.google.common.collect.ImmutableList;
import com.google.inject.Guice;
import com.google.inject.Injector;
import net.sunxu.mybatis.automapper.processor.environment.TestEnvironmentModule;
import org.junit.internal.runners.statements.RunAfters;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.mockito.MockitoAnnotations;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;

import static net.sunxu.mybatis.automapper.processor.testutil.TestException.wrapException;

public class TestEnvRunner extends BlockJUnit4ClassRunner {

    /**
     * Creates a BlockJUnit4ClassRunner to run {@code klass}
     *
     * @param klass
     * @throws InitializationError if the test class is malformed.
     */
    public TestEnvRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }

    private static Thread environmentThread;
    private static Object lock = new Object();
    private static ProcessingEnvironment env;
    private static RoundEnvironment round;


    @Override
    protected Statement withBeforeClasses(Statement statement) {
        if (environmentThread == null) {
            environmentThread = new Thread(() -> ProcessingEnvUtils.getProcessingEnvironment((env, round) -> {
                //System.out.println("\nJdk compiler for running annotation processor started.");
                TestEnvRunner.this.env = env;
                TestEnvRunner.this.round = round;
                synchronized (lock) {
                    lock.notify();
                    wrapException(() -> lock.wait());
                }
            }));
            environmentThread.setName("annotation processor thread");
            environmentThread.start();

            synchronized (lock) {
                wrapException(() -> lock.wait());
            }
        }
        return super.withBeforeClasses(statement);
    }

    @Override
    protected Statement withAfterClasses(Statement statement) {
        return super.withAfterClasses(new RunAfters(statement, ImmutableList.of(
                new FrameworkMethod(wrapException(() -> getClass().getDeclaredMethod("closeEnvironmentThread")))
        ), this));
    }

    public void closeEnvironmentThread() {
        if (environmentThread != null) {
            synchronized (lock) {
                lock.notify();
                //System.out.println("\nJdk compiler for running annotation processor ended.");
                environmentThread = null;
            }
        }
    }

    @Override
    protected Statement methodInvoker(FrameworkMethod method, Object test) {
        initialTester(test);
        return super.methodInvoker(method, test);
    }

    public void initialTester(Object tester) {
        MockitoAnnotations.initMocks(tester);
        ProcessingEnvironment env = this.env;
        RoundEnvironment round = this.round;
        getInjector(env, round).injectMembers(tester);
    }

    private Injector getInjector(ProcessingEnvironment env, RoundEnvironment round) {
        TestEnvironmentModule module = new TestEnvironmentModule(env, round);
        Injector injector = Guice.createInjector(module);
        module.setEnvInjector(injector);
        return injector;
    }
}
