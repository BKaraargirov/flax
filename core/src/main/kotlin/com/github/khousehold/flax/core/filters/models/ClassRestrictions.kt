package com.github.khousehold.flax.core.filters.models

import kotlin.reflect.KClass
import kotlin.reflect.KType

/**
 * Contains all filter restrictions for a given class
 */
data class ClassRestrictions(
        val classInfo: KClass<*>,
        val filterableProperties: Map<String, KType>
) {
    init {
        assert(classInfo.qualifiedName != null) { "Class \"${classInfo}\" qualified name is missing" }
    }

    val className: String = classInfo.qualifiedName!! // You have it in the info, but for ease of use
}