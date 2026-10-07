package t12

/*
 * T12 · Classes, constructors, init, visibility      Slides 53-56, 58
 *
 * Dart                                  ->  Kotlin
 * class Car { final String brand; ... } ->  class Car(val brand: String)
 * User.fromJson(...) named constructor  ->  companion object { fun fromJson(...) }
 * _private                              ->  private / protected / internal
 */

class Car(val brand: String, var speed: Int = 0) {     // primary constructor
    fun accelerate(by: Int) {
        speed += by
    }

    override fun toString() = "Car(brand=$brand, speed=$speed)"
}

class Person(val name: String) {
    val greeting: String

    init {
        println("  init 1: name=$name")
        greeting = "Hi, $name"
    }

    val nameLength = name.length.also { println("  property initializer: nameLength=$it") }

    init {
        println("  init 2: runs after the property above (top to bottom)")
    }
}

class User(val name: String, val age: Int = 18) {
    init {
        println("  init: $name")
    }

    constructor(json: Map<String, Any>) : this(json["name"] as String, json["age"] as Int) {
        println("  secondary constructor body runs AFTER init")
    }

    companion object {
        fun fromJson(json: Map<String, Any>): User =
            User(json["name"] as String, json["age"] as? Int ?: 18)
    }

    override fun toString() = "User($name, $age)"
}

class Account(private val owner: String) {
    private var balance = 0                 // only inside this class
    internal val bank = "Kotlin Bank"       // anywhere in this module

    fun deposit(amount: Int) {
        require(amount > 0) { "amount must be positive, was $amount" }
        balance += amount
    }

    fun show() = "$owner: $balance EGP at $bank"
}

fun main() {
    section("1. Class = blueprint, object = one thing built from it")
    val a = Car("BMW")                      // no `new`
    val b = Car("Kia", 40)
    a.accelerate(30)
    println(a)
    println(b)
    val sameRef = a
    println("a === b       : ${a === b}")
    println("a === sameRef : ${a === sameRef}")

    section("2. init blocks + property initializers run top to bottom")
    val p = Person("Mona")
    println("greeting = ${p.greeting}")

    section("3. Default parameters beat extra constructors")
    println(User("Ali"))
    println(User("Ali", 25))
    println(User(age = 30, name = "Mona"))

    section("4. Secondary constructor (must call this(...))")
    println(User(mapOf("name" to "Omar", "age" to 21)))

    section("5. Kotlin has no named constructors: use a factory")
    println(User.fromJson(mapOf("name" to "Sara")))

    section("6. Visibility")
    val acc = Account("Samy")
    acc.deposit(100)
    println(acc.show())
    // acc.balance              // ERROR: cannot access 'balance': it is private
    tryIt("deposit(-5)") { acc.deposit(-5) }

    section("7. WATCH OUT")
    println("Classes are final by default: see T14 for open.")
    println("A property needs a value: in the constructor, an initializer, init, or lateinit.")
}

private fun section(title: String) = println("\n=== $title ===")

private inline fun tryIt(label: String, block: () -> Unit) {
    try {
        block()
    } catch (e: Exception) {
        println("CRASH  $label -> ${e::class.simpleName}: ${e.message}")
    }
}
