package dev.reimer.kotlin.jvm.ktx

import kotlin.random.Random

fun <K, V> Map<K, V>.inverse() = entries.groupBy({ it.value }, { it.key })

fun <K, V : Any> Iterable<Pair<K?, V>>.filterKeyNotNull(): List<Pair<K, V>> {
    val destination = mutableListOf<Pair<K, V>>()
    for ((first, second) in this) if (first != null) destination.add(first to second)
    return destination
}

fun <K, V : Any> Iterable<Pair<K, V?>>.filterValueNotNull(): List<Pair<K, V>> {
    val destination = mutableListOf<Pair<K, V>>()
    for ((first, second) in this) if (second != null) destination.add(first to second)
    return destination
}

fun <K, V : Any> Map<K, V?>.filterValueNotNull(): Map<K, V> {
    val destination = mutableMapOf<K, V>()
    for ((first, second) in this) if (second != null) destination[first] = second
    return destination
}

fun <K, V : Any> Map<K?, V>.filterKeyNotNull(): Map<K, V> {
    val destination = mutableMapOf<K, V>()
    for ((first, second) in this) if (first != null) destination[first] = second
    return destination
}

fun <K, V> Iterable<Map<K, V>>.flatten(): Map<K, List<V>> {
    val destination = mutableMapOf<K, MutableList<V>>()
    for (map in this) {
        for ((key, value) in map) {
            val list = destination.getOrPut(key) { mutableListOf() }
            list += value
        }
    }
    return destination
}

inline fun <T, K, V> Iterable<T>.flatMapToMap(transform: (T) -> Map<K, V>) =
    map(transform).flatten()

inline fun <T, K, V> Iterable<T>.mapToMap(transform: (T) -> Pair<K, V>) =
    map(transform).toMap()

inline fun <K1, V1, K2, V2> Map<K1, V1>.mapEntries(transform: (Map.Entry<K1, V1>) -> Pair<K1, V1>) =
    entries.map(transform).toMap()

fun <K, T> Map<K, Iterable<T>>.countValues() =
    mapValues { it.value.count() }

inline fun <K, T> Map<K, Iterable<T>>.countValues(predicate: (T) -> Boolean) =
    mapValues { it.value.count(predicate) }

fun <T> MutableCollection<T>.removeRandom(random: Random) =
    random(random).also { remove(it) }

fun <T> MutableList<T>.removeRandom(random: Random): T {
    if (isEmpty()) throw NoSuchElementException("List is empty.")
    return removeAt(random.nextInt(size))
}

/**
 * Balance the size of two collections incrementally,
 * by duplicating elements in the smaller, and removing elements from the larger collection.
 *
 * @return Whether the size equals the other collection's size, or not.
 */
fun <T> Pair<MutableCollection<T>, MutableCollection<T>>.balance(
    maxChange: Int = -1,
    drawFrom: Pair<Collection<T>, Collection<T>> = this,
    random: Random = Random
) = first.balanceWith(second, maxChange, drawFrom.first, drawFrom.second, random)

/**
 * Balance the size of this collection with the other [other] incrementally,
 * by duplicating elements in the smaller, and removing elements from the larger collection.
 *
 * @return Whether the size equals the other collection's size, or not.
 */
fun <T> MutableCollection<T>.balanceWith(
    other: MutableCollection<T>,
    maxChange: Int = -1,
    drawFrom: Collection<T> = this,
    drawFromOther: Collection<T> = other,
    random: Random = Random
): Boolean {

    require(maxChange.isEven) {
        "Argument maxChange must be divisible by 2, as there would always be two elements changed."
    }

    require(isNotEmpty() && other.isNotEmpty()) {
        "Can't balance empty collections."
    }

    // Break if [maxChange] < 2 (and not negative).
    if (maxChange in 0 until 2) return size == other.size

    when {
        size < other.size -> {
            add(drawFrom.random())
            other.removeRandom(random)
        }
        else -> {
            other.add(drawFromOther.random())
            removeRandom(random)
        }
    }

    // Balance recursively, until we changed at least [maxChange] elements.
    // If [maxChange] is negative, continue until fully balanced.
    return balanceWith(other, maxChange - 1)
}

fun <T> Sequence<T>.append(element: T) = this + element

@JvmName("appendAll")
fun <T> Sequence<T>.append(vararg elements: T) = this + elements

fun <T> Sequence<T>.append(elements: Array<out T>) = this + elements

fun <T> Sequence<T>.append(elements: Iterable<T>) = this + elements

fun <T> Sequence<T>.append(elements: Sequence<T>) = this + elements

fun <T> Sequence<T>.prepend(element: T) = sequenceOf(element) + this

@JvmName("prependAll")
fun <T> Sequence<T>.prepend(vararg elements: T) = elements.asSequence() + this

fun <T> Sequence<T>.prepend(elements: Array<out T>) = elements.asSequence() + this

fun <T> Sequence<T>.prepend(elements: Iterable<T>) = elements.asSequence() + this

fun <T> Sequence<T>.prepend(elements: Sequence<T>) = elements.asSequence() + this
