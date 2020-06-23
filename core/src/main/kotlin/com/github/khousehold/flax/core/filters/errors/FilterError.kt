package com.github.khousehold.flax.core.filters.errors

import com.github.khousehold.flax.core.errors.ErrorBase
import com.github.khousehold.flax.core.filters.models.FilterOperation
import kotlin.reflect.KType

sealed class FilterError(
  override val id: String, override val message: String
) : ErrorBase(id, message, "Filtration") {
  data class FilterUnsupportedTypeError(val msg: KType) : FilterError("FTR_01_TYP", "$msg types do not support filtration")

  data class FilterOperationNotSupportedError(val type: KType, val operation: FilterOperation) :
    FilterError("FTR_02_NOP", "$type does not support $operation")

  data class FilterNameIsNotCorrectError(val filterName: String) : FilterError("FTR_03_FNC", "$filterName is not correct")

  class InvalidNotApplication() :
    FilterError(id = "FTR_04_NOT", message = "NOT filter must have only one child which is AND or OR") {
    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (javaClass != other?.javaClass) return false
      return true
    }

    override fun hashCode(): Int {
      return javaClass.hashCode()
    }
  }
}
