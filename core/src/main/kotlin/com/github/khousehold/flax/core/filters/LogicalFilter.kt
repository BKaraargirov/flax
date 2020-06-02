package com.github.khousehold.flax.core.filters

class LogicalFilter(
        val type: LogicalFilterType, val filters: List<IFilter>
) : IFilter