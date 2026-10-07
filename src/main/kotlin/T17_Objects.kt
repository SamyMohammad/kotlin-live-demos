package t17

/*
 * T17 · object, companion object, object expressions   Slides 68-69
 *
 * Dart static            ->  companion object
 * Dart singleton pattern ->  object
 * Dart has no anonymous classes; Kotlin has object expressions.
 */

object Logger {                                   // singleton in one word
    private var count = 0
    fun log(msg: String) {
        count++
        println("  [$count] $msg")
    }
}

class ApiClient private constructor(val baseUrl: String) {
    companion object {
        const val DEFAULT_URL = "https://api.example.com"
        private var created = 0

        fun create(url: String = DEFAULT_URL): ApiClient {
            created++
            return ApiClient(url)
        }

        fun createdCount() = created
    }
}

class Config {
    companion object Factory {                    // a companion can have a name
        fun default() = Config()
    }
}

interface ClickListener {
    fun onClick(id: Int)
}

interface TextWatcherLike {                       // like Android's TextWatcher: 3 methods
    fun before(s: String)
    fun on(s: String)
    fun after(s: String)
}

fun interface OnTap {                             // one method -> can take a lambda
    fun tap(x: Int)
}

fun registerListener(listener: ClickListener) = listener.onClick(7)
fun registerTap(onTap: OnTap) = onTap.tap(3)

fun makeListener(): ClickListener = object : ClickListener {
    override fun onClick(id: Int) {}
}

fun main() {
    section("1. object = singleton (created lazily on first use)")
    Logger.log("first")
    Logger.log("second")
    println("Logger === Logger: ${Logger === Logger}")

    section("2. companion object (Dart: static)")
    // ApiClient("x")           // ERROR: cannot access the constructor: it is private
    val c1 = ApiClient.create()
    val c2 = ApiClient.create("https://test.example.com")
    println("c1=${c1.baseUrl} c2=${c2.baseUrl}")
    println("DEFAULT_URL=${ApiClient.DEFAULT_URL} created=${ApiClient.createdCount()}")

    section("3. Named companion")
    println("Config.default()         -> ${Config.default()::class.simpleName}")
    println("Config.Factory.default() -> ${Config.Factory.default()::class.simpleName}")

    section("4. Object expression: an anonymous object on the spot")
    registerListener(object : ClickListener {
        override fun onClick(id: Int) = println("  clicked item $id")
    })
    val watcher = object : TextWatcherLike {
        override fun before(s: String) = println("  before: $s")
        override fun on(s: String) = println("  on: $s")
        override fun after(s: String) = println("  after: $s")
    }
    watcher.before("")
    watcher.on("K")
    watcher.after("Ko")

    section("5. Ad-hoc object with no type")
    val point = object {
        val x = 3
        val y = 4
    }
    println("x + y = ${point.x + point.y}")

    section("6. fun interface + lambda (SAM conversion)")
    registerTap { println("  tapped at $it") }
    val tap = OnTap { x -> println("  tap $x") }
    tap.tap(9)

    section("7. Java single-method interfaces work the same")
    val task = Runnable { println("  running a Java Runnable") }
    task.run()
    val byLength = Comparator<String> { a, b -> a.length - b.length }
    println("  sorted by length: ${listOf("ccc", "a", "bb").sortedWith(byLength)}")

    section("8. WATCH OUT: object expressions are NOT singletons")
    println("same instance? ${makeListener() === makeListener()}")
}

private fun section(title: String) = println("\n=== $title ===")
