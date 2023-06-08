package advent2019.day7

import advent2019.coIntCode.Computer
import advent2019.coIntCode.IOMessage
import advent2019.coIntCode.IntCodeCompHalted
import kotlinx.coroutines.*

fun main() {
    // part1
    println(optimize(getProgram(), ::runAmpSeries, part1Settings()))
    // part2
    println(optimize(getProgram(), ::runAmpLoopSeries, part2Settings()))
}

fun part1Settings() = listOf(0L, 1, 2, 3, 4)

fun part2Settings() = listOf(5L, 6, 7, 8, 9)

fun optimize(program: String, fn: (String, List<Long>) -> Long, possibleSettings: List<Long>): Pair<List<Long>, Long> {
    val possiblePermutations = permute(possibleSettings)
    return possiblePermutations
        .map { Pair(it, fn(program, it)) }
        .maxBy { it.second } ?: Pair(listOf(9L), -1L)
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
        amp1.inputChannel.send(IOMessage.Value(settings[0]))
        amp1.inputChannel.send(IOMessage.Value(0))
    }
    launch { amp2.inputChannel.send(IOMessage.Value(settings[1])) }
    launch { amp3.inputChannel.send(IOMessage.Value(settings[2])) }
    launch { amp4.inputChannel.send(IOMessage.Value(settings[3])) }
    launch { amp5.inputChannel.send(IOMessage.Value(settings[4])) }

    // Start the computers
    listOf(amp1, amp2, amp3, amp4).map { async { Computer.run(it) } }
    val amp5FinalState = Computer.run(amp5)
    val amp5Output = amp5FinalState.outputsRecord
    return@runBlocking amp5Output.first()
}

fun runAmpLoopSeries(program: String, settings: List<Long>): Long = runBlocking {
    // initializing and wiring
    val amp1 = Computer.init(program)
    val amp2 = Computer.init(program).copy(inputChannel = amp1.outputChannel)
    val amp3 = Computer.init(program).copy(inputChannel = amp2.outputChannel)
    val amp4 = Computer.init(program).copy(inputChannel = amp3.outputChannel)
    val amp5 = Computer.init(program)
            .copy(inputChannel = amp4.outputChannel, outputChannel = amp1.inputChannel)

    // Setup the initial inputs
    val initialized = listOf(amp1, amp2, amp3, amp4, amp5).mapIndexed { i, x ->
        async { x.inputChannel.send(IOMessage.Value(settings[i])) }
    }
    val amp1Inited = async {
        initialized.first().await()
        amp1.inputChannel.send(IOMessage.Value(0))
    }

    // Start the computers - make sure to get all amp1 initial inputs in before starting amp5
    val amp1to4 = listOf(amp1, amp2, amp3, amp4).map { async { Computer.run(it) } }
    amp1Inited.await()
    launch { Computer.run(amp5) }

    // Wait for amp1-4 to finish since amp5 will block trying to send its last output
    amp1to4.awaitAll()

    // Get the last output out of amp5
    val amp5Result = async { amp5.outputChannel.receive() }
    return@runBlocking when (val rawResult = amp5Result.await()) {
        is IOMessage.Value -> rawResult.value
        is IOMessage.Halted -> throw IntCodeCompHalted()
    }
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