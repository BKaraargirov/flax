package com.github.khousehold.flax.core.filters.models

data class Filter(
  val propertyName: String,
  val value: Any?,
  val operation: FilterOperation
): IFilter