package t03

import kotlin.math.roundToInt

/*
 * T03 · Types and numbers                            Slides 21-22
 *
 * Dart int / double / bool  ->  Kotlin Int, Long, Double, Float, Boolean
 * Types start with a capital letter. Everything is an object.
 */

fun main() {
    section("1. Basic types")
    val i: Int = 42
    val l: Long = 42L
    val d: Double = 3.14
    val f: Float = 3.14f
    val b: Boolean = true
    val c: Char = 'A'
    val s: String = "Hi"
    println("$i $l $d $f $b $c $s")
    println("Int range : ${Int.MIN_VALUE} .. ${Int.MAX_VALUE}")
    println("Readable  : ${1_000_000}")        // underscores for big numbers

    section("2. No automatic conversion")
    val small: Int = 10
    // val wrong: Long = small            // ERROR: type mismatch, expected Long, actual Int
    val big: Long = small.toLong()
    println("toLong=$big toDouble=${small.toDouble()} toString='${small}'")
    println("\"42\".toInt()        = ${"42".toInt()}")
    println("\"abc\".toIntOrNull() = ${"abc".toIntOrNull()}")   // null, no crash
    tryIt("\"abc\".toInt()") { "abc".toInt() }

    section("3. WATCH OUT: Int / Int is an Int")
    // Dart: 7 / 2 == 3.5   and   7 ~/ 2 == 3
    println("7 / 2   = ${7 / 2}")       // 3 (!)
    println("7 / 2.0 = ${7 / 2.0}")     // 3.5
    println("7 % 2   = ${7 % 2}")       // 1
    println("7.9.toInt()       = ${7.9.toInt()}")        // 7: cuts, doesn't round
    println("7.5.roundToInt()  = ${7.5.roundToInt()}")   // 8

    section("4. WATCH OUT: Int overflow wraps silently")
    val max = Int.MAX_VALUE
    println("Int.MAX_VALUE + 1 = ${max + 1}")             // negative!
    println("as Long           = ${max.toLong() + 1}")

    section("5. Any, is (type check) and smart casts")
    val things: List<Any> = listOf(1, "two", 3.0, 'c', true)
    for (t in things) {
        val kind = when (t) {
            is Int -> "Int, doubled = ${t * 2}"            // t is smart-cast to Int
            is String -> "String of length ${t.length}"    // t is smart-cast to String
            else -> t::class.simpleName
        }
        println("$t -> $kind")
    }

    section("6. as (unsafe) vs as? (safe) casts")
    val x: Any = "hello"
    val asString = x as String          // fine here
    val asInt = x as? Int               // wrong type -> null, no crash
    println("as String = $asString, as? Int = $asInt")
    tryIt("x as Int") { x as Int }      // wrong type -> ClassCastException
}

private fun section(title: String) = println("\n=== $title ===")

private inline fun tryIt(label: String, block: () -> Unit) {
    try {
        block()
    } catch (e: Exception) {
        println("CRASH  $label -> ${e::class.simpleName}: ${e.message}")
    }
}
