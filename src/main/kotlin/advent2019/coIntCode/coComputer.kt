package advent2019.coIntCode

import advent2019.util.toCharStringList
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.channels.produce

val MEM_SIZE = 2048

fun inputStringToList(inputStr: String) = inputStr.split(",").map { it.toLong() }

fun runComputer(programRaw: String, inputs: List<Long> = listOf(), debug: Boolean = false): List<Long> {
    val program = inputStringToList(programRaw)
    return runComputer(program, inputs, debug)
}

/**
 * For use from code that doesn't want/need to care about coroutines and channels
 */
fun runComputer(program: List<Long>, inputs: List<Long> = listOf(), debug: Boolean = false): List<Long> {
    // Pad memory out to more than just the program
    val memPad = (0 until MEM_SIZE - program.count()).map { 0L }
    if (program.count() > MEM_SIZE) throw Exception("program too big for current computer memory")

    val outputChannel = Channel<Long>()
    val comp = CoICComp(
        memory = program.plus(memPad),
        inputChannel = produceInputs(inputs),
        outputChannel = outputChannel,
        debug = debug
    )

    var outputs = listOf<Long>()
    runBlocking {
        val result = run(comp)
        outputs = result.outputChannel.toList()
    }
    return outputs
}

fun produceInputs(inputs: List<Long>) = produce { inputs.forEach {send(it)} }

suspend fun run(startingState: CoICComp): CoICComp {
    var state = startingState
    while (state.status != "halted") {
        state = state.runNext()
    }
    return state
}

data class CoICComp(
    val memory: List<Long>,
    val currentPos: Int = 0,
    val status: String = "running",
    val currentRelBase: Int = 0,
    val inputChannel: Channel<Long>,
    val outputChannel: Channel<Long>,
    val debug: Boolean = false
) {

    suspend fun currentInstruction() = memory[currentPos].toInt()

    suspend fun currentOpCode() = currentInstruction() - (100 * (currentInstruction() / 100))

    suspend fun valueAt(position: Int) = memory[position]

    suspend fun valueAt(position: Long) = memory[position.toInt()]

    suspend fun runNext(): CoICComp {
        if (debug) println(this)
        return applyOpcode()
    }

    suspend fun copyWithNewValueAt(position: Int, newValue: Long) = this.copy(
        memory = memory.mapIndexed { index, x -> if (index == position) newValue else x },
        currentPos = currentPos + opcodeLength()
    )

    suspend fun memoryAsString() = memory.joinToString(",") { it.toString() }

    suspend fun getInParam(paramOrd: Int): Long {
        val mode = getParamModes()[paramOrd - 1]
        return when (mode) {
            0    -> valueAt(valueAt(currentPos + paramOrd))
            1    -> valueAt(currentPos + paramOrd)
            2    -> valueAt(currentRelBase + valueAt(currentPos + paramOrd))
            else -> throw Exception("(╯°□°)╯︵ ┻━┻ parameter mode $mode is unknown")
        }
    }

    suspend fun getOutPos(paramOrd: Int): Int {
        val mode = getParamModes()[paramOrd - 1]
        return when (mode) {
            0    -> valueAt(currentPos + paramOrd).toInt()
            2    -> (currentRelBase + valueAt(currentPos + paramOrd)).toInt()
            else -> throw Exception("(╯°□°)╯︵ ┻━┻ parameter mode $mode for result param is unexpected")
        }
    }

    suspend fun getParamModes() =
        ("000" + currentInstruction() / 100)
            .reversed()
            .substring(0, 3)
            .toCharStringList()
            .map { it.toInt() }

    suspend fun applyOpcode() = when (currentOpCode()) {
        1    -> applyAdd()
        2    -> applyMultiply()
        3    -> applyInput()
        4    -> applyOutput()
        5    -> applyJumpIfTrue()
        6    -> applyJumpIfFalse()
        7    -> applyLessThan()
        8    -> applyEqual()
        9    -> applyChangeRelBase()
        99   -> applyHalt()
        else -> throw Exception("(╯°□°)╯︵ ┻━┻ opcode ${currentOpCode()} is unknown")
    }

    suspend fun opcodeLength(): Int = when (currentOpCode()) {
        1    -> 4
        2    -> 4
        3    -> 2
        4    -> 2
        5    -> 3
        6    -> 3
        7    -> 4
        8    -> 4
        9    -> 2
        99   -> 1
        else -> throw Exception("(╯°□°)╯︵ ┻━┻ opcode ${currentOpCode()} is unknown")
    }

    suspend fun applyAdd(): CoICComp {
        val x = getInParam(1)
        val y = getInParam(2)
        val outaddr = getOutPos(3)
        if (debug) println("[${currentPos}, ${currentRelBase}] adding $x to $y, result to &$outaddr")
        return copyWithNewValueAt(position = outaddr, newValue = x + y)
    }

    suspend fun applyMultiply(): CoICComp {
        val x = getInParam(1)
        val y = getInParam(2)
        val outaddr = getOutPos(3)
        if (debug) println("[${currentPos}, ${currentRelBase}] multiplying $x to $y, result to &$outaddr")
        return copyWithNewValueAt(position = outaddr, newValue = x * y)
    }

    suspend fun applyInput(): CoICComp {
        val outaddr = getOutPos(1)
        val inputVal = inputChannel.receive()
        if (debug) println("[${currentPos}, ${currentRelBase}] inputing $inputVal result to &$outaddr")
        return copyWithNewValueAt(position = outaddr, newValue = inputVal)
    }

    suspend fun applyOutput(): CoICComp {
        val outval = getInParam(1)
        if (debug) println("[${currentPos}, ${currentRelBase}] outputing $outval")
        outputChannel.send(outval)
        return copy(currentPos = currentPos + opcodeLength())
    }

    suspend fun applyJumpIfTrue(): CoICComp {
        val checkVal = getInParam(1)
        if (debug) println("[${currentPos}, ${currentRelBase}] jump-if-true $checkVal")
        return if (checkVal != 0L) applyJump() else advancePastJump()
    }

    suspend fun applyJumpIfFalse(): CoICComp {
        val checkVal = getInParam(1)
        if (debug) println("[${currentPos}, ${currentRelBase}] jump-if-false $checkVal")
        return if (checkVal == 0L) applyJump() else advancePastJump()
    }

    suspend fun advancePastJump(): CoICComp {
        if (debug) println("[${currentPos}, ${currentRelBase}] jump-check failed")
        return copy(currentPos = currentPos + opcodeLength())
    }

    suspend fun applyJump(): CoICComp {
        val jumpTarget = getInParam(2).toInt()
        if (debug) println("[${currentPos}, ${currentRelBase}] jumping to &$jumpTarget")
        return copy(currentPos = jumpTarget)
    }

    suspend fun applyLessThan(): CoICComp {
        val x = getInParam(1)
        val y = getInParam(2)
        val outaddr = getOutPos(3)
        val result = if (x < y) 1L else 0L
        if (debug) println("[${currentPos}, ${currentRelBase}] lessthan-check result $result, storing result to &$outaddr")
        return copyWithNewValueAt(position = outaddr, newValue = result)
    }

    suspend fun applyEqual(): CoICComp {
        val x = getInParam(1)
        val y = getInParam(2)
        val outaddr = getOutPos(3)
        val result = if (x == y) 1L else 0L
        if (debug) println("[${currentPos}, ${currentRelBase}] equality-check result $result, storing result to &$outaddr")
        return copyWithNewValueAt(position = outaddr, newValue = result)
    }

    suspend fun applyChangeRelBase(): CoICComp {
        val a = getInParam(1)
        if (debug) println("[${currentPos}, ${currentRelBase}] changing relative base by $a")
        return copy(
            currentRelBase = currentRelBase + a.toInt(),
            currentPos = currentPos + opcodeLength()
        )
    }

    suspend fun applyHalt(): CoICComp {
        if (debug) println("halting (99)")
        return copy(status = "halted")
    }
}
