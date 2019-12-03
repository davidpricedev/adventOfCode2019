package advent2019.day1

fun main() {
    print(day1_1())
    print("\n")
    print(day1_2())
}

fun day1_1(): Double {
    val inputs = get_inputs()
    val result = massesToFuelNaive(inputs)
    return result
}

fun day1_2(): Double {
    val inputs = get_inputs()
    val result = massesToFuel(inputs)
    return result
}

fun massesToFuelNaive(masses: List<Int>) = masses.map {massToFuelNaive(it.toDouble())}.sum()

fun massToFuelNaive(mass: Double) = kotlin.math.floor(mass / 3) - 2

fun massesToFuel(masses: List<Int>) = masses.map {massToFuel(it.toDouble())}.sum()

fun massToFuel(mass: Double): Double = calculateFuelForFuelMass(massToFuelNaive(mass))

tailrec fun calculateFuelForFuelMass(fuelMass: Double, sumSoFar: Double = 0.0): Double =
    if (fuelMass <= 0) {
        sumSoFar
    } else {
        calculateFuelForFuelMass(massToFuelNaive(fuelMass), sumSoFar + fuelMass)
    }

fun get_inputs() = listOf(
    136995,
    113523,
    51895,
    79350,
    124361,
    62331,
    93313,
    67673,
    65387,
    139570,
    74864,
    73723,
    142366,
    108790,
    50333,
    109242,
    67155,
    126685,
    148459,
    126160,
    56323,
    123773,
    116336,
    123357,
    117877,
    90720,
    105322,
    92084,
    100609,
    143050,
    99542,
    137618,
    70385,
    116984,
    137877,
    142591,
    104263,
    77096,
    107016,
    106030,
    88411,
    56359,
    129141,
    88179,
    82296,
    66855,
    146894,
    65655,
    65481,
    107083,
    129529,
    94207,
    118038,
    93389,
    116976,
    141468,
    137616,
    78852,
    57602,
    82514,
    59790,
    115105,
    125868,
    104067,
    100487,
    109434,
    68047,
    84831,
    64928,
    131286,
    78450,
    70130,
    84341,
    105659,
    61101,
    137930,
    125096,
    73937,
    58756,
    123901,
    123296,
    110409,
    104259,
    87899,
    97236,
    111253,
    60227,
    129468,
    79553,
    55170,
    99267,
    101485,
    146930,
    105511,
    145835,
    98257,
    147609,
    143714,
    55276,
    66162)