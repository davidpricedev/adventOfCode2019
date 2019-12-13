package advent2019.day12

import kotlin.math.absoluteValue

/**
 * Might be another use case for generateSequence?
 * Logic for calculating the velocity has to be aware of the entire system.
 * For every axis of every moon we'll have to calculate how all the other moons affect it to calculate the next velocity
 * velocity in the examples is trailing - i.e. we are looking at velocity that got us to the current position, not the next velocity
 */
fun main() {
    // part1
    println(totalEnergy(advanceTime(getMoonsStart(), 1000)))
}

data class Point(val x: Int = 0, val y: Int = 0, val z: Int = 0) {
    fun absSum() = x.absoluteValue + y.absoluteValue + z.absoluteValue

    fun add(point: Point) = Point(x + point.x, y + point.y, z + point.z)

    override fun toString(): String =
        listOf(x, y, z).joinToString(prefix = "(", postfix = ")") { it.toString().padStart(3, ' ') }
}

data class Moon(val position: Point, val velocity: Point = Point()) {
    fun totalEnergy() = position.absSum() * velocity.absSum()

    fun applyVelocity() = Moon(position.add(velocity), velocity)

    override fun toString(): String = "$position, $velocity"
}

fun advanceTime(moons: List<Moon>, timeCount: Int): List<Moon> {
    val sequences = generateSequence(Pair(moons, 0)) {
        println("-- time step ${it.second} --")
        it.first.forEach { m -> println(m) }
        Pair(advanceTime(it.first), it.second + 1).takeIf { it.second <= timeCount }
    }.toList()
    return sequences.last().first
}

fun advanceTime(moons: List<Moon>) = applyVelocity(calculateVelocity(moons))

fun applyVelocity(moons: List<Moon>) = moons.map { it.applyVelocity() }

fun calculateVelocity(moons: List<Moon>): List<Moon> {
    val xs = moons.map { it.position.x }
    val ys = moons.map { it.position.y }
    val zs = moons.map { it.position.z }
    return moons.map { moon ->
        Moon(moon.position, velocity = nextVelocity(moon, xs, ys, zs))
    }
}

fun nextVelocity(moon: Moon, xs: List<Int>, ys: List<Int>, zs: List<Int>): Point {
    return Point(
        oneDimDeltaV(moon.position.x, moon.velocity.x, xs),
        oneDimDeltaV(moon.position.y, moon.velocity.y, ys),
        oneDimDeltaV(moon.position.z, moon.velocity.z, zs)
    )
}

fun oneDimDeltaV(pos: Int, velocity: Int, others: List<Int>) =
    velocity + others.filter { it > pos }.count() - others.filter { it < pos }.count()

fun totalEnergy(moons: List<Moon>) = moons.sumBy { it.totalEnergy() }

fun getMoonsStart() = listOf(
    Moon(Point(x = -1, y = 7, z = 3)),
    Moon(Point(x = 12, y = 2, z = -13)),
    Moon(Point(x = 14, y = 18, z = -8)),
    Moon(Point(x = 17, y = 4, z = -4))
)