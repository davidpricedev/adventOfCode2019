package advent2019.day14

import advent2019.util.pow
import kotlin.math.ceil

/**
 * Part 2 Notes:
 * naive option of starting fuel gueses at 1T/ore-for-1-fuel and incrementing up from there is waaaay to slow
 * I could look for a way to turn my logic inside out maybe?
 * Maybe there is an LCM/GCF solution in there somewhere? The more I think about it the more I'm certain there is
 *  and that that is probably the way I'm supposed to solve it.
 * The other option would be to narrow in on the answer by varying the increments too - instead of +1, start with +10^n
 *  and decrement n as we overshoot
 */
fun main() {
    // part1
    println(calculateOreNeeded(getInputReactions()))
    // part2
    println(optimizeForOre(getInputReactions()))
}

data class ChemState(
    val reactions: Map<String, Reaction>,
    val needs: Map<String, Long>,
    val has: Map<String, Long> = mapOf()
) {
    // Done when only ore is needed
    fun isDone() = needs.count() == 1 && needs.keys.first() == "ORE"
}

fun calculateOreNeeded(reactions: Map<String, Reaction>, fuelNeeded: Long = 1L, debug: Boolean = false): Long {
    val init = ChemState(reactions, needs = mapOf("FUEL" to fuelNeeded))
    return generateSequence(init) { state ->
        if (debug) println(state)
        calculateNext(state).takeIf { !state.isDone() }
    }.last().needs.values.first()
}

/**
 * Narrow in on the right answer by making big jumps and slowly scaling back the size of the jumps we are making
 */
fun optimizeForOre(reactions: Map<String, Reaction>, debug: Boolean = true): Long {
    val oreProvided = 1000000000000
    val oreFor1Fuel = calculateOreNeeded(reactions)
    val startingFuelGuess = oreProvided / oreFor1Fuel
    var guess = startingFuelGuess
    for (n in 8 downTo  0) {
        for (i in guess..Long.MAX_VALUE step 10L.pow(n)) {
            val result = calculateOreNeeded(reactions, i)
            if (debug) println("$i fuel requires $result ore")
            if (result < oreProvided) {
                // make sure guess is always the highest fuel value that hasn't exceeded ore provided
                guess = i
            } else if (n == 0) {
                // incrementing is down to 1 so we've found the highest fuel inside the ore limits
                return guess
            } else {
                // we've gone as far as we can with increment of 10^n, time to decrease n
                break
            }
        }
    }
    return -1
}

fun calculateNext(state: ChemState): ChemState {
    if (state.isDone()) return state

    val entry = state.needs.entries.first { it.key != "ORE" }
    val reaction = state.reactions[entry.key]
    val amountNeeded = entry.value - (state.has[entry.key] ?: 0)
    val reactionCount = reaction!!.reactionsNeededToProduce(amountNeeded)

    val newNeeds = reaction.newNeeds(reactionCount)
    val nextNeeds = combineMaps(state.needs.filterKeys { it != entry.key }, newNeeds)

    val hasAmt = reactionCount * reaction.output.amount - amountNeeded
    val nextHas = combineMaps(state.has.filterKeys { it != entry.key }, mapOf(entry.key to hasAmt))

    return state.copy(needs = nextNeeds, has = nextHas)
}

fun combineMaps(baseMap: Map<String, Long>, additional: Map<String, Long>): Map<String, Long> =
    (baseMap.asSequence() + additional.asSequence()).groupBy({ it.key }, { it.value }).mapValues { it.value.sum() }

fun parseInput(rawInput: String) =
    rawInput.trimIndent().trim().split("\n")
        .map { Reaction.parse(it) }
        .map { it.output.name to it }.toMap()

data class ChemAmt(val name: String, val amount: Long) {
    companion object {
        fun parse(chemAmtStr: String): ChemAmt {
            val info = chemAmtStr.split(" ")
            return ChemAmt(name = info.last(), amount = info.first().toLong())
        }
    }
}

data class Reaction(val inputs: List<ChemAmt>, val output: ChemAmt) {
    fun reactionsNeededToProduce(n: Long) = ceil(n.toDouble() / output.amount).toLong()

    fun newNeeds(n: Long) = inputs.map { it.name to it.amount * n }.toMap()

    companion object {
        fun parse(reactionStr: String): Reaction {
            val io = reactionStr.split(" => ")
            val inputs = io.first().split(", ")
            val output = io.last()
            return Reaction(
                inputs = inputs.map { ChemAmt.parse(it) },
                output = ChemAmt.parse(output)
            )
        }
    }
}

fun getInputReactions() = parseInput(
    """
        10 LSZLT, 29 XQJK => 4 BMRQJ
        22 HCKS => 1 GQKCZ
        1 HCKS, 8 WZWRV, 18 HVZR => 7 BGFR
        1 LSZLT, 1 WRKJ, 3 LJFP, 3 RNLPB, 1 NZGK, 3 LDSV, 5 RJDN, 8 HGFGC => 3 QZTXD
        1 BRSGQ, 1 XGLF, 1 ZHSK, 20 LSZLT, 16 WFCPT, 3 KTWV, 1 QRJC => 4 XPKX
        1 DCLR, 6 RNLPB => 5 HCKS
        1 HFHFV => 3 SHLMF
        2 LTMZQ, 21 FGCXN => 6 QKFKV
        3 BGFR => 7 WRKJ
        3 KHSB => 2 XQJL
        3 SHLMF => 2 LPLG
        12 SVHWT, 20 BXPSZ => 9 NBMF
        2 FGCXN, 32 DCSVN => 8 TBDWZ
        1 KHSB, 3 HGFGC => 6 WZWRV
        27 WFCPT, 4 KTWV, 14 BRSGQ, 1 MFNK, 1 WRKJ, 2 NZGK, 24 FBFLK => 5 TRLCK
        2 SVHWT => 3 QRJC
        1 MNVR, 1 FKBMW => 2 FGCXN
        4 GJXW => 9 JXFS
        3 XQJK => 5 WNJM
        1 WZVWZ, 1 XQJL => 9 SHKJV
        2 DCSVN => 4 HDVC
        2 GJXW => 2 RNLPB
        1 QKFKV, 1 PBRWB => 5 WTZQ
        14 QKFKV => 6 RDFTD
        166 ORE => 1 QDSXV
        2 DCSVN => 5 BXPSZ
        113 ORE => 6 LTMZQ
        13 MNVR => 7 RJDN
        2 NZGK, 9 XQJK, 18 WRKJ => 9 KTWV
        1 NZGK => 8 XQJK
        6 RZCGN, 6 HDVC, 1 DLKR => 9 DSLXW
        18 HVZR => 8 LJFP
        7 XQJL => 1 NPDS
        15 DLKR, 1 DSLXW, 26 MJFVP => 3 FBFLK
        125 ORE => 9 MNVR
        3 RJDN => 4 HFHFV
        1 TBDWZ, 1 DCLR => 2 HVZR
        2 SHKJV => 5 GJXW
        7 LTMZQ, 1 QDSXV, 1 FKBMW => 3 DCSVN
        9 LPLG, 11 JXFS => 3 BRSGQ
        5 JXFS, 1 ZHSK, 25 XGLF => 4 MFNK
        5 PBRWB => 2 SVHWT
        15 SHKJV => 5 XGLF
        1 XQJL, 2 NPDS => 4 DLKR
        39 JXFS => 5 KSHF
        6 GJXW, 1 FBFLK => 7 HGFGC
        3 JXFS => 1 LSZLT
        3 NBMF, 1 BMRQJ => 2 LDSV
        1 JXFS, 25 GJXW, 10 HGFGC => 4 NZGK
        8 QZTXD, 26 KSHF, 60 WNJM, 6 GJXW, 9 TRLCK, 20 XPKX, 21 FGCXN, 57 GQKCZ, 6 WRKJ => 1 FUEL
        4 SVHWT, 1 RZCGN => 3 ZHSK
        1 BXPSZ => 7 DCLR
        8 RDFTD, 1 SHKJV, 1 HFHFV => 6 MJFVP
        1 LTMZQ => 9 KHSB
        5 WTZQ, 4 HGFGC, 4 HCKS => 9 WFCPT
        184 ORE => 4 FKBMW
        4 XQJL => 3 WZVWZ
        12 QDSXV => 9 RZCGN
        1 FBFLK, 7 HVZR => 9 PBRWB
    """.trimIndent()
)
