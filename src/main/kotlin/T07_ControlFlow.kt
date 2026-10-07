package t07

/*
 * T07 · Control flow: if, when, loops, ranges        Slides 36-40
 *
 * Dart                       ->  Kotlin
 * c ? a : b                  ->  if (c) a else b
 * switch (x) { 1 => ... }    ->  when (x) { 1 -> ... }
 * for (var i = 0; i < 5; i++)->  for (i in 0 until 5)
 */

fun httpMessage(code: Int) = when (code) {
    200 -> "OK"
    201, 204 -> "Success"                 // several values in one branch
    in 400..499 -> "Client error"         // ranges
    in 500..599 -> "Server error"
    else -> "Unknown"
}

fun ageGroup(age: Int) = when {           // no subject: replaces if-else chains
    age < 13 -> "Child"
    age < 20 -> "Teen"
    else -> "Adult"
}

fun describe(x: Any): String = when (x) { // types: smart cast inside each branch
    is Int -> "Int"
    is String -> "String of length ${x.length}"
    is List<*> -> "List of ${x.size}"
    else -> "Something else"
}

fun main() {
    section("1. if is an expression (no ternary)")
    val score = 72
    val result = if (score >= 50) "Pass" else "Fail"
    println("result = $result")
    val grade = if (score >= 90) {
        "A"
    } else if (score >= 70) {
        println("(a block's value is its last line)")
        "B"
    } else {
        "C"
    }
    println("grade = $grade")
    // val oops = if (score > 50) "Yes"  // ERROR: 'if' used as an expression needs 'else'

    section("2. when with a subject (Dart: switch)")
    listOf(200, 204, 404, 503, 999).forEach { println("$it -> ${httpMessage(it)}") }

    section("3. when without a subject")
    listOf(8, 16, 30).forEach { println("$it -> ${ageGroup(it)}") }

    section("4. when with types")
    listOf(1, "Kotlin", listOf(1, 2), 2.5).forEach { println("$it -> ${describe(it)}") }

    section("5. Ranges")
    for (i in 1..5) print("$i ");             println("   <- 1..5 includes 5")
    for (i in 1 until 5) print("$i ");        println("     <- until excludes 5")
    for (i in 1..<5) print("$i ");            println("     <- ..< is the same as until")
    for (i in 10 downTo 0 step 2) print("$i "); println("<- downTo + step")
    for (c in 'a'..'e') print("$c ");         println("   <- Char ranges")
    println("5 in 1..10 = ${5 in 1..10}   15 !in 1..10 = ${15 !in 1..10}")

    section("6. Looping over collections")
    val names = listOf("Mona", "Ali", "Omar")
    for (n in names) print("$n ")
    println()
    for ((index, n) in names.withIndex()) println("$index: $n")
    for (i in names.indices) print("[$i] ")
    println()
    names.forEachIndexed { i, n -> print("$i=$n ") }
    println()

    section("7. while, do-while, repeat")
    var n = 3
    while (n > 0) {
        print("$n ")
        n--
    }
    println("liftoff!")
    var tries = 0
    do {
        tries++
    } while (tries < 3)
    println("tries = $tries")
    repeat(3) { println("Hi #${it + 1}") }

    section("8. break, continue and labels")
    for (i in 1..10) {
        if (i == 3) continue
        if (i == 6) break
        print("$i ")
    }
    println()
    outer@ for (i in 1..3) {
        for (j in 1..3) {
            if (j == 2) continue@outer      // jump to the next i
            if (i == 3) break@outer         // stop both loops
            print("($i,$j) ")
        }
    }
    println()

    section("9. WATCH OUT: 10..1 is empty")
    for (i in 10..1) println("never printed")  // no error, no output
    println("(10..1).count()   = ${(10..1).count()}")
    println("10 downTo 1       = ${(10 downTo 1).toList()}")
}

private fun section(title: String) = println("\n=== $title ===")
