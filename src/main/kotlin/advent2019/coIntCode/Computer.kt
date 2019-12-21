package advent2019.coIntCode

import advent2019.util.toCharStringList
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*

val MEM_SIZE = 8192
val OUT_BUFFER = 200

fun inputStringToList(inputStr: String) = inputStr.split(",").map { it.toLong() }

/**
 * Borrowed from https://stackoverflow.com/a/54400933/567493
 */
fun getRandomString(length: Int): String {
    val allowedChars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz"
    return (1..length)
        .map { allowedChars.random() }
        .joinToString("")
}

enum class CompStatus {
    ready,
    running,
    halted,
}

data class Computer(
    val memory: List<Long>,
    val currentPos: Int = 0,
    val currentRelBase: Int = 0,
    val inputChannel: Channel<Long> = Channel(),
    val outputChannel: Channel<Long> = Channel(OUT_BUFFER),

    // Behaviour flags
    val ioDebug: Boolean = false,
    val debug: Boolean = false,
    val closeChannels: Boolean = false,

    // identifier for differentiating between computers
    val name: String = getRandomString(7),
    // Status
    val status: CompStatus = CompStatus.ready,
    // Historical Record
    val inputsRecord: List<Long> = listOf(),
    val outputsRecord: List<Long> = listOf()
) {

    fun currentInstruction() = memory[currentPos].toInt()

    fun currentOpCode() = currentInstruction() - (100 * (currentInstruction() / 100))

    fun valueAt(position: Int) = memory[position]

    fun valueAt(position: Long) = memory[position.toInt()]

    suspend fun runNext(): Computer {
        if (debug) println(this)
        return applyOpcode()
    }

    fun copyWithNewValueAt(position: Int, newValue: Long) = this.copy(
        memory = memory.mapIndexed { index, x -> if (index == position) newValue else x },
        currentPos = currentPos + opcodeLength()
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

    fun getParamModes() =
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

    fun opcodeLength(): Int = when (currentOpCode()) {
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

    fun applyAdd(): Computer {
        val x = getInParam(1)
        val y = getInParam(2)
        val outaddr = getOutPos(3)
        if (debug) println("$name: [${currentPos}, ${currentRelBase}] adding $x to $y, result to &$outaddr")
        return copyWithNewValueAt(position = outaddr, newValue = x + y)
    }

    fun applyMultiply(): Computer {
        val x = getInParam(1)
        val y = getInParam(2)
        val outaddr = getOutPos(3)
        if (debug) println("$name: [${currentPos}, ${currentRelBase}] multiplying $x to $y, result to &$outaddr")
        return copyWithNewValueAt(position = outaddr, newValue = x * y)
    }

    suspend fun applyInput(): Computer {
        val outaddr = getOutPos(1)
        if (debug or ioDebug) println("$name: [${currentPos}, ${currentRelBase}] waiting for input")
        val inputVal = inputChannel.receive()
        if (debug or ioDebug) println("$name: [${currentPos}, ${currentRelBase}] inputing $inputVal result to &$outaddr")
        return copyWithNewValueAt(position = outaddr, newValue = inputVal).copy(
            inputsRecord = inputsRecord.plus(
                inputVal
            )
        )
    }

    suspend fun applyOutput(): Computer {
        val outval = getInParam(1)
        if (debug or ioDebug) println("$name: [${currentPos}, ${currentRelBase}] outputing $outval")
        outputChannel.send(outval)
        if (debug or ioDebug) println("$name: [${currentPos}, ${currentRelBase}] pennding output complete, continuing")
        return copy(currentPos = currentPos + opcodeLength(), outputsRecord = outputsRecord.plus(outval))
    }

    fun applyJumpIfTrue(): Computer {
        val checkVal = getInParam(1)
        if (debug) println("$name: [${currentPos}, ${currentRelBase}] jump-if-true $checkVal")
        return if (checkVal != 0L) applyJump() else advancePastJump()
    }

    fun applyJumpIfFalse(): Computer {
        val checkVal = getInParam(1)
        if (debug) println("$name: [${currentPos}, ${currentRelBase}] jump-if-false $checkVal")
        return if (checkVal == 0L) applyJump() else advancePastJump()
    }

    fun advancePastJump(): Computer {
        if (debug) println("$name: [${currentPos}, ${currentRelBase}] jump-check failed")
        return copy(currentPos = currentPos + opcodeLength())
    }

    fun applyJump(): Computer {
        val jumpTarget = getInParam(2).toInt()
        if (debug) println("$name: [${currentPos}, ${currentRelBase}] jumping to &$jumpTarget")
        return copy(currentPos = jumpTarget)
    }

    fun applyLessThan(): Computer {
        val x = getInParam(1)
        val y = getInParam(2)
        val outaddr = getOutPos(3)
        val result = if (x < y) 1L else 0L
        if (debug) println("$name: [${currentPos}, ${currentRelBase}] lessthan-check result $result, storing result to &$outaddr")
        return copyWithNewValueAt(position = outaddr, newValue = result)
    }

    fun applyEqual(): Computer {
        val x = getInParam(1)
        val y = getInParam(2)
        val outaddr = getOutPos(3)
        val result = if (x == y) 1L else 0L
        if (debug) println("$name: [${currentPos}, ${currentRelBase}] equality-check result $result, storing result to &$outaddr")
        return copyWithNewValueAt(position = outaddr, newValue = result)
    }

    fun applyChangeRelBase(): Computer {
        val a = getInParam(1)
        if (debug) println("$name: [${currentPos}, ${currentRelBase}] changing relative base by $a")
        return copy(
            currentRelBase = currentRelBase + a.toInt(),
            currentPos = currentPos + opcodeLength()
        )
    }

    fun applyHalt(): Computer {
        if (debug or ioDebug) println("$name: [$currentPos, $currentRelBase] halting (99)")
        return copy(status = CompStatus.halted)
    }

    companion object {
        suspend fun initAndRun(
            program: List<Long>,
            inputs: List<Long> = listOf(),
            ioDebug: Boolean = false,
            debug: Boolean = false
        ): List<Long> =
            coroutineScope {
                val comp = init(program, ioDebug, debug)
                launch { inputs.forEach { comp.inputChannel.send(it) } }
                val result = run(comp)
                val outputs = result.outputsRecord
                return@coroutineScope outputs
            }

        fun init(program: String, ioDebug: Boolean = false, debug: Boolean = false): Computer =
            init(inputStringToList(program), ioDebug, debug)

        fun init(program: List<Long>, ioDebug: Boolean = false, debug: Boolean = false): Computer {
            // Pad memory out to more than just the program
            val memPad = (0 until MEM_SIZE - program.count()).map { 0L }
            if (program.count() > MEM_SIZE) throw Exception("program too big for current computer memory")
            return Computer(
                memory = program.plus(memPad),
                ioDebug = ioDebug,
                debug = debug
            )
        }

        suspend fun run(startingState: Computer): Computer = coroutineScope {
            var state = startingState.copy(status = CompStatus.running)
            for (i in 0..Int.MAX_VALUE) {
                if (state.debug) println("Running program loop $i")
                if (i % 100000 == 0 && i > 0) println("Running program loop $i")
                state = state.runNext()
                if (state.status == CompStatus.halted) return@coroutineScope state
            }
            return@coroutineScope state
        }
    }
}
