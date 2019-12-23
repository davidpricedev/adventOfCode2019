# Notes on specific puzzles

## Intcode Computer & Coroutines

Day 2 and all odd days >= 5 deal with the intcode computer - a computer that responds to integers
 as commands, similar to assembly code.
 
Day 2, 5, 7, and 9 have you incrementally building out the computer functionality.
Odd days beyond that use the intcode computer as a building block to help solve the puzzles.

* I've added a debug flag which logs essentially everything the computer does
* I've added an ioDebug flag which logs all the IO the computer does. This becomes esp useful on day 7 and beyond day 9

### Coroutines


## day 12 : Orbiting Moons

Part 2 violates the original stipulations of the advent puzzles which states that any puzzle
 can be solved by an older computer in a few seconds.
The solution seems to require simulating the moons across billions or trillions of time cycles.
I know the LCM of the individual periods can be used to calculate the overall without simulating the entire overall.
However, simulating even the 1st of the individual periods will take too long -
I gave up after an hour (and my computer isn't a slouch).

## day 13 : Breakout Game

Kotlin and the jvm generally doesn't handle reading single keypresses from the console.
That means that playing an ascii game from the console is really annoying -
 you must type a movement command and then hit enter.
In fact it is so annoying that I gave up trying to play it interactively.
I have a few ideas about how to write an AI that would play it for me though.
* 1st track the ball's trajectory
* start by keeping under the ball at all times
* add more flexibility as needed - esp for cases where the ball isn't within 6-10 spaces of the paddle.

## day 14 : Ore to Fuel Crafting

There is probably a LCM/GCF way of solving part 2, but we can use part1 solver to do it too.
Part1 determines ore needs for n fuel. So we can start with 1T/ore-needs-for-1-fuel as a good 
 starting guess and increase fuel guesses until we find the maximum that doesn't exceed 1T ore.

## day 16 : Signal Processing

While part1 was pretty easy, part2 involves some sort of trick.
My initial extension of part1 blew the heap.
After tweaking things a bit, I was able to avoid that, at the cost of time (probably roughly 100 hours)
Clearly there are one or more maths tricks that can greatly short circuit the amount of processing.
I'm here to learn Kotlin not maths tricks.


