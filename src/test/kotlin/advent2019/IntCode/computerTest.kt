package advent2019.IntCode

import io.kotlintest.data.forall
import io.kotlintest.shouldBe
import io.kotlintest.specs.FreeSpec
import io.kotlintest.tables.row

class IntCodeTest : FreeSpec(
    {
        "State.getInParam" - {
            val input = "1002,4,3,4,33"
            val state = State(inputStringToList(input))
            state.getInParam(1) shouldBe 33
            state.getInParam(2) shouldBe 3
        }
        "State.getOutPos" - {
            val input = "1002,4,3,4,33"
            val state = State(inputStringToList(input))
            state.getOutPos(3) shouldBe 4
        }
        "_getOpcodeFromInstruction" - {
            "extracts opcode" {
                forall(
                   row(1002, 2),
                   row(11002, 2),
                   row(11102, 2),
                   row(2, 2)
                ) { input, expected ->
                    _getOpcodeFromInstruction(input) shouldBe expected
                }
            }
        }
        "_getParamModes" - {
            "extracts param modes" {
                forall(
                    row(2, listOf(0,0,0)),
                    row(10002, listOf(0,0,1)),
                    row(1002, listOf(0,1,0)),
                    row(11002, listOf(0,1,1)),
                    row(102, listOf(1,0,0)),
                    row(10102, listOf(1,0,1)),
                    row(1102, listOf(1,1,0)),
                    row(11102, listOf(1,1,1))
                ) { input, expected ->
                    _getParamModes(input) shouldBe expected
                }
            }
        }
    })
