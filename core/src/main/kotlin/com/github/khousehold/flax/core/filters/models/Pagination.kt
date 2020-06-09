package com.github.khousehold.flax.core.filters.models

data class Pagination @ExperimentalUnsignedTypes constructor(
  val page: Int,
  val pageSize: Int
) {
  fun getSkipSize(): Int = pageSize * page
}