package com.github.khousehold.flax.core.filters

import com.github.khousehold.flax.core.filters.errors.FilterError
import com.github.khousehold.flax.core.filters.errors.FilterError.*
import com.github.khousehold.flax.core.filters.models.*
import java.util.*
import kotlin.reflect.KProperty1

typealias ClassName = String

/**
 * Checks if an filter can be applied to a property.
 */
class FilterValidator(val classRestrictions: Map<ClassName, ClassRestrictions>, val typeRestrictions: List<FilterRestriction>) {
  fun validate(targetClassName: ClassName, filters: IFilter): List<Optional<FilterError>> {
    fun loop(filter: IFilter): List<Optional<FilterError>> {
      return when (filter) {
        is LogicalFilter -> {
          val validation = validateLogicalFilters(filter)
          val childValidations = filter.filters.flatMap { loop(it) }

          // Merge and check if errors are present
          (listOf(validation) + childValidations).filter { it.isPresent }
        }
        is Filter -> {
          listOf(validateFilter(filter, mapOf()))
        }
        else -> throw UnknownError()
      }
    }

    return loop(filters)
  }

  /**
   * Check if the the logical filters are applied correctly.
   * Checks only the first level of the tree.
   *
   * @return an non empty optional if an error occured.
   */
  private fun validateLogicalFilters(logicalFilter: LogicalFilter): Optional<FilterError> =
    if (logicalFilter.type == LogicalFilterType.NOT) isNegationValid(logicalFilter)
    else Optional.empty()

  /**
   * Check if an applied NOT logical application is valid.
   * @return Empty optional if everything is ok. @InvalidNotApplication error otherwise.
   */
  private fun isNegationValid(logicalFilter: LogicalFilter): Optional<FilterError> =
      if (logicalFilter.filters.size == 1 && (isAND(logicalFilter.filters[0]) || isOR(logicalFilter.filters[0]))) {
        Optional.empty()
      } else {
        Optional.of(InvalidNotApplication())
      }


  private fun validateFilter(filter: Filter, propertyCache: Map<String, KProperty1<*, *>>): Optional<FilterError> {
    val isNameCorrect = isFilterNameCorrect(filter, propertyCache)
    return if (isNameCorrect.isPresent) isNameCorrect
    else canOperationBeApplied(propertyCache.getValue(filter.propertyName), filter.operation)
  }

  private fun isFilterNameCorrect(filter: Filter, propertyCache: Map<String, KProperty1<*, *>>): Optional<FilterError> =
    if (propertyCache.containsKey(filter.propertyName)) {
      Optional.empty()
    } else {
      Optional.of(FilterNameIsNotCorrectError(filter.propertyName))
    }

  /**
   * Check if a given operation can be applied to a given type
   * @return Empty optional if everythin is ok, A error with information with what isn't if it is not
   */
  private fun canOperationBeApplied(property: KProperty1<*, *>, operation: FilterOperation): Optional<FilterError> {
    val applicableType = this.typeRestrictions.filter { it.predicate.invoke(property) }

    if (applicableType.isEmpty()) {
      return Optional.of(FilterUnsupportedTypeError(property.returnType))
    }

    val supportedOperations = applicableType.first()
      .applicableFilters

    if (supportedOperations.contains(operation) == false) {
      return Optional.of(FilterOperationNotSupportedError(property.returnType, operation))
    }

    return Optional.empty()
  }


  private fun isAND(filter: IFilter): Boolean = when (filter) {
    is LogicalFilter -> filter.type == LogicalFilterType.AND
    else -> false
  }

  private fun isOR(filter: IFilter): Boolean = when (filter) {
    is LogicalFilter -> filter.type == LogicalFilterType.OR
    else -> false
  }
}