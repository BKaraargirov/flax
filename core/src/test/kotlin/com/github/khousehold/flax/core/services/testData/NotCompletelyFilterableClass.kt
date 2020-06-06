package com.github.khousehold.flax.core.services.testData

import com.github.khousehold.flax.core.filters.Filterable
import com.github.khousehold.flax.core.filters.NotFilterable

@Filterable
data class NotCompletelyFilterableClass(
        val age: Int,
        val name: String,
        @NotFilterable val address: String,
        private val sex: Char
) {
}