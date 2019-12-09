package advent2019.day2

import advent2019.IntCode.State
import advent2019.IntCode.inputStringToList

fun main() {
    // part 1
    println(run(12, 2))
    // part 2
    println(findInputsForGivenOutput())
}

fun findInputsForGivenOutput(): Int {
    val output = 19690720
    for (x in 0..99) {
        for (y in 0..99) {
            val result = run(x, y)
            if (result == output) {
                println("$x, $y -> $result")
                return 100 * x + y
            }
        }
    }
    return -1
}

fun run(noun: Int, verb: Int) = State(get_real_program(noun, verb)).run().memory[0]

/**
 * Deal with the replacement insanity
 */
fun get_real_program(noun: Int, verb: Int) =
    inputStringToList(get_bogus_program()).mapIndexed { i, x ->
        if (i == 1) noun else if (i == 2) verb else x
    }

fun get_bogus_program() =
    "1,0,0,3,1,1,2,3,1,3,4,3,1,5,0,3,2,13,1,19,1,6,19,23,2,23,6,27,1,5,27,31,1,10,31,35,2,6,35,39,1,39,13,43,1,43,9,47,2,47,10,51,1,5,51,55,1,55,10,59,2,59,6,63,2,6,63,67,1,5,67,71,2,9,71,75,1,75,6,79,1,6,79,83,2,83,9,87,2,87,13,91,1,10,91,95,1,95,13,99,2,13,99,103,1,103,10,107,2,107,10,111,1,111,9,115,1,115,2,119,1,9,119,0,99,2,0,14,0"
