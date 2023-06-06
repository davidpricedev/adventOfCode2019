package advent2019.day12

import io.kotlintest.data.forall
import io.kotlintest.shouldBe
import io.kotlintest.specs.FreeSpec
import io.kotlintest.tables.row
import kotlin.math.PI

class Day12Test : FreeSpec(
    {
        "Part1" - {
            "One Dimension Velocity Change" {
                forall(
                    row(0, 0, listOf(1, 1, 5, 10), 4),
                    row(1, 1, listOf(1, 1, 5, 10), 3),
                    row(-1, -1, listOf(1, 1, 5, 10), 3),
                    row(0, 0, listOf(-1, 1, -5, 10), 0),
                    row(1, 1, listOf(-1, 1, -5, 10), 0),
                    row(-1, -1, listOf(-1, 1, -5, 10), 0)
                ) { pos, velocity, xs, expected ->
                    oneDimDeltaV(pos, velocity, xs) shouldBe expected
                }
            }
            "example 1" {
                val moonsStart = listOf(
                    Moon(Point(-1, 0, 2)),
                    Moon(Point(2, -10, -7)),
                    Moon(Point(4, -8, 8)),
                    Moon(Point(3, 5, -1))
                )
                val moonsTime1 = advanceTime(moonsStart)
                moonsTime1[0].position shouldBe Point(2, -1, 1)
                moonsTime1[0].velocity shouldBe Point(3, -1, -1)
                val moonsTime1_1 = advanceTime(moonsStart, 1)
                moonsTime1_1[0].position shouldBe Point(2, -1, 1)
                moonsTime1_1[0].velocity shouldBe Point(3, -1, -1)
                val moonsTime2 = advanceTime(moonsStart, 2)
                moonsTime2[0].position shouldBe Point(5, -3, -1)
                moonsTime2[0].velocity shouldBe Point(3, -2, -2)
                val moonsTime10 = advanceTime(moonsStart, 10)
                moonsTime10[0].position shouldBe Point(2, 1, -3)
                moonsTime10[0].velocity shouldBe Point(-3, -2, 1)
                totalEnergy(moonsTime10) shouldBe 179
            }
            "example 2" {
                val moonsStart = listOf(
                    Moon(Point(x = -8, y = -10, z = 0)),
                    Moon(Point(x = 5, y = 5, z = 10)),
                    Moon(Point(x = 2, y = -7, z = 3)),
                    Moon(Point(x = 9, y = -8, z = -3))
                )
                val moonsTime20 = advanceTime(moonsStart, 20)
                moonsTime20[0].position shouldBe Point(x = -10, y = 3, z = -4)
                moonsTime20[0].velocity shouldBe Point(x = -5, y = 2, z = 0)
                val moonsTime100 = advanceTime(moonsStart, 100)
                moonsTime100[0].position shouldBe Point(8, -12, -9)
                moonsTime100[0].velocity shouldBe Point(-7, 3, 0)
                totalEnergy(moonsTime100) shouldBe 1940
            }
        }
        "Part2" - {
            "calculate periodicy - example 1" {
                val moonStart = listOf(
                        Moon(Point(-1, 0, 2)),
                        Moon(Point(2, -10, -7)),
                        Moon(Point(4, -8, 8)),
                        Moon(Point(3, 5, -1))
                )
                calculatePeriodicity(moonStart) shouldBe 2772L
            }
            "calculate periodicy - example 2" {
                val moonStart = listOf(
                        Moon(Point(-8, -10, 0)),
                        Moon(Point(5, 5, 10)),
                        Moon(Point(2, -7, 3)),
                        Moon(Point(9, -8, -3))
                )
                calculatePeriodicity(moonStart) shouldBe 4686774924L
            }
        }
    })