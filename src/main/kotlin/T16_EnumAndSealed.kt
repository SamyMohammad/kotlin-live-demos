package t16

/*
 * T16 · Enum classes and sealed types                Slides 65-67, 40
 *
 * Dart enum            ->  enum class
 * Dart 3 sealed class  ->  sealed class / sealed interface
 */

enum class Status { ACTIVE, BLOCKED, PENDING }

enum class Plan(val price: Int, val label: String) {
    FREE(0, "Free"),
    PRO(99, "Pro"),
    TEAM(299, "Team");                       // ; needed before members

    fun isPaid() = price > 0
}

fun statusColor(s: Status) = when (s) {     // exhaustive: no else needed
    Status.ACTIVE -> "green"
    Status.BLOCKED -> "red"
    Status.PENDING -> "orange"
}

sealed interface UiState {
    data object Loading : UiState
    data class Success(val items: List<String>) : UiState
    data class Error(val message: String) : UiState
}

fun render(state: UiState): String = when (state) {
    UiState.Loading -> "Spinner..."
    is UiState.Success -> "List: ${state.items.joinToString()}"  // smart cast
    is UiState.Error -> "Error: ${state.message}"
}

// WATCH OUT: else hides new cases from the compiler
fun renderLazy(state: UiState): String = when (state) {
    is UiState.Success -> "List"
    else -> "Something else"
}

sealed class Shape(val name: String) {      // sealed CLASS: can share state
    class Circle(val r: Double) : Shape("Circle")
    class Rect(val w: Double, val h: Double) : Shape("Rect")
}

fun area(s: Shape): Double = when (s) {
    is Shape.Circle -> Math.PI * s.r * s.r
    is Shape.Rect -> s.w * s.h
}

object PlainLoading

fun main() {
    section("1. enum basics")
    val s = Status.ACTIVE
    println("s=$s name=${s.name} ordinal=${s.ordinal}")
    println("entries = ${Status.entries}")          // old style: values()
    println("valueOf(\"BLOCKED\") = ${Status.valueOf("BLOCKED")}")
    tryIt("valueOf(\"DELETED\")") { Status.valueOf("DELETED") }
    println("safe lookup = ${Status.entries.find { it.name == "DELETED" }}")

    section("2. enum with properties and functions")
    Plan.entries.forEach { println("${it.label}: ${it.price} EGP, paid=${it.isPaid()}") }

    section("3. when over an enum is exhaustive")
    Status.entries.forEach { println("$it -> ${statusColor(it)}") }

    section("4. sealed interface for UI state")
    listOf(UiState.Loading, UiState.Success(listOf("A", "B")), UiState.Error("No internet"))
        .forEach { println(render(it)) }

    section("5. sealed class with shared state")
    listOf(Shape.Circle(1.0), Shape.Rect(2.0, 3.0))
        .forEach { println("${it.name}: ${"%.2f".format(area(it))}") }

    section("6. enum or sealed?")
    println("enum   : a fixed set of VALUES, all the same shape (Status, Plan)")
    println("sealed : a fixed set of TYPES, each with its own data (Loading / Success(items) / Error(msg))")

    section("7. WATCH OUT: avoid else with sealed types")
    println(renderLazy(UiState.Error("x")))
    println("Add `data object Empty : UiState` -> render() stops compiling (good),")
    println("renderLazy() silently says 'Something else' (bad).")

    section("8. Why data object?")
    println("data object : ${UiState.Loading}")
    println("plain object: $PlainLoading")
}

private fun section(title: String) = println("\n=== $title ===")

private inline fun tryIt(label: String, block: () -> Unit) {
    try {
        block()
    } catch (e: Exception) {
        println("CRASH  $label -> ${e::class.simpleName}: ${e.message}")
    }
}
