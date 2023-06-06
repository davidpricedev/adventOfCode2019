package advent2019.day12

import advent2019.util.lcm
import kotlin.math.absoluteValue

/**
 * Might be another use case for generateSequence?
 * Logic for calculating the velocity has to be aware of the entire system.
 * For every axis of every moon we'll have to calculate how all the other moons affect it to calculate the next velocity
 * velocity in the examples is trailing - i.e. we are looking at velocity that got us to the current position, not the next velocity
 * ---
 * For part2, maybe rather than simulating all 3 dimensions at once, we could do the same calculations for a single
 * dimension at a time and find that dimension's periodicity, and then it should be a matter of calculatign the LCM
 * to get the periodicity of the system as a whole.
 */
fun main() {
    // part1
    println(totalEnergy(advanceTime(getMoonsStart(), 1000)))
    // part2
    println(calculatePeriodicity(getMoonsStart()))
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
        // println("-- time step ${it.second} --")
        // it.first.forEach { m -> println(m) }
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

fun calculatePeriodicity(moons: List<Moon>): Long {
    val moonsX = moons.map { Moon1d(it.position.x, it.velocity.x) }
    val px = getPeriodicity1d(moonsX)
    val moonsY = moons.map { Moon1d(it.position.y, it.velocity.y) }
    val py = getPeriodicity1d(moonsY)
    val moonsZ = moons.map { Moon1d(it.position.z, it.velocity.z) }
    val pz = getPeriodicity1d(moonsZ)
    println("results: ${px}, ${py}, ${pz}")
    return lcm(lcm(px.toLong(), py.toLong()), pz.toLong())
}

data class Moon1d(val position: Int, val velocity: Int = 0) {
    fun applyVelocity1d() = Moon1d(position + velocity, velocity)

    override fun toString(): String = "$position, $velocity"
}

fun getPeriodicity1d(moons: List<Moon1d>): Int {
    val sequences = generateSequence(moons) {
        // println("-- time step --")
        // it.forEach { m -> println(m) }
        val next = applyVelocity1d(calculateVelocity1d(it))
        next.takeIf { !stateMatches(next, moons) }
    }
    return sequences.count()
}

fun stateMatches(moonsA: List<Moon1d>, moonsB: List<Moon1d>): Boolean {
    val moonsAStr = moonsA.map { it.toString() }.joinToString(separator = ";")
    val moonsBStr = moonsB.map { it.toString() }.joinToString(separator = ";")
    return moonsAStr == moonsBStr
}

fun applyVelocity1d(moons: List<Moon1d>) = moons.map { it.applyVelocity1d() }

fun calculateVelocity1d(moons: List<Moon1d>): List<Moon1d> {
    val xs = moons.map { it.position }
    return moons.map { moon ->
        Moon1d(moon.position, velocity = nextVelocity1d(moon, xs))
    }
}

fun nextVelocity1d(moon: Moon1d, xs: List<Int>) = oneDimDeltaV(moon.position, moon.velocity, xs)

fun getMoonsStart() = listOf(
    Moon(Point(x = -1, y = 7, z = 3)),
    Moon(Point(x = 12, y = 2, z = -13)),
    Moon(Point(x = 14, y = 18, z = -8)),
    Moon(Point(x = 17, y = 4, z = -4))
)

fun getSample1MoonsStart() = listOf(
    Moon(Point(-1, 0, 2)),
    Moon(Point(2, -10, -7)),
    Moon(Point(4, -8, -8)),
    Moon(Point(3, 5, -1))
)