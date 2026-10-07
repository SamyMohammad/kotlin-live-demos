package t04

/*
 * T04 · Strings                                      Slides 23-24
 *
 * Templates work exactly like Dart: $name and ${expression}.
 * Only the quotes change: "String", 'C' for Char, """raw""" for multi-line.
 */

fun main() {
    section("1. Templates")
    val name = "Samy"
    val items = listOf("a", "b", "c")
    println("Hi $name")
    println("Sum: ${1 + 2}")
    println("Items: ${items.size}")
    println("WATCH OUT: $items.size")      // $ takes only the name -> "[a, b, c].size"
    println("Price: \$99")                  // escape a real dollar sign

    section("2. Raw strings (Dart: ''' ''')")
    val json = """
        {
          "name": "$name",
          "track": "Android"
        }
    """.trimIndent()                        // removes the common indentation
    println(json)
    val poem = """
        |Roses are red,
        |Kotlin is neat.
    """.trimMargin()                         // removes everything up to |
    println(poem)

    section("3. Useful functions")
    val s = "  Hello, Kotlin  "
    println("trim       : [${s.trim()}]")
    println("uppercase  : ${s.trim().uppercase()}")
    println("length     : ${s.length}")
    println("startsWith : ${"Kotlin".startsWith("Kot")}")
    println("contains   : ${"Kotlin".contains("lin")}")
    println("split      : ${"a,b,c".split(",")}")
    println("replace    : ${"I love Dart".replace("Dart", "Kotlin")}")
    println("repeat     : ${"ab".repeat(3)}")
    println("reversed   : ${"Kotlin".reversed()}")
    println("padStart   : ${"7".padStart(3, '0')}")
    println("first/last : ${"Kotlin"[0]} / ${"Kotlin".last()}")
    println("isEmpty    : ${"   ".isEmpty()}   isBlank: ${"   ".isBlank()}")
    println("substring  : ${"Kotlin".substring(0, 3)}")
    println("capitalize : ${"kotlin".replaceFirstChar { it.uppercase() }}")

    section("4. Comparing strings")
    val a = "kotlin"
    val b = "Kotlin"
    println("a == b             : ${a == b}")          // == compares content (no .equals needed)
    println("equals ignoreCase  : ${a.equals(b, ignoreCase = true)}")
    println("compareTo          : ${a.compareTo(b)}")  // > 0 means a comes after b

    section("5. Building strings")
    val sb = StringBuilder()
    for (i in 1..3) sb.append(i).append(' ')
    println(sb.toString())
    val built = buildString {               // lambda with receiver (see T10)
        append("Built ")
        append("with buildString")
    }
    println(built)
    println(listOf("Mona", "Ali", "Omar").joinToString(separator = " | ", prefix = "[", postfix = "]"))

    section("6. Char helpers")
    val ch = 'K'
    println("code=${ch.code} isLetter=${ch.isLetter()} isDigit=${'7'.isDigit()} digitToInt=${'7'.digitToInt()}")
}

private fun section(title: String) = println("\n=== $title ===")
