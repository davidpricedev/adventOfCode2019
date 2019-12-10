package advent2019.day10

import kotlin.math.atan2

/**
 * Notes:
 * 1st translate dots and hashes into a list of asteroids w/ coordinates
 * 2nd for each asteroid calculate how many asteroids it can "see"
 *  - this means finding ray collisions or being able to answer is point x on the line between A -> B?
 *  - alternatively, if we calculate the angle in radians or degrees at which the other asteroids are located
 *    we could simplify that to just picking one when the angles for more than one asteroid are equivalent
 *    this appears to be atan2(dy,dx) to calculate the angle in radians
 */
fun main() {
    // part1
    println(pickBestAsteroid(inputToAsteroids(getPart1Input())))
}

sealed class MaybeRoid {
    data class Asteroid(val x: Int, val y: Int) : MaybeRoid()
    object EmptySpace : MaybeRoid()
}

fun inputToAsteroids(input: List<String>) =
    input.mapIndexed { y, line ->
        line.mapIndexed { x, char ->
            if (char == '#') MaybeRoid.Asteroid(x, y) else MaybeRoid.EmptySpace
        }
    }.flatten().filter { it != MaybeRoid.EmptySpace }

fun pickBestAsteroid(asteroids: List<MaybeRoid>) =
    asteroids.map { Pair(it, calculateVisible(it, asteroids)) }.maxBy { it.second }

fun calculateVisible(center: MaybeRoid, others: List<MaybeRoid>) =
    calculateAngles(center, others).filter { it != -99.9 }.distinct().count()

fun calculateAngles(center: MaybeRoid, others: List<MaybeRoid>) =
    when (center) {
        MaybeRoid.EmptySpace  -> listOf(-99.9)
        is MaybeRoid.Asteroid -> others.map { other ->
            when (other) {
                MaybeRoid.EmptySpace  -> -99.9
                center                -> -99.9
                is MaybeRoid.Asteroid -> atan2(other.y - center.y.toDouble(), other.x - center.x.toDouble())
            }
        }
    }

fun getPart1Input() =
    """
        ##.###.#.......#.#....#....#..........#.
        ....#..#..#.....#.##.............#......
        ...#.#..###..#..#.....#........#......#.
        #......#.....#.##.#.##.##...#...#......#
        .............#....#.....#.#......#.#....
        ..##.....#..#..#.#.#....##.......#.....#
        .#........#...#...#.#.....#.....#.#..#.#
        ...#...........#....#..#.#..#...##.#.#..
        #.##.#.#...#..#...........#..........#..
        ........#.#..#..##.#.##......##.........
        ................#.##.#....##.......#....
        #............#.........###...#...#.....#
        #....#..#....##.#....#...#.....#......#.
        .........#...#.#....#.#.....#...#...#...
        .............###.....#.#...##...........
        ...#...#.......#....#.#...#....#...#....
        .....#..#...#.#.........##....#...#.....
        ....##.........#......#...#...#....#..#.
        #...#..#..#.#...##.#..#.............#.##
        .....#...##..#....#.#.##..##.....#....#.
        ..#....#..#........#.#.......#.##..###..
        ...#....#..#.#.#........##..#..#..##....
        .......#.##.....#.#.....#...#...........
        ........#.......#.#...........#..###..##
        ...#.....#..#.#.......##.###.###...#....
        ...............#..#....#.#....#....#.#..
        #......#...#.....#.#........##.##.#.....
        ###.......#............#....#..#.#......
        ..###.#.#....##..#.......#.............#
        ##.#.#...#.#..........##.#..#...##......
        ..#......#..........#.#..#....##........
        ......##.##.#....#....#..........#...#..
        #.#..#..#.#...........#..#.......#..#.#.
        #.....#.#.........#............#.#..##.#
        .....##....#.##....#.....#..##....#..#..
        .#.......#......#.......#....#....#..#..
        ...#........#.#.##..#.#..#..#........#..
        #........#.#......#..###....##..#......#
        ...#....#...#.....#.....#.##.#..#...#...
        #.#.....##....#...........#.....#...#...
    """.trimIndent().trim().split("\n")