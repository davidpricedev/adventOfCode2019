package advent2019.day8

import io.kotlintest.shouldBe
import io.kotlintest.specs.FreeSpec
import io.kotlintest.data.forall
import io.kotlintest.tables.row

class Day8Test : FreeSpec(
    {
        "Part1" - {
            "Examples" {
                val input = intStringToIntList("123456789012")
                val layers = parseImageToLayers(input, 3, 2)
                layers.count() shouldBe 2
                layers[0].count() shouldBe 6
                val result = checkSumOfMinZeroLayer(input, 3, 2)
                result shouldBe 0
            }
        }
    })
