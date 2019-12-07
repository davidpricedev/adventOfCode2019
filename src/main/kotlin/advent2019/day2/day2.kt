package advent2019.day2

fun main() {
    runPart1()
}

fun runPart1() {
    val resultState = applyOpcodes(get_real_input())
    val answer = resultState.memory[0]
    println(answer)
}

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

    fun currentValue() = memory[currentPos]

    fun valueAt(position: Int) = memory[position]

    fun runNext(): State =
        if (isDone()) {
            State(memory, currentPos, "halted")
        } else {
            applyOpcode(this)
        }

    fun copyWithNewValueAt(position: Int, newValue: Int) = State(
        memory = memory.mapIndexed { index, x -> if (index == position) newValue else x },
        currentPos = currentPos + opcodeLength(currentValue())
    )

    fun memoryAsString() = memory.joinToString(",") { it.toString() }
}

fun applyOpcode(state: State) = when (state.currentValue()) {
    1    -> applyAdd(state)
    2    -> applyMultiply(state)
    else -> state
}

fun opcodeLength(opcode: Int): Int = when (opcode) {
    1    -> 4
    2    -> 4
    else -> 1
}

fun applyAdd(state: State): State {
    val xaddr = state.valueAt(state.currentPos + 1)
    val yaddr = state.valueAt(state.currentPos + 2)
    val outaddr = state.valueAt(state.currentPos + 3)
    val x = state.valueAt(xaddr)
    val y = state.valueAt(yaddr)
    return state.copyWithNewValueAt(position = outaddr, newValue = x + y)
}

fun applyMultiply(state: State): State {
    val xaddr = state.valueAt(state.currentPos + 1)
    val yaddr = state.valueAt(state.currentPos + 2)
    val outaddr = state.valueAt(state.currentPos + 3)
    val x = state.valueAt(xaddr)
    val y = state.valueAt(yaddr)
    return state.copyWithNewValueAt(position = outaddr, newValue = x * y)
}

/**
 * Deal with the replacement insanity
 */
fun get_real_input() =
    inputStringToList(get_bogus_input()).mapIndexed { i, x ->
        if (i == 1) 12 else if (i == 2) 2 else x.toInt()
    }

fun inputStringToList(inputStr: String) = inputStr.split(",").map { it.toInt() }

fun get_bogus_input() =
    "1,0,0,3,1,1,2,3,1,3,4,3,1,5,0,3,2,13,1,19,1,6,19,23,2,23,6,27,1,5,27,31,1,10,31,35,2,6,35,39,1,39,13,43,1,43,9,47,2,47,10,51,1,5,51,55,1,55,10,59,2,59,6,63,2,6,63,67,1,5,67,71,2,9,71,75,1,75,6,79,1,6,79,83,2,83,9,87,2,87,13,91,1,10,91,95,1,95,13,99,2,13,99,103,1,103,10,107,2,107,10,111,1,111,9,115,1,115,2,119,1,9,119,0,99,2,0,14,0"
