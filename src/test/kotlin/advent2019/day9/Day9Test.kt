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
                    row("109,1,204,-1,1001,100,1,100,1008,100,16,101,1006,101,0,99", listOf(109L,1,204,-1,1001,100,1,100,1008,100,16,101,1006,101,0,99)),
                    row("1102,34915192,34915192,7,4,7,99,0", listOf(1219070632396864L)),
                    row("104,1125899906842624,99", listOf(1125899906842624))
                ) { program, expected ->
                    runComputer(program) shouldBe expected
                }
            }
        }
//        "Part2" - {
//            "Examples" {
//                forall(
//                    row("R75,D30,R83,U83,L12,D49,R71,U7,L72", "U62,R66,U55,R34,D71,R55,D58,R83", 610),
//                    row("R98,U47,R26,D63,R33,U87,L62,D20,R33,U53,R51", "U98,R91,D20,R16,D67,R40,U7,R15,U6,R7", 410)
//                ) { a, b, expected ->
//                    calculateShortestWire(a.split(","), b.split(",")) shouldBe expected
//                }
//            }
//        }
    })
