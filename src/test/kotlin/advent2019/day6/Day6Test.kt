package advent2019.day6

import io.kotlintest.shouldBe
import io.kotlintest.specs.FreeSpec
import io.kotlintest.data.forall
import io.kotlintest.tables.row

class Day6Test : FreeSpec(
    {
        "Part1" - {
            "Example" {
                val input = """
                    COM)B
                    B)C
                    C)D
                    D)E
                    E)F
                    B)G
                    G)H
                    D)I
                    E)J
                    J)K
                    K)L""".trimIndent().trim().split("\n")
                countAllAncestors(inputToMap(input)) shouldBe 42
            }
        }
        "Part2" - {
            "Examples" {
                val input = """
                    COM)B
                    B)C
                    C)D
                    D)E
                    E)F
                    B)G
                    G)H
                    D)I
                    E)J
                    J)K
                    K)L
                    K)YOU
                    I)SAN""".trimIndent().trim().split("\n")
                calculateShortestPath(inputToMap(input), "YOU", "SAN") shouldBe 4
            }
        }
    })
