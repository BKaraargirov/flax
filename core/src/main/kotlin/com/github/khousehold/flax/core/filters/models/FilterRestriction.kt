package com.github.khousehold.flax.core.filters.models

import kotlin.reflect.KType

/**
 * Represents a filter constraint, which checks whether a filter is valid or not
 * @param predicate Do a check on the type, return true if  it is supported and false if it is not
 * @param applicableFilters contains filter operations which can be applied to this type.
 */
data class FilterRestriction(
    val predicate: (KType) -> Boolean,
    val applicableFilters: List<FilterOperation>) { }