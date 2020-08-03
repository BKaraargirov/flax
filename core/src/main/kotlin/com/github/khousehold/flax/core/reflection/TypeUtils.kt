package oink.server.common.reflection

import java.math.BigDecimal
import java.math.BigInteger
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.KType
import kotlin.reflect.full.createType
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.jvm.jvmErasure

object TypeUtils {
  fun createPropertyCache(targetClass: KClass<*>): Map<String, KType> =
    targetClass.declaredMemberProperties.map { p -> Pair(p.name, p.returnType) }.toMap()

  fun getPropertyTypes(targetClassType: KType): Map<String, KType> =
    this.createPropertyCache(targetClassType.jvmErasure)

  fun isNumber(propertyType: KType): Boolean {
    return isTypeOf(propertyType, Integer::class) || isTypeOf(propertyType, Double::class) || isTypeOf(propertyType,
      Float::class) || isTypeOf(propertyType, Long::class) || isTypeOf(propertyType, BigDecimal::class) || isTypeOf(
      propertyType, Short::class) || isTypeOf(propertyType, BigInteger::class)
  }

  fun isTypeOf(propertyType: KType, targetType: KType): Boolean {
    return propertyType.isSubtypeOf(targetType)
  }

  fun isTypeOf(propertyType: KType, targetType: KClass<*>): Boolean {
    return propertyType.isSubtypeOf(targetType.createType())
  }

  fun getNullableType(targetClass: KClass<*>): KType = targetClass.createType(nullable = true)
}