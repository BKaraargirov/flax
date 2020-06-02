package com.github.khousehold.flax.core.filters

data class Pagination @ExperimentalUnsignedTypes constructor(
  val page: Int,
  val pageSize: Int
) {
  fun getSkipSize(): Int = pageSize * page
}