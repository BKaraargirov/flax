package com.github.khousehold.flax.core.services

import com.github.khousehold.flax.core.services.testData.NotCompletelyFilterableClass
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import kotlin.reflect.full.createType
import kotlin.reflect.full.isSubclassOf

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

        result.classInfo.isSubclassOf(NotCompletelyFilterableClass::class) shouldBe true
        result.filterableProperties.size shouldBe expectedResultSize
        result.filterableProperties.containsKey("age") shouldBe true
        result.filterableProperties.get("age") shouldBe Int::class.createType()
        result.filterableProperties.containsKey("name") shouldBe true
        result.filterableProperties.get("name") shouldBe String::class.createType()
    }

})