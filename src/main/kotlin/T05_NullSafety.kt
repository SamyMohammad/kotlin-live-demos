package t05

import javainterop.JavaUser

/*
 * T05 · Null safety                                  Slides 25-30
 *
 * Dart  ->  Kotlin
 * ?.    ->  ?.    (safe call, same)
 * ??    ->  ?:    (Elvis)
 * !     ->  !!    (not-null assertion, crashes if wrong)
 * late  ->  lateinit var / by lazy
 */

class User(var name: String?)

lateinit var token: String

fun main() {
    section("1. Nullable vs non-null types")
    var city: String = "Alex"
    // city = null                    // ERROR: null can not be a value of a non-null type
    var nickname: String? = null      // ? = can hold null
    println("city=$city nickname=$nickname")

    section("2. Safe call ?.")
    println("nickname?.length = ${nickname?.length}")   // null, no crash
    nickname = "Sam"
    println("nickname?.length = ${nickname?.length}")
    city = "Cairo"

    section("3. Elvis ?:  (Dart: ??)")
    val empty: String? = readMaybe(false)
    println("length or 0   = ${empty?.length ?: 0}")
    println("name or Guest = ${empty ?: "Guest"}")

    section("4. Not-null assertion !!  (Dart: !)")
    val sure: String? = readMaybe(true)
    println("sure!!.length = ${sure!!.length}")
    tryIt("empty!!.length") { empty!!.length }          // NullPointerException

    section("5. Smart cast after a null check")
    val input: String? = readMaybe(true)
    if (input != null) {
        println("input.length = ${input.length}")       // input is String here
    }

    section("6. ?.let { } runs only when not null")
    input?.let { println("let got: $it") }
    empty?.let { println("never printed") }
    val label = empty?.let { "Hi $it" } ?: "No name"
    println("label = $label")

    section("7. Elvis with return / throw (early exit)")
    println(greet(null))
    println(greet("Ali"))
    tryIt("requireName(null)") { requireName(null) }

    section("8. Chained safe calls")
    val users: List<User?> = listOf(User("Mona"), User(null), null)
    users.forEach { println("upper = ${it?.name?.uppercase() ?: "unknown"}") }

    section("9. WATCH OUT: no smart cast on var properties")
    val user = User("Ali")
    // if (user.name != null) println(user.name.length)
    // ERROR: smart cast is impossible, 'name' is a mutable property
    user.name?.let { println("fix 1, ?.let     : ${it.length}") }
    val localName = user.name
    if (localName != null) println("fix 2, local val : ${localName.length}")

    section("10. lateinit")
    tryIt("read token before init") { println(token) }  // UninitializedPropertyAccessException
    println("isInitialized = ${::token.isInitialized}")
    token = "abc123"
    println("token=$token isInitialized=${::token.isInitialized}")
    // lateinit only works with var and non-primitive types:
    // lateinit var count: Int        // ERROR: not allowed on primitive types

    section("11. WATCH OUT: Java returns platform types")
    val javaUser = JavaUser(null)
    val fromJava = javaUser.name         // type is String! (Kotlin doesn't know)
    tryIt("fromJava.length") { fromJava.length }
    val safe: String? = javaUser.name    // declare it nullable to stay safe
    println("safe?.length = ${safe?.length}")

    section("12. Collections and nulls")
    val mixed = listOf("a", null, "b", null)
    println("filterNotNull = ${mixed.filterNotNull()}")
    println("isNullOrBlank = ${empty.isNullOrBlank()}")
    println("orEmpty       = '${empty.orEmpty()}'")
}

// Returns a String or null; hides the value from the compiler so the demos stay realistic.
fun readMaybe(present: Boolean): String? = if (present) "Kotlin" else null

fun greet(name: String?): String {
    val n = name ?: return "Hello, stranger"
    return "Hello, $n"
}

fun requireName(name: String?): String =
    name ?: throw IllegalArgumentException("name is required")

private fun section(title: String) = println("\n=== $title ===")

private inline fun tryIt(label: String, block: () -> Unit) {
    try {
        block()
    } catch (e: Exception) {
        println("CRASH  $label -> ${e::class.simpleName}: ${e.message}")
    }
}
