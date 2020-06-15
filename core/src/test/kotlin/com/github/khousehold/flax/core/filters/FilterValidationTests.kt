package com.github.khousehold.flax.core.filters

import com.github.khousehold.flax.core.services.DiscoveryService
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

    }
  }
})
