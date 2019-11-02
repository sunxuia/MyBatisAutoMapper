package net.sunxu.mybatis.automapper.processor.mapper.mapper;

import com.google.inject.Inject;
import net.sunxu.mybatis.automapper.processor.mapper.MapperElementsCreator;

public class DecorateDirectorImpl implements DecorateDirector {

    @Inject
    private EntityMapperHandler entityMapperHandler;
    @Inject
    private InsertOneHandler insertOneHandler;
    @Inject
    private SelectAllHandler selectAllHandler;
    @Inject
    private SelectAnyHandler selectAnyHandler;
    @Inject
    private CountByIndexHandler countByIndexHandler;
    @Inject
    private DeleteByIndexHandler deleteByIndexHandler;
    @Inject
    private SelectByIndexHandler selectByIndexHandler;
    @Inject
    private SelectDistinctIndexValueHandler selectDistinctIndexValueHandler;
    @Inject
    private UpdateByIndexHandler updateByIndexHandler;
    @Inject
    private UpdateIndexValueHandler updateIndexValueHandler;


    public MapperElementsCreator decorate(MapperElementsCreator provider, MapperModel mapperModel) {
        return getHandler().decorate(provider, mapperModel);
    }

    private boolean initialized;

    private HandlerForDecorator getHandler() {
        if (!initialized) {
            entityMapperHandler.setNext(insertOneHandler)
                    .setNext(selectAllHandler)
                    .setNext(selectAnyHandler)
                    .setNext(countByIndexHandler)
                    .setNext(deleteByIndexHandler)
                    .setNext(selectByIndexHandler)
                    .setNext(selectDistinctIndexValueHandler)
                    .setNext(updateByIndexHandler)
                    .setNext(updateIndexValueHandler);
            initialized = true;
        }

        return entityMapperHandler;
    }
}
