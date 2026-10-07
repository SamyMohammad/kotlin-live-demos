package t23

/*
 * T23 · Operators, infix, typealias, value classes (bonus)
 *
 * Dart: operator +(Money other)   ->  Kotlin: operator fun plus(other: Money)
 */

data class Money(val amount: Int, val currency: String = "EGP") {
    operator fun plus(other: Money) = Money(amount + other.amount, currency)
    operator fun times(factor: Int) = Money(amount * factor, currency)
    operator fun compareTo(other: Money) = amount.compareTo(other.amount)
    override fun toString() = "$amount $currency"
}

infix fun Int.percentOf(total: Int) = total * this / 100

class MinValidator(private val min: Int) {
    operator fun invoke(value: Int) = value >= min      // call the object like a function
}

class Team(private val members: List<String>) {
    operator fun get(index: Int) = members[index]       // team[0]
    operator fun contains(name: String) = name in members // "Ali" in team
    operator fun iterator() = members.iterator()        // for (m in team)
}

typealias OnClick = () -> Unit
typealias TracksByStudent = Map<String, List<String>>

@JvmInline
value class Email(val value: String) {
    init {
        require("@" in value) { "invalid email: $value" }
    }
}

fun sendWelcome(email: Email) = println("  sending to ${email.value}")

fun main() {
    section("1. Operator overloading")
    val a = Money(100)
    val b = Money(50)
    println("a + b = ${a + b}   a * 3 = ${a * 3}   a > b = ${a > b}")

    section("2. infix functions")
    println("20 percentOf 300 = ${20 percentOf 300}")
    println("Also infix: \"Ali\" to 20 -> ${"Ali" to 20},  1 until 4 -> ${(1 until 4).toList()}")

    section("3. invoke: an object you can call")
    val isAdult = MinValidator(18)
    println("isAdult(20)=${isAdult(20)} isAdult(15)=${isAdult(15)}")

    section("4. get / contains / iterator")
    val team = Team(listOf("Mona", "Ali", "Omar"))
    println("team[0]=${team[0]}  \"Ali\" in team=${"Ali" in team}")
    for (m in team) print("$m ")
    println()

    section("5. typealias: a shorter name for a type")
    val onClick: OnClick = { println("  clicked") }
    onClick()
    val tracks: TracksByStudent = mapOf("Mona" to listOf("Flutter", "Android"))
    println("  $tracks")

    section("6. value class: type safety with no runtime cost")
    sendWelcome(Email("samy@mail.com"))
    // sendWelcome("samy@mail.com")      // ERROR: a String is not an Email
    tryIt("Email(\"nope\")") { Email("nope") }

    section("7. Next session")
    println("Coroutines: suspend fun, launch, async/await, Flow (Dart: Future, Stream).")
}

private fun section(title: String) = println("\n=== $title ===")

private inline fun tryIt(label: String, block: () -> Unit) {
    try {
        block()
    } catch (e: Exception) {
        println("CRASH  $label -> ${e::class.simpleName}: ${e.message}")
    }
}
