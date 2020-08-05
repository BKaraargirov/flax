package com.github.khousehold.flax.mongo.filters

import com.github.khousehold.flax.core.errors.ErrorHandlingUtils
import com.github.khousehold.flax.core.filters.*
import com.github.khousehold.flax.core.filters.errors.FilterValidationErrorFactory
import com.github.khousehold.flax.core.filters.models.*
import com.mongodb.client.model.Filters
import org.bson.codecs.configuration.CodecRegistry
import org.bson.conversions.Bson
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.jvm.jvmErasure

class BsonFilterFactory(
  private val filterValidator: FilterValidator
) : FilterFactory<Bson> {
  override fun transformFilters(
          filters: IFilter, targetClass: KClass<*>
  ): Bson {
    val validations = filterValidator.validate(targetClass.qualifiedName!!.toLowerCase(), filters)

    ErrorHandlingUtils.throwIfInvalid(validations, FilterValidationErrorFactory())

    return createDocumentFilter(filters)
  }

  fun createDocumentFilter(filter: IFilter): Bson = when (filter) {
    is LogicalFilter -> {
      when (filter.type) {
        LogicalFilterType.AND -> Filters.and(filter.filters.map { createDocumentFilter(it) })
        LogicalFilterType.OR -> Filters.or(filter.filters.map { createDocumentFilter(it) })
        LogicalFilterType.NOT -> Filters.not(createDocumentFilter(filter.filters[0]))
      }
    }
    is Filter -> transformFilter(filter)
    else -> throw UnknownError()
  }

  /**
   * Transform a single custom filter into a MongoDB filter
   */
  fun transformFilter(filter: Filter): Bson {
    return when (filter.operation) {
      FilterOperation.Equal -> Filters.eq(filter.propertyName, filter.value)

      FilterOperation.Contains -> Filters.regex(filter.propertyName, ".*" + filter.value + ".*")

      FilterOperation.GreaterThan -> Filters.gt(filter.propertyName, filter.value)

      FilterOperation.GreaterThanEq -> Filters.gte(filter.propertyName, filter.value)

      FilterOperation.LowerThan -> Filters.lt(filter.propertyName, filter.value)

      FilterOperation.LowerThanEq -> Filters.lte(filter.propertyName, filter.value)
    }
  }
}