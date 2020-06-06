package com.github.khousehold.flax.core.services.testData

import com.github.khousehold.flax.core.filters.annotations.Filterable

@Filterable
data class BasicFilterableClass(
        val myStringField: String,
        val myIntFiled: Int
) {
}