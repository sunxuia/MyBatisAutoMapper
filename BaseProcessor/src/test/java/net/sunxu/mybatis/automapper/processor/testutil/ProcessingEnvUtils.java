package net.sunxu.mybatis.automapper.processor.testutil;

import com.google.common.collect.ImmutableSet;
import org.apache.ibatis.type.Alias;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import javax.tools.*;
import java.io.File;
import java.util.Arrays;
import java.util.Set;
import java.util.function.BiConsumer;


public final class ProcessingEnvUtils {
    private ProcessingEnvUtils() {}

    public static void getProcessingEnvironment(BiConsumer<ProcessingEnvironment, RoundEnvironment> visitor) {
        String dirPath = ProcessingEnvUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        String fileName = dirPath + "net/sunxu/mybatis/automapper/processor/test/ClassForAPTUse.java";
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnosticCollector = new DiagnosticCollector<>();
        StandardJavaFileManager standardJavaFileManager = compiler
                .getStandardFileManager(diagnosticCollector, null, null);
        Iterable<? extends JavaFileObject> javaFiles = standardJavaFileManager
                .getJavaFileObjectsFromFiles(Arrays.asList(new File(fileName)));
        JavaCompiler.CompilationTask task = compiler.getTask(null, standardJavaFileManager,
                diagnosticCollector, null, null, javaFiles);

        AbstractProcessor annotationProcessor = new AbstractProcessor() {
            @Override
            public Set<String> getSupportedAnnotationTypes() {
                return ImmutableSet.of(Alias.class.getCanonicalName());
            }

            @Override
            public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
                visitor.accept(processingEnv, roundEnv);
                throw new RuntimeException("stop processing");
            }
        };
        task.setProcessors(Arrays.asList(annotationProcessor));
        try {
            Boolean res = task.call();
        } catch (RuntimeException err) {
            if (err.getCause() != null &&
                    "stop processing".equals(err.getCause().getMessage())) {
                return;
            }
            throw err;
        }
    }
}
