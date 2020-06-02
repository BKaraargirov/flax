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
  fun createPropertyCache(targetClass: KClass<*>): Map<String, KProperty1<*, *>> =
    targetClass.declaredMemberProperties.map { p -> Pair(p.name, p) }.toMap()

  fun createPropertyCache(targetType: KType): Map<String, KProperty1<*,*>> =
    this.createPropertyCache(targetType.jvmErasure)

  fun isNumber(propertyType: KProperty1<*, *>): Boolean {
    return isTypeOf(propertyType, Integer::class) || isTypeOf(propertyType, Double::class) || isTypeOf(propertyType,
      Float::class) || isTypeOf(propertyType, Long::class) || isTypeOf(propertyType, BigDecimal::class) || isTypeOf(
      propertyType, Short::class) || isTypeOf(propertyType, BigInteger::class)
  }

  fun isTypeOf(propertyType: KProperty1<*, *>, targetType: KType): Boolean {
    return propertyType.returnType.isSubtypeOf(targetType)
  }

  fun isTypeOf(propertyType: KProperty1<*, *>, targetType: KClass<*>): Boolean {
    return propertyType.returnType.isSubtypeOf(targetType.createType())
  }

  fun getNullableType(targetClass: KClass<*>): KType = targetClass.createType(nullable = true)
}