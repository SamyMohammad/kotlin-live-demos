package t22

/*
 * T22 · Exceptions and error handling (bonus)
 *
 * Dart try / catch / finally  ->  same in Kotlin, and try is an expression.
 * Dart on FormatException catch (e)  ->  catch (e: NumberFormatException)
 */

class InsufficientFundsException(val needed: Int) : Exception("Need $needed EGP more")

fun withdraw(balance: Int, amount: Int): Int {
    if (amount > balance) throw InsufficientFundsException(amount - balance)
    return balance - amount
}

fun setAge(age: Int) {
    require(age >= 0) { "age must be >= 0, was $age" }       // bad argument
}

fun startPayment(connected: Boolean) {
    check(connected) { "not connected to the server" }       // bad state
}

fun findUser(id: Int): String? = if (id == 1) "Mona" else null

fun main() {
    section("1. try / catch / finally")
    try {
        withdraw(100, 150)
    } catch (e: InsufficientFundsException) {
        println("  caught: ${e.message} (needed=${e.needed})")
    } finally {
        println("  finally always runs")
    }

    section("2. try is an expression")
    val n = try {
        "42x".toInt()
    } catch (e: NumberFormatException) {
        -1
    }
    println("n = $n")

    section("3. require / check / error")
    tryIt("setAge(-1)") { setAge(-1) }               // IllegalArgumentException
    tryIt("startPayment(false)") { startPayment(false) }  // IllegalStateException
    tryIt("error(\"boom\")") { error("boom") }         // IllegalStateException

    section("4. runCatching: errors as values")
    val r = runCatching { "abc".toInt() }
    println("isFailure=${r.isFailure} getOrNull=${r.getOrNull()} getOrElse=${r.getOrElse { 0 }}")
    r.onFailure { println("  failed with ${it::class.simpleName}") }
    val ok = runCatching { "7".toInt() }.map { it * 2 }
    println("ok = ${ok.getOrThrow()}")

    section("5. throw is an expression too (type Nothing)")
    tryIt("findUser(2)") {
        val name: String = findUser(2) ?: throw NoSuchElementException("user 2 not found")
        println(name)
    }

    section("6. WATCH OUT")
    println("- Kotlin has no checked exceptions: nothing forces you to catch. Handle errors on purpose.")
    println("- Catch the specific exception, not Exception, or you hide real bugs.")
}

private fun section(title: String) = println("\n=== $title ===")

private inline fun tryIt(label: String, block: () -> Unit) {
    try {
        block()
    } catch (e: Exception) {
        println("CRASH  $label -> ${e::class.simpleName}: ${e.message}")
    }
}
