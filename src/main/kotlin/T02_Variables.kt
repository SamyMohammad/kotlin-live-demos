package t02

/*
 * T02 · Variables: val and var                       Slides 20, 30
 *
 * Dart          ->  Kotlin
 * final x = 1;  ->  val x = 1
 * var y = 1;    ->  var y = 1
 * const z = 1;  ->  const val Z = 1   (top level or inside an object)
 */

const val APP_NAME = "Kotlin Diploma"   // compile-time constant
// const val STARTED = System.currentTimeMillis()   // ERROR: const needs a value known at compile time

fun main() {
    section("1. val vs var")
    val name = "Samy"      // read-only: assigned once
    var count = 0          // mutable
    count += 1
    // name = "Ali"        // ERROR: 'val' cannot be reassigned
    println("name=$name count=$count")

    section("2. Type inference vs explicit types")
    val city = "Alexandria"        // inferred: String
    val year: Int = 2026           // explicit type: name: Type
    var score: Double = 9.5
    score += 0.5
    // score = "high"              // ERROR: type mismatch
    println("$city $year $score")

    section("3. Declare now, assign later (once)")
    val grade: String
    val mark = 75
    grade = if (mark >= 50) "Pass" else "Fail"
    // grade = "Again"             // ERROR: 'val' cannot be reassigned
    println("grade=$grade")

    section("4. const val vs val")
    println("APP_NAME=$APP_NAME")              // known at compile time
    val startedAt = System.currentTimeMillis() // val can be computed at runtime (see STARTED above)
    println("startedAt=$startedAt")

    section("5. WATCH OUT: val is not immutable")
    val list = mutableListOf(1, 2)
    list.add(3)                    // allowed: the reference is fixed, not the content
    // list = mutableListOf()      // ERROR: 'val' cannot be reassigned
    println("list=$list")
    // In Compose, changing a plain mutableList does NOT redraw the UI.
    // Use mutableStateListOf(...) or assign a NEW list to a state.

    section("6. Rule of thumb")
    println("Start with val. Switch to var only when the value must change.")
}

private fun section(title: String) = println("\n=== $title ===")
