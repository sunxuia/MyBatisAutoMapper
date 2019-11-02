package net.sunxu.mybatis.automapper.processor.mapper.entity;

import net.sunxu.mybatis.automapper.processor.property.field.ColumnField;
import org.apache.ibatis.type.JdbcType;

class CustomColumnField implements ColumnField {
    private String columnName;
    private JdbcType jdbcType;
    private String outDbExpression, inDbExpression;
    private boolean insertable, updatable;
    private String propertyName, javaType, typeHandler;
    private boolean useJavaType;
    private boolean isPreferredWhenColumnNameConfilict;

    CustomColumnField(ColumnField columnField) {
        columnName = columnField.columnName();
        jdbcType = columnField.jdbcType();
        outDbExpression = columnField.outDbExpression();
        inDbExpression = columnField.inDbExpression();
        insertable = columnField.insertable();
        updatable = columnField.updatable();
        propertyName = columnField.propertyName();
        javaType = columnField.javaType();
        typeHandler = columnField.typeHandler();
        useJavaType = columnField.useJavaType();
        isPreferredWhenColumnNameConfilict = columnField.isPreferredWhenColumnNameConflict();
    }

    @Override
    public String columnName() {
        return columnName;
    }

    @Override
    public JdbcType jdbcType() {
        return jdbcType;
    }

    @Override
    public String outDbExpression() {
        return outDbExpression;
    }

    @Override
    public String inDbExpression() {
        return inDbExpression;
    }

    @Override
    public boolean insertable() {
        return insertable;
    }

    @Override
    public boolean updatable() {
        return updatable;
    }

    @Override
    public String propertyName() {
        return propertyName;
    }

    @Override
    public String javaType() {
        return javaType;
    }

    @Override
    public boolean useJavaType() {
        return useJavaType;
    }

    @Override
    public String typeHandler() {
        return typeHandler;
    }

    @Override
    public boolean isPreferredWhenColumnNameConflict() {
        return isPreferredWhenColumnNameConfilict;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public void setJdbcType(JdbcType jdbcType) {
        this.jdbcType = jdbcType;
    }

    public void setOutDbExpression(String outDbExpression) {
        this.outDbExpression = outDbExpression;
    }

    public void setInDbExpression(String inDbExpression) {
        this.inDbExpression = inDbExpression;
    }

    public void setInsertable(boolean insertable) {
        this.insertable = insertable;
    }

    public void setUpdatable(boolean updatable) {
        this.updatable = updatable;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public void setJavaType(String javaType) {
        this.javaType = javaType;
    }

    public void setTypeHandler(String typeHandler) {
        this.typeHandler = typeHandler;
    }

    public void setUseJavaType(boolean useJavaType) {
        this.useJavaType = useJavaType;
    }

    public void setPreferredWhenColumnNameConfilict(boolean preferredWhenColumnNameConfilict) {
        isPreferredWhenColumnNameConfilict = preferredWhenColumnNameConfilict;
    }
}
