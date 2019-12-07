package advent2019.day5

import advent2019.IntCode.State
import advent2019.IntCode.applyOpcodes
import advent2019.IntCode.inputStringToList
import io.kotlintest.shouldBe
import io.kotlintest.specs.FreeSpec
import io.kotlintest.data.forall
import io.kotlintest.tables.row

class Day5Test : FreeSpec(
    {
        "Part1" - {
            "handles parameter modes" {
                val input = "1002,4,3,4,33"
                applyOpcodes(inputStringToList(input)).memoryAsString() shouldBe "1002,4,3,4,99"
            }
            "handles negative values" {
                val input = "1101,100,-1,4,0"
                applyOpcodes(inputStringToList(input)).memoryAsString() shouldBe "1101,100,-1,4,99"
            }
            "handles input and output" {
                val program = "3,0,4,0,99"
                val programList = inputStringToList(program)
                val comp = State(programList, inputs = listOf(10))
                val result = comp.run()
                result.memoryAsString() shouldBe "10,0,4,0,99"
                result.outputs[0] shouldBe 10
            }
        }
        "Part2" - {
            "Examples" {
                1 shouldBe 1
            }
        }
    })
