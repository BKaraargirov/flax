package com.github.khousehold.flax.core.filters

data class Filter(
  val propertyName: String,
  val value: Any?,
  val operation: FilterOperation
): IFilter