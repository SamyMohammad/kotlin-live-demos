package t15

/*
 * T15 · Data classes                                 Slides 62-64, 71
 *
 * Dart: equals, hashCode, toString, copyWith by hand (or freezed).
 * Kotlin: one keyword -> data class.
 */

data class User(val name: String, val age: Int)

class PlainUser(val name: String, val age: Int)

data class LoginUiState(
    val email: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
)

data class Cart(val items: MutableList<String>)

data class Profile(val name: String) {
    var visits: Int = 0                 // NOT part of equals / toString / copy
}

fun main() {
    section("1. toString for free")
    println(User("Ali", 20))
    println(PlainUser("Ali", 20))       // a normal class prints its memory address

    section("2. equals compares values")
    println("data  == : ${User("Ali", 20) == User("Ali", 20)}")
    println("plain == : ${PlainUser("Ali", 20) == PlainUser("Ali", 20)}")
    println("data === : ${User("Ali", 20) === User("Ali", 20)}   <- different objects")

    section("3. hashCode: works in sets and as map keys")
    println("set of 2 equal data users  -> size ${setOf(User("Ali", 20), User("Ali", 20)).size}")
    println("set of 2 equal plain users -> size ${setOf(PlainUser("Ali", 20), PlainUser("Ali", 20)).size}")

    section("4. copy (Dart: copyWith)")
    val u = User("Ali", 20)
    val older = u.copy(age = 21)
    println("u=$u older=$older")

    section("5. Destructuring")
    val (name, age) = u
    println("name=$name age=$age")
    for ((n, a) in listOf(User("Mona", 22), User("Omar", 19))) println("$n is $a")

    section("6. UI state pattern (ViewModel + Compose)")
    var state = LoginUiState()
    println(state)
    state = state.copy(email = "samy@mail.com", isLoading = true)
    println(state)
    state = state.copy(isLoading = false, error = "Wrong password")
    println(state)

    section("7. WATCH OUT: body properties are ignored")
    val p1 = Profile("Mona").apply { visits = 5 }
    val p2 = Profile("Mona")
    println("p1 == p2: ${p1 == p2}   (visits ignored)")
    println("p1 = $p1                (no visits in toString)")

    section("8. WATCH OUT: copy() is shallow")
    val cart1 = Cart(mutableListOf("Milk"))
    val cart2 = cart1.copy()
    cart2.items.add("Eggs")
    println("cart1 = $cart1   <- changed too! Both share the same list")
    println("Fix: use List (read-only) in state and copy with a new list.")

    section("9. Rules")
    // data class Empty()               // ERROR: needs at least one constructor parameter
    // open data class Base(val a: Int) // ERROR: data classes can't be open / abstract / sealed
    println("Uncomment the lines above to see the errors live.")
}

private fun section(title: String) = println("\n=== $title ===")
