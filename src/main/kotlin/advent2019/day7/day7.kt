package advent2019.day7

import advent2019.coIntCode.Computer
import advent2019.coIntCode.Computer.Companion.runAsync
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() {
    // part1
    println(optimize(getProgram()))
}

fun optimize(program: String): Pair<List<Long>, Long> {
    val possibleSettings = listOf(0L, 1, 2, 3, 4)
    val possiblePermutations = permute(possibleSettings)
    return possiblePermutations.map { Pair(it, runAmpSeries(program, it)) }.maxBy { it.second } ?: Pair(listOf(9L), -1L)
}

fun runAmpSeries(program: String, settings: List<Long>): Long = runBlocking {
    // initializing and wiring
    val amp1 = Computer.init(program)
    val amp2 = Computer.init(program).copy(inputChannel = amp1.outputChannel)
    val amp3 = Computer.init(program).copy(inputChannel = amp2.outputChannel)
    val amp4 = Computer.init(program).copy(inputChannel = amp3.outputChannel)
    val amp5 = Computer.init(program).copy(inputChannel = amp4.outputChannel)
    // Setup the initial inputs
    launch {
        amp1.inputChannel.send(settings[0])
        amp1.inputChannel.send(0)
    }
    launch { amp2.inputChannel.send(settings[1]) }
    launch { amp3.inputChannel.send(settings[2]) }
    launch { amp4.inputChannel.send(settings[3]) }
    launch { amp5.inputChannel.send(settings[4]) }
    // Start the computers
    listOf(amp1, amp2, amp3, amp4).map { runAsync(it) }
    val amp5FinalState = runAsync(amp5).await()
    val amp5Output = amp5FinalState.outputs
    return@runBlocking amp5Output.first()
}

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