package advent2019.IntCode

import advent2019.util.toCharStringList
import kotlin.math.floor

val MEM_SIZE = 2048

fun inputStringToList(inputStr: String) = inputStr.split(",").map { it.toLong() }

fun runComputer(programRaw: String, inputs: List<Long> = listOf(), debug: Boolean = false): List<Long> {
    val program = inputStringToList(programRaw)
    return runComputer(program, inputs, debug)
}

fun runComputer(program: List<Long>, inputs: List<Long> = listOf(), debug: Boolean = false): List<Long> {
    // Pad memory out to more than just the program
    val memPad = (0 until MEM_SIZE - program.count()).map { 0L }
    if (program.count() > MEM_SIZE) throw Exception("program too big for current computer memory")

    val comp = ICComp(memory = program.plus(memPad), inputs = inputs, debug = debug)
    val result = comp.run()
    return result.outputs
}

data class ICComp(
    val memory: List<Long>,
    val currentPos: Int = 0,
    val status: String = "running",
    val currentRelBase: Int = 0,
    val inputs: List<Long> = listOf(),
    val inputPtr: Int = 0,
    val outputs: List<Long> = listOf(),
    val debug: Boolean = false
) {
    fun currentInstruction() = memory[currentPos].toInt()

    fun currentOpCode() = currentInstruction() - (100 * (currentInstruction() / 100))

    fun valueAt(position: Int) = memory[position]

    fun valueAt(position: Long) = memory[position.toInt()]

    fun run(): ICComp =
        generateSequence(this) {
            it.runNext().takeIf { x -> x.status != "halted" }
        }.toList().last()

    fun runNext(): ICComp {
        if (debug) println(this)
        return applyOpcode()
    }

    fun copyWithNewValueAt(position: Int, newValue: Long) = this.copy(
        memory = memory.mapIndexed { index, x -> if (index == position) newValue else x },
        currentPos = currentPos + opcodeLength(currentOpCode())
    )

    fun memoryAsString() = memory.joinToString(",") { it.toString() }

    fun getInParam(paramOrd: Int): Long {
        val mode = getParamModes()[paramOrd - 1]
        return when (mode) {
            0    -> valueAt(valueAt(currentPos + paramOrd))
            1    -> valueAt(currentPos + paramOrd)
            2    -> valueAt(currentRelBase + valueAt(currentPos + paramOrd))
            else -> throw Exception("(╯°□°)╯︵ ┻━┻ parameter mode $mode is unknown")
        }
    }

    fun getOutPos(paramOrd: Int): Int {
        val mode = getParamModes()[paramOrd - 1]
        return when (mode) {
            0    -> valueAt(currentPos + paramOrd).toInt()
            2    -> (currentRelBase + valueAt(currentPos + paramOrd)).toInt()
            else -> throw Exception("(╯°□°)╯︵ ┻━┻ parameter mode $mode for result param is unexpected")
        }
    }

    fun applyOpcode() = when (currentOpCode()) {
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
        99   -> 1
        else -> throw Exception("(╯°□°)╯︵ ┻━┻ opcode $opcode is unknown")
    }

    fun getParamModes() =
        ("000" + currentInstruction() / 100)
            .reversed()
            .substring(0, 3)
            .toCharStringList()
            .map { it.toInt() }

    fun applyAdd(): ICComp {
        val x = getInParam(1)
        val y = getInParam(2)
        val outaddr = getOutPos(3)
        if (debug) println("[${currentPos}, ${currentRelBase}] adding $x to $y, result to &$outaddr")
        return copyWithNewValueAt(position = outaddr, newValue = x + y)
    }

    fun applyMultiply(): ICComp {
        val x = getInParam(1)
        val y = getInParam(2)
        val outaddr = getOutPos(3)
        if (debug) println("[${currentPos}, ${currentRelBase}] multiplying $x to $y, result to &$outaddr")
        return copyWithNewValueAt(position = outaddr, newValue = x * y)
    }

    fun applyInput(): ICComp {
        if (inputs.count() == 0 || inputPtr >= inputs.count())
            throw Exception("(╯°□°)╯︵ ┻━┻ trying to read input that doesn't exist")
        val outaddr = getOutPos(1)
        val inputVal = inputs[inputPtr]
        if (debug) println("[${currentPos}, ${currentRelBase}] inputing $inputVal result to &$outaddr")
        return copyWithNewValueAt(position = outaddr, newValue = inputVal)
            .copy(inputPtr = inputPtr + 1)
    }

    fun applyOutput(): ICComp {
        val outval = getInParam(1)
        if (debug) println("[${currentPos}, ${currentRelBase}] outputing $outval")
        return copy(
            outputs = outputs.plus(outval),
            currentPos = currentPos + opcodeLength(currentOpCode())
        )
    }

    fun applyJumpIfTrue(): ICComp {
        val checkVal = getInParam(1)
        if (debug) println("[${currentPos}, ${currentRelBase}] jump-if-true $checkVal")
        return if (checkVal != 0L) applyJump() else advancePastJump()
    }

    fun applyJumpIfFalse(): ICComp {
        val checkVal = getInParam(1)
        if (debug) println("[${currentPos}, ${currentRelBase}] jump-if-false $checkVal")
        return if (checkVal == 0L) applyJump() else advancePastJump()
    }

    fun advancePastJump(): ICComp {
        if (debug) println("[${currentPos}, ${currentRelBase}] jump-check failed")
        return copy(currentPos = currentPos + opcodeLength(currentOpCode()))
    }

    fun applyJump(): ICComp {
        val jumpTarget = getInParam(2).toInt()
        if (debug) println("[${currentPos}, ${currentRelBase}] jumping to &$jumpTarget")
        return copy(currentPos = jumpTarget)
    }

    fun applyLessThan(): ICComp {
        val x = getInParam(1)
        val y = getInParam(2)
        val outaddr = getOutPos(3)
        val result = if (x < y) 1L else 0L
        if (debug) println("[${currentPos}, ${currentRelBase}] lessthan-check result $result, storing result to &$outaddr")
        return copyWithNewValueAt(position = outaddr, newValue = result)
    }

    fun applyEqual(): ICComp {
        val x = getInParam(1)
        val y = getInParam(2)
        val outaddr = getOutPos(3)
        val result = if (x == y) 1L else 0L
        if (debug) println("[${currentPos}, ${currentRelBase}] equality-check result $result, storing result to &$outaddr")
        return copyWithNewValueAt(position = outaddr, newValue = result)
    }

    fun applyChangeRelBase(): ICComp {
        val a = getInParam(1)
        if (debug) println("[${currentPos}, ${currentRelBase}] changing relative base by $a")
        return copy(
            currentRelBase = currentRelBase + a.toInt(),
            currentPos = currentPos + opcodeLength(currentOpCode())
        )
    }

    fun applyHalt(): ICComp {
        if (debug) println("halting (99)")
        return copy(status = "halted")
    }
}
