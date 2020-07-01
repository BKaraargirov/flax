package com.github.khousehold.flax.core.filters

import arrow.core.*
import com.github.khousehold.flax.core.errors.ValidationError
import com.github.khousehold.flax.core.filters.errors.FilterError
import com.github.khousehold.flax.core.filters.errors.FilterError.*
import com.github.khousehold.flax.core.filters.models.*
import java.util.*
import kotlin.reflect.KProperty1
import kotlin.reflect.KType

typealias ClassName = String
typealias ValidatedFilter = Validated<Nel<FilterError>, IFilter>
typealias EitherFilter = Either<Nel<FilterError>, IFilter>

/**
 * Checks if an filter can be applied to a property.
 */
class FilterValidator(val restrictionsCache: Map<ClassName, ClassRestrictions>,
                      val typeRestrictions: List<FilterRestriction>) {
  fun validate(targetClassName: ClassName, filters: IFilter): Validated<Nel<FilterError>, IFilter> {
    val flattenedFilters = flattenFilterTree(filters)

    val errors = flattenedFilters.map { filter ->

    }


    return
  }

  private fun validateFilter(filter: IFilter, targetClassName: ClassName): ValidatedFilter = when (filter) {
      is LogicalFilter -> validateLogicalFilters(filter)

      is Filter ->
        if (restrictionsCache.containsKey(targetClassName) == false ||
            restrictionsCache.get(targetClassName) == null) {
          ClassNotFilterable(targetClassName).invalidNel()
        } else {
          validateFilter(filter, restrictionsCache.get(targetClassName)!!)
        }

      else -> throw UnknownError()
    }

  private fun flattenFilterTree(filter: IFilter): List<IFilter> {
    fun traverse(curentFilter: IFilter): List<IFilter> {
      when (curentFilter) {
        is LogicalFilter -> {
          return if(curentFilter.filters.isEmpty())
            listOf()
          else
            curentFilter.filters.flatMap{ traverse(it) }
        }
        is Filter -> {
          return listOf(curentFilter)
        }
        else -> throw UnknownError()
      }
    }

    return traverse(filter)
  }

  /**
   * Check if the the logical filters are applied correctly.
   * Checks only the first level of the tree.
   *
   * @return an non empty optional if an error occured.
   */
  private fun validateLogicalFilters(logicalFilter: LogicalFilter): ValidatedFilter =
    if (logicalFilter.type == LogicalFilterType.NOT) isNegationValid(logicalFilter)
    else
      //TODO: check if AND and OR have at least 1 child
      logicalFilter.valid()

  /**
   * Check if an applied NOT logical application is valid.
   * @return Empty optional if everything is ok. @InvalidNotApplication error otherwise.
   */
  private fun isNegationValid(logicalFilter: LogicalFilter): ValidatedFilter =
      if (logicalFilter.filters.size == 1 && (isAND(logicalFilter.filters[0]) || isOR(logicalFilter.filters[0]))) {
        logicalFilter.valid()
      } else {
        InvalidNotApplication().invalidNel()
      }


  private fun validateFilter(filter: Filter, classRestrictions: ClassRestrictions): ValidatedFilter =
    isFilterNameCorrect(filter, classRestrictions.filterableProperties)
        .withEither {
          it.flatMap { f -> canOperationBeApplied(f as Filter, classRestrictions).toEither() }
        }

  private fun isFilterNameCorrect(filter: Filter, propertyCache: Map<String, KType>): ValidatedFilter =
    if (propertyCache.containsKey(filter.propertyName)) {
      filter.valid()
    } else {
      FilterNameIsNotCorrectError(filter.propertyName).invalidNel()
    }

  /**
   * Check if a given operation can be applied to a given type
   * @return Empty optional if everythin is ok, A error with information with what isn't if it is not
   */
  private fun canOperationBeApplied(filter: Filter, classRestrictions: ClassRestrictions): ValidatedFilter {
    val property = classRestrictions.filterableProperties.getValue(filter.propertyName)
    val applicableType = this.typeRestrictions.filter { it.predicate.invoke(property) }

    if (applicableType.isEmpty()) {
      return FilterUnsupportedTypeError(property).invalidNel()
    }

    val supportedOperations = applicableType.first()
      .applicableFilters

    if (supportedOperations.contains(filter.operation) == false) {
      return FilterOperationNotSupportedError(property, filter.operation).invalidNel()
    }

    return filter.valid()
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