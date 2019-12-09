package advent2019.IntCode

import kotlin.math.floor

val MEM_SIZE = 1024

fun inputStringToList(inputStr: String) = inputStr.split(",").map { it.toLong() }

fun runComputer(programRaw: String, inputs: List<Long> = listOf(), debug: Boolean = false): List<Long> {
    val program = inputStringToList(programRaw)
    return runComputer(program, inputs, debug)
}

fun runComputer(program: List<Long>, inputs: List<Long> = listOf(), debug: Boolean = false): List<Long> {
    // Pad memory out to more than just the program
    val memPad = (0 until MEM_SIZE - program.count()).map { 0L }
    val comp = State(memory = program.plus(memPad), inputs = inputs, debug = debug)
    val result = comp.run()
    return result.outputs
}

data class State(
    val memory: List<Long>,
    val currentPos: Int = 0,
    val status: String = "running",
    val currentRelBase: Int = 0,
    val inputs: List<Long> = listOf(),
    val inputPtr: Int = 0,
    val outputs: List<Long> = listOf(),
    val debug: Boolean = false
) {
    fun isDone() = currentPos >= memory.count() || memory[currentPos] == 99L

    fun currentInstruction() = memory[currentPos].toInt()

    fun currentOpCode() = _getOpcodeFromInstruction(currentInstruction())

    fun valueAt(position: Int) = memory[position]

    fun valueAt(position: Long) = memory[position.toInt()]

    fun run(): State =
        generateSequence(this) {
            it.runNext().takeIf { x -> x.status != "halted" }
        }.toList().last()

    fun runNext(): State =
        if (isDone()) {
            if (debug) println("halting")
            State(memory, currentPos, "halted")
        } else {
            if (debug) println(this)
            applyOpcode(this)
        }

    fun copyWithNewValueAt(position: Int, newValue: Long) = this.copy(
        memory = memory.mapIndexed { index, x -> if (index == position) newValue else x },
        currentPos = currentPos + opcodeLength(currentOpCode())
    )

    fun memoryAsString() = memory.joinToString(",") { it.toString() }

    fun getInParam(paramOrd: Int): Long {
        val mode = _getParamModes(currentInstruction())[paramOrd - 1]
        return when (mode) {
            0    -> valueAt(valueAt(currentPos + paramOrd))
            1    -> valueAt(currentPos + paramOrd)
            2    -> valueAt(currentRelBase + valueAt(currentPos + paramOrd))
            else -> throw Exception("(╯°□°)╯︵ ┻━┻ parameter mode $mode is unknown")
        }
    }

    fun getOutPos(paramOrd: Int): Int {
        val mode = _getParamModes(currentInstruction())[paramOrd - 1]
        return when (mode) {
            0    -> valueAt(currentPos + paramOrd).toInt()
            else -> throw Exception("(╯°□°)╯︵ ┻━┻ parameter mode $mode for result param is unexpected")
        }
    }
}

fun applyOpcode(state: State) = when (state.currentOpCode()) {
    1    -> applyAdd(state)
    2    -> applyMultiply(state)
    3    -> applyInput(state)
    4    -> applyOutput(state)
    5    -> applyJumpIfTrue(state)
    6    -> applyJumpIfFalse(state)
    7    -> applyLessThan(state)
    8    -> applyEqual(state)
    9    -> applyChangeRelBase(state)
    else -> throw Exception("(╯°□°)╯︵ ┻━┻ opcode ${state.currentOpCode()} is unknown")
}

fun opcodeLength(opcode: Int): Int = when (opcode) {
    1    -> 4
    2    -> 4
    3    -> 2
    4    -> 2
    5    -> 3
    6    -> 3
    7    -> 4
    8    -> 4
    9    -> 2
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
    if (state.debug) println("[${state.currentPos}, ${state.currentRelBase}] adding $x to $y, result to &$outaddr")
    return state.copyWithNewValueAt(position = outaddr, newValue = x + y)
}

fun applyMultiply(state: State): State {
    val x = state.getInParam(1)
    val y = state.getInParam(2)
    val outaddr = state.getOutPos(3)
    if (state.debug) println("[${state.currentPos}, ${state.currentRelBase}] multiplying $x to $y, result to &$outaddr")
    return state.copyWithNewValueAt(position = outaddr, newValue = x * y)
}

fun applyInput(state: State): State {
    val outaddr = state.getOutPos(1)
    val inputVal = state.inputs[state.inputPtr]
    if (state.debug) println("[${state.currentPos}, ${state.currentRelBase}] inputing $inputVal result to &$outaddr")
    return state
        .copyWithNewValueAt(position = outaddr, newValue = inputVal)
        .copy(inputPtr = state.inputPtr + 1)
}

fun applyOutput(state: State): State {
    val outval = state.getInParam(1)
    if (state.debug) println("[${state.currentPos}, ${state.currentRelBase}] outputing $outval")
    return state.copy(
        outputs = state.outputs.plus(outval),
        currentPos = state.currentPos + opcodeLength(state.currentOpCode())
    )
}

fun applyJumpIfTrue(state: State): State {
    val checkVal = state.getInParam(1)
    if (state.debug) println("[${state.currentPos}, ${state.currentRelBase}] jump-if-true $checkVal")
    return if (checkVal != 0L) applyJump(state) else advancePastJump(state)
}

fun applyJumpIfFalse(state: State): State {
    val checkVal = state.getInParam(1)
    if (state.debug) println("[${state.currentPos}, ${state.currentRelBase}] jump-if-false $checkVal")
    return if (checkVal == 0L) applyJump(state) else advancePastJump(state)
}

fun advancePastJump(state: State): State {
    if (state.debug) println("[${state.currentPos}, ${state.currentRelBase}] jump-check failed")
    return state.copy(currentPos = state.currentPos + opcodeLength(state.currentOpCode()))
}

fun applyJump(state: State): State {
    val jumpTarget = state.getInParam(2).toInt()
    if (state.debug) println("[${state.currentPos}, ${state.currentRelBase}] jumping to &$jumpTarget")
    return state.copy(currentPos = jumpTarget)
}

fun applyLessThan(state: State): State {
    val x = state.getInParam(1)
    val y = state.getInParam(2)
    val outaddr = state.getOutPos(3)
    val result = if (x < y) 1L else 0L
    if (state.debug) println("[${state.currentPos}, ${state.currentRelBase}] lessthan-check result $result, storing result to &$outaddr")
    return state.copyWithNewValueAt(position = outaddr, newValue = result)
}

fun applyEqual(state: State): State {
    val x = state.getInParam(1)
    val y = state.getInParam(2)
    val outaddr = state.getOutPos(3)
    val result = if (x == y) 1L else 0L
    if (state.debug) println("[${state.currentPos}, ${state.currentRelBase}] equality-check result $result, storing result to &$outaddr")
    return state.copyWithNewValueAt(position = outaddr, newValue = result)
}

fun applyChangeRelBase(state: State): State {
    val a = state.getInParam(1)
    if (state.debug) println("[${state.currentPos}, ${state.currentRelBase}] changing relative base by $a")
    return state.copy(
        currentRelBase = state.currentRelBase + a.toInt(),
        currentPos = state.currentPos + opcodeLength(state.currentOpCode())
    )
}
