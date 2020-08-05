package com.github.khousehold.flax.core.dataaccess

import com.github.khousehold.flax.core.filters.models.IFilter
import com.github.khousehold.flax.core.filters.models.Pagination

interface Repository<A> {
  fun get(filters: IFilter?, pagination: Pagination?): List<A>

  fun create(newItem: A): Boolean
}