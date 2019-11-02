package net.sunxu.mybatis.automapper.processor.mapper;

import com.google.inject.*;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import net.sunxu.mybatis.automapper.processor.environment.EnvironmentModule;
import net.sunxu.mybatis.automapper.processor.environment.SystemHelper;
import net.sunxu.mybatis.automapper.processor.mapper.mapper.DecoratorFactory;
import net.sunxu.mybatis.automapper.processor.mapper.mapper.MapperElementsCreatorsProvider;
import net.sunxu.mybatis.automapper.processor.mapper.xml.XmlHandler;
import net.sunxu.mybatis.automapper.processor.mapper.xml.XmlHandlerFactory;
import net.sunxu.mybatis.automapper.processor.util.HelpUtils;

import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class AutoMapperBuilder {
    @Inject
    private EnvironmentModule.EnvironmentInjector envInjector;
    @Inject
    private SystemHelper systemHelper;

    public void build(Set<Class<? extends MapperElementsCreator>> creators) {
        Injector injector = getInjector(creators);
        Map<String, MapperElementsCreator> mappers =
                injector.getInstance(new Key<Map<String, MapperElementsCreator>>() {});

        XmlHandlerFactory xmlHandlerFactory = injector.getInstance(XmlHandlerFactory.class);
        for (Map.Entry<String, MapperElementsCreator> pair : mappers.entrySet()) {
            XmlHandler xmlHandler = xmlHandlerFactory.getXmlHandler(pair.getKey());
            xmlHandler.addElements(pair.getValue().getElements());
            xmlHandler.save();
        }
    }

    private Injector getInjector(Set<Class<? extends MapperElementsCreator>> creators) {
        Map<Class<? extends MapperElementsCreator>, Class<? extends MapperElementsCreator>> customImpls = systemHelper
                .getClassesInPackage("net.sunxu.mybatis.automapper.processor.mapper.mapper")
                .stream()
                .filter(c -> MapperElementsCreator.class.isAssignableFrom(c) &&
                        !Modifier.isAbstract(c.getModifiers()) &&
                        Modifier.isPublic(c.getModifiers()))
                .map(c -> (Class<? extends MapperElementsCreator>) c)
                .collect(Collectors.toMap(c -> c, c -> c));
        HelpUtils.forEach(creators, clazz -> {
            HelpUtils.forEach(customImpls.keySet(), customClass -> {
                if (!Modifier.isAbstract(customClass.getModifiers()) && customClass.isAssignableFrom(clazz)) {
                    customImpls.put(customClass, clazz);
                    return false;
                }
                return true;
            });
        });

        Injector injector = envInjector.createChildInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(new TypeLiteral<Map<String, MapperElementsCreator>>() {})
                        .toProvider(MapperElementsCreatorsProvider.class)
                        .in(Singleton.class);

                install(new FactoryModuleBuilder().build(XmlHandlerFactory.class));

                customImpls.forEach((localClass, customClass) -> {
                    if (customClass == localClass) {
                        install(new FactoryModuleBuilder()
                                .build(TypeLiteral.get(getElementsProviderType(localClass))));
                    } else {
                        install(new FactoryModuleBuilder()
                                .implement((Class) localClass, customClass)
                                .build(TypeLiteral.get(getElementsProviderType(localClass))));
                    }
                });
            }
        });
        return injector;
    }

    private ParameterizedType getElementsProviderType(Class<?> clazz) {
        return new ParameterizedType() {

            @Override
            public Type[] getActualTypeArguments() {
                return new Type[]{clazz};
            }

            @Override
            public Type getRawType() {
                return DecoratorFactory.class;
            }

            @Override
            public Type getOwnerType() {
                return null;
            }
        };
    }

}
