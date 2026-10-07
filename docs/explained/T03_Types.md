<div dir="rtl">

# شرح T03: الأنواع والأرقام والـ casts

**الملف:** `src/main/kotlin/T03_Types.kt`، وسلايدز 21–22.

## الخلاصة

في Dart عندك `int` و `double` و `num` بس. في Kotlin عندك عيلة أرقام كاملة زي Java، ومفيش أي تحويل تلقائي بينها. وكمان القسمة بين اتنين `Int` بترجع `Int`، ودي أكتر حاجة هتوقعك.

## خريطة Dart ← Kotlin

</div>

<div dir="ltr">

| Dart | Kotlin |
| --- | --- |
| `int` (64-bit) | `Int` (32-bit) و `Long` (64-bit) و `Short` و `Byte` |
| `double` | `Double` (64-bit) و `Float` (32-bit) |
| `bool` | `Boolean` |
| — | `Char` |
| `Object` / `dynamic` | `Any` |
| `x is int` | `x is Int` |
| `x as int` | `x as Int` و `x as? Int` |
| `7 ~/ 2` | `7 / 2` |
| `int.parse` / `int.tryParse` | `toInt()` / `toIntOrNull()` |

</div>

<div dir="rtl">

## الشرح

### القسم 1: الأنواع الأساسية

<div dir="ltr">

```kotlin
val i: Int = 42
val l: Long = 42L
val d: Double = 3.14
val f: Float = 3.14f
println("Readable  : ${1_000_000}")
```

</div>

- **أهم فرق:** `int` في Dart حجمه 64-bit، بس `Int` في Kotlin حجمه **32-bit** بس، يعني أقصاه حوالي 2.1 مليار. لو بتتعامل مع timestamps بالـ milliseconds أو IDs كبيرة، استخدم `Long`، و `System.currentTimeMillis()` نفسها بترجع `Long`.
- **اللواحق:** `L` معناها `Long`، و `f` معناها `Float`. من غيرهم `42` بتبقى `Int` و `3.14` بتبقى `Double`.
- **الـ underscore:** `1_000_000` للقراءة بس، والكومبايلر بيتجاهلها. Dart 3.6 ضافت نفس الميزة (digit separators).
- **كل حاجة object:** تقدر تكتب `42.toString()`، بس الكومبايلر بيحوّل `Int` لـ `int` primitive في الـ JVM لما يقدر، عشان الأداء.

### القسم 2: مفيش تحويل تلقائي 🆕 جديد عليك

<div dir="ltr">

```kotlin
val small: Int = 10
// val wrong: Long = small   // ERROR: type mismatch
val big: Long = small.toLong()
println("${"abc".toIntOrNull()}")   // null, no crash
tryIt("\"abc\".toInt()") { "abc".toInt() }
```

</div>

حتى من `Int` لـ `Long`، وده تحويل آمن تمامًا، Kotlin مش بتعمله لوحدها، ولازم تكتب `toLong()` صريح. الفكرة إن الحاجات اللي بتحصل من غير ما تشوفها بتعمل bugs.

بالنسبة لتحويل الـ strings:

- `"42".toInt()` تقابل `int.parse`، وبترمي `NumberFormatException` لو النص مش رقم.
- `toIntOrNull()` تقابل `int.tryParse`، وبترجع `null`. دي اللي هتستخدمها مع input اليوزر.

### القسم 3: خد بالك، Int على Int بيطلع Int

<div dir="ltr">

```kotlin
println("7 / 2   = ${7 / 2}")     // 3 (!)
println("7 / 2.0 = ${7 / 2.0}")   // 3.5
println("7.9.toInt()       = ${7.9.toInt()}")       // 7
println("7.5.roundToInt()  = ${7.5.roundToInt()}")  // 8
```

</div>

دي **أخطر** فخ في الملف لأي حد جاي من Dart:

- **في Dart:** العلامة `/` بترجع `double` دايمًا، و `~/` هي القسمة الصحيحة.
- **في Kotlin:** العلامة `/` بين اتنين `Int` بتعمل **قسمة صحيحة**، يعني `7 / 2 = 3`. لو عايز النتيجة كسر، لازم واحد من الطرفين يبقى `Double`.

المثال الكلاسيكي اللي هيوقعك: حساب percentage بـ `done / total * 100`، والنتيجة هتطلع `0` دايمًا لو `done < total`. الحل: `done * 100 / total` أو `done.toDouble() / total`.

كمان `toInt()` على `Double` **بتقطع** الكسر ومش بتقرّب، يعني `7.9` بتبقى `7`. للتقريب استخدم `roundToInt()` من `kotlin.math`.

### القسم 4: خد بالك، الـ overflow بيلف من غير ما يقولك

<div dir="ltr">

```kotlin
val max = Int.MAX_VALUE
println("Int.MAX_VALUE + 1 = ${max + 1}")   // negative!
```

</div>

مفيش exception. الرقم بيلف ويبقى `-2147483648`. في Dart الـ native كان `int` بيلف برضه، بس عند 64-bit، فعمرك ما لاحظته. هنا عند 2.1 مليار، وده رقم ممكن توصله فعلًا، زي مجموع فلوس بالقروش.

### القسم 5: Any و is والـ smart cast 🆕 جديد عليك

<div dir="ltr">

```kotlin
val things: List<Any> = listOf(1, "two", 3.0, 'c', true)
for (t in things) {
    val kind = when (t) {
        is Int -> "Int, doubled = ${t * 2}"
        is String -> "String of length ${t.length}"
        else -> t::class.simpleName
    }
}
```

</div>

- **الـ `Any`:** هو الأب لكل الأنواع اللي مش nullable، زي `Object` في Dart. والـ `Any?` هو الأب لكل حاجة.
- **الـ smart cast:** بعد `is Int`، الكومبايلر بيعتبر `t` من نوع `Int` جوه الفرع ده، فتقدر تعمل `t * 2` من غير cast. Dart عندها type promotion شبه كده، فالفكرة مش غريبة عليك. الفرق إن Kotlin بتعملها في حالات أكتر، زي جوه `when` و `&&`.
- **الـ `when`:** هي `switch` بتاعة Kotlin، وهنا **بترجع قيمة**. شرحها كامل في T07.
- **الـ `t::class.simpleName`:** reflection بسيطة بتجيب اسم النوع، زي `runtimeType` في Dart.

### القسم 6: الفرق بين as و as?

<div dir="ltr">

```kotlin
val asString = x as String   // fine
val asInt = x as? Int        // wrong type -> null
tryIt("x as Int") { x as Int }   // ClassCastException
```

</div>

- **الـ `as`:** زي Dart، وبيرمي exception لو النوع غلط. في Kotlin اسمه `ClassCastException`.
- **الـ `as?`:** 🆕 جديد عليك. بيرجع `null` بدل ما يكسر. وغالبًا هتستخدمه مع Elvis: `(x as? Int) ?: 0`.

## الـ tryIt helper

<div dir="ltr">

```kotlin
private inline fun tryIt(label: String, block: () -> Unit) {
    try {
        block()
    } catch (e: Exception) {
        println("CRASH  $label -> ${e::class.simpleName}: ${e.message}")
    }
}
```

</div>

دي function بتاخد lambda (شرحها في T09) وبتنفذها جوه `try`، عشان الـ crash يتطبع والملف يكمل. الـ `inline` هتتشرح في T09 برضه.

## الفخاخ في الملف

- **ERROR:** إسناد `Int` لـ `Long` من غير تحويل.
- **CRASH:** `"abc".toInt()` بيرمي `NumberFormatException`.
- **CRASH:** `x as Int` بيرمي `ClassCastException`.
- **WATCH OUT:** القسمة الصحيحة، وقطع الكسر في `toInt()`، والـ overflow.

## عادات Flutter اللي هتوقعك

- افتراض إن `/` بترجع كسر.
- استخدام `Int` لـ timestamps، والصح `Long`.
- انتظار تحويل تلقائي من `Int` لـ `Double` في function بتاخد `Double`، زي `sqrt(4)`. لازم تكتب `sqrt(4.0)`.

## الخلاصة في 3 سطور

1. الـ `Int` حجمه 32-bit، فاستخدم `Long` للأرقام الكبيرة، ومفيش أي تحويل تلقائي بين الأنواع.
2. القسمة بين اتنين `Int` بترجع `Int`، و `toInt()` بتقطع ومش بتقرّب.
3. استخدم `is` مع smart cast، واستخدم `as?` بدل `as` لما مش متأكد من النوع.

</div>
