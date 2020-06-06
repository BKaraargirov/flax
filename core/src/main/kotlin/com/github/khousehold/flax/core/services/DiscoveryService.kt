package com.github.khousehold.flax.core.services


import com.github.khousehold.flax.core.filters.Filterable
import com.github.khousehold.flax.core.filters.NotFilterable
import io.github.classgraph.ClassGraph
import io.github.classgraph.ClassInfo
import io.github.classgraph.ScanResult
import java.lang.reflect.Modifier
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.KVisibility
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField
import kotlin.reflect.jvm.kotlinProperty

class DiscoveryService {
    fun findFilterables(pkgName: String): List<KClass<*>> {
        val filterableAnnotation = Filterable::class.qualifiedName

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