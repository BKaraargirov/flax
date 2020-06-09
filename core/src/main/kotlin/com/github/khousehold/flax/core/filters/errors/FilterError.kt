package com.github.khousehold.flax.core.filters.errors

import com.github.khousehold.flax.core.errors.ErrorBase
import com.github.khousehold.flax.core.filters.models.FilterOperation
import kotlin.reflect.KType

sealed class FilterError(
  override val id: String, override val message: String
) : ErrorBase(id, message, "Filtration") {
  class FilterUnsupportedTypeError(msg: KType) : FilterError("FTR_01_TYP", "$msg types do not support filtration")

  class FilterOperationNotSupportedError(type: KType, operation: FilterOperation) :
    FilterError("FTR_02_NOP", "$type does not support $operation")

  class FilterNameIsNotCorrectError(filterName: String) : FilterError("FTR_03_FNC", "$filterName is not correct")

  class InvalidNotApplication() :
    FilterError(id = "FTR_04_NOT", message = "NOT filter must have only one child which is AND or OR")
}


