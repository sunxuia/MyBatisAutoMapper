package net.sunxu.mybatis.automapper.processor.processor;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import com.google.inject.Injector;
import net.sunxu.mybatis.automapper.mapper.AutoMapper;
import net.sunxu.mybatis.automapper.processor.environment.EnvironmentModule;
import net.sunxu.mybatis.automapper.processor.mapper.AutoMapperBuilder;
import net.sunxu.mybatis.automapper.processor.util.AutoMapperException;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Set;


public abstract class AbstractAnnotationProcessor extends AbstractProcessor {
    @Override
    public final Set<String> getSupportedAnnotationTypes() {
        return ImmutableSet.of(AutoMapper.class.getCanonicalName());
    }

    @Override
    public final SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_8;
    }

    public final boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (annotations.size() == 0) {
            return false;
        }

        try {
            CustomSetting setting = getCustomSetting();
            EnvironmentModule module = new EnvironmentModule(processingEnv, roundEnv, setting);
            module.setProcessorName(setting.getProcessorName());

            Injector injector = Guice.createInjector(module);
            module.setEnvInjector(injector);

            AutoMapperBuilder builder = injector.getInstance(AutoMapperBuilder.class);
            builder.build(setting.getMapperBuilder());

        } catch (Exception err) {
            err = getCause(err);
            err.printStackTrace();

            StringBuilder sb = new StringBuilder("AutoMapper annotation processor failed with error : \n");
            sb.append(err.getMessage());
//            ByteArrayOutputStream memOutput = new ByteArrayOutputStream();
//            PrintStream printStream = new PrintStream(memOutput);
//            err.printStackTrace(printStream);
//            String errOutput = wrapException(() -> new String(memOutput.toByteArray(), "utf-8"));
//            sb.append('\n').append(errOutput);
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, sb.toString());
        }
        return true;
    }

    private Exception getCause(Exception err) {
        for (Throwable cause = err; cause != null; cause = cause.getCause()) {
            if (cause instanceof AutoMapperException) {
                return (Exception) cause;
            }
        }
        return err;
    }

    protected abstract CustomSetting getCustomSetting();
}
