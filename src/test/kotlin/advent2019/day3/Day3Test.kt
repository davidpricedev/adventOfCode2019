package advent2019.day3

import io.kotlintest.shouldBe
import io.kotlintest.specs.FreeSpec
import io.kotlintest.data.forall
import io.kotlintest.tables.row

class Day3Test : FreeSpec(
    {
        "Part1" - {
            "Examples" {
                forall(
                    row("R8,U5,L5,D3", "U7,R6,D4,L4", 6),
                    row("R75,D30,R83,U83,L12,D49,R71,U7,L72", "U62,R66,U55,R34,D71,R55,D58,R83", 159),
                    row("R98,U47,R26,D63,R33,U87,L62,D20,R33,U53,R51", "U98,R91,D20,R16,D67,R40,U7,R15,U6,R7", 135)
                ) { a, b, expected ->
                    calculateClosestIntersection(a.split(","), b.split(",")) shouldBe expected
                }
            }
        }
        "Part2" - {
            "Examples" {
                forall(
                    row("R75,D30,R83,U83,L12,D49,R71,U7,L72", "U62,R66,U55,R34,D71,R55,D58,R83", 610),
                    row("R98,U47,R26,D63,R33,U87,L62,D20,R33,U53,R51", "U98,R91,D20,R16,D67,R40,U7,R15,U6,R7", 410)
                ) { a, b, expected ->
                    calculateShortestWire(a.split(","), b.split(",")) shouldBe expected
                }
            }
        }
    })
