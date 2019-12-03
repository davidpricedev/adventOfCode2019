package advent2019.day2

fun main() {
    runExamplesPart1()
    println(applyOpcodes(get_input()))
}

fun runExamplesPart1() {
    println("Examples:")
    println(applyOpcodes(listOf(1, 0, 0, 0, 99)))
    println(applyOpcodes(listOf(2, 3, 0, 3, 99)))
    println(applyOpcodes(listOf(2, 4, 4, 5, 99, 0)))
    println(applyOpcodes(listOf(1, 1, 1, 4, 99, 5, 6, 0, 99)))
    println("END Examples")
}

fun applyOpcodes(inputMem: List<Int>): List<Int> {
    return applyOpcodesR(inputMem)
}

tailrec fun applyOpcodesR(inputMem: List<Int>, currentPos: Int = 0): List<Int> =
    if (currentPos >= inputMem.count()) {
        inputMem
    } else {
        val opcode = getPosValue(inputMem, currentPos)
        val nextPos = currentPos + opcodeLength(opcode)
        applyOpcodesR(applyOpcode(inputMem, opcode, currentPos), nextPos)
    }

fun applyOpcode(inputMem: List<Int>, opcode: Int, currentPos: Int) = when (opcode) {
    1    -> applyAdd(inputMem, currentPos)
    2    -> applyMultiply(inputMem, currentPos)
    else -> inputMem
}

fun opcodeLength(opcode: Int): Int = when (opcode) {
    1    -> 4
    2    -> 4
    else -> 1
}

fun applyAdd(inputMem: List<Int>, currentPos: Int): List<Int> {
    val xaddr = getPosValue(inputMem, currentPos + 1)
    val yaddr = getPosValue(inputMem, currentPos + 2)
    val outaddr = getPosValue(inputMem, currentPos + 3)
    val x = getPosValue(inputMem, xaddr)
    val y = getPosValue(inputMem, yaddr)
    val result = setPosValue(inputMem, outaddr, x + y)
    return result
}

fun applyMultiply(inputMem: List<Int>, currentPos: Int): List<Int> {
    val xaddr = getPosValue(inputMem, currentPos + 1)
    val yaddr = getPosValue(inputMem, currentPos + 2)
    val outaddr = getPosValue(inputMem, currentPos + 3)
    val x = getPosValue(inputMem, xaddr)
    val y = getPosValue(inputMem, yaddr)
    return setPosValue(inputMem, outaddr, x * y)
}

fun getPosValue(inputMem: List<Int>, position: Int) = inputMem[position]

fun setPosValue(inputMem: List<Int>, position: Int, newValue: Int) =
    inputMem.mapIndexed { index, x -> if (index == position) newValue else x }

fun get_input() = listOf(
    1,
    0,
    0,
    3,
    1,
    1,
    2,
    3,
    1,
    3,
    4,
    3,
    1,
    5,
    0,
    3,
    2,
    13,
    1,
    19,
    1,
    6,
    19,
    23,
    2,
    23,
    6,
    27,
    1,
    5,
    27,
    31,
    1,
    10,
    31,
    35,
    2,
    6,
    35,
    39,
    1,
    39,
    13,
    43,
    1,
    43,
    9,
    47,
    2,
    47,
    10,
    51,
    1,
    5,
    51,
    55,
    1,
    55,
    10,
    59,
    2,
    59,
    6,
    63,
    2,
    6,
    63,
    67,
    1,
    5,
    67,
    71,
    2,
    9,
    71,
    75,
    1,
    75,
    6,
    79,
    1,
    6,
    79,
    83,
    2,
    83,
    9,
    87,
    2,
    87,
    13,
    91,
    1,
    10,
    91,
    95,
    1,
    95,
    13,
    99,
    2,
    13,
    99,
    103,
    1,
    103,
    10,
    107,
    2,
    107,
    10,
    111,
    1,
    111,
    9,
    115,
    1,
    115,
    2,
    119,
    1,
    9,
    119,
    0,
    99,
    2,
    0,
    14,
    0
)
