package t21

/*
 * T21 · Generics (bonus, not in the slides)
 *
 * Dart: class Box<T>, T extends num   ->  Kotlin: class Box<T>, T : Number
 * You meet generics daily: List<User>, StateFlow<UiState>, Result<T>.
 */

class Box<T>(val value: T) {
    fun <R> map(transform: (T) -> R): Box<R> = Box(transform(value))
    override fun toString() = "Box($value)"
}

fun <T> firstOrDefault(list: List<T>, default: T): T = list.firstOrNull() ?: default

fun <T : Comparable<T>> biggest(a: T, b: T, c: T): T = maxOf(a, maxOf(b, c))

fun <T : Number> sumAll(nums: List<T>): Double = nums.sumOf { it.toDouble() }

// A common pattern for API calls
sealed interface ApiResult<out T> {
    data class Ok<T>(val value: T) : ApiResult<T>
    data class Err(val error: String) : ApiResult<Nothing>
}

fun parseAge(text: String): ApiResult<Int> =
    text.toIntOrNull()?.let { ApiResult.Ok(it) } ?: ApiResult.Err("'$text' is not a number")

inline fun <reified T> List<Any>.onlyOf(): List<T> = filterIsInstance<T>()

open class Animal(val name: String)
class Cat(name: String) : Animal(name)

fun printNames(animals: List<Animal>) = println("  " + animals.joinToString { it.name })
fun printSize(list: List<*>) = println("  size = ${list.size}")

fun main() {
    section("1. Generic class")
    val box = Box(42)
    val text = box.map { "value is $it" }
    println("$box -> $text")

    section("2. Generic functions")
    println(firstOrDefault(listOf("a", "b"), "none"))
    println(firstOrDefault(emptyList(), "none"))

    section("3. Constraints")
    println("biggest(3, 9, 4)       = ${biggest(3, 9, 4)}")
    println("biggest(\"b\",\"c\",\"a\")  = ${biggest("b", "c", "a")}")
    println("sumAll(1, 2.5, 3L)     = ${sumAll(listOf(1, 2.5, 3L))}")
    // biggest(listOf(1), listOf(2), listOf(3))  // ERROR: List is not Comparable

    section("4. Generic sealed result")
    listOf("21", "abc").forEach { input ->
        when (val r = parseAge(input)) {
            is ApiResult.Ok -> println("  ok: ${r.value}")
            is ApiResult.Err -> println("  error: ${r.error}")
        }
    }

    section("5. reified: the type survives at runtime")
    val mixed: List<Any> = listOf(1, "two", 3, "four")
    println("strings = ${mixed.onlyOf<String>()}  ints = ${mixed.onlyOf<Int>()}")

    section("6. Variance (FYI)")
    val cats = listOf(Cat("Tom"), Cat("Kitty"))
    printNames(cats)                  // List<Cat> works as List<Animal>: List is `out`
    val mutableCats = mutableListOf(Cat("Tom"))
    // val animals: MutableList<Animal> = mutableCats   // ERROR: MutableList is invariant
    printSize(mutableCats)            // List<*>: "a list of something"
}

private fun section(title: String) = println("\n=== $title ===")
