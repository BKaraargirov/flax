package com.github.khousehold.flax.mongo.infrastructure

import com.mongodb.client.FindIterable
import com.mongodb.client.MongoCollection
import com.github.khousehold.flax.core.filters.FilterFactory
import com.github.khousehold.flax.core.filters.IFilter
import com.github.khousehold.flax.core.filters.Pagination
import org.bson.conversions.Bson
import org.litote.kmongo.deleteMany
import kotlin.reflect.KType

abstract class MongoGenericRepository<A>(
  private val reflected: KType, private val filterFactory: FilterFactory<Bson>
) {

  abstract fun getCollection(): MongoCollection<A>

  fun get(filters: IFilter?, pagination: Pagination?): List<A> {
    val search = if (filters == null) {
      getCollection().find()
    } else {
      getCollection().find(this.filterFactory.transformFilters(filters, reflected))
    }

    return addPagination(search, pagination)
  }

  fun create(newItem: A): Boolean {
    return try {
      getCollection().insertOne(newItem)
      true
    } catch (e: Exception) {
      //TODO: log
        //TODO: fix
      throw Exception("Could not be inserted")
    }
  }

  /**
   * Remove all documents from the collection
   * @return the number of documents that where deleted
   */
  fun removeAll(): Long {
    return getCollection().deleteMany().deletedCount
  }

  private fun addPagination(result: FindIterable<A>, pagination: Pagination?): List<A> =
    if(pagination == null) {
      result.toList()
    } else {
      result.skip(pagination.getSkipSize()).take(pagination.pageSize)
    }
}