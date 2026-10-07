package t06

/*
 * T06 · Functions, named arguments, varargs          Slides 32-35, 40
 *
 * Dart                               ->  Kotlin
 * int add(int a, int b) { ... }      ->  fun add(a: Int, b: Int): Int { ... }
 * int sq(int x) => x * x;            ->  fun sq(x: Int) = x * x
 * void log(String m)                 ->  fun log(m: String)        (returns Unit)
 * {String greeting = 'Hello'}        ->  greeting: String = "Hello" (any param can be named)
 */

fun add(a: Int, b: Int): Int {
    return a + b
}

fun square(x: Int) = x * x                 // expression body, type inferred

fun log(msg: String) {                     // returns Unit (Dart: void)
    println("[log] $msg")
}

fun greet(name: String, greeting: String = "Hello", punctuation: String = "!") =
    "$greeting, $name$punctuation"

fun sum(vararg nums: Int): Int = nums.sum()   // nums is an IntArray inside

fun <T> printAll(label: String, vararg items: T) {
    println("$label: ${items.joinToString()}")
}

fun tag(vararg words: String, separator: String) = words.joinToString(separator)

fun fail(message: String): Nothing = throw IllegalStateException(message)

fun factorial(n: Int): Long = if (n <= 1) 1 else n * factorial(n - 1)

fun main() {
    section("1. Block body, expression body, Unit")
    println("add(2, 3)  = ${add(2, 3)}")
    println("square(4)  = ${square(4)}")
    log("Unit means 'returns nothing useful'")
    val result: Unit = log("even Unit is a real value")
    println("result = $result")

    section("2. Default and named arguments")
    println(greet("Ali"))
    println(greet("Ali", "Hi"))
    println(greet("Ali", punctuation = "?"))
    println(greet(punctuation = ".", name = ""))   // any order when named
    // Dart needs { } to make params named. In Kotlin every param can be named.

    section("3. Varargs")
    println("sum()        = ${sum()}")
    println("sum(1, 2, 3) = ${sum(1, 2, 3)}")
    val more = intArrayOf(4, 5)
    println("sum(1, *more) = ${sum(1, *more)}")          // * spreads an array
    val fromList = listOf(7, 8)
    println("sum(*list)   = ${sum(*fromList.toIntArray())}")
    printAll("names", "Mona", "Ali", "Omar")
    // You use varargs every day: listOf(1, 2, 3) is listOf(vararg elements: T)

    section("4. Local functions")
    fun isValid(username: String): Boolean {
        fun notBlank() = username.isNotBlank()
        fun shortEnough() = username.length <= 10
        return notBlank() && shortEnough()
    }
    println("isValid(\"samy\") = ${isValid("samy")}")
    println("isValid(\"\")     = ${isValid("")}")

    section("5. Nothing: a function that never returns")
    tryIt("fail(\"Boom\")") { fail("Boom") }

    section("6. Recursion")
    println("factorial(10) = ${factorial(10)}")

    section("7. Functions are values (more in T09)")
    val op: (Int, Int) -> Int = ::add      // function reference
    println("op(10, 5) = ${op(10, 5)}")

    section("8. WATCH OUT")
    // A) Named arguments don't work when calling Java code:
    // Math.max(a = 1, b = 2)   // ERROR: Java methods can't be called with named arguments
    println("Math.max(1, 2) = ${Math.max(1, 2)}")
    // B) Parameters after a vararg must be passed by name:
    println(tag("a", "b", "c", separator = "-"))
    // C) Only ONE vararg per function.
}

private fun section(title: String) = println("\n=== $title ===")

private inline fun tryIt(label: String, block: () -> Unit) {
    try {
        block()
    } catch (e: Exception) {
        println("CRASH  $label -> ${e::class.simpleName}: ${e.message}")
    }
}
