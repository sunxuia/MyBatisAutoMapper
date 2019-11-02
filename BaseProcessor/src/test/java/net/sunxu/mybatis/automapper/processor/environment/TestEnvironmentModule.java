package net.sunxu.mybatis.automapper.processor.environment;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import net.sunxu.mybatis.automapper.processor.mapper.MapperElementsCreator;
import net.sunxu.mybatis.automapper.processor.processor.CustomSetting;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import java.util.Collections;
import java.util.Set;

import static org.mockito.Mockito.spy;


public class TestEnvironmentModule extends AbstractModule {

    private static class DefaultCustomSetting implements CustomSetting {

        @Override
        public Class<? extends Configuration> getConfiguration() {
            return DefaultConfiguration.class;
        }

        @Override
        public Set<Class<? extends MapperElementsCreator>> getMapperBuilder() {
            return Collections.emptySet();
        }

        @Override
        public String getProcessorName() {
            return "testProcessorName";
        }

    }

    public static final String TestProcessorName = "TestProcessorName";

    private static class DefaultConfiguration extends Configuration {}

    public TestEnvironmentModule(ProcessingEnvironment processingEnv,
                                 RoundEnvironment roundEnv) {
        this.processingEnv = processingEnv;
        this.roundEnv = roundEnv;
        this.provider = spy(new DefaultCustomSetting());
    }

    private final ProcessingEnvironment processingEnv;
    private final RoundEnvironment roundEnv;
    private final CustomSetting provider;

    @Override
    protected void configure() {
        //do nothing
    }

    private EnvironmentModule.EnvironmentInjector envInjector = new EnvironmentModule.EnvironmentInjector();

    @Provides
    @Singleton
    private EnvironmentModule.EnvironmentInjector getEnvInjector() {
        return envInjector;
    }

    public void setEnvInjector(Injector injector) {
        this.envInjector.envInjector = injector;
    }

    @Provides
    @Singleton
    private EnvironmentHelper getEnvironmentHelper() {
        return spy(new EnvironmentHelper(processingEnv, roundEnv));
    }

    @Provides
    @Singleton
    private Configuration getConfiguration() {
        Configuration conf = envInjector.envInjector.getInstance(provider.getConfiguration());
        return spy(conf);
    }

    @Provides
    @Singleton
    private SystemHelper getSystemHelper() {
        SystemHelper systemHelper = new SystemHelper();
        return spy(systemHelper);
    }

    @Provides
    @Singleton
    private GenericHelper createGenericHelper() {
        return spy(new GenericHelper(processingEnv));
    }

    @Provides
    @Singleton
    private MessageHelper createMessageHelper() {
        return spy(new MessageHelper(processingEnv.getMessager()));
    }
}
