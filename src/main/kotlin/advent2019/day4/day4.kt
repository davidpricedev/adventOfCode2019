package advent2019.day4

fun main() {
    runExamplesPart1()
    runRangePart1(264360, 746325)
    runExamplesPart2()
    runRangePart2(264360, 746325)
}

fun runRangePart1(start: Long, end: Long) =
    println((start..end).filter(::filterPart1).count())

fun runRangePart2(start: Long, end: Long) =
    println((start..end).filter(::filterPart2).count())

fun runExamplesPart1() {
    println("Examples Part1:")
    println(filterPart1(111111))
    println(filterPart1(223450))
    println(filterPart1(123789))
    println("END Examples")
}

fun runExamplesPart2() {
    println("Examples Part2:")
    println(filterPart2(112233))
    println(filterPart2(123444))
    println(filterPart2(111122))
    println(filterPart2(112345))
    println("END Examples")
}

fun filterPart1(number: Long): Boolean {
    val digits = number.toString().map { it.toInt() }
    val uniqueDigits = digits.toSet()
    val is6Digits = digits.count() == 6
    val hasDouble = uniqueDigits.count() < 6
    val isIncreasing = digits.mapIndexed { i, it -> i == 0 || it >= digits[i - 1] }.reduce { acc, it -> acc && it }
    return is6Digits && hasDouble && isIncreasing
}

fun filterPart2(number: Long): Boolean {
    val digits = number.toString().map { it.toInt() }
    return filterPart1(number) && hasDigitAppearingExactlyTwice(digits)
}

fun hasDigitAppearingExactlyTwice(digits: List<Int>) =
    digits.groupBy { it }.map { it.value.count() }.contains(2)
