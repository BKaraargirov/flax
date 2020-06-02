package com.github.khousehold.flax.core.datastructures

class BiMap<A, B>() {
  private val keyMap: MutableMap<A, B> = hashMapOf()
  private val valueMap: MutableMap<B, A> = hashMapOf()

  constructor(pairs: Collection<Pair<A, B>>) : this() {
    pairs.map { (a, b) -> put(a, b) }
  }

  constructor(vararg pairs: Pair<A, B>) : this() {
    pairs.map { (a, b) -> put(a, b) }
  }

  fun getByKey(key: A): B? {
    return this.keyMap[key]
  }

  fun getByValue(value: B): A? {
    return this.valueMap[value]
  }

  fun put(pair: Pair<A, B>) {
    this.put(pair.first, pair.second)
  }

  fun put(
    key: A,
    value: B
  ) {
    //Clean up if the key or value existed before
    this.remove(key, value)

    keyMap[key] = value
    valueMap[value] = key
  }

  fun remove(pair: Pair<A, B>) {
    this.remove(pair.first, pair.second)
  }

  fun remove(
    key: A,
    value: B
  ) {
    val oldValue = keyMap.remove(key)
    valueMap.remove(oldValue)
    val oldKey = valueMap.remove(value)
    keyMap.remove(oldKey)
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as BiMap<*, *>

    if (keyMap != other.keyMap) return false
    if (valueMap != other.valueMap) return false

    return true
  }

  override fun hashCode(): Int {
    var result = keyMap.hashCode()
    result = 31 * result + valueMap.hashCode()
    return result
  }

  companion object {
    fun <A, B> empty(): BiMap<A, B> {
      return BiMap<A, B>()
    }
  }
}