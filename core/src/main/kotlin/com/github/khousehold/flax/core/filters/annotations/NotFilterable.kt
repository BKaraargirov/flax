package com.github.khousehold.flax.core.filters.annotations

/**
 * Ignore a field and make it non searchable.
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class NotFilterable {
}