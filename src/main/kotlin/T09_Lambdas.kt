package t09

/*
 * T09 · Lambdas, higher-order functions, function types   Slides 44-46, 50-51
 *
 * Dart                          ->  Kotlin
 * (x) => x * 2                  ->  { x -> x * 2 }    or  { it * 2 }
 * void Function() / VoidCallback->  () -> Unit
 * int Function(int)             ->  (Int) -> Int
 */

fun multiply(a: Int, b: Int) = a * b

fun operate(a: Int, b: Int, op: (Int, Int) -> Int): Int = op(a, b)

fun twice(action: () -> Unit) {
    action()
    action()
}

// Looks like Compose's Button: two lambdas, the LAST one can trail
fun button(text: String, onClick: () -> Unit, content: (String) -> Unit) {
    content(text)
    onClick()
}

fun multiplier(factor: Int): (Int) -> Int = { it * factor }

fun printPositivesSkip(nums: List<Int>) {
    nums.forEach {
        if (it < 0) return@forEach        // skips this item only
        print("$it ")
    }
    println("<- reached the end")
}

fun printUntilNegative(nums: List<Int>) {
    nums.forEach {
        if (it < 0) return                // exits the WHOLE function!
        print("$it ")
    }
    println("never printed")
}

fun main() {
    section("1. Lambda syntax")
    val double: (Int) -> Int = { x -> x * 2 }
    val add = { a: Int, b: Int -> a + b }
    val hello = { println("Hello from a lambda") }
    val inc: (Int) -> Int = { it + 1 }      // one parameter -> it
    val describe = { x: Int ->
        val y = x * 2
        "x=$x, doubled=$y"                  // last line is the result, no return
    }
    println("double(4)=${double(4)} add(2, 3)=${add(2, 3)} inc(9)=${inc(9)}")
    hello()
    println(describe(5))

    section("2. Higher-order functions")
    println("operate(6, 3) { x, y -> x - y } = ${operate(6, 3) { x, y -> x - y }}")
    println("operate(6, 3, ::multiply)      = ${operate(6, 3, ::multiply)}")

    section("3. Trailing lambdas: the secret of Compose syntax")
    twice({ print("A ") })
    twice() { print("B ") }
    twice { print("C ") }                   // all three are the same call
    println()
    button("Tap me", onClick = { println("  clicked!") }) { label ->
        println("  drawing button: $label")
    }
    // Compose: Column { Text("Hi") }   Button(onClick = { }) { Text("Tap") }

    section("4. Function types as parameters (Compose callbacks)")
    val onClick: () -> Unit = { println("clicked") }
    val onValueChange: (String) -> Unit = { println("typed: $it") }
    val isValid: (String) -> Boolean = { it.length >= 3 }
    onClick()
    onValueChange("Kot")
    println("isValid(\"Ko\") = ${isValid("Ko")}")

    section("5. Nullable function types")
    var onDismiss: (() -> Unit)? = null
    onDismiss?.invoke()                     // nothing happens
    onDismiss = { println("dismissed") }
    onDismiss?.invoke()

    section("6. Returning a function")
    val triple = multiplier(3)
    println("triple(5) = ${triple(5)}")

    section("7. Closures capture variables")
    var counter = 0
    val increment = { counter++ }
    repeat(3) { increment() }
    println("counter = $counter")

    section("8. Destructuring and _ in lambdas")
    mapOf("a" to 1, "b" to 2).forEach { (key, value) -> println("$key=$value") }
    mapOf("x" to 10).forEach { (_, value) -> println("value only: $value") }

    section("9. WATCH OUT: return inside forEach")
    printPositivesSkip(listOf(1, -2, 3))
    printUntilNegative(listOf(1, -2, 3))
    println("<- printUntilNegative stopped at -2 and left the function")
}

private fun section(title: String) = println("\n=== $title ===")
