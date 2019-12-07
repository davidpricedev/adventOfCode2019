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
            "simple comparison examples" {
                forall(
                    // equals
                    row("3,9,8,9,10,9,4,9,99,-1,8", 8, 1),
                    row("3,9,8,9,10,9,4,9,99,-1,8", 1, 0),
                    row("3,3,1108,-1,8,3,4,3,99", 8, 1),
                    row("3,3,1108,-1,8,3,4,3,99", 1, 0),
                    // less-than
                    row("3,9,7,9,10,9,4,9,99,-1,8", 1, 1),
                    row("3,9,7,9,10,9,4,9,99,-1,8", 12, 0),
                    row("3,3,1107,-1,8,3,4,3,99", 1, 1),
                    row("3,3,1107,-1,8,3,4,3,99", 12, 0)
                ) { program, input, expected ->
                    val programList = inputStringToList(program)
                    run(programList, input) shouldBe expected
                }
            }
            "simple jump examples" {
                forall(
                    // jumptrue
                    row("3,11,5,11,8,4,0,99,4,7,99,0", 0, 3),
                    row("3,11,5,11,8,4,0,99,4,7,99,0", 1, 99),
                    row("1105,0,6,4,5,99,4,0,99", 1, 99),
                    row("1105,1,6,4,5,99,4,0,99", 1, 1105),
                    // jumpfalse
                    row("3,11,6,11,8,4,0,99,4,7,99,0", 0, 99),
                    row("3,11,6,11,8,4,0,99,4,7,99,0", 1, 3),
                    row("1106,0,6,4,5,99,4,0,99", 1, 1106),
                    row("1106,1,6,4,5,99,4,0,99", 1, 99)
                ) { program, input, expected ->
                    val programList = inputStringToList(program)
                    run(programList, input) shouldBe expected
                }
            }
            "longer example" {
                val program =
                    "3,21,1008,21,8,20,1005,20,22,107,8,21,20,1006,20,31,1106,0,36,98,0,0,1002,21,125,20,4,20,1105,1,46,104,999,1105,1,46,1101,1000,1,20,4,20,1105,1,46,98,99"
                forall(
                    row(1, 999),
                    row(5, 999),
                    row(8, 1000),
                    row(9, 1001),
                    row(1234, 1001)
                ) { input, expected ->
                    val programList = inputStringToList(program)
                    run(programList, input) shouldBe expected
                }
            }
        }
    })
