package com.github.khousehold.flax.core.filters

import arrow.core.Option
import com.github.khousehold.flax.core.filters.errors.FilterError
import com.github.khousehold.flax.core.filters.errors.FilterError.*
import com.github.khousehold.flax.core.filters.models.*
import java.util.*
import kotlin.reflect.KProperty1
import kotlin.reflect.KType

typealias ClassName = String

/**
 * Checks if an filter can be applied to a property.
 */
class FilterValidator(val restrictionsCache: Map<ClassName, ClassRestrictions>,
                      val typeRestrictions: List<FilterRestriction>) {
  fun validate(targetClassName: ClassName, filters: IFilter): List<Option<FilterError>> {
    fun loop(filter: IFilter): List<Option<FilterError>> {
      return when (filter) {
        is LogicalFilter -> {
          val validation = validateLogicalFilters(filter)
          val childValidations = filter.filters.flatMap { loop(it) }

          // Merge and check if errors are present
          (listOf(validation) + childValidations).filter { it.nonEmpty() }
        }
        is Filter -> {
          if(restrictionsCache.containsKey(targetClassName) == false ||
              restrictionsCache.get(targetClassName) == null) {
            throw Error("Class $targetClassName not filterable")
          }

          listOf(validateFilter(filter, restrictionsCache.get(targetClassName)!!))
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
  private fun validateLogicalFilters(logicalFilter: LogicalFilter): Option<FilterError> =
    if (logicalFilter.type == LogicalFilterType.NOT) isNegationValid(logicalFilter)
    else Option.empty()

  /**
   * Check if an applied NOT logical application is valid.
   * @return Empty optional if everything is ok. @InvalidNotApplication error otherwise.
   */
  private fun isNegationValid(logicalFilter: LogicalFilter): Option<FilterError> =
      if (logicalFilter.filters.size == 1 && (isAND(logicalFilter.filters[0]) || isOR(logicalFilter.filters[0]))) {
        Option.empty()
      } else {
        Option(InvalidNotApplication())
      }


  private fun validateFilter(filter: Filter, classRestrictions: ClassRestrictions): Option<FilterError> {
    val isNameCorrect = isFilterNameCorrect(filter, classRestrictions.filterableProperties)
    return if (isNameCorrect.nonEmpty()) isNameCorrect
    else canOperationBeApplied(classRestrictions.filterableProperties.getValue(filter.propertyName), filter.operation)
  }

  private fun isFilterNameCorrect(filter: Filter, propertyCache: Map<String, KType>): Option<FilterError> =
    if (propertyCache.containsKey(filter.propertyName)) {
      Option.empty()
    } else {
      Option(FilterNameIsNotCorrectError(filter.propertyName))
    }

  /**
   * Check if a given operation can be applied to a given type
   * @return Empty optional if everythin is ok, A error with information with what isn't if it is not
   */
  private fun canOperationBeApplied(property: KType, operation: FilterOperation): Option<FilterError> {
    val applicableType = this.typeRestrictions.filter { it.predicate.invoke(property) }

    if (applicableType.isEmpty()) {
      return Option(FilterUnsupportedTypeError(property))
    }

    val supportedOperations = applicableType.first()
      .applicableFilters

    if (supportedOperations.contains(operation) == false) {
      return Option(FilterOperationNotSupportedError(property, operation))
    }

    return Option.empty()
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