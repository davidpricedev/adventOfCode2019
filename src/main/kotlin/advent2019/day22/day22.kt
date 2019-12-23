package advent2019.day22

fun main() {
    // part1
    println(parseInputs(getInput()).applyOps(factoryDeck()).indexOf(2019))
}

/**
 * convenience function
 */
fun List<ShuffleOp>.applyOps(deck: List<Int>) = applyShuffleOps(this, 0, deck)

tailrec fun applyShuffleOps(ops: List<ShuffleOp>, i: Int, deck: List<Int>): List<Int> =
    if (i >= ops.count()) {
        deck
    } else {
        applyShuffleOps(ops, i + 1, ops[i].run(deck))
    }

sealed class ShuffleOp {
    object Reverse : ShuffleOp() {
        override fun run(deck: List<Int>) = deck.reversed()
    }

    data class Cut(val n: Int) : ShuffleOp() {
        /**
         * cut the deck at a position and flip the the order of the two stacks
         */
        override fun run(deck: List<Int>): List<Int> {
            val cutPoint = if (n > 0) n else deck.count() + n
            val cut1 = deck.subList(0, cutPoint)
            val cut2 = deck.subList(cutPoint, deck.count())
            return cut2.plus(cut1)
        }
    }

    data class RoundRobin(val n: Int) : ShuffleOp() {
        /**
         * assign input cards into output slots via round-robin algorithm step-size of n
         */
        override fun run(deck: List<Int>): List<Int> {
            val length = deck.count()
            val outList = MutableList<Int>(length) { -1 }
            var rrPos = 0
            for (i in 0 until length) {
                outList[rrPos] = deck[i]
                rrPos = (rrPos + n) % length
            }
            return outList
        }
    }

    open fun run(deck: List<Int>): List<Int> = throw NotImplementedError()
}

fun parseInputs(inputStr: String) = inputStr.split("\n").map { parseInputCmd(it)}

fun parseInputCmd(inputStr: String) =
    when {
        inputStr == "deal into new stack"          -> ShuffleOp.Reverse
        inputStr.startsWith("deal with increment") ->
            ShuffleOp.RoundRobin(inputStr.substring(20).toInt())
        inputStr.startsWith("cut")                 ->
            ShuffleOp.Cut(inputStr.substring(4).toInt())
        else                                       -> throw Exception("(╯°□°)╯︵ ┻━┻  unparseable input: $inputStr")
    }

fun factoryDeck() = (0..10_006).toList()

fun getInput() =
    """
        cut 9374
        deal with increment 48
        cut -2354
        deal with increment 12
        cut -7039
        deal with increment 14
        cut -2325
        deal with increment 40
        deal into new stack
        cut 4219
        deal with increment 15
        cut -3393
        deal with increment 48
        cut 1221
        deal with increment 66
        cut 1336
        deal with increment 53
        deal into new stack
        cut -5008
        deal into new stack
        deal with increment 34
        cut 8509
        deal with increment 24
        cut -1292
        deal into new stack
        cut 8404
        deal with increment 17
        cut -105
        deal with increment 51
        cut 2974
        deal with increment 5
        deal into new stack
        deal with increment 53
        cut 155
        deal with increment 31
        cut 2831
        deal with increment 61
        cut -4193
        deal into new stack
        cut 9942
        deal with increment 13
        cut -532
        deal with increment 41
        cut 2847
        deal into new stack
        cut -2609
        deal with increment 72
        cut 9098
        deal with increment 64
        deal into new stack
        cut 4292
        deal into new stack
        cut -4427
        deal with increment 24
        cut -4713
        deal into new stack
        cut 5898
        deal with increment 56
        cut -2515
        deal with increment 2
        cut -5502
        deal with increment 66
        cut 8414
        deal with increment 7
        deal into new stack
        deal with increment 35
        deal into new stack
        deal with increment 29
        cut -2176
        deal with increment 14
        cut 7773
        deal with increment 36
        cut 2903
        deal into new stack
        deal with increment 75
        cut 239
        deal with increment 45
        cut 5450
        deal with increment 10
        cut 6661
        deal with increment 64
        cut -6842
        deal with increment 40
        deal into new stack
        deal with increment 31
        deal into new stack
        deal with increment 46
        cut 6462
        deal into new stack
        cut -8752
        deal with increment 28
        deal into new stack
        deal with increment 43
        deal into new stack
        deal with increment 54
        cut 9645
        deal with increment 44
        cut 5342
        deal with increment 66
        cut 3785
    """.trimIndent().trim()
