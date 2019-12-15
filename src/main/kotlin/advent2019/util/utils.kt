package advent2019.util

import kotlin.math.pow

fun Long.pow(n: Int): Long = this.toDouble().pow(n).toLong()

fun String.toCharStringList() = this.toCharArray().map { it.toString() }
