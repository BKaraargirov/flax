package com.github.khousehold.flax.core.filters

import oink.server.common.reflection.TypeUtils.isNumber
import oink.server.common.reflection.TypeUtils.isTypeOf
import com.github.khousehold.flax.core.filters.models.FilterOperation.*
import com.github.khousehold.flax.core.filters.models.FilterRestriction
import oink.server.common.reflection.TypeUtils
import java.time.LocalDateTime

object DefaultFilterRestrictions {
  val RESTRICTIONS = listOf(
          FilterRestriction({ k -> k.classifier  == String::class }, listOf(Equal, Contains)),
          FilterRestriction({ k -> k.classifier == TypeUtils.getNullableType(String::class) }, listOf(Equal)),
          FilterRestriction({ k -> isNumber(k) }, listOf(Equal, GreaterThan, GreaterThanEq, LowerThan, LowerThanEq)),
          FilterRestriction({ k -> k.classifier == LocalDateTime::class }, listOf(GreaterThan, GreaterThanEq, LowerThan, LowerThanEq))
  // TODO: add year eq, year + month, year + month + day eq,
  )
}