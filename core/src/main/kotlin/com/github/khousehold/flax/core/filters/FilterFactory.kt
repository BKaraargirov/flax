package com.github.khousehold.flax.core.filters

import kotlin.reflect.KType

interface FilterFactory<T> {
  fun transformFilters(filters: IFilter, kType: KType): T
}