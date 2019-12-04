package advent2019.day4

import advent2019.day3.calculateClosestIntersection

/*
* Start with a filter function and see how far that goes
 */

fun main() {
    runExamplesPart1()
    runRange(264360, 746325)
}

fun runRange(start: Long, end: Long) =
    println((start..end).filter(::filterNumber).count())

fun runExamplesPart1() {
    println("Examples:")
    println(filterNumber(111111))
    println(filterNumber(223450))
    println(filterNumber(123789))
    println("END Examples")
}

fun filterNumber(number: Long): Boolean {
    val str = number.toString()
    val digits = str.map { it.toInt() }
    val uniqueDigits = digits.toSet()
    val is6Digits = digits.count() == 6
    val hasDouble = uniqueDigits.count() < 6
    val isIncreasing = digits.mapIndexed { i, it -> i == 0 || it >= digits[i - 1] }.reduce { acc, it -> acc && it }
    return is6Digits && hasDouble && isIncreasing
}