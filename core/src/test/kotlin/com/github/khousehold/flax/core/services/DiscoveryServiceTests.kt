package com.github.khousehold.flax.core.services

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class DiscoveryServiceTests: StringSpec({
    val unitUnderTest = DiscoveryService()

    "Filterable classes should be discovered" {
        val targetPackage = "com.github.khousehold.flax.core.services"
        val expectedResultSize = 1

        val actualResult = unitUnderTest.findFilterables(targetPackage)

        actualResult.size shouldBe expectedResultSize
    }
})