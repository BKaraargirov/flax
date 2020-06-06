package com.github.khousehold.flax.core.services

import com.github.khousehold.flax.core.services.testData.NotCompletelyFilterableClass
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import kotlin.reflect.full.createType

class DiscoveryServiceTests: StringSpec({
    val unitUnderTest = DiscoveryService()

    "Filterable classes should be discovered" {
        val targetPackage = "com.github.khousehold.flax.core.services"
        val expectedResultSize = 2

        val actualResult = unitUnderTest.findFilterables(targetPackage)

        actualResult.size shouldBe expectedResultSize
    }

    "Discovery service should be able to find only filterable fields" {
        val targetPackage = "com.github.khousehold.flax.core.services"
        val targetClass = NotCompletelyFilterableClass::class.simpleName
        val expectedResultSize = 2

        val cls = unitUnderTest.findFilterables(targetPackage).filter { it.simpleName == targetClass }.first()
        val result = unitUnderTest.getFilterableFields(cls)

        result.size shouldBe expectedResultSize
        result.containsKey("age") shouldBe true
        result.get("age") shouldBe Int::class.createType()
        result.containsKey("name") shouldBe true
        result.get("name") shouldBe String::class.createType()
    }

})