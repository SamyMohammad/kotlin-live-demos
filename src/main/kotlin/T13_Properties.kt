package t13

/*
 * T13 · Properties: getters, setters, lateinit, lazy   Slides 27, 57
 *
 * Kotlin has properties, not fields. Every var gets a getter and a setter.
 * Dart: int _count; int get count => _count; set count(v) {...}
 */

class Rect(val width: Int, val height: Int) {
    val area: Int
        get() = width * height              // computed on every read
    val isSquare get() = width == height
}

class Counter {
    var count = 0
        set(value) {
            if (value >= 0) field = value   // field = the hidden storage
            else println("  rejected $value")
        }

    var total = 0
        private set                         // read anywhere, write only inside

    fun add(n: Int) {
        count += n
        total += n
    }
}

class Temperature {
    var celsius = 0.0
    var fahrenheit: Double                  // no storage of its own
        get() = celsius * 9 / 5 + 32
        set(value) {
            celsius = (value - 32) * 5 / 9
        }
}

class Profile {
    var name: String = ""
        get() = field.uppercase()
        set(value) {
            field = value.trim()
        }
}

class Screen {
    lateinit var title: String
    val heavy: String by lazy {
        println("  computing heavy value...")
        "READY"
    }

    fun setup() {
        title = "Home"
    }

    fun isReady() = this::title.isInitialized
}

/* WATCH OUT: this setter calls itself forever -> StackOverflowError
class Broken {
    var x = 0
        set(value) { x = value }            // should be: field = value
}
*/

fun main() {
    section("1. Computed getter")
    val r = Rect(3, 4)
    println("area=${r.area} isSquare=${r.isSquare}")

    section("2. Setter with validation (field)")
    val c = Counter()
    c.count = 5
    c.count = -1
    println("count=${c.count}")

    section("3. private set")
    c.add(10)
    println("total=${c.total}")
    // c.total = 99             // ERROR: cannot access 'total': its setter is private

    section("4. Property with no storage (computed both ways)")
    val t = Temperature()
    t.celsius = 100.0
    println("100 C = ${t.fahrenheit} F")
    t.fahrenheit = 32.0
    println("32 F  = ${t.celsius} C")

    section("5. Custom get and set together")
    val p = Profile()
    p.name = "   samy  "
    println("name='${p.name}'")

    section("6. lateinit vs lazy")
    val s = Screen()
    println("ready before setup? ${s.isReady()}")
    s.setup()
    println("ready after setup?  ${s.isReady()} title=${s.title}")
    println("heavy 1st: ${s.heavy}")
    println("heavy 2nd: ${s.heavy}   <- computed only once")

    section("7. In Compose")
    println("var uiState by mutableStateOf(UiState())  private set")
    println("-> the screen can read it, only the ViewModel can change it")
}

private fun section(title: String) = println("\n=== $title ===")
