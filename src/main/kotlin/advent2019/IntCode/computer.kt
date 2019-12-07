package advent2019.IntCode

import kotlin.math.floor

fun inputStringToList(inputStr: String) = inputStr.split(",").map { it.toInt() }

fun applyOpcodes(inputMem: List<Int>) =
    generateSequence(State(inputMem)) {
        it.runNext().takeIf { x -> x.status != "halted" }
    }.toList().last()

data class State(
    val memory: List<Int>,
    val currentPos: Int = 0,
    val status: String = "running"
) {
    fun isDone() = currentPos >= memory.count() || memory[currentPos] == 99

    fun currentInstruction() = memory[currentPos]

    fun currentOpCode() = _getOpcodeFromInstruction(currentInstruction())

    fun valueAt(position: Int) = memory[position]

    fun runNext(): State =
        if (isDone()) {
            State(memory, currentPos, "halted")
        } else {
            applyOpcode(this)
        }

    fun copyWithNewValueAt(position: Int, newValue: Int) = State(
        memory = memory.mapIndexed { index, x -> if (index == position) newValue else x },
        currentPos = currentPos + opcodeLength(currentOpCode())
    )

    fun memoryAsString() = memory.joinToString(",") { it.toString() }

    fun getInParam(paramOrd: Int): Int {
        val mode = _getParamModes(currentInstruction())[paramOrd - 1]
        return when(mode) {
            0 -> valueAt(valueAt(currentPos + paramOrd))
            1 -> valueAt(currentPos + paramOrd)
            else -> throw Exception("(╯°□°)╯︵ ┻━┻")
        }
    }

    fun getOutPos(paramOrd: Int): Int {
        val mode = _getParamModes(currentInstruction())[paramOrd - 1]
        return when(mode) {
            0 -> valueAt(currentPos + paramOrd)
            1 -> currentPos + paramOrd
            else -> throw Exception("(╯°□°)╯︵ ┻━┻")
        }
    }
}

fun applyOpcode(state: State) = when (state.currentOpCode()) {
    1    -> applyAdd(state)
    2    -> applyMultiply(state)
    else -> state
}

fun opcodeLength(opcode: Int): Int = when (opcode) {
    1    -> 4
    2    -> 4
    else -> 1
}

fun _getOpcodeFromInstruction(inst: Int): Int {
    val paramModes = floor(inst / 100.0)
    return inst - (100 * paramModes).toInt()
}

fun _getParamModes(inst: Int): List<Int> {
    val paddedStr = ("000" + floor(inst / 100.0).toInt().toString()).reversed().substring(0,3)
    return _splitToList(paddedStr).map { it.toInt() }
}

fun _splitToList(str: String): List<String> {
    val rawlist = str.split("")
    return rawlist.subList(1, rawlist.count() - 1)
}

fun applyAdd(state: State): State {
    val x = state.getInParam(1)
    val y = state.getInParam(2)
    val outaddr = state.getOutPos(3)
    return state.copyWithNewValueAt(position = outaddr, newValue = x + y)
}

fun applyMultiply(state: State): State {
    val x = state.getInParam(1)
    val y = state.getInParam(2)
    val outaddr = state.getOutPos(3)
    return state.copyWithNewValueAt(position = outaddr, newValue = x * y)
}
