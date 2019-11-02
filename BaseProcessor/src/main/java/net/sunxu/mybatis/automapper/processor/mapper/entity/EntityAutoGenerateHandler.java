package net.sunxu.mybatis.automapper.processor.mapper.entity;

import com.google.inject.Inject;
import net.sunxu.mybatis.automapper.entity.annotation.AutoGenerate.Kind;
import net.sunxu.mybatis.automapper.processor.environment.MessageHelper;
import net.sunxu.mybatis.automapper.processor.property.Field;
import net.sunxu.mybatis.automapper.processor.property.field.AutoGenerateProperty;

import java.util.*;

class EntityAutoGenerateHandler extends AbstractBuildHandler {
    @Inject
    private MessageHelper messageHelper;

    private Set<String> initialedEntityNames = new HashSet<>();

    @Override
    protected void build() {
        if (initialedEntityNames.add(builder.getName())) {
            Map<Kind, List<Field>> autoFields = getFieldsWithAutoGenerate();
            builder.getAutoGenerateSelectKeys().putAll(getSelectKeys(autoFields));
            builder.getAutoGenerateSqlEmbedded().putAll(getSQLEmbedded(autoFields));
        }
    }

    private Map<Kind, List<Field>> getFieldsWithAutoGenerate() {
        Map<Kind, List<Field>> autoFields = new HashMap<>();
        for (Field field : type.getFields()) {
            if (field.contains(AutoGenerateProperty.class)) {
                AutoGenerateProperty autoGenerate = field.get(AutoGenerateProperty.class);
                for (Kind type : autoGenerate.getSupportTypes()) {
                    if (!autoFields.containsKey(type)) {
                        autoFields.put(type, new ArrayList<>(1));
                    }
                    autoFields.get(type).add(field);
                }
            }
        }
        return autoFields;
    }

    private Map<Kind, EntityAutoGenerateSelectKey> getSelectKeys(Map<Kind, List<Field>> autoFields) {
        if (autoFields.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<Kind, EntityAutoGenerateSelectKey> selectKeys = new HashMap<>(4);
        autoFields.forEach((kind, fields) -> {
            if (!(kind == Kind.INSERT || kind == Kind.UPDATE)) {
                selectKeys.put(kind,
                        new EntityAutoGenerateSelectKey
                                (messageHelper, builder.getName(), kind, fields,
                                        builder.getFieldPropertyNames(), builder.getColumnFields()));
            }
        });
        return selectKeys;
    }

    private Map<Kind, EntityAutoGenerateSqlEmbedded> getSQLEmbedded(Map<Kind, List<Field>> autoFields) {
        if (autoFields.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<Kind, EntityAutoGenerateSqlEmbedded> sqlEmbedded = new HashMap<>(2);
        autoFields.forEach((kind, fields) -> {
            if (kind == Kind.INSERT || kind == Kind.UPDATE) {
                sqlEmbedded.put(kind,
                        new EntityAutoGenerateSqlEmbedded(messageHelper, builder.getName(), kind, fields));
            }
        });
        return sqlEmbedded;
    }
}
