package t10

/*
 * T10 · Lambda with receiver                         Slide 47
 *
 * (T) -> Unit   : the object comes in as `it`
 * T.() -> Unit  : the object becomes `this` inside the braces
 *
 * No direct Dart equivalent (closest feeling: the cascade ..).
 * apply, with, run, buildString and every Compose scope use this.
 */

// apply is just a lambda with receiver. Here is our own version:
inline fun <T> T.myApply(block: T.() -> Unit): T {
    block()
    return this
}

class Settings {
    var theme = "light"
    var fontSize = 14
    override fun toString() = "Settings(theme=$theme, fontSize=$fontSize)"
}

// ---- A tiny HTML DSL, built the same way Compose is ----
class Html {
    private val sb = StringBuilder()
    fun h1(text: String) { sb.append("<h1>$text</h1>\n") }
    fun p(text: String) { sb.append("<p>$text</p>\n") }
    fun ul(block: Ul.() -> Unit) {
        val ul = Ul()
        ul.block()
        sb.append(ul.render())
    }
    override fun toString() = sb.toString()
}

class Ul {
    private val items = mutableListOf<String>()
    fun li(text: String) { items += text }
    fun render() = "<ul>\n" + items.joinToString("") { "  <li>$it</li>\n" } + "</ul>\n"
}

fun html(block: Html.() -> Unit): Html = Html().apply(block)

fun main() {
    section("1. Normal lambda vs lambda with receiver")
    val normal: (StringBuilder) -> Unit = { it.append("normal ") }
    val withReceiver: StringBuilder.() -> Unit = { append("receiver ") }  // this.append
    val sb = StringBuilder()
    normal(sb)
    sb.withReceiver()          // call it like a member...
    withReceiver(sb)           // ...or pass the receiver as the first argument
    println(sb)

    section("2. buildString: a lambda with receiver from the stdlib")
    val s = buildString {
        append("Hello, ")       // this is a StringBuilder
        append("Kotlin")
    }
    println(s)

    section("3. apply is built on this. Our own myApply:")
    val settings = Settings().myApply {
        theme = "dark"          // this.theme
        fontSize = 18
    }
    println(settings)

    section("4. with / run also take T.() -> R")
    val summary = with(settings) { "theme=$theme size=$fontSize" }
    println(summary)

    section("5. A tiny DSL, like Compose")
    val page = html {
        h1("Kotlin for Flutter devs")
        p("Lambdas with receivers build DSLs.")
        ul {
            li("val and var")
            li("when")
            li("lambdas")
        }
    }
    print(page)
    // li("oops")               // ERROR: li() only exists inside ul { }

    section("6. Why it matters")
    println("Row { }, Column { }, LazyColumn { } all give you a scope as `this`.")
    println("That's why weight() works inside Row and nowhere else. See T11.")
    // FYI: real DSLs add @DslMarker so you can't call outer-scope functions by mistake.
}

private fun section(title: String) = println("\n=== $title ===")
