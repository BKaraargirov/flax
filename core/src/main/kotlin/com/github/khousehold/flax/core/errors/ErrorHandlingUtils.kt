package com.github.khousehold.flax.core.errors

import java.util.*

object ErrorHandlingUtils {
  fun <T : ErrorBase> throwIfInvalid(
    validations: List<Optional<T>>,
    factory: ValidationErrorFactory<T>
  ) {
    val hasError = validations.any { it.isPresent }

    if (hasError == false) {
      return
    }

    val errors = validations.filter { it.isPresent }.map { it.get() }

    throw factory.create(errors)
  }
}