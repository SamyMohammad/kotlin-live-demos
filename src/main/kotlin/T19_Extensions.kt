package t19

/*
 * T19 · Extension functions and properties           Slides 73, 78
 *
 * Dart: extension StringX on String { ... }
 * Kotlin: fun String.initials() = ...   (no wrapper block)
 */

fun String.initials(): String =
    split(" ").filter { it.isNotBlank() }.map { it.first().uppercaseChar() }.joinToString("")

val String.wordCount: Int
    get() = trim().split(Regex("\\s+")).size

fun Int.isEven() = this % 2 == 0

fun String?.orDash(): String = if (this.isNullOrBlank()) "-" else this   // nullable receiver

fun <T> List<T>.secondOrNull(): T? = if (size >= 2) this[1] else null    // generic extension

@JvmInline
value class Dp(val value: Int) {
    override fun toString() = "$value.dp"
}

val Int.dp: Dp get() = Dp(this)          // exactly how Compose's 16.dp works

class Api {
    companion object
}

fun Api.Companion.defaultTimeout() = 30   // extend a companion -> Api.defaultTimeout()

class Box {
    private val secret = "hidden"
    fun show() = "member"
}

@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
fun Box.show() = "extension"              // never called: the member wins
fun Box.paint(color: String) = "Box painted $color"
// fun Box.reveal() = secret              // ERROR: extensions can't see private members

open class Shape
class Circle : Shape()

fun Shape.kind() = "Shape"
fun Circle.kind() = "Circle"

fun main() {
    section("1. Extension functions")
    println("\"samy mohammad\".initials() = ${"samy mohammad".initials()}")
    println("4.isEven() = ${4.isEven()}")

    section("2. Extension properties")
    println("wordCount = ${"Kotlin is pretty neat".wordCount}")

    section("3. Nullable receiver")
    val missing: String? = null
    println("missing.orDash() = ${missing.orDash()}   \"Kotlin\".orDash() = ${"Kotlin".orDash()}")

    section("4. Generic extension")
    println("secondOrNull = ${listOf(10, 20, 30).secondOrNull()}  empty -> ${emptyList<Int>().secondOrNull()}")

    section("5. Like Compose: 16.dp")
    val padding = 16.dp
    println("padding = $padding")

    section("6. Extending a companion")
    println("Api.defaultTimeout() = ${Api.defaultTimeout()}")

    section("7. WATCH OUT: members always win")
    println("Box().show() = ${Box().show()}")
    println("Box().paint() = ${Box().paint("red")}   <- no member with that name, extension runs")

    section("8. WATCH OUT: extensions are resolved statically")
    val s: Shape = Circle()
    println("s.kind() = ${s.kind()}   <- uses the declared type (Shape), not the real object")
}

private fun section(title: String) = println("\n=== $title ===")
