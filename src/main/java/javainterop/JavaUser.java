package javainterop;

// A plain Java class, used in T05_NullSafety.kt to show "platform types".
// Java has no null safety: getName() may return null and Kotlin can't know.
public class JavaUser {
    private final String name;

    public JavaUser(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
