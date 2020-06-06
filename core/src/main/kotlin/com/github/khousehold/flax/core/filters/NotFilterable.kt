package com.github.khousehold.flax.core.filters

/**
 * Ignore a field and make it non searchable.
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class NotFilterable {
}