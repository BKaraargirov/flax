package com.github.khousehold.flax.core.filters

import com.github.khousehold.flax.core.filters.errors.FilterError
import com.github.khousehold.flax.core.filters.errors.FilterError.*
import com.github.khousehold.flax.core.filters.models.*
import oink.server.common.reflection.TypeUtils
import java.util.*
import kotlin.reflect.KProperty1
import kotlin.reflect.KType

typealias ClassName = String

/**
 * Checks if an filter can be applied to a property.
 */
class FilterValidator(val classRestrictions: Map<ClassName, ClassRestrictions>, val typeRestrictions: List<FilterRestriction>) {
  fun validate(targetClassName: ClassName, filters: IFilter, target: KType): List<Optional<FilterError>> {
    val propertyCache = TypeUtils.createPropertyCache(target)

    fun loop(filter: IFilter): List<Optional<FilterError>> {
      return when (filter) {
        is LogicalFilter -> {
          val validation = validateFilterCombination(filter)
          val childValidations = filter.filters.flatMap { loop(it) }

          // Merge and check if errors are present
          (listOf(validation) + childValidations).filter { it.isPresent }
        }
        is Filter -> {
          listOf(validateFilter(filter, propertyCache))
        }
        else -> throw UnknownError()
      }
    }

    return loop(filters)
  }

  private fun validateFilterCombination(logicalFilter: LogicalFilter): Optional<FilterError> =
    if (logicalFilter.type == LogicalFilterType.NOT) isNotValid(logicalFilter)
    else Optional.empty()


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

  private fun isNotValid(logicalFilter: LogicalFilter): Optional<FilterError> =
    if (logicalFilter.filters.size == 1 && (isAND(logicalFilter.filters[0]) || isOR(logicalFilter.filters[0]))) {
      Optional.empty()
    } else {
      Optional.of(InvalidNotApplication())
    }

  private fun isAND(filter: IFilter): Boolean = when (filter) {
    is LogicalFilter -> filter.type == LogicalFilterType.AND
    is Filter -> false
    else -> false
  }

  private fun isOR(filter: IFilter): Boolean = when (filter) {
    is LogicalFilter -> filter.type == LogicalFilterType.OR
    is Filter -> false
    else -> false
  }
}