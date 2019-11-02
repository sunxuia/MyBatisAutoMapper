package net.sunxu.mybatis.automapper.processor.property.field;

import com.google.inject.Inject;
import net.sunxu.mybatis.automapper.entity.annotation.*;
import net.sunxu.mybatis.automapper.processor.property.Field;
import net.sunxu.mybatis.automapper.processor.property.PropertyForAnnotation;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Strings.isNullOrEmpty;


@PropertyForAnnotation({Index.class, Indexes.class, PrimaryKey.class})
public class IndexProperty extends AbstractFieldAnnotationProperty {
    private final Map<String, Integer> indexes = new HashMap<>();

    @Inject
    private Field field;

    @Override
    protected void initial() {
        validate();
        for (Index index : getAnnotations(Index.class)) {
            String indexName = getIndexName(index);
            validate(!indexes.containsKey(indexName),
                    "duplicated index name of [%s]", indexName);
            indexes.put(indexName, index.order());
        }

        PrimaryKey primaryKey = getAnnotation(PrimaryKey.class);
        if (primaryKey != null) {
            indexes.put("", primaryKey.value());
        }
    }

    private void validate() {
        validate(getAnnotation(Column.class) != null ||
                        getAnnotation(Reference.class) != null ||
                        getAnnotations(Composite.class).length > 0,
                "field with @Index should have one of @Column or @Reference or @Composite(s).");
    }

    private String getIndexName(Index index) {
        if (isNullOrEmpty(index.value())) {
            return field.getName();
        }
        return index.value();
    }

    public Set<String> getIndexNames() {
        return indexes.keySet();
    }

    public int getOrder(String indexName) {
        return indexes.get(indexName);
    }
}
