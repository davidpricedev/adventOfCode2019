package advent2019.day5

import advent2019.IntCode.applyOpcodes
import advent2019.IntCode.inputStringToList
import io.kotlintest.shouldBe
import io.kotlintest.specs.FreeSpec
import io.kotlintest.data.forall
import io.kotlintest.tables.row

class Day5Test : FreeSpec(
    {
        "Part1" - {
            "Handles Parameter Modes" {
                val input = "1002,4,3,4,33"
                applyOpcodes(inputStringToList(input)).memoryAsString() shouldBe "1002,4,3,4,99"
            }
        }
    })
