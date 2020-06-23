package com.github.khousehold.flax.core.reflection

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import oink.server.common.reflection.TypeUtils
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.reflect.full.memberProperties

class FilterTests : StringSpec({
  data class TestClass(
    val type: String, val missingType: String?, val size: Int, val money: BigDecimal, val digit: Short,
    val bigOne: Long, val precision: Double, val p2: Float, val giga: BigInteger
  )

  "Compare kclass and kproperty types should be success" {
    val stringProperty = TestClass::class.memberProperties.first { it.name == "type" }
    val targetClass = String::class
    val expectedResult = true

    TypeUtils.isTypeOf(stringProperty, targetClass) shouldBe expectedResult
  }

  "Nullable string should be success" {
    val stringProperty = TestClass::class.memberProperties.first { it.name == "missingType" }
    val targetClass = TypeUtils.getNullableType(String::class)
    val expectedResult = true

    TypeUtils.isTypeOf(stringProperty, targetClass) shouldBe expectedResult
  }

  "Compare kclass and kproperty types should be failure" {
    val stringProperty = TestClass::class.memberProperties.first { it.name == "type" }
    val targetClass = Int::class
    val expectedResult = false

    TypeUtils.isTypeOf(stringProperty, targetClass) shouldBe expectedResult
  }

  "Int property should be a numeric" {
    val intProperty = TestClass::class.memberProperties.first { it.name == "size" }.returnType

    TypeUtils.isNumber(intProperty) shouldBe true
  }

  "BigDecimal property should be a numeric" {
    val target = TestClass::class.memberProperties.first { it.name == "money" }.returnType

    TypeUtils.isNumber(target) shouldBe true
  }

  "Long property should be a numeric" {
    val target = TestClass::class.memberProperties.first { it.name == "bigOne" }.returnType

    TypeUtils.isNumber(target) shouldBe true
  }

  "Short property should be a numeric" {
    val target = TestClass::class.memberProperties.first { it.name == "digit" }.returnType

    TypeUtils.isNumber(target) shouldBe true
  }

  "Double property should be a numeric" {
    val target = TestClass::class.memberProperties.first { it.name == "precision" }.returnType

    TypeUtils.isNumber(target) shouldBe true
  }

  "Float property should be a numeric" {
    val target = TestClass::class.memberProperties.first { it.name == "p2" }.returnType

    TypeUtils.isNumber(target) shouldBe true
  }

  "BigInt property should be a numeric" {
    val target = TestClass::class.memberProperties.first { it.name == "giga" }.returnType

    TypeUtils.isNumber(target) shouldBe true
  }

  "String property should not be a numeric" {
    val target = TestClass::class.memberProperties.first { it.name == "type" }.returnType

    TypeUtils.isNumber(target) shouldBe false
  }
})