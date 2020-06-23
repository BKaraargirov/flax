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

  fun isNumber(propertyType: KType): Boolean {
    return propertyType.classifier == Integer::class ||
        propertyType.classifier == Double::class ||
        propertyType.classifier == Float::class ||
        propertyType.classifier == Long::class ||
        propertyType.classifier== BigDecimal::class ||
        propertyType.classifier == Short::class ||
        propertyType.classifier == BigInteger::class
  }

  fun isTypeOf(propertyType: KProperty1<*, *>, targetType: KType): Boolean {
    return propertyType.returnType.isSubtypeOf(targetType)
  }

  fun isTypeOf(propertyType: KProperty1<*, *>, targetType: KClass<*>): Boolean {
    return propertyType.returnType.isSubtypeOf(targetType.createType())
  }

  fun getNullableType(targetClass: KClass<*>): KType = targetClass.createType(nullable = true)
}