package net.sunxu.mybatis.automapper.processor.mapper.entity;

import net.sunxu.mybatis.automapper.entity.annotation.AutoGenerate;
import net.sunxu.mybatis.automapper.processor.property.field.*;

import java.util.Collections;
import java.util.Map;

public class EntityModel {
    private final String entityName;
    private String tableName;
    private String alias;

    private Map<String, EntityIndex> entityIndexes;
    private Map<AutoGenerate.Kind, EntityAutoGenerateSelectKey> entityAutoGenerateSelectKeys;
    private Map<AutoGenerate.Kind, EntityAutoGenerateSqlEmbedded> entityAutoGenerateSqlEmbeddeds;

    private Map<String, String> columnNamePropertyNameMap;
    private Map<String, ColumnField> properties;

    private Map<String, ColumnProperty> columns;
    private Map<String, CompositesProperty> composites;
    private Map<String, CascadeProperty> cascades;
    private Map<String, ReferenceProperty> references;
    private Map<String, String> fields;

    public EntityModel(String entityName) {
        this.entityName = entityName;
    }

    public String getName() {
        return entityName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getAlias() {
        return alias;
    }

    void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Map<String, EntityIndex> getEntityIndexes() {
        return entityIndexes;
    }

    void setEntityIndexes(Map<String, EntityIndex> entityIndexes) {
        this.entityIndexes = Collections.unmodifiableMap(entityIndexes);
    }

    public Map<AutoGenerate.Kind, EntityAutoGenerateSelectKey> getEntityAutoGenerateSelectKeys() {
        return entityAutoGenerateSelectKeys;
    }

    void setEntityAutoGenerateSelectKeys(
            Map<AutoGenerate.Kind, EntityAutoGenerateSelectKey> entityAutoGenerateSelectKeys) {
        this.entityAutoGenerateSelectKeys = Collections.unmodifiableMap(entityAutoGenerateSelectKeys);
    }

    public Map<AutoGenerate.Kind, EntityAutoGenerateSqlEmbedded> getEntityAutoGenerateSqlEmbeddeds() {
        return entityAutoGenerateSqlEmbeddeds;
    }

    void setEntityAutoGenerateSqlEmbeddeds(
            Map<AutoGenerate.Kind, EntityAutoGenerateSqlEmbedded> entityAutoGenerateSqlEmbeddeds) {
        this.entityAutoGenerateSqlEmbeddeds = Collections.unmodifiableMap(entityAutoGenerateSqlEmbeddeds);
    }

    public Map<String, String> getColumnNamePropertyNameMap() {
        return columnNamePropertyNameMap;
    }

    void setColumnNamePropertyNameMap(Map<String, String> columnNamePropertyNameMap) {
        this.columnNamePropertyNameMap = Collections.unmodifiableMap(columnNamePropertyNameMap);
    }

    public Map<String, ColumnField> getColumnFields() {
        return properties;
    }

    void setProperties(Map<String, ColumnField> properties) {
        this.properties = Collections.unmodifiableMap(properties);
    }

    public Map<String, ColumnProperty> getColumns() {
        return columns;
    }

    void setColumns(Map<String, ColumnProperty> columns) {
        this.columns = Collections.unmodifiableMap(columns);
    }

    public Map<String, CompositesProperty> getComposites() {
        return composites;
    }

    void setComposites(Map<String, CompositesProperty> composites) {
        this.composites = Collections.unmodifiableMap(composites);
    }

    public Map<String, CascadeProperty> getCascades() {
        return cascades;
    }

    void setCascades(Map<String, CascadeProperty> cascades) {
        this.cascades = Collections.unmodifiableMap(cascades);
    }

    public Map<String, ReferenceProperty> getReferences() {
        return references;
    }

    void setReferences(Map<String, ReferenceProperty> references) {
        this.references = Collections.unmodifiableMap(references);
    }

    void setFields(Map<String, String> fields) {
        this.fields = Collections.unmodifiableMap(fields);
    }

    public Map<String, String> getFields() {
        return fields;
    }

    @Override
    public String toString() {
        return entityName;
    }
}
