package net.sunxu.mybatis.automapper.processor.mapper.entity;

import net.sunxu.mybatis.automapper.entity.annotation.AutoGenerate;
import net.sunxu.mybatis.automapper.processor.property.Field;
import net.sunxu.mybatis.automapper.processor.property.Type;
import net.sunxu.mybatis.automapper.processor.property.field.*;

import java.util.*;


class EntityModelBuilder {
    private final String entityName;
    private final Map<String, EntityModel> entities;
    private Set<String> relatedEntityNames = new HashSet<>();
    private boolean isInterrupt;

    private Type type;
    private String tableName;
    private String alias;

    private Map<String, List<Field>> indexFields = new HashMap<>();
    private Map<AutoGenerate.Kind, EntityAutoGenerateSelectKey> autoGenerateSelectKeys = new HashMap<>(2);
    private Map<AutoGenerate.Kind, EntityAutoGenerateSqlEmbedded> autoGenerateSqlEmbedded = new HashMap<>(2);
    private Map<String, EntityIndex> entityIndexes = new HashMap<>();

    private Map<String, String> columnNamePropertyNameMap = new HashMap<>();
    private Map<Field, List<String>> fieldPropertyNames = new HashMap<>();
    private Map<String, ColumnField> properties = new HashMap<>();

    private Map<String, ColumnProperty> columns = new HashMap<>();
    private Map<String, CompositesProperty> composites = new HashMap<>();
    private Map<String, ReferenceProperty> references = new HashMap<>();
    private Map<String, CascadeProperty> cascades = new HashMap<>();

    EntityModelBuilder(String entityName, Map<String, EntityModel> entities) {
        this.entityName = entityName;
        this.entities = entities;
    }

    public String getName() {
        return entityName;
    }

    public EntityModel getEntityByName(String entitynName) {
        return entities.get(entitynName);
    }

    public Set<String> getRelatedEntityNames() {
        return relatedEntityNames;
    }

    public void addRelatedEntityName(String relatedEntityName) {
        relatedEntityNames.add(relatedEntityName);
    }

    public boolean isInterrupt() {
        return isInterrupt;
    }

    public void setInterrupt(boolean interrupt) {
        isInterrupt = interrupt;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

//    public String getAlias() {
//        return alias;
//    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Map<String, List<Field>> getIndexFields() {
        return indexFields;
    }

    public Map<AutoGenerate.Kind, EntityAutoGenerateSelectKey> getAutoGenerateSelectKeys() {
        return autoGenerateSelectKeys;
    }

    public Map<AutoGenerate.Kind, EntityAutoGenerateSqlEmbedded> getAutoGenerateSqlEmbedded() {
        return autoGenerateSqlEmbedded;
    }

    public Map<String, EntityIndex> getEntityIndexes() {
        return entityIndexes;
    }

    public Map<String, String> getColumnNamePropertyNameMap() {
        return columnNamePropertyNameMap;
    }

    public Map<Field, List<String>> getFieldPropertyNames() {
        return fieldPropertyNames;
    }

    public Map<String, ColumnField> getColumnFields() {
        return properties;
    }

    public Map<String, ColumnProperty> getColumns() {
        return columns;
    }

    public Map<String, CompositesProperty> getComposites() {
        return composites;
    }

    public Map<String, ReferenceProperty> getReferences() {
        return references;
    }

    public Map<String, CascadeProperty> getCascades() {
        return cascades;
    }

    @Override
    public String toString() {
        return entityName;
    }

    public EntityModel retrieveEntityModel() {
        EntityModel entityModel = new EntityModel(entityName);
        entityModel.setTableName(tableName);
        entityModel.setAlias(alias);

        entityModel.setEntityIndexes(entityIndexes);
        entityModel.setEntityAutoGenerateSelectKeys(autoGenerateSelectKeys);
        entityModel.setEntityAutoGenerateSqlEmbeddeds(autoGenerateSqlEmbedded);
        entityModel.setEntityIndexes(entityIndexes);

        entityModel.setProperties(properties);
        entityModel.setColumnNamePropertyNameMap(columnNamePropertyNameMap);

        entityModel.setColumns(columns);
        entityModel.setComposites(composites);
        entityModel.setReferences(references);
        entityModel.setCascades(cascades);
        Map<String, String> fields = new HashMap<>(
                columns.size() + composites.size() + references.size() + cascades.size());
        columns.forEach((fieldName, property) -> fields.put(fieldName, property.javaType()));
        composites.forEach((fieldName, property) -> fields.put(fieldName, property.javaType()));
        references.forEach((fieldName, property) -> fields.put(fieldName, property.javaType()));
        cascades.forEach((fieldName, property) -> fields.put(fieldName, property.javaType()));
        entityModel.setFields(fields);

        return entityModel;
    }
}
