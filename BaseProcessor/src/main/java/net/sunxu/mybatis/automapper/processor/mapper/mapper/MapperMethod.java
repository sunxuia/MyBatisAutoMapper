package net.sunxu.mybatis.automapper.processor.mapper.mapper;

import net.sunxu.mybatis.automapper.processor.property.Method;

import java.util.List;

public class MapperMethod {
    private final Method method;
    private String returnType;
    private boolean isReturnTypeArray, isReturnTypeList;

    public MapperMethod(Method method) {
        this.method = method;

        setReturnType();
    }

    private void setReturnType() {
        returnType = method.getReturnType();
        String listName = List.class.getCanonicalName();
        if (returnType.startsWith(listName)) {
            isReturnTypeList = true;
            if (returnType.equals(listName)) {
                returnType = Object.class.getCanonicalName();
            } else {
                returnType = returnType.substring(listName.length() + 1, returnType.length() - 1);
                if (returnType.contains("<")) {
                    returnType = returnType.substring(0, returnType.indexOf('<') - 1);
                }
            }
        } else if (returnType.endsWith("[]")) {
            isReturnTypeArray = true;
            returnType = returnType.substring(0, returnType.length() - 2);
        }
    }

    public String getName() {
        return method.getName();
    }

    public Method getMethod() {
        return method;
    }

    public String getReturnType() {
        return returnType;
    }

    public boolean isReturnTypeArray() {
        return isReturnTypeArray;
    }

    public boolean isReturnTypeList() {
        return isReturnTypeList;
    }
}
