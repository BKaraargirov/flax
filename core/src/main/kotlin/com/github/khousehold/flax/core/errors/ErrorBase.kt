package com.github.khousehold.flax.core.errors

open class ErrorBase(
  open val id: String,
  override val message: String,
  open val origin: String
) : RuntimeException()

