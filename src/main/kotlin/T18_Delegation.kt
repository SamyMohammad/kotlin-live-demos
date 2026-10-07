package t18

import kotlin.properties.Delegates
import kotlin.reflect.KProperty

/*
 * T18 · Delegation with `by`                         Slides 70-71
 *
 * Class delegation    : class Repo(l: Logger) : Logger by l
 * Property delegation : val x by lazy { }  /  var count by remember { mutableStateOf(0) }
 * No built-in Dart equivalent.
 */

interface Logger {
    fun log(msg: String)
}

class ConsoleLogger(private val prefix: String) : Logger {
    override fun log(msg: String) = println("  $prefix $msg")
}

class Repo(logger: Logger) : Logger by logger {   // compiler writes the forwarding
    fun load() = log("Loading users...")
}

class LoudRepo(logger: Logger) : Logger by logger {
    override fun log(msg: String) = println("  !!! ${msg.uppercase()}")   // override one, keep the rest
}

// A tiny version of Compose's MutableState, to show what `by` really calls
class MyState<T>(private var value: T) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        println("    (get ${property.name} = $value)")
        return value
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, newValue: T) {
        println("    (set ${property.name} = $newValue -> Compose would redraw here)")
        value = newValue
    }
}

fun <T> myMutableStateOf(initial: T) = MyState(initial)

class User(map: Map<String, Any?>) {
    val name: String by map
    val age: Int by map
}

class Settings {
    var theme: String by Delegates.observable("light") { prop, old, new ->
        println("  ${prop.name}: $old -> $new")
    }
    var volume: Int by Delegates.vetoable(50) { _, _, new -> new in 0..100 }
}

fun main() {
    section("1. Class delegation: Logger by logger")
    val repo = Repo(ConsoleLogger("[repo]"))
    repo.load()
    repo.log("direct call works too")

    section("2. Override some methods, delegate the rest")
    LoudRepo(ConsoleLogger("[x]")).log("careful")

    section("3. by lazy: computed once, on first use")
    val config by lazy {
        println("  loading config...")
        "config v1"
    }
    println("1st: $config")
    println("2nd: $config")

    section("4. Delegates.observable and vetoable")
    val s = Settings()
    s.theme = "dark"
    s.theme = "blue"
    s.volume = 80
    s.volume = 150                                  // rejected: not in 0..100
    println("volume = ${s.volume}")

    section("5. Delegate to a map (handy with JSON)")
    val u = User(mapOf("name" to "Mona", "age" to 22))
    println("${u.name} is ${u.age}")

    section("6. How `by mutableStateOf` works in Compose (mini version)")
    var count by myMutableStateOf(0)
    count++                                         // a get, then a set
    println("count = $count")
    // Real Compose:
    //   var count by remember { mutableStateOf(0) }
    // WATCH OUT: needs these imports or it won't compile:
    //   import androidx.compose.runtime.getValue
    //   import androidx.compose.runtime.setValue
    // Without `by`: val count = remember { mutableStateOf(0) }  ->  count.value++
}

private fun section(title: String) = println("\n=== $title ===")
