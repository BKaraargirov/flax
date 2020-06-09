package com.github.khousehold.flax.core.filters.models

class LogicalFilter(
        val type: LogicalFilterType, val filters: List<IFilter>
) : IFilter