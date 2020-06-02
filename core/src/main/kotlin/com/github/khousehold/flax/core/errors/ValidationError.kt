package com.github.khousehold.flax.core.errors

interface ValidationErrorFactory<T : ErrorBase> {
  fun create(messages: List<T>): ValidationError
}

open class ValidationError(
        override val id: String,
        open val messages: List<ErrorBase>,
        override val origin: String
) : ErrorBase(id, messages.fold("") { acc: String, cur: ErrorBase -> "$acc \n, $cur" }, origin)

