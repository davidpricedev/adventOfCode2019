package advent2019.day22

import advent2019.util.repeat
import advent2019.util.toPerCharIntList
import io.kotlintest.data.forall
import io.kotlintest.shouldBe
import io.kotlintest.specs.FreeSpec
import io.kotlintest.tables.row
import kotlin.math.PI

class Day22Test : FreeSpec(
    {
        "Part1" - {
            "cut" {
                val input = listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
                forall(
                    row(3, listOf(3, 4, 5, 6, 7, 8, 9, 0, 1, 2)),
                    row(-4, listOf(6, 7, 8, 9, 0, 1, 2, 3, 4, 5))
                ) { cutPoint, expected ->
                    ShuffleOp.Cut(cutPoint).run(input) shouldBe expected
                }
            }
            "round robin" {
                val input = listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
                val expected = listOf(0, 7, 4, 1, 8, 5, 2, 9, 6, 3)
                ShuffleOp.RoundRobin(3).run(input) shouldBe expected
            }
            "example 1" {
                val startingDeck = listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
                val inputs = """
                    deal with increment 7
                    deal into new stack
                    deal into new stack 
                """.trimIndent().trim()
                val expected = listOf(0, 3, 6, 9, 2, 5, 8, 1, 4, 7)
                parseInputs(inputs).applyOps(startingDeck) shouldBe expected
            }
            "example 2" {
                val startingDeck = listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
                val inputs = """
                    cut 6
                    deal with increment 7
                    deal into new stack
                """.trimIndent().trim()
                val expected = listOf(3, 0, 7, 4, 1, 8, 5, 2, 9, 6)
                parseInputs(inputs).applyOps(startingDeck) shouldBe expected
            }
            "example 3" {
                val startingDeck = listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
                val inputs = """
                    deal with increment 7
                    deal with increment 9
                    cut -2
                """.trimIndent().trim()
                val expected = listOf(6, 3, 0, 7, 4, 1, 8, 5, 2, 9)
                parseInputs(inputs).applyOps(startingDeck) shouldBe expected
            }
            "example 4" {
                val startingDeck = listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
                val inputs = """
                    deal into new stack
                    cut -2
                    deal with increment 7
                    cut 8
                    cut -4
                    deal with increment 7
                    cut 3
                    deal with increment 9
                    deal with increment 3
                    cut -1
                """.trimIndent().trim()
                val expected = listOf(9, 2, 5, 8, 1, 4, 7, 0, 3, 6)
                parseInputs(inputs).applyOps(startingDeck) shouldBe expected
            }
        }
    })
