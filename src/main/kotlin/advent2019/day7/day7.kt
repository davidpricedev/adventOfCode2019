package advent2019.day7

import advent2019.IntCode.runComputer

fun main() {
    // part1
    println(optimize(getProgram()))
}

fun optimize(program: String): Pair<List<Long>, Long> {
    val possibleSettings = listOf(0L,1,2,3,4)
    val possiblePermutations = permute(possibleSettings)
    return possiblePermutations.map { Pair(it, runAmpSeries(program, it) )}.maxBy { it.second } ?: Pair(listOf(9L), -1L)
}

fun runAmpSeries(program: String, settings: List<Long>): Long {
    val amp1Signal = runAmp(program, 0, settings[0])
    val amp2Signal = runAmp(program, amp1Signal, settings[1])
    val amp3Signal = runAmp(program, amp2Signal, settings[2])
    val amp4Signal = runAmp(program, amp3Signal, settings[3])
    val amp5Signal = runAmp(program, amp4Signal, settings[4])
    return amp5Signal
}

fun runAmp(program: String, signal: Long, setting: Long) = runComputer(program, listOf(setting, signal)).last()

/**
 * Borrowed from https://code.sololearn.com/c24EP02YuQx3/#kt
 */
fun <T> permute(list: List<T>): List<List<T>> {
    if (list.size == 1) return listOf(list)
    val perms = mutableListOf<List<T>>()
    val sub = list[0]
    for (perm in permute(list.drop(1)))
        for (i in 0..perm.size) {
            val newPerm = perm.toMutableList()
            newPerm.add(i, sub)
            perms.add(newPerm)
        }
    return perms
}

fun getProgram() =
    "3,8,1001,8,10,8,105,1,0,0,21,42,67,84,109,126,207,288,369,450,99999,3,9,102,4,9,9,1001,9,4,9,102,2,9,9,101,2,9,9,4,9,99,3,9,1001,9,5,9,1002,9,5,9,1001,9,5,9,1002,9,5,9,101,5,9,9,4,9,99,3,9,101,5,9,9,1002,9,3,9,1001,9,2,9,4,9,99,3,9,1001,9,2,9,102,4,9,9,101,2,9,9,102,4,9,9,1001,9,2,9,4,9,99,3,9,102,2,9,9,101,5,9,9,1002,9,2,9,4,9,99,3,9,1002,9,2,9,4,9,3,9,1002,9,2,9,4,9,3,9,1002,9,2,9,4,9,3,9,101,2,9,9,4,9,3,9,101,2,9,9,4,9,3,9,1001,9,2,9,4,9,3,9,101,2,9,9,4,9,3,9,1001,9,2,9,4,9,3,9,1002,9,2,9,4,9,3,9,1001,9,1,9,4,9,99,3,9,1001,9,2,9,4,9,3,9,1002,9,2,9,4,9,3,9,1002,9,2,9,4,9,3,9,1001,9,2,9,4,9,3,9,102,2,9,9,4,9,3,9,102,2,9,9,4,9,3,9,1001,9,2,9,4,9,3,9,102,2,9,9,4,9,3,9,1002,9,2,9,4,9,3,9,102,2,9,9,4,9,99,3,9,102,2,9,9,4,9,3,9,1001,9,1,9,4,9,3,9,101,1,9,9,4,9,3,9,101,1,9,9,4,9,3,9,1002,9,2,9,4,9,3,9,102,2,9,9,4,9,3,9,101,2,9,9,4,9,3,9,101,1,9,9,4,9,3,9,101,1,9,9,4,9,3,9,101,2,9,9,4,9,99,3,9,1001,9,2,9,4,9,3,9,101,1,9,9,4,9,3,9,101,2,9,9,4,9,3,9,1001,9,1,9,4,9,3,9,1001,9,2,9,4,9,3,9,1001,9,1,9,4,9,3,9,101,1,9,9,4,9,3,9,102,2,9,9,4,9,3,9,102,2,9,9,4,9,3,9,101,1,9,9,4,9,99,3,9,102,2,9,9,4,9,3,9,1001,9,1,9,4,9,3,9,101,2,9,9,4,9,3,9,1002,9,2,9,4,9,3,9,102,2,9,9,4,9,3,9,1001,9,1,9,4,9,3,9,1002,9,2,9,4,9,3,9,101,1,9,9,4,9,3,9,102,2,9,9,4,9,3,9,1001,9,2,9,4,9,99"