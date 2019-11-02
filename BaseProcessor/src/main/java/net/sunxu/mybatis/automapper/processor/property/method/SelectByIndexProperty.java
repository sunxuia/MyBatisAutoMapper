package net.sunxu.mybatis.automapper.processor.property.method;

import com.google.inject.Inject;
import net.sunxu.mybatis.automapper.mapper.annotation.entity.SelectByIndex;
import net.sunxu.mybatis.automapper.processor.property.PropertyForAnnotation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Strings.isNullOrEmpty;

@PropertyForAnnotation(SelectByIndex.class)
public class SelectByIndexProperty extends AbstractMethodAnnotationProperty {
    @Inject
    private SelectByIndex index;

    public String indexName() {
        return index.value();
    }

    public String withLockExpression() {
        return index.withLock().trim();
    }

    public boolean hasLock() {
        return !isNullOrEmpty(withLockExpression());
    }

    String orderByExpression = null;

    public String orderByExpression() {
        if (orderByExpression == null) {
            String orderBy = index.orderBy();
            Pattern pattern = Pattern.compile("^\\s*order\\s+by\\s+(.+)$", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(orderBy);
            if (matcher.find()) {
                orderByExpression = matcher.group(1).trim();
            } else {
                orderByExpression = orderBy.trim();
            }
        }
        return orderByExpression;
    }

    public boolean hasOrderBy() {
        return !isNullOrEmpty(orderByExpression());
    }
}
