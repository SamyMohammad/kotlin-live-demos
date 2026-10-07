package t14

/*
 * T14 · Inheritance, abstract classes, interfaces    Slides 59-61, 71
 *
 * Dart                          ->  Kotlin
 * class Cat extends Animal      ->  class Cat : Animal()
 * class B implements A          ->  class B : A
 * @override                     ->  override (a required keyword)
 * every class can be extended   ->  only `open` classes can
 */

open class Animal(val name: String) {
    open fun sound() = "..."
    fun describe() = "$name says ${sound()}"      // final: can't be overridden
    open val legs: Int = 4
}

class Cat(name: String) : Animal(name) {
    override fun sound() = "Meow"
}

class Bird(name: String) : Animal(name) {
    override fun sound() = "Tweet"
    override val legs = 2
}

open class Dog(name: String) : Animal(name) {
    override fun sound() = "Woof"
}

class Puppy(name: String) : Dog(name) {
    override fun sound() = super.sound() + " (tiny)"   // call the parent's version
}

abstract class Shape(val name: String) {
    abstract fun area(): Double                  // must be overridden, open automatically
    open fun describe() = "$name with area ${"%.2f".format(area())}"
}

class Circle(val r: Double) : Shape("Circle") {
    override fun area() = Math.PI * r * r
}

class Square(val side: Double) : Shape("Square") {
    override fun area() = side * side
    override fun describe() = "[square] " + super.describe()
}

interface Clickable {
    fun click()
    fun showOff() = println("  I'm clickable")    // default method
}

interface Focusable {
    fun focus() = println("  focused")
    fun showOff() = println("  I'm focusable")
}

class AppButton : Clickable, Focusable {           // many interfaces
    override fun click() = println("  clicked")
    override fun showOff() {                       // same method in both: must choose
        super<Clickable>.showOff()
        super<Focusable>.showOff()
    }
}

interface Named {
    val title: String                              // property, no stored state
}

class Page(override val title: String) : Named

fun main() {
    section("1. open + override, polymorphism")
    val animals: List<Animal> = listOf(Cat("Tom"), Bird("Tweety"), Puppy("Rex"))
    animals.forEach { println("${it.describe()}, legs=${it.legs}") }

    section("2. abstract class")
    // Shape("x")               // ERROR: cannot create an instance of an abstract class
    listOf(Circle(1.0), Square(2.0)).forEach { println(it.describe()) }

    section("3. Interfaces: many per class, default methods")
    val btn = AppButton()
    btn.click()
    btn.focus()
    btn.showOff()

    section("4. Interface properties")
    val page: Named = Page("Home")
    println("title = ${page.title}")

    section("5. Type checks")
    val a: Animal = Cat("Kitty")
    if (a is Cat) println("a is a Cat (smart cast): ${a.sound()}")
    println("Root of every class: ${Any::class.simpleName} (Dart: Object)")

    section("6. abstract class or interface?")
    println("Shared state / constructor -> abstract class (only one)")
    println("Just a contract            -> interface (as many as you want)")

    section("7. WATCH OUT")
    // class Lion : Cat("Leo")                // ERROR: this type is final, so it cannot be extended
    // override fun describe() = "" (in Cat)  // ERROR: 'describe' in 'Animal' is final
    println("Uncomment the lines above to see the errors live.")
}

private fun section(title: String) = println("\n=== $title ===")
