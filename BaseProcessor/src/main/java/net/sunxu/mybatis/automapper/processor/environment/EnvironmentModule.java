package net.sunxu.mybatis.automapper.processor.environment;

import com.google.inject.*;
import net.sunxu.mybatis.automapper.processor.processor.CustomSetting;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


public class EnvironmentModule extends AbstractModule {
    public EnvironmentModule(ProcessingEnvironment processingEnv,
                             RoundEnvironment roundEnv,
                             CustomSetting provider) {
        this.processingEnv = processingEnv;
        this.roundEnv = roundEnv;
        this.provider = provider;
    }

    private final ProcessingEnvironment processingEnv;
    private final RoundEnvironment roundEnv;
    private final CustomSetting provider;

    @Override
    protected void configure() {
        bind(String.class).annotatedWith(ProcessorName.class).toInstance(processorName);
    }


    public static class EnvironmentInjector {
        Injector envInjector;

        public Injector createChildInjector(Module... modules) {
            return envInjector.createChildInjector(modules);
        }

        private EnvironmentInjector injector;
    }

    private EnvironmentInjector envInjector = new EnvironmentInjector();

    @Provides
    @Singleton
    private EnvironmentInjector getEnvInjector() {
        return envInjector;
    }

    public void setEnvInjector(Injector injector) {
        this.envInjector.envInjector = injector;
    }

    @Provides
    @Singleton
    private EnvironmentHelper getEnvironmentHelper() {
        return new EnvironmentHelper(processingEnv, roundEnv);
    }

    @Provides
    @Singleton
    private Configuration getConfiguration() {
        Configuration conf = envInjector.envInjector.getInstance(provider.getConfiguration());
        return conf;
    }

    @Provides
    @Singleton
    private SystemHelper getSystemHelper() {
        SystemHelper systemHelper = new SystemHelper();
        return systemHelper;
    }

    @Provides
    @Singleton
    private GenericHelper createGenericHelper() {
        return new GenericHelper(processingEnv);
    }

    @Provides
    @Singleton
    private MessageHelper createMessageHelper() {
        return new MessageHelper(processingEnv.getMessager());
    }

    public void setProcessorName(String processorName) {
        this.processorName = processorName;
    }

    private String processorName;

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @BindingAnnotation
    public @interface ProcessorName {}
}
