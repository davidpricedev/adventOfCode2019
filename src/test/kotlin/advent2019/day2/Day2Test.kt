package advent2019.day2

import io.kotlintest.shouldBe
import io.kotlintest.specs.FreeSpec
import io.kotlintest.data.forall
import io.kotlintest.tables.row

class Day2Test : FreeSpec(
    {
        "Part1" - {
            "Examples" {
                forall(
                    row("1,0,0,0,99", "2,0,0,0,99"),
                    row("2,3,0,3,99", "2,3,0,6,99"),
                    row("2,4,4,5,99,0", "2,4,4,5,99,9801"),
                    row("1,1,1,4,99,5,6,0,99", "30,1,1,4,2,5,6,0,99")
                ) { input, expected ->
                    applyOpcodes(inputStringToList(input)).memoryAsString() shouldBe expected
                }
            }
        }
    })