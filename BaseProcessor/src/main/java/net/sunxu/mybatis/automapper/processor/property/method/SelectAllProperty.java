package net.sunxu.mybatis.automapper.processor.property.method;

import com.google.inject.Inject;
import net.sunxu.mybatis.automapper.mapper.annotation.entity.SelectAll;
import net.sunxu.mybatis.automapper.processor.property.PropertyForAnnotation;

import static com.google.common.base.Strings.isNullOrEmpty;

@PropertyForAnnotation(SelectAll.class)
public class SelectAllProperty extends AbstractMethodAnnotationProperty {
    @Inject
    private SelectAll selectAll;

    public String orderBy() {
        return selectAll.orderBy().trim();
    }

    public boolean hasOrderBy() {
        return !isNullOrEmpty(orderBy());
    }
}
