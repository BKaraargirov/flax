package com.github.khousehold.flax.core.services


import com.github.khousehold.flax.core.filters.annotations.Filterable
import com.github.khousehold.flax.core.filters.annotations.NotFilterable
import io.github.classgraph.ClassGraph
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.KVisibility
import kotlin.reflect.jvm.kotlinProperty

/**
 * Responsible for discovering filters and filterables and extracting the needed meta data from them.
 */
class DiscoveryService {
    /**
     * Scan a class path to find all classes marked with @Filterable
     */
    fun findFilterables(pkgName: String): List<KClass<*>> {
        val filterableAnnotation = Filterable::class.qualifiedName

        //TODO: change to own implementation since this is the only place where the dependency is used
        return ClassGraph()
                .enableAllInfo()
                .whitelistPackages(pkgName)
                .scan()
                .use{ scanResult ->
                    scanResult
                            .getClassesWithAnnotation(filterableAnnotation)
                            .loadClasses().map { it.kotlin }
                }
    }

    /**
     * Create a mapping of all fields of a given class that can be used as a filter.
     * All fields which are market as @NotFilterable or are non public are ignored.
     */
    fun getFilterableFields(classInfo: KClass<*>): Map<String, KType> {
         return classInfo.java.declaredFields
                .filter { property ->
                    val isFilterable = property.annotations
                            .all {
                                p -> p.annotationClass != NotFilterable::class
                            }
                    isFilterable
                }.map { it.kotlinProperty }
                 .filter { p -> p != null && p.visibility == KVisibility.PUBLIC }
                 .map { property -> Pair(property!!.name, property.returnType) }
                 .toMap()
    }
}