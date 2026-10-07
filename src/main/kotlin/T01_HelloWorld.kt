package t01

/*
 * T01 · Hello Kotlin + the JVM                       Slides 10-17
 *
 * LIVE DEMO
 *  1. Run main() with the green arrow next to it.
 *  2. Tools > Kotlin > Show Kotlin Bytecode > Decompile
 *     This file becomes a Java class named T01_HelloWorldKt.
 *     The JVM only runs classes, so Kotlin wraps top-level
 *     functions in a class named after the file + "Kt".
 *     (Main.kt -> MainKt, the slide 14 question.)
 */

fun main() {
    section("1. Hello world")
    // Dart: void main() { print('Hello'); }
    println("Hello, Kotlin!")          // println = print + new line
    print("print stays on the same line... ")
    print("see?\n")

    section("2. No semicolons, double quotes")
    val name = "Android"               // a new line ends the statement
    println("Hello, $name!")

    section("3. Char vs String")
    val letter: Char = 'K'             // single quotes = ONE character
    val word: String = "K"             // double quotes = String
    println("letter=$letter word=$word")
    // val wrong: String = 'K'         // ERROR: Char is not a String

    section("4. Where is this code running?")
    println("Kotlin version : ${KotlinVersion.CURRENT}")
    println("Java version   : ${System.getProperty("java.version")}")
    println("JVM name       : ${System.getProperty("java.vm.name")}")
    // The class the JVM actually runs for this file:
    println("Class name     : ${object {}.javaClass.enclosingClass?.name}")
}

private fun section(title: String) = println("\n=== $title ===")
