package advent2019.util

import advent2019.day16.basePattern
import kotlin.math.pow

fun Long.pow(n: Int): Long = this.toDouble().pow(n).toLong()

fun String.toCharStringList() = this.toCharArray().map { it.toString() }

fun String.toPerCharIntList() = this.toCharArray().map { it.toString().toInt() }

fun <E> List<E>.repeat(n: Int) = (1..n).flatMap { this }

// Memoize borrowed from https://github.com/aballano/mnemonik

fun <A, R> ((A) -> R).memoize(
    initialCapacity: Int = 256
): (A) -> R = memoize(HashMap(initialCapacity))

fun <A, R> ((A) -> R).memoize(
    cache: MutableMap<A, R>
): (A) -> R = { a: A ->
    cache.getOrPut(a) { this(a) }
}

fun <A, B, R> ((A, B) -> R).memoize(
    initialCapacity: Int = 256
): (A, B) -> R = memoize(HashMap(initialCapacity))

fun <A, B, R> ((A, B) -> R).memoize(
    cache: MutableMap<Pair<A, B>, R>
): (A, B) -> R = { a: A, b: B ->
    cache.getOrPut(a to b) { this(a, b) }
}

fun <A, B, C, R> ((A, B, C) -> R).memoize(
    initialCapacity: Int = 256
): (A, B, C) -> R = memoize(HashMap(initialCapacity))

fun <A, B, C, R> ((A, B, C) -> R).memoize(
    cache: MutableMap<Triple<A, B, C>, R>
): (A, B, C) -> R = { a: A, b: B, c: C ->
    cache.getOrPut(Triple(a, b, c)) { this(a, b, c) }
}
