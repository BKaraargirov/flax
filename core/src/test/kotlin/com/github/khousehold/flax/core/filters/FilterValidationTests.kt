package com.github.khousehold.flax.core.filters

import com.github.khousehold.flax.core.filters.models.Filter
import com.github.khousehold.flax.core.filters.models.FilterOperation
import com.github.khousehold.flax.core.filters.models.LogicalFilter
import com.github.khousehold.flax.core.filters.models.LogicalFilterType
import com.github.khousehold.flax.core.services.DiscoveryService
import com.github.khousehold.flax.core.services.testData.NotCompletelyFilterableClass
import io.kotlintest.shouldBe
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object FilterValidationTests: Spek({
  describe("Valite filters") {
    val packageUnderTest = "com.github.khousehold.flax.core"
    val discoveryService = DiscoveryService()
    val classRestrictions = discoveryService.discover(packageUnderTest)

    lateinit var unitUnderTest: FilterValidator

    beforeEachTest {
      unitUnderTest = FilterValidator(classRestrictions, listOf())
    }

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

      validationResult shouldBe emptyList<Optional<>>()
    }
  }
})
