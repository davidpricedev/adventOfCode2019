package advent2019.day4

import io.kotlintest.shouldBe
import io.kotlintest.specs.FreeSpec
import io.kotlintest.data.forall
import io.kotlintest.tables.row

class Day4Test : FreeSpec(
    {
        "Part1" - {
            "Examples" {
                forall(
                    row(111111, true),
                    row(223450, false),
                    row(123789, false)
                ) { input, expected ->
                    filterPart1(input) shouldBe expected
                }
            }
        }
        "Part2" - {
            "Examples" {
                forall(
                    row(112233, true),
                    row(123444, false),
                    row(111122, true),
                    row(112345, true)
                ) { input, expected ->
                    filterPart2(input) shouldBe expected
                }
            }
        }
    })
