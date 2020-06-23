package com.github.khousehold.flax.core.filters.models

import kotlin.reflect.KType

/**
 * Represents a filter constraint, which checks whether a filter is valid or not
 */
data class FilterRestriction(
  val predicate: (KType) -> Boolean,
  val applicableFilters: List<FilterOperation>) { }