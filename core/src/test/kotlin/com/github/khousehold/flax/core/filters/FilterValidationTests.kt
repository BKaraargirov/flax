package com.github.khousehold.flax.core.filters

import arrow.core.Option
import arrow.core.Some
import arrow.core.extensions.list.foldable.exists
import com.github.khousehold.flax.core.filters.errors.FilterError
import com.github.khousehold.flax.core.filters.models.Filter
import com.github.khousehold.flax.core.filters.models.FilterOperation
import com.github.khousehold.flax.core.filters.models.LogicalFilter
import com.github.khousehold.flax.core.filters.models.LogicalFilterType
import com.github.khousehold.flax.core.services.DiscoveryService
import com.github.khousehold.flax.core.services.testData.NotCompletelyFilterableClass
import io.kotlintest.matchers.collections.shouldContain
import io.kotlintest.shouldBe
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object FilterValidationTests: Spek({
  val packageUnderTest = "com.github.khousehold.flax.core"
  val discoveryService = DiscoveryService()
  val classRestrictions = discoveryService.discover(packageUnderTest)
  val unitUnderTest: FilterValidator = FilterValidator(
      classRestrictions,
      DefaultFilterRestrictions.RESTRICTIONS)

  describe("Valite filters") {
    describe("of correct tree") {
      val validFiltrationTree = LogicalFilter(
          LogicalFilterType.AND,
          listOf(
              LogicalFilter(
                  LogicalFilterType.OR,
                  listOf(
                      Filter("age", 50, FilterOperation.GreaterThan),
                      Filter("age", 10, FilterOperation.LowerThan)
                      )
              ),
              Filter("name", "Corey", FilterOperation.Equal)
          )
      )

      val validationResult = unitUnderTest.validate(
          NotCompletelyFilterableClass::class.qualifiedName!!,
          validFiltrationTree
      )

      validationResult shouldBe emptyList()
    }

    describe("of incorrect property name") {
      val validFiltrationTree = LogicalFilter(
          LogicalFilterType.AND,
          listOf(
              LogicalFilter(
                  LogicalFilterType.OR,
                  listOf(
                      Filter("ag3", 50, FilterOperation.GreaterThan),
                      Filter("@ge", 10, FilterOperation.LowerThan)
                  )
              ),
              Filter("name", "Corey", FilterOperation.Equal)
          )
      )

      val validationResult = unitUnderTest.validate(
          NotCompletelyFilterableClass::class.qualifiedName!!,
          validFiltrationTree
      )

      validationResult.size shouldBe 2
      validationResult shouldContain Some(FilterError.FilterNameIsNotCorrectError("ag3"))
      validationResult shouldContain Some(FilterError.FilterNameIsNotCorrectError("@ge"))
    }
  }
})
