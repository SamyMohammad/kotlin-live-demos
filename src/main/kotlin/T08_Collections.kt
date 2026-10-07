package t08

/*
 * T08 · Collections: List, Set, Map + operations     Slides 42-43, 51
 *
 * Dart                      ->  Kotlin
 * [1, 2] (mutable)          ->  listOf(1, 2) read-only / mutableListOf(1, 2)
 * {1, 2}                    ->  setOf(1, 2)
 * {'a': 1}                  ->  mapOf("a" to 1)
 * .where((x) => ...)        ->  .filter { ... }
 * .map(...).toList()        ->  .map { ... }        (already a List)
 */

data class Student(val name: String, val grade: Int, val track: String)

val students = listOf(
    Student("Mona", 92, "Android"),
    Student("Ali", 74, "Flutter"),
    Student("Omar", 41, "Android"),
    Student("Sara", 88, "Flutter"),
    Student("Youssef", 67, "Android"),
)

fun main() {
    section("1. Read-only vs mutable lists")
    val nums = listOf(3, 1, 4, 1, 5)
    // nums.add(9)                  // ERROR: List is read-only, no add()
    val mutable = mutableListOf(1, 2)
    mutable.add(3)
    mutable.remove(1)
    mutable[0] = 20
    mutable += 30                   // same as add(30)
    println("nums=$nums mutable=$mutable")

    section("2. Sets and maps")
    val ids = setOf(1, 2, 2, 3)
    println("set removes duplicates: $ids")
    val ages = mapOf("Ali" to 20, "Mona" to 22)   // "Ali" to 20 creates a Pair
    println("ages[\"Ali\"]    = ${ages["Ali"]}")
    println("ages[\"Nobody\"] = ${ages["Nobody"]}")          // null, no crash
    println("getOrDefault   = ${ages.getOrDefault("Nobody", 0)}")
    val scores = mutableMapOf("A" to 1)
    scores["B"] = 2
    scores.remove("A")
    println("scores = $scores")
    for ((name, age) in ages) println("$name is $age")
    val pair = "Ali" to 20
    println("pair.first=${pair.first} pair.second=${pair.second}")

    section("3. Accessing elements")
    println("nums[0]=${nums[0]} first=${nums.first()} last=${nums.last()}")
    println("getOrNull(10)=${nums.getOrNull(10)} indexOf(4)=${nums.indexOf(4)} 4 in nums=${4 in nums}")

    section("4. Transform")
    println("filter { it > 2 }   = ${nums.filter { it > 2 }}")
    println("filterNot           = ${nums.filterNot { it > 2 }}")
    println("map { it * 10 }     = ${nums.map { it * 10 }}")
    println("mapIndexed          = ${nums.mapIndexed { i, n -> "$i:$n" }}")
    println("distinct            = ${nums.distinct()}")
    println("sorted / desc       = ${nums.sorted()} / ${nums.sortedDescending()}")
    println("reversed            = ${nums.reversed()}")

    section("5. Ask questions")
    println("any { it > 4 }  = ${nums.any { it > 4 }}")
    println("all { it > 0 }  = ${nums.all { it > 0 }}")
    println("none { it < 0 } = ${nums.none { it < 0 }}")
    println("count { it == 1 } = ${nums.count { it == 1 }}")

    section("6. Aggregate")
    println("sum=${nums.sum()} average=${nums.average()} max=${nums.max()} min=${nums.min()}")
    println("fold   = ${nums.fold(0) { acc, n -> acc + n }}")
    println("reduce = ${nums.reduce { acc, n -> acc * n }}")

    section("7. Real-world style")
    println("Top first : ${students.sortedByDescending { it.grade }.map { it.name }}")
    println("By track  : ${students.groupBy { it.track }.mapValues { (_, list) -> list.map { it.name } }}")
    val (passed, failed) = students.partition { it.grade >= 50 }
    println("Passed    : ${passed.map { it.name }}  Failed: ${failed.map { it.name }}")
    println("Best      : ${students.maxByOrNull { it.grade }?.name}")
    println("Total     : ${students.sumOf { it.grade }}")
    println("Android   : ${students.filter { it.track == "Android" }.joinToString { it.name }}")
    println("By name   : ${students.associateBy { it.name }["Sara"]}")

    section("8. More helpers")
    println("take(2)=${nums.take(2)} drop(2)=${nums.drop(2)} takeLast(2)=${nums.takeLast(2)}")
    println("chunked(2)=${nums.chunked(2)}")
    println("zip=${listOf("a", "b", "c").zip(listOf(1, 2, 3))}")
    println("flatMap=${listOf(listOf(1, 2), listOf(3)).flatMap { it }}  flatten=${listOf(listOf(1), listOf(2, 3)).flatten()}")
    println("find { it > 3 } = ${nums.find { it > 3 }}")

    section("9. Sequences: lazy, step by step (FYI)")
    val firstThree = (1..1_000_000).asSequence()
        .map { it * 2 }
        .filter { it % 3 == 0 }
        .take(3)
        .toList()                    // only does the work it needs
    println("firstThree = $firstThree")

    section("10. WATCH OUT")
    tryIt("first { it > 100 }") { nums.first { it > 100 } }   // NoSuchElementException
    println("firstOrNull { it > 100 } = ${nums.firstOrNull { it > 100 }}")
    tryIt("nums[99]") { nums[99] }                             // IndexOutOfBoundsException
    // Read-only is not immutable: someone else may still change it
    val backing = mutableListOf(1, 2)
    val readOnly: List<Int> = backing
    backing.add(3)
    println("readOnly now = $readOnly")
    // Compose: LazyColumn { items(students) { StudentRow(it) } }
}

private fun section(title: String) = println("\n=== $title ===")

private inline fun tryIt(label: String, block: () -> Unit) {
    try {
        block()
    } catch (e: Exception) {
        println("CRASH  $label -> ${e::class.simpleName}: ${e.message}")
    }
}
