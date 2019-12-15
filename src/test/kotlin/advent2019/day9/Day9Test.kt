package advent2019.day9

import advent2019.IntCode.runComputer
import io.kotlintest.shouldBe
import io.kotlintest.specs.FreeSpec
import io.kotlintest.data.forall
import io.kotlintest.tables.row

class Day9Test : FreeSpec(
    {
        "Part1" - {
            "Examples" {
                forall(
                    row("109,1,204,-1,1001,100,1,100,1008,100,16,101,1006,101,0,99", listOf(109L,1,204,-1,1001,100,1,100,1008,100,16,101,1006,101,0,99)),
                    row("1102,34915192,34915192,7,4,7,99,0", listOf(1219070632396864L)),
                    row("104,1125899906842624,99", listOf(1125899906842624))
                ) { program, expected ->
                    runComputer(program) shouldBe expected
                }
            }
            "Test cases borrowed from reddit" {
                // https://www.reddit.com/r/adventofcode/comments/e8aw9j/2019_day_9_part_1_how_to_fix_203_error/fac3294/?utm_source=share&utm_medium=web2x
                forall(
                    row(listOf(109L, -1, 4, 1, 99), listOf(-1L)),
                    row(listOf(109L, -1, 104, 1, 99), listOf(1L)),
                    row(listOf(109L, -1, 204, 1, 99), listOf(109L)),
                    row(listOf(109L, 1, 9, 2, 204, -6, 99), listOf(204L)),
                    row(listOf(109L, 1, 109, 9, 204, -6, 99), listOf(204L)),
                    row(listOf(109L, 1, 209, -1, 204, -106, 99), listOf(204L))
                ) { program, expected ->
                    runComputer(program) shouldBe expected
                }
            }
            "More Test cases borrowed from reddit" {
                // https://www.reddit.com/r/adventofcode/comments/e8aw9j/2019_day_9_part_1_how_to_fix_203_error/fac3294/?utm_source=share&utm_medium=web2x
                forall(
                    row(listOf(109L, 1, 3, 3, 204, 2, 99), listOf(1234L), listOf(1234L)),
                    row(listOf(109L, 1, 203, 2, 204, 2, 99), listOf(1234L), listOf(1234L))
                ) { program, input, expected ->
                runComputer(program, input, debug = true) shouldBe expected
            }
            }
        }
    })
