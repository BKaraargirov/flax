package com.github.khousehold.flax.core.filters.models

import com.github.khousehold.flax.core.filters.models.FilterOperation
import kotlin.reflect.KProperty1

/**
 * Represents a filter constraint, which checks whether a filter is valid or not
 */
data class FilterRestriction(
  val predicate: (KProperty1<*, *>) -> Boolean,
  val applicableFilters: List<FilterOperation>) { }