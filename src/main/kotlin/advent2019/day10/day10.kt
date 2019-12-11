package advent2019.day10

import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Part1 Notes:
 * 1st translate dots and hashes into a list of asteroids w/ coordinates
 * 2nd for each asteroid calculate how many asteroids it can "see"
 *  - this means finding ray collisions or being able to answer is point x on the line between A -> B?
 *  - alternatively, if we calculate the angle in radians or degrees at which the other asteroids are located
 *    we could simplify that to just picking one when the angles for more than one asteroid are equivalent
 *    this appears to be atan2(dy,dx) to calculate the angle in radians
 * Part2 Notes:
 * Angles matter here, so we might need to flip things around or figure out what our native zero angle is and adjust for it
 * instead of doing a distinct + count to get the number, we can simply add them to a list and sort by distance
 * Then we can merge the x,y,angle,list-depth into an object and sort by "$listDepth_$radian" and pick the 200th element
 */
fun main() {
    // part1
    println(pickBestAsteroid(inputToAsteroids(getPart1Input())))
    // part2
    println(find200th(inputToAsteroids(getPart1Input())))
}

sealed class MaybeRoid {
    data class Asteroid(val x: Int, val y: Int) : MaybeRoid() {
        fun toSingleNumber() = x * 100 + y
    }

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
                is MaybeRoid.Asteroid -> calculateAngle(center, other)
            }
        }
    }

/**
 * Rotates angle coordinates by 90degrees anti-clockwise (so "up" is zero angle)
 * and shifts the range from (-PI,PI) to (0,2PI)
 */
fun rotateAnti90(angle: Double) = (angle + (2.5 * PI)) % (2 * PI)

fun calculateAngle(a: MaybeRoid.Asteroid, b: MaybeRoid.Asteroid) =
    atan2(b.y - a.y.toDouble(), b.x - a.x.toDouble())

fun distance(a: MaybeRoid.Asteroid, b: MaybeRoid.Asteroid) =
    sqrt((a.x - b.x).toDouble().pow(2.0) + (a.y - b.y).toDouble().pow(2.0))

data class SurveyData(
    val x: Int,
    val y: Int,
    val angle: Double,
    val distance: Double = -1.0,
    val ordinalDistance: Int = -1
) {
    companion object {
        fun bogusValue() = SurveyData(-1, -1, -99.9)
    }
}

fun calculateAngleData(center: MaybeRoid, others: List<MaybeRoid>) =
    when (center) {
        MaybeRoid.EmptySpace  -> listOf(SurveyData.bogusValue())
        is MaybeRoid.Asteroid -> others.map { other ->
            when (other) {
                MaybeRoid.EmptySpace  -> SurveyData.bogusValue()
                center                -> SurveyData.bogusValue()
                is MaybeRoid.Asteroid -> SurveyData(other.x,
                                                    other.y,
                                                    angle = rotateAnti90(calculateAngle(center, other)),
                                                    distance = distance(center, other))
            }
        }
    }

fun find200th(asteroids: List<MaybeRoid>): SurveyData {
    val center = pickBestAsteroid(asteroids)?.first ?: asteroids.first()
    val surveyData = calculateAngleData(center, asteroids).filterNot { it == SurveyData.bogusValue()}
    // turn distance and radians into ordinal distance for each angle
    val ordinaledData = surveyData.groupBy { it.angle }.map { angleInfo ->
        angleInfo.value
            .sortedBy { it.distance }
            .mapIndexed { i, x -> x.copy(ordinalDistance = i) }
    }.flatten()
    val finalData = ordinaledData.sortedWith(compareBy({it.ordinalDistance}, {it.angle}))
    return finalData[199]
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