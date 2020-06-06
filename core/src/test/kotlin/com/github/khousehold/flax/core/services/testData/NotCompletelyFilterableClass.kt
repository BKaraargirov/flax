package com.github.khousehold.flax.core.services.testData

import com.github.khousehold.flax.core.filters.annotations.Filterable
import com.github.khousehold.flax.core.filters.annotations.NotFilterable

@Filterable
data class NotCompletelyFilterableClass(
        val age: Int,
        val name: String,
        @NotFilterable val address: String,
        private val sex: Char
) {
}