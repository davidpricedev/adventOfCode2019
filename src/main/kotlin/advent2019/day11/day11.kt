package advent2019.day11

import advent2019.coIntCode.Computer
import kotlinx.coroutines.*
import java.lang.Exception

/**
 * Part 1 Notes:
 * Assuming painted all black to start with, but that doesn't seem to be explicitly stated
 * I need to maintain the state of the robot which will include the direction its facing as well as xy position
 * I need to maintain a set of xy points the robot has painted as well as the color,
 *  the colour needs to be fed back into the robots input.
 * The robot only paints as it moves.
 */
fun main() {
    // part1
    val finalState1 = runRobot()
    println(finalState1.hull.count())
    visualizeHull(finalState1.hull)
    // part2
    val finalState2 = runRobot(mapOf(Position() to Colour.White))
    visualizeHull(finalState2.hull)
}

/**
 * There may be 2+ ways of approaching this.
 * The while loop should work
 * There might be a way to use generateSequence with either tuples or an object
 *  that contains all 3 elements (panel state + robot state + computer).
 *  We just need a nextState function that is probably a suspend function and hopefully generateSequence can work with that?
 *
 *  Nope, generateSequence isn't suspendable and https://github.com/Kotlin/KEEP/blob/master/proposals/coroutines.md#asynchronous-sequences
 *  is the only? way to build such a suspendable sequence generator
 *  Although, as they suggest there, it may be possible to simulate generator-like behaviour using channels
 *
 *  Ooo another option? Simmulate the state of the world as a coroutine too - with its own channels and input/output
 *  Then the code mostly becomes wiring one into the other similar to the day7 amp? Or even 3 separate worlds -
 *  one each for the hull-panels, robot, and computer
 */
fun runRobot(hull: Map<Position, Colour> = mapOf()) = runBlocking {
    val comp = Computer.init(getProgram())
    launch { Computer.run(comp) }
    var state = State(comp, hull = hull)
    while (!state.done) {
        state = state.next()
    }
    return@runBlocking state
}

fun visualizeHull(hull: Map<Position, Colour>) {
    val rightBound = hull.keys.maxBy { it.x }?.x ?: 0
    val leftBound = hull.keys.minBy { it.x }?.x ?: 0
    val upperBound = hull.keys.maxBy { it.y }?.y ?: 0
    val lowerBound = hull.keys.minBy { it.y }?.y ?: 0
    fun printRow(row: Int) {
        println((leftBound..rightBound)
                    .map { col -> hull.getColourAt(Position(col, row)).viz() }
                    .joinToString(""))
    }
    (lowerBound..upperBound).forEach { row -> printRow(row) }
}

data class State(
    val comp: Computer,
    val robot: Robot = Robot(),
    val hull: Map<Position, Colour> = mapOf(),
    val done: Boolean = false
) {
    /**
     * I really want a Promise.race equivalent
     * with that I could wait for the input-send to complete OR computer to halt
     * without it, I think the best option might be to timeout the send if it takes too long
     */
    suspend fun next(): State {
        // Read the color at current position into the computer
        val sendResult = withTimeoutOrNull(500L) {
            comp.inputChannel.send(hull.getColourAt(robot.pos()).value)
        } ?: return copy(done = true)

        // receive colour and paint
        val nextColour = Colour.fromLong(comp.outputChannel.receive())
        val nextHull = hull.plus(robot.pos() to nextColour)

        // receive turn and move
        val nextDirection = robot.direction.nextDirection(comp.outputChannel.receive())
        val nextRobot = robot.nextPosition(nextDirection)

        // Next
        return copy(robot = nextRobot, hull = nextHull)
    }
}

fun Map<Position, Colour>.getColourAt(pos: Position) = this[pos] ?: Colour.Black

data class Robot(val x: Int = 0, val y: Int = 0, val direction: Direction = Direction.Up) {
    fun pos() = Position(x, y)

    fun nextPosition(nextDirection: Direction) =
        when (nextDirection) {
            Direction.Up    -> Robot(x, y - 1, nextDirection)
            Direction.Down  -> Robot(x, y + 1, nextDirection)
            Direction.Left  -> Robot(x - 1, y, nextDirection)
            Direction.Right -> Robot(x + 1, y, nextDirection)
        }
}

enum class Direction {
    Up,
    Down,
    Left,
    Right;

    fun rotateLeft() = when (this) {
        Up    -> Left
        Down  -> Right
        Left  -> Down
        Right -> Up
    }

    fun rotateRight() = when (this) {
        Up    -> Right
        Down  -> Left
        Left  -> Up
        Right -> Down
    }

    fun nextDirection(turnDirection: Long) =
        when (turnDirection) {
            0L   -> rotateLeft()
            1L   -> rotateRight()
            else -> throw Exception("(╯°□°)╯︵ ┻━┻  Unknown direction $turnDirection")
        }
}

enum class Colour(val value: Long) {
    Black(0),
    White(1);

    fun viz() =
        when (this) {
            Black -> " . "
            White -> "<#>"
        }

    companion object {
        fun fromLong(intColour: Long) =
            when (intColour) {
                0L   -> Black
                1L   -> White
                else -> throw Exception("(╯°□°)╯︵ ┻━┻  Unknown colour $intColour")
            }
    }
}

data class Position(val x: Int = 0, val y: Int = 0)

fun getProgram() =
    "3,8,1005,8,352,1106,0,11,0,0,0,104,1,104,0,3,8,102,-1,8,10,101,1,10,10,4,10,108,1,8,10,4,10,102,1,8,28,1,1003,20,10,2,106,11,10,2,1107,1,10,1,1001,14,10,3,8,1002,8,-1,10,1001,10,1,10,4,10,1008,8,0,10,4,10,1002,8,1,67,2,1009,7,10,3,8,1002,8,-1,10,1001,10,1,10,4,10,108,0,8,10,4,10,101,0,8,92,1,105,9,10,1006,0,89,1,108,9,10,3,8,1002,8,-1,10,1001,10,1,10,4,10,1008,8,1,10,4,10,1002,8,1,126,1,1101,14,10,1,1005,3,10,1006,0,29,1006,0,91,3,8,102,-1,8,10,101,1,10,10,4,10,108,1,8,10,4,10,1002,8,1,161,1,1,6,10,1006,0,65,2,106,13,10,1006,0,36,3,8,1002,8,-1,10,1001,10,1,10,4,10,1008,8,1,10,4,10,102,1,8,198,1,105,15,10,1,1004,0,10,3,8,1002,8,-1,10,1001,10,1,10,4,10,1008,8,0,10,4,10,101,0,8,228,2,1006,8,10,2,1001,16,10,3,8,102,-1,8,10,1001,10,1,10,4,10,108,0,8,10,4,10,1001,8,0,257,1006,0,19,2,6,10,10,2,4,13,10,2,1002,4,10,3,8,102,-1,8,10,1001,10,1,10,4,10,1008,8,1,10,4,10,1002,8,1,295,3,8,1002,8,-1,10,101,1,10,10,4,10,108,0,8,10,4,10,101,0,8,316,2,101,6,10,1006,0,84,2,1004,13,10,1,1109,3,10,101,1,9,9,1007,9,1046,10,1005,10,15,99,109,674,104,0,104,1,21101,387365315340,0,1,21102,369,1,0,1105,1,473,21101,666685514536,0,1,21102,380,1,0,1106,0,473,3,10,104,0,104,1,3,10,104,0,104,0,3,10,104,0,104,1,3,10,104,0,104,1,3,10,104,0,104,0,3,10,104,0,104,1,21102,1,46266346536,1,21102,427,1,0,1105,1,473,21101,235152829659,0,1,21101,438,0,0,1105,1,473,3,10,104,0,104,0,3,10,104,0,104,0,21102,838337188620,1,1,21101,461,0,0,1105,1,473,21102,988753429268,1,1,21102,1,472,0,1106,0,473,99,109,2,22101,0,-1,1,21101,40,0,2,21101,504,0,3,21102,494,1,0,1106,0,537,109,-2,2105,1,0,0,1,0,0,1,109,2,3,10,204,-1,1001,499,500,515,4,0,1001,499,1,499,108,4,499,10,1006,10,531,1101,0,0,499,109,-2,2106,0,0,0,109,4,2101,0,-1,536,1207,-3,0,10,1006,10,554,21102,1,0,-3,21202,-3,1,1,21201,-2,0,2,21102,1,1,3,21101,573,0,0,1105,1,578,109,-4,2105,1,0,109,5,1207,-3,1,10,1006,10,601,2207,-4,-2,10,1006,10,601,21201,-4,0,-4,1105,1,669,22101,0,-4,1,21201,-3,-1,2,21202,-2,2,3,21101,620,0,0,1106,0,578,22102,1,1,-4,21101,0,1,-1,2207,-4,-2,10,1006,10,639,21101,0,0,-1,22202,-2,-1,-2,2107,0,-3,10,1006,10,661,22101,0,-1,1,21102,661,1,0,106,0,536,21202,-2,-1,-2,22201,-4,-2,-4,109,-5,2106,0,0"
