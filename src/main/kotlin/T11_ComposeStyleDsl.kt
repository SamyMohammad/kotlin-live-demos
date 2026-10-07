package t11

/*
 * T11 · A mini "Compose" in plain Kotlin             Slides 45, 48, 73, 77
 *
 * Real Compose functions are @Composable and draw pixels.
 * Ours print a tree to the console, but the KOTLIN is the same:
 *  - Column { } / Row { }               -> trailing lambdas
 *  - content: RowScope.() -> Unit       -> lambda with receiver
 *  - Modifier.padding(...).background() -> extension functions
 *  - 16.dp                              -> extension property on Int
 *  - modifier: Modifier = Modifier      -> default argument
 */

// ---------- Modifier: a chain built from extension functions ----------
interface Modifier {
    val parts: List<String>

    companion object : Modifier {            // `Modifier` alone = the empty modifier
        override val parts: List<String> = emptyList()
    }
}

private class ChainedModifier(override val parts: List<String>) : Modifier

fun Modifier.then(part: String): Modifier = ChainedModifier(parts + part)
fun Modifier.padding(all: Dp): Modifier = then("padding($all)")
fun Modifier.background(color: String): Modifier = then("background($color)")
fun Modifier.fillMaxWidth(): Modifier = then("fillMaxWidth()")
fun Modifier.describe(): String = if (parts.isEmpty()) "" else "Modifier." + parts.joinToString(".")

// ---------- Dp: an extension property on Int ----------
@JvmInline
value class Dp(val value: Int) {
    override fun toString() = "$value.dp"
}

val Int.dp: Dp get() = Dp(this)

// ---------- Scopes: weight() lives ONLY inside these ----------
class RowScope {
    fun Modifier.weight(weight: Float): Modifier = then("weight(${weight}f)")
}

class ColumnScope {
    fun Modifier.weight(weight: Float): Modifier = then("weight(${weight}f)")
}

// ---------- "Composables" ----------
private var depth = 0
private fun line(text: String) = println("  ".repeat(depth) + text)

fun Text(text: String, modifier: Modifier = Modifier) = line("Text(\"$text\")  ${modifier.describe()}")

fun Row(modifier: Modifier = Modifier, content: RowScope.() -> Unit) {
    line("Row  ${modifier.describe()}")
    depth++
    RowScope().content()
    depth--
}

fun Column(modifier: Modifier = Modifier, content: ColumnScope.() -> Unit) {
    line("Column  ${modifier.describe()}")
    depth++
    ColumnScope().content()
    depth--
}

fun Button(onClick: () -> Unit, modifier: Modifier = Modifier, content: () -> Unit) {
    line("Button  ${modifier.describe()}")
    depth++
    content()
    depth--
    onClick()                                 // pretend the user tapped it
}

fun main() {
    section("1. A Compose-like screen, in plain Kotlin")
    Column(Modifier.padding(16.dp)) {
        Text("Welcome, Samy", Modifier.background("Yellow").padding(8.dp))
        Row(Modifier.fillMaxWidth()) {
            Text("1f", Modifier.weight(1f))   // weight() exists: this is RowScope
            Text("2f", Modifier.weight(2f))
        }
        Button(onClick = { line("-> clicked!") }) {
            Text("Tap me")
        }
    }

    section("2. WATCH OUT: weight() only exists inside Row / Column")
    // Text("x", Modifier.weight(1f))   // ERROR: unresolved reference 'weight'
    println("Uncomment the line above to see the compile error live.")

    section("3. WATCH OUT: Modifier order matters (read top to bottom)")
    println(Modifier.padding(16.dp).background("Yellow").describe())
    println("  -> padding first, yellow only inside it")
    println(Modifier.background("Yellow").padding(16.dp).describe())
    println("  -> yellow everywhere, content inset by 16")

    section("4. The Kotlin features behind it")
    println(
        """
        Column { }                     trailing lambda
        content: RowScope.() -> Unit   lambda with receiver
        Modifier.padding(...)          extension function
        16.dp                          extension property on Int
        modifier: Modifier = Modifier  default argument
        """.trimIndent()
    )
}

private fun section(title: String) = println("\n=== $title ===")
