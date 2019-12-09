package advent2019.day7

import advent2019.IntCode.inputStringToList
import io.kotlintest.shouldBe
import io.kotlintest.specs.FreeSpec
import io.kotlintest.data.forall
import io.kotlintest.tables.row

class Day7Test : FreeSpec(
    {
        "Part1" - {
            "amp series examples" {
                forall(
                    row("3,15,3,16,1002,16,10,16,1,16,15,15,4,15,99,0,0", listOf(4, 3, 2, 1, 0), 43210),
                    row(
                        "3,23,3,24,1002,24,10,24,1002,23,-1,23,101,5,23,23,1,24,23,23,4,23,99,0,0",
                        listOf(0, 1, 2, 3, 4),
                        54321
                    ),
                    row(
                        "3,31,3,32,1002,32,10,32,1001,31,-2,31,1007,31,0,33,1002,33,7,33,1,33,31,31,1,32,31,31,4,31,99,0,0,0",
                        listOf(1, 0, 4, 3, 2),
                        65210
                    )
                ) { program, settings, expected ->
                    runAmpSeries(program, settings.map {it.toLong()}) shouldBe expected.toLong()
                }
            }
            "optimization examples" {
                forall(
                    row("3,15,3,16,1002,16,10,16,1,16,15,15,4,15,99,0,0", Pair(listOf(4L, 3, 2, 1, 0), 43210L)),
                    row(
                        "3,23,3,24,1002,24,10,24,1002,23,-1,23,101,5,23,23,1,24,23,23,4,23,99,0,0",
                        Pair(listOf(0L, 1, 2, 3, 4), 54321L)
                    ),
                    row(
                        "3,31,3,32,1002,32,10,32,1001,31,-2,31,1007,31,0,33,1002,33,7,33,1,33,31,31,1,32,31,31,4,31,99,0,0,0",
                        Pair(listOf(1L, 0, 4, 3, 2), 65210L)
                    )
                ) { program, expected ->
                    optimize(program) shouldBe expected
                }
            }
        }
    })
