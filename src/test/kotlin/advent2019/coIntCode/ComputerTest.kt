package advent2019.coIntCode

import io.kotlintest.shouldBe
import io.kotlintest.specs.FreeSpec
import io.kotlintest.data.forall
import io.kotlintest.tables.row

class ComputerTest : FreeSpec(
    {
        "Basic IO" - {
            "Echo Program to Output" {
                val program = inputStringToList("109,1,204,-1,1001,100,1,100,1008,100,16,101,1006,101,0,99")
                Computer.initAndRun(program) shouldBe program
            }

            "Compare input" {
                val program = inputStringToList(
                    "3,21,1008,21,8,20,1005,20,22,107,8,21,20,1006,20,31,1106,0,36,98,0,0,1002,21,125,20,4,20,1105,1,46,104,999,1105,1,46,1101,1000,1,20,4,20,1105,1,46,98,99")
                Computer.initAndRun(program, listOf(1L))[0] shouldBe 999L
                Computer.initAndRun(program, listOf(8L))[0] shouldBe 1000L
                Computer.initAndRun(program, listOf(12L))[0] shouldBe 1001L
            }
        }
    })
