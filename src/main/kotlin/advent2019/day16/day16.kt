package advent2019.day16

import advent2019.util.memoize
import advent2019.util.toPerCharIntList
import kotlin.math.abs

fun main() {
    // part1
    println(first8Digits(calculateNthPhase(100, getInput())))
}

fun calculateNthPhase(n: Int, startingInput: List<Int>): List<Int> =
    generateSequence(Pair(startingInput, 1)) { (input, i) ->
        Pair(calculateNextPhase(input), i + 1).takeIf { i <= n }
    }.last().first

fun calculateNextPhase(input: List<Int>): List<Int> =
    input.mapIndexed { i, _ -> calculateNextValue(i + 1, input) }

fun calculateNextValue(n: Int, input: List<Int>): Int {
    val pattern = patternN(n, input.count())
    return absOnes(input.zip(pattern).map { (a, b) -> a * b }.sum())
}

val patternN = ::rawPatternN.memoize()
fun rawPatternN(n: Int, length: Int): List<Int> {
    val base = basePatternN(n)
    return base.repeat(1 + (length / base.count())).subList(1, length + 1)
}
}

fun first8Digits(input: List<Int>) = input.subList(0, 8).joinToString("")

fun absOnes(n: Int) = abs(n % 10)

fun basePatternN(n: Int) = basePattern().flatMap { b -> listOf(b).repeat(n) }

fun basePattern() = listOf(0, 1, 0, -1)

fun getInput() =
    "59718730609456731351293131043954182702121108074562978243742884161871544398977055503320958653307507508966449714414337735187580549358362555889812919496045724040642138706110661041990885362374435198119936583163910712480088609327792784217885605021161016819501165393890652993818130542242768441596060007838133531024988331598293657823801146846652173678159937295632636340994166521987674402071483406418370292035144241585262551324299766286455164775266890428904814988362921594953203336562273760946178800473700853809323954113201123479775212494228741821718730597221148998454224256326346654873824296052279974200167736410629219931381311353792034748731880630444730593"
        .toPerCharIntList()
