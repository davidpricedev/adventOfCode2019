package advent2019.day10

import io.kotlintest.shouldBe
import io.kotlintest.specs.FreeSpec
import kotlin.math.PI

class Day10Test : FreeSpec(
    {
        "Part1" - {
            "example 1" {
                val input = """
                    ......#.#.
                    #..#.#....
                    ..#######.
                    .#.#.###..
                    .#..#.....
                    ..#....#.#
                    #..#....#.
                    .##.#..###
                    ##...#..#.
                    .#....####
                """.trimIndent().trim().split("\n")
                val asteroids = inputToAsteroids(input)
                asteroids.count() shouldBe 40
                pickBestAsteroid(asteroids) shouldBe Pair(MaybeRoid.Asteroid(5, 8), 33)
            }
            "example 2" {
                val input = """
                    #.#...#.#.
                    .###....#.
                    .#....#...
                    ##.#.#.#.#
                    ....#.#.#.
                    .##..###.#
                    ..#...##..
                    ..##....##
                    ......#...
                    .####.###.
                """.trimIndent().trim().split("\n")
                val asteroids = inputToAsteroids(input)
                asteroids.count() shouldBe 40
                pickBestAsteroid(asteroids) shouldBe Pair(MaybeRoid.Asteroid(1, 2), 35)
            }
            "example 3" {
                val input = """
                    .#..#..###
                    ####.###.#
                    ....###.#.
                    ..###.##.#
                    ##.##.#.#.
                    ....###..#
                    ..#.#..#.#
                    #..#.#.###
                    .##...##.#
                    .....#.#..
                """.trimIndent().trim().split("\n")
                val asteroids = inputToAsteroids(input)
                asteroids.count() shouldBe 50
                pickBestAsteroid(asteroids) shouldBe Pair(MaybeRoid.Asteroid(6, 3), 41)
            }
            "example 4" {
                val input = """
                    .#..##.###...#######
                    ##.############..##.
                    .#.######.########.#
                    .###.#######.####.#.
                    #####.##.#.##.###.##
                    ..#####..#.#########
                    ####################
                    #.####....###.#.#.##
                    ##.#################
                    #####.##.###..####..
                    ..######..##.#######
                    ####.##.####...##..#
                    .#####..#.######.###
                    ##...#.##########...
                    #.##########.#######
                    .####.#.###.###.#.##
                    ....##.##.###..#####
                    .#.#.###########.###
                    #.#.#.#####.####.###
                    ###.##.####.##.#..##
                """.trimIndent().trim().split("\n")
                val asteroids = inputToAsteroids(input)
                asteroids.count() shouldBe 300
                pickBestAsteroid(asteroids) shouldBe Pair(MaybeRoid.Asteroid(11, 13), 210)
            }
        }
        "Part 2" - {
            "angle-start" {
                val a = MaybeRoid.Asteroid(5, 5)
                val b = MaybeRoid.Asteroid(5, 0)
                // up
                calculateAngle(a, b) shouldBe -0.5 * PI
                rotateAnti90(calculateAngle(a, b)) shouldBe 0.0
                // down
                calculateAngle(b, a) shouldBe 0.5 * PI
                rotateAnti90(calculateAngle(b, a)) shouldBe PI
                val m = MaybeRoid.Asteroid(0, 5)
                val n = MaybeRoid.Asteroid(5, 5)
                // right
                calculateAngle(m, n) shouldBe 0.0
                rotateAnti90(calculateAngle(m, n)) shouldBe 0.5 * PI
                // left
                calculateAngle(n, m) shouldBe PI
                rotateAnti90(calculateAngle(n, m)) shouldBe 1.5 * PI
            }
            "example" {
                val input = """
                    .#..##.###...#######
                    ##.############..##.
                    .#.######.########.#
                    .###.#######.####.#.
                    #####.##.#.##.###.##
                    ..#####..#.#########
                    ####################
                    #.####....###.#.#.##
                    ##.#################
                    #####.##.###..####..
                    ..######..##.#######
                    ####.##.####...##..#
                    .#####..#.######.###
                    ##...#.##########...
                    #.##########.#######
                    .####.#.###.###.#.##
                    ....##.##.###..#####
                    .#.#.###########.###
                    #.#.#.#####.####.###
                    ###.##.####.##.#..##
                """.trimIndent().trim().split("\n")
                val result = find200th(inputToAsteroids(input))
                result.x shouldBe 8
                result.y shouldBe 2
            }
        }
    })
