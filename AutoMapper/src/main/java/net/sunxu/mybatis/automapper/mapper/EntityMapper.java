package net.sunxu.mybatis.automapper.mapper;

/**
 * entity mapper for entity.
 *
 * It'll generate a result map named "FULL_COLUMN", a sql element named "FULL_COLUMN",
 * methods like "selectByIndex_" + indexName(case sensitive) to select by index name in the mapper's xml.
 * If there are no xml file in there, it'll create a new file.
 *
 * For entities with no EntityMapper but referred by other entities. The annotation processor will generate a new class file
 * named "AnnoymousEntityMapper" in the location of the first entity mapper (sorted by entity mapper's full qualified name)
 * and a new xml file for this new mapper. If there're already a class named "AnnoymousEntityMapper" in the same location, it
 * will add a "A" to 'AnnoymousEntityMapper" like "AnnoymousEntityMapperA" and add more "A" if exist class files with same name.
 * You specific the 'AnnoymousEntityMapper" in mybatis's config file or include it in the package.
 *
 * The annotation processor will add a new element to the xml if there's no element with same id. If there exist a element with same
 * id, it will check attributes except for "id", and inner xml.
 * For resultmap, it will add new id/result/association/collection to the resultmap if not exist element with same property name.
 * For sql/insert/update/delete/select, the processor will check the attribute, if there is more attributes than a "id", it will not edit it's attributes.
 * If the element has no inner element or text, will add the auto-generated context to it.
 * You can add a comment above the element to control the annotation processor's behavior.
 * for more information.
 */
public interface EntityMapper<T> {

}
