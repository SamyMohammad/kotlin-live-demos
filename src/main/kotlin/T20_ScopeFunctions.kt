package t20

/*
 * T20 · Scope functions: let, run, with, apply, also   Slides 74-76, 78
 *
 *  fun    | object is | returns         | typical use
 *  let    | it        | lambda result   | null checks, transform
 *  run    | this      | lambda result   | compute something from an object
 *  with   | this      | lambda result   | many calls on one object
 *  apply  | this      | the object      | configure an object (Dart: ..)
 *  also   | it        | the object      | side effects: log, validate
 */

data class Person(var name: String, var age: Int = 0, var city: String = "")

fun findName(found: Boolean): String? = if (found) "kotlin" else null

fun main() {
    section("1. let: null-check or transform (it -> result)")
    val found: String? = findName(true)
    val length = found?.let { it.length }
    println("length = $length")
    found?.let { println("Hello ${it.replaceFirstChar { c -> c.uppercase() }}") }

    section("2. apply: configure an object (this -> the object)")
    val p = Person("Ali").apply {
        age = 25
        city = "Alexandria"
    }
    println(p)

    section("3. also: side effects (it -> the object)")
    val list = mutableListOf(1, 2)
        .also { println("  created: $it") }
        .apply { add(3) }
    println("list = $list")

    section("4. run: compute a result (this -> result)")
    val bio = p.run { "$name, $age, from $city" }
    println(bio)
    val total = run {                    // run without a receiver = a scoped block
        val a = 10
        val b = 20
        a + b
    }
    println("total = $total")

    section("5. with: many calls on one object")
    val report = with(StringBuilder()) {
        append("Name: ${p.name}\n")
        append("Age : ${p.age}")
        toString()
    }
    println(report)

    section("6. takeIf / takeUnless")
    val adult = p.takeIf { it.age >= 18 }
    val input = "   ".takeUnless { it.isBlank() } ?: "default"
    println("adult = ${adult?.name}  input = $input")

    section("7. Common real-world combo")
    val user = findName(false)?.let { Person(it) } ?: Person("Guest")
    println("user = $user")

    section("8. WATCH OUT: let without ?")
    val nothing: String? = findName(false)
    nothing.let { println("  let without ?. runs anyway: $it") }
    nothing?.let { println("  never printed") }

    section("9. WATCH OUT: what comes back?")
    val a = "Hi".apply { length }         // returns "Hi"
    val b = "Hi".let { it.length }        // returns 2
    println("apply -> $a   let -> $b")

    section("10. WATCH OUT: local names hide `this` members")
    val city = "Cairo"
    val where = p.run { "lives in $city" }          // the LOCAL city wins, not p.city!
    println("$where   <- but p.city is ${p.city}")
    println("Fix: write this.city inside the block -> ${p.run { "lives in ${this.city}" }}")

    section("11. WATCH OUT: don't over-nest")
    println("p.let { it.apply { also { run { } } } }  <- unreadable. One level is usually enough.")
}

private fun section(title: String) = println("\n=== $title ===")
