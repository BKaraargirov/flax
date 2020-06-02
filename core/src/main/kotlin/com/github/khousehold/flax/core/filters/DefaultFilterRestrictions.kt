package com.github.khousehold.flax.core.filters

import oink.server.common.reflection.TypeUtils.isNumber
import oink.server.common.reflection.TypeUtils.isTypeOf
import com.github.khousehold.flax.core.filters.FilterOperation.*
import oink.server.common.reflection.TypeUtils
import java.time.LocalDateTime

object DefaultFilterRestrictions {
  val RESTRICTIONS = listOf(
  FilterRestriction({ k -> isTypeOf(k, String::class) }, listOf(Equal, Contains)),
  FilterRestriction( { k -> isTypeOf(k, TypeUtils.getNullableType(String::class)) }, listOf(Equal)),
  FilterRestriction({ k -> isNumber(k) }, listOf(Equal, GreaterThan, GreaterThanEq, LowerThan, LowerThanEq)),
  FilterRestriction({ k -> isTypeOf(k, LocalDateTime::class) }, listOf(GreaterThan, GreaterThanEq, LowerThan, LowerThanEq))
  // TODO: add year eq, year + month, year + month + day eq,
  )
}