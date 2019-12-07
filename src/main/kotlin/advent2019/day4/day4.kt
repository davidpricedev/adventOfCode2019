package advent2019.day4

fun main() {
    runRangePart1(264360, 746325)
    runRangePart2(264360, 746325)
}

fun runRangePart1(start: Int, end: Int) =
    println((start..end).filter(::filterPart1).count())

fun runRangePart2(start: Int, end: Int) =
    println((start..end).filter(::filterPart2).count())

fun filterPart1(number: Int): Boolean {
    val digits = number.toString().map { it.toInt() }
    val uniqueDigits = digits.toSet()
    val is6Digits = digits.count() == 6
    val hasDouble = uniqueDigits.count() < 6
    val isIncreasing = digits.mapIndexed { i, it -> i == 0 || it >= digits[i - 1] }.reduce { acc, it -> acc && it }
    return is6Digits && hasDouble && isIncreasing
}

fun filterPart2(number: Int): Boolean {
    val digits = number.toString().map { it.toInt() }
    return filterPart1(number) && hasDigitAppearingExactlyTwice(digits)
}

fun hasDigitAppearingExactlyTwice(digits: List<Int>) =
    digits.groupBy { it }.map { it.value.count() }.contains(2)
