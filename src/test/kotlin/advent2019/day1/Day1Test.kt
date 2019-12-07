package advent2019.day1

import io.kotlintest.shouldBe
import io.kotlintest.specs.FreeSpec
import io.kotlintest.data.forall
import io.kotlintest.tables.row

class Day1Test : FreeSpec(
    {
        "Part1" - {
            "Examples" {
                forall(
                    row(12.0, 2.0),
                    row(14.0, 2.0),
                    row(1969.0, 654.0),
                    row(100756.0, 33583.0)
                ) { input, expected ->
                    massToFuelNaive(input) shouldBe expected
                }
            }
        }
        "Part2" - {
            "Examples" {
                forall(
                    row(14.0, 2.0),
                    row(1969.0, 966.0),
                    row(100756.0, 50346.0)
                ) { input, expected ->
                    massToFuel(input) shouldBe expected
                }
            }
        }
    })
