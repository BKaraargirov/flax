package com.github.khousehold.flax.core.filters.errors

import com.github.khousehold.flax.core.errors.ValidationError
import com.github.khousehold.flax.core.errors.ValidationErrorFactory

class FilterValidationErrorFactory: ValidationErrorFactory<FilterError> {
  override fun create(errors: List<FilterError>): ValidationError = FilterValidationError(errors)
}

class FilterValidationError(
  errors: List<FilterError>
) : ValidationError(
  "FTR_99_VLD",
  errors,
  "Filtration"
)