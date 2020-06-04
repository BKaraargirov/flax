package com.github.khousehold.flax.core.services


import com.github.khousehold.flax.core.filters.Filterable
import io.github.classgraph.ClassGraph
import io.github.classgraph.ClassInfo
import io.github.classgraph.ScanResult

class DiscoveryService {
    fun findFilterables(pkgName: String): List<ClassInfo> {
        val filterableAnnotation = Filterable::class.qualifiedName

        return ClassGraph()
                .enableAllInfo()
                .whitelistPackages(pkgName)
                .scan()
                .use{ it.getClassesWithAnnotation(filterableAnnotation) }
    }
}