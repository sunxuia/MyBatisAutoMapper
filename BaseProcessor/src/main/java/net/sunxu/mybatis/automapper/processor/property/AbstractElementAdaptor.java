package net.sunxu.mybatis.automapper.processor.property;

import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.MutableClassToInstanceMap;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import java.util.Set;


public abstract class AbstractElementAdaptor {
    public abstract String getName();

    private static class RecentlyUsed<P extends AbstractAnnotationProperty> {
        private Class<P> key;
        private P value;
    }

    private RecentlyUsed recentlyUsed = new RecentlyUsed();

    private final ClassToInstanceMap<AbstractAnnotationProperty> properties = MutableClassToInstanceMap.create();

    void setProperties(Set<AbstractAnnotationProperty> properties) {
        for (AbstractAnnotationProperty property : properties) {
            @SuppressWarnings("unchecked")
            Class<AbstractAnnotationProperty> clazz = (Class<AbstractAnnotationProperty>) property.getClass();
            this.properties.putInstance(clazz, property);
        }
    }

    public synchronized <T extends AbstractAnnotationProperty> T get(Class<T> propertyClass) {
        if (recentlyUsed.key != propertyClass) {
            recentlyUsed.key = propertyClass;
            recentlyUsed.value = properties.getInstance(propertyClass);
        }
        return (T) recentlyUsed.value;
    }

    public boolean contains(Class<? extends AbstractAnnotationProperty> propertyClass) {
        return get(propertyClass) != null;
    }
}
