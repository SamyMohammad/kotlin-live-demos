<div dir="rtl">

# شرح T22: الـ Exceptions (بونص)

**الملف:** `src/main/kotlin/T22_Exceptions.kt`، ومش في السلايدز.

## الخلاصة

الـ exceptions في Kotlin قريبة من Dart جدًا: `try` و `catch` و `finally` و `throw`، ومفيش checked exceptions في الاتنين. الجديد عليك: **`try` و `throw` expressions**، و `require` و `check` و `error`، و `runCatching` اللي بيحوّل الـ exception لقيمة 🆕.

## خريطة Dart ← Kotlin

</div>

<div dir="ltr">

| Dart | Kotlin |
| --- | --- |
| `on FormatException catch (e)` | `catch (e: NumberFormatException)` |
| `catch (e)` | `catch (e: Exception)` |
| `catch (e, stackTrace)` | `e.stackTrace` / `e.stackTraceToString()` |
| `rethrow` | `throw e` |
| `class MyEx implements Exception` | `class MyEx : Exception("msg")` |
| `ArgumentError` | `IllegalArgumentException` / `require` |
| `StateError` | `IllegalStateException` / `check` / `error` |
| — | `runCatching { }` → `Result<T>` |

</div>

<div dir="rtl">

## الشرح

### القسم 1: try و catch و finally

<div dir="ltr">

```kotlin
class InsufficientFundsException(val needed: Int) : Exception("Need $needed EGP more")

try {
    withdraw(100, 150)
} catch (e: InsufficientFundsException) {
    println("  caught: ${e.message} (needed=${e.needed})")
} finally {
    println("  finally always runs")
}
```

</div>

- **النوع جوه أقواس الـ catch:** `catch (e: Type)` بدل `on Type catch (e)`. ولو عايز أكتر من نوع، اكتب أكتر من `catch` ورا بعض.
- **الـ custom exception:** class بتورث من `Exception` وبتبعتله الرسالة، وتقدر تضيف properties زي `needed`.
- **`finally`:** زي Dart، بتتنفذ دايمًا.

### القسم 2: try بترجع قيمة 🆕 جديد عليك

<div dir="ltr">

```kotlin
val n = try {
    "42x".toInt()
} catch (e: NumberFormatException) {
    -1
}
```

</div>

زي `if` و `when`، الـ `try` **expression**. آخر سطر في الـ `try` أو الـ `catch` هو القيمة. في Dart كنت هتعمل `int n;` وتعيّنه جوه كل فرع، وهنا `val` واحدة.

### القسم 3: require و check و error 🆕 جديد عليك

<div dir="ltr">

```kotlin
fun setAge(age: Int) {
    require(age >= 0) { "age must be >= 0, was $age" }   // IllegalArgumentException
}

fun startPayment(connected: Boolean) {
    check(connected) { "not connected to the server" }   // IllegalStateException
}

error("boom")                                            // IllegalStateException
```

</div>

الـ standard library بتديك 3 functions بتوضح **نوع** المشكلة:

</div>

<div dir="ltr">

| الـ function | المعنى | بترمي |
| --- | --- | --- |
| `require(cond)` | الـ **input** غلط (ذنب اللي نادى) | `IllegalArgumentException` |
| `check(cond)` | الـ **state** غلط (الـ object مش جاهز) | `IllegalStateException` |
| `error(msg)` | حاجة مش المفروض تحصل | `IllegalStateException` |

</div>

<div dir="rtl">

- **الرسالة جوه lambda:** عشان الـ string متتبنيش إلا لو الشرط فشل، وده توفير صغير.
- **عكس `assert` في Dart:** دول شغالين دايمًا، في release كمان.
- **فيه كمان `requireNotNull(x)` و `checkNotNull(x)`:** بيرجعوا القيمة non-null أو بيرموا exception.

### القسم 4: runCatching، الأخطاء كقيم 🆕 جديد عليك

<div dir="ltr">

```kotlin
val r = runCatching { "abc".toInt() }
println("isFailure=${r.isFailure} getOrNull=${r.getOrNull()} getOrElse=${r.getOrElse { 0 }}")
r.onFailure { println("  failed with ${it::class.simpleName}") }
val ok = runCatching { "7".toInt() }.map { it * 2 }
println("ok = ${ok.getOrThrow()}")
```

</div>

`runCatching` بتنفذ الـ block وبترجع `Result<T>`، وده **نجاح بقيمة أو فشل بـ exception**، من غير ما حاجة تترمي. وبعدها:

- **`getOrNull()` و `getOrElse { }` و `getOrThrow()`:** طرق مختلفة تطلع بيها القيمة.
- **`onSuccess { }` و `onFailure { }`:** تنفذ حاجة حسب النتيجة.
- **`map { }`:** تحوّل القيمة لو نجح.
- **`fold(onSuccess, onFailure)`:** تتعامل مع الحالتين.

أقرب حاجة في Dart هي `Either` من `dartz` أو `fpdart`، أو `Future.catchError`.

**تحذير للسيشن الجاية:** `runCatching` بتمسك **كل** حاجة، ومنها `CancellationException` بتاعة الـ coroutines، وده ممكن يبوظ الـ cancellation. جوه `suspend` functions خلي بالك منها.

### القسم 5: throw كمان expression

<div dir="ltr">

```kotlin
val name: String = findUser(2) ?: throw NoSuchElementException("user 2 not found")
```

</div>

شفتها في T05 و T06: نوع `throw` هو `Nothing`، فينفع يتحط على يمين `?:`، و `name` بيبقى `String` مش `String?`.

### القسم 6: خد بالك

- **مفيش checked exceptions:** Java كانت بتجبرك تكتب `throws IOException` وتعمل catch. Kotlin مش بتجبرك، زي Dart بالظبط. يعني **مسؤوليتك** تعرف إيه اللي ممكن يترمي وتتعامل معاه. الـ docs بتاعة كل function هي المرجع، أو `@Throws` annotation.
- **اعمل catch للـ exception المحدد مش `Exception`:** لو مسكت `Exception` بشكل عام، هتخبي bugs حقيقية، زي `NullPointerException` من غلطة في كودك.

### فرق بين Exception و Error

في Dart فيه فرق بين `Exception` (حاجة متوقعة) و `Error` (bug في الكود). في Kotlin/JVM الكل بيورث من `Throwable`:

- **`Exception`:** حاجات ممكن تتعالج.
- **`Error`:** مشاكل في الـ JVM نفسه زي `StackOverflowError` و `OutOfMemoryError`. و `catch (e: Exception)` **مش** بيمسكهم.

## الفخاخ في الملف

- **CRASH:** `setAge(-1)` بيرمي `IllegalArgumentException`.
- **CRASH:** `startPayment(false)` بيرمي `IllegalStateException`.
- **CRASH:** `error("boom")` بيرمي `IllegalStateException`.
- **CRASH:** `findUser(2)` بيرمي `NoSuchElementException`.

## عادات Flutter اللي هتوقعك

- كتابة `on X catch (e)`، والصح `catch (e: X)`.
- كتابة `rethrow`، والصح `throw e`.
- استخدام `assert` للـ validation، والصح `require` أو `check`.

## الخلاصة في 3 سطور

1. الـ `try` و `throw` expressions، ومفيش checked exceptions زي Dart.
2. الـ `require` للـ input الغلط، و `check` و `error` للـ state الغلط، وكلهم شغالين في release.
3. الـ `runCatching` بيرجع `Result` بدل ما يرمي، بس خلي بالك منه مع الـ coroutines.

</div>
