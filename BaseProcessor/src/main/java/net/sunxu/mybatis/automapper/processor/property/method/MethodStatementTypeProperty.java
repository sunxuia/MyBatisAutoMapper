package net.sunxu.mybatis.automapper.processor.property.method;

import com.google.inject.Inject;
import net.sunxu.mybatis.automapper.mapper.annotation.addition.MethodStatementType;
import net.sunxu.mybatis.automapper.processor.property.PropertyForAnnotation;
import org.apache.ibatis.mapping.StatementType;


@PropertyForAnnotation(MethodStatementType.class)
public class MethodStatementTypeProperty extends AbstractMethodAnnotationProperty {
    @Inject
    private MethodStatementType statementType;

    public StatementType statementType() {
        return statementType.value();
    }
}
