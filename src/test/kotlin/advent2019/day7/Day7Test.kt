package advent2019.day7

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
                    optimize(program, ::runAmpSeries, part1Settings()) shouldBe expected
                }
            }
        }
        "Part2" - {
            "part2:example 1" {
                val program = "3,26,1001,26,-4,26,3,27,1002,27,2,27,1,27,26,27,4,27,1001,28,-1,28,1005,28,6,99,0,0,5"
                optimize(program, ::runAmpLoopSeries, part2Settings()).second shouldBe 139629729L
            }
            "part2:example 2" {
                val program = "3,52,1001,52,-5,52,3,53,1,52,56,54,1007,54,5,55,1005,55,26,1001,54,-5,54,1105,1,12,1,53,54,53,1008,54,0,55,1001,55,1,55,2,53,55,53,4,53,1001,56,-1,56,1005,56,6,99,0,0,0,0,10"
                optimize(program, ::runAmpLoopSeries, part2Settings()).second shouldBe 18216L
            }
        }
    })
