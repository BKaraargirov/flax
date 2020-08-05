package com.github.khousehold.flax.core.filters

import com.github.khousehold.flax.core.filters.errors.FilterError
import com.github.khousehold.flax.core.filters.errors.FilterError.*
import com.github.khousehold.flax.core.filters.models.*
import java.util.*
import kotlin.reflect.KType

typealias ClassName = String

/**
 * Checks if an filter can be applied to a property.
 */
class FilterValidator(val classRestrictions: Map<ClassName, ClassRestrictions>,
                      val filterRestrictions: List<FilterRestriction>) {
  fun validate(targetClassName: ClassName,
               filters: IFilter): List<Optional<FilterError>> {
    fun loop(filter: IFilter, classRestriction: ClassRestrictions): List<Optional<FilterError>> {
      return when (filter) {
        is LogicalFilter -> {
          val validation = validateFilterCombination(filter)
          val childValidations = filter.filters.flatMap { loop(it, classRestriction) }

          // Merge and check if errors are present
          (listOf(validation) + childValidations).filter { it.isPresent }
        }
        is Filter -> {
          listOf(validateFilter(filter, classRestriction))
        }
        else -> throw UnknownError()
      }
    }

    return if(!classRestrictions.containsKey(targetClassName)) {
      listOf(Optional.of(FilterClassNotFound(targetClassName) as FilterError))
    } else {
      loop(filters, classRestrictions.getValue(targetClassName))
    }
  }

  /**
   * Check if the logical operations of the filtration tree is ok.
   * Currently NOT is not supported
   */
  private fun validateFilterCombination(logicalFilter: LogicalFilter): Optional<FilterError> =
    if (logicalFilter.type == LogicalFilterType.NOT) isNotValid(logicalFilter)
    else Optional.empty()


  private fun validateFilter(filter: Filter, classRestriction: ClassRestrictions): Optional<FilterError> {
    val isNameCorrect = isFilterNameCorrect(filter, classRestriction.filterableProperties)
    return if (isNameCorrect.isPresent) isNameCorrect
    else canOperationBeApplied(classRestriction.filterableProperties.getValue(filter.propertyName), filter.operation)
  }

  /**
   * Check if the property on which the filter is to be applied exists
   */
  private fun isFilterNameCorrect(filter: Filter, propertyCache: Map<String, KType>): Optional<FilterError> =
    if (propertyCache.containsKey(filter.propertyName)) {
      Optional.empty()
    } else {
      Optional.of(FilterNameIsNotCorrectError(filter.propertyName))
    }

  /**
   * Check if a given operation can be applied to a given type
   * @return Empty optional if everythin is ok, A error with information with what isn't if it is not
   */
  private fun canOperationBeApplied(property: KType, operation: FilterOperation): Optional<FilterError> {
    val applicableType = this.filterRestrictions.filter { it.predicate.invoke(property) }

    if (applicableType.isEmpty()) {
      return Optional.of(FilterUnsupportedTypeError(property))
    }

    val supportedOperations = applicableType.first()
      .applicableFilters

    if (supportedOperations.contains(operation) == false) {
      return Optional.of(FilterOperationNotSupportedError(property, operation))
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