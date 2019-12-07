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
    val status: String = "running",
    val inputs: List<Int> = listOf(),
    val inputPtr: Int = 0,
    val outputs: List<Int> = listOf(),
    val debug: Boolean = false
) {
    fun isDone() = currentPos >= memory.count() || memory[currentPos] == 99

    fun currentInstruction() = memory[currentPos]

    fun currentOpCode() = _getOpcodeFromInstruction(currentInstruction())

    fun valueAt(position: Int) = memory[position]

    fun run(): State =
        generateSequence(this) {
            it.runNext().takeIf { x -> x.status != "halted" }
        }.toList().last()

    fun runNext(): State =
        if (isDone()) {
            State(memory, currentPos, "halted")
        } else {
            if (debug) println(memory)
            applyOpcode(this)
        }

    fun copyWithNewValueAt(position: Int, newValue: Int) = this.copy(
        memory = memory.mapIndexed { index, x -> if (index == position) newValue else x },
        currentPos = currentPos + opcodeLength(currentOpCode())
    )

    fun memoryAsString() = memory.joinToString(",") { it.toString() }

    fun getInParam(paramOrd: Int): Int {
        val mode = _getParamModes(currentInstruction())[paramOrd - 1]
        return when (mode) {
            0    -> valueAt(valueAt(currentPos + paramOrd))
            1    -> valueAt(currentPos + paramOrd)
            else -> throw Exception("(╯°□°)╯︵ ┻━┻")
        }
    }

    fun getOutPos(paramOrd: Int): Int {
        val mode = _getParamModes(currentInstruction())[paramOrd - 1]
        return when (mode) {
            0    -> valueAt(currentPos + paramOrd)
            1    -> currentPos + paramOrd
            else -> throw Exception("(╯°□°)╯︵ ┻━┻")
        }
    }
}

fun applyOpcode(state: State) = when (state.currentOpCode()) {
    1    -> applyAdd(state)
    2    -> applyMultiply(state)
    3    -> applyInput(state)
    4    -> applyOutput(state)
    else -> state
}

fun opcodeLength(opcode: Int): Int = when (opcode) {
    1    -> 4
    2    -> 4
    3    -> 2
    4    -> 2
    else -> 1
}

fun _getOpcodeFromInstruction(inst: Int): Int {
    val paramModes = floor(inst / 100.0)
    return inst - (100 * paramModes).toInt()
}

fun _getParamModes(inst: Int): List<Int> {
    val paddedStr = ("000" + floor(inst / 100.0).toInt().toString()).reversed().substring(0, 3)
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
    if (state.debug) println("[${state.currentPos}] adding $x to $y, result to &$outaddr")
    return state.copyWithNewValueAt(position = outaddr, newValue = x + y)
}

fun applyMultiply(state: State): State {
    val x = state.getInParam(1)
    val y = state.getInParam(2)
    val outaddr = state.getOutPos(3)
    if (state.debug) println("[${state.currentPos}] multiplying $x to $y, result to &$outaddr")
    return state.copyWithNewValueAt(position = outaddr, newValue = x * y)
}

fun applyInput(state: State): State {
    val outaddr = state.getOutPos(1)
    val inputVal = state.inputs[state.inputPtr]
    if (state.debug) println("[${state.currentPos}] inputing $inputVal result to &$outaddr")
    return state
        .copyWithNewValueAt(position = outaddr, newValue = inputVal)
        .copy(inputPtr = state.inputPtr + 1)
}

fun applyOutput(state: State): State {
    val outval = state.getInParam(1)
    if (state.debug) println("[${state.currentPos}] outputing $outval")
    return state.copy(
        outputs = state.outputs.plus(outval),
        currentPos = state.currentPos + opcodeLength(state.currentOpCode())
    )
}
