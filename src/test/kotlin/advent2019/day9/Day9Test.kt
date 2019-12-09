package advent2019.day9

import advent2019.IntCode.inputStringToList
import advent2019.IntCode.runComputer
import advent2019.day3.calculateClosestIntersection
import advent2019.day3.calculateShortestWire
import io.kotlintest.shouldBe
import io.kotlintest.specs.FreeSpec
import io.kotlintest.data.forall
import io.kotlintest.tables.row

class Day9Test : FreeSpec(
    {
        "Part1" - {
            "Examples" {
                forall(
                    row("109,1,204,-1,1001,100,1,100,1008,100,16,101,1006,101,0,99", listOf(109,1,204,-1,1001,100,1,100,1008,100,16,101,1006,101,0,99))
                    //row("1102,349I15192,34915192,7,4,7,99,0", listOf<Int>()),
                    //row("104,1125899906842624,99", listOf(125899906842624))
                ) { program, expected ->
                    runComputer(program) shouldBe expected
                }
            }
        }
    })
