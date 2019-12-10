package advent2019.day10

import io.kotlintest.shouldBe
import io.kotlintest.specs.FreeSpec

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
    })
