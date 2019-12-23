package advent2019.day16

import advent2019.util.repeat
import advent2019.util.toPerCharIntList
import io.kotlintest.data.forall
import io.kotlintest.shouldBe
import io.kotlintest.specs.FreeSpec
import io.kotlintest.tables.row
import kotlin.math.PI

class Day16Test : FreeSpec(
    {
        "Part1" - {
            "list repeat" {
                forall(
                    row(listOf(0), 3, listOf(0, 0, 0)),
                    row(listOf(0, 3), 4, listOf(0, 3, 0, 3, 0, 3, 0, 3))
                ) { inputList, n, expected ->
                    inputList.repeat(n) shouldBe expected
                }
            }
            "base pattern expansion" {
                basePatternN(3) shouldBe listOf(0, 0, 0, 1, 1, 1, 0, 0, 0, -1, -1, -1)
            }
            "absOnes" {
                absOnes(38) shouldBe 8
                absOnes(-17) shouldBe 7
            }
            "full pattern" {
                patternN(2, 6) shouldBe listOf(0,1,1,0,0,-1)
            }
            "calculate next value example 1 step 1" {
                val input = listOf(1,2,3,4,5,6,7,8)
                forall(
                    row(1, 4),
                    row(2, 8),
                    row(3, 2),
                    row(4, 2),
                    row(5, 6),
                    row(6, 1),
                    row(7, 5),
                    row(8, 8)
                ) { n, expected ->
                    calculateNextValue(n, input) shouldBe expected
                }
            }
            "calculate the next phase" {
                val input = listOf(1,2,3,4,5,6,7,8)
                val expected = listOf(4,8,2,2,6,1,5,8)
                calculateNextPhase(input) shouldBe expected
            }
            "calculate example phases" {
                forall(
                    row("12345678", "48226158"),
                    row("48226158", "34040438"),
                    row("34040438", "03415518"),
                    row("03415518", "01029498")
                ) { inputStr, expectedStr ->
                    calculateNextPhase(inputStr.toPerCharIntList()) shouldBe expectedStr.toPerCharIntList()
                }
            }
            "calculate 100th phases" {
                forall(
                    row("80871224585914546619083218645595", "24176176"),
                    row("19617804207202209144916044189917", "73745418"),
                    row("69317163492948606335995924319873", "52432133")
                ) { inputStr, expectedStr ->
                    first8Digits(calculateNthPhase(100, inputStr.toPerCharIntList())) shouldBe expectedStr
                }
            }
        }
    })
