<div dir="rtl">

# شرح T07: الـ Control flow

**الملف:** `src/main/kotlin/T07_ControlFlow.kt`، وسلايدز 36–40.

## الخلاصة

الحاجات المختلفة عن Dart هنا تلاتة: `if` بترجع قيمة ومفيش ternary، و `when` بدل `switch` وأقوى منها، و **ranges** زي `1..5` بدل الـ C-style for loop اللي مش موجودة أصلًا في Kotlin.

## خريطة Dart ← Kotlin

</div>

<div dir="ltr">

| Dart | Kotlin |
| --- | --- |
| `c ? a : b` | `if (c) a else b` |
| `switch (x) { case 1: ... }` | `when (x) { 1 -> ... }` |
| `switch (x) { 1 => 'a', _ => 'b' }` | `when (x) { 1 -> "a" else -> "b" }` |
| `for (var i = 0; i < 5; i++)` | `for (i in 0 until 5)` |
| `for (final x in list)` | `for (x in list)` |
| `list.asMap().entries` | `list.withIndex()` |
| `outer: for ... break outer;` | `outer@ for ... break@outer` |

</div>

<div dir="rtl">

## الشرح

### القسم 1: if بترجع قيمة، ومفيش ternary 🆕 جديد عليك

<div dir="ltr">

```kotlin
val result = if (score >= 50) "Pass" else "Fail"
val grade = if (score >= 90) {
    "A"
} else if (score >= 70) {
    println("(a block's value is its last line)")
    "B"
} else {
    "C"
}
// val oops = if (score > 50) "Yes"  // ERROR: 'if' used as an expression needs 'else'
```

</div>

- **مفيش `? :` في Kotlin.** الـ `if` نفسها expression بترجع قيمة.
- **قيمة الـ block هي آخر سطر فيه:** فتقدر تعمل `println` وبعدها ترجع `"B"`. ده مستحيل في ternary بتاع Dart.
- **لو استخدمت `if` كقيمة، لازم `else`:** لأن من غيرها القيمة هتبقى إيه لو الشرط مش متحقق؟

### القسم 2: when مع subject

<div dir="ltr">

```kotlin
fun httpMessage(code: Int) = when (code) {
    200 -> "OK"
    201, 204 -> "Success"
    in 400..499 -> "Client error"
    in 500..599 -> "Server error"
    else -> "Unknown"
}
```

</div>

لو بتعرف الـ switch expression بتاعة Dart 3، فإنت عارف `when` تقريبًا:

- **قيم متعددة:** `201, 204 ->` في فرع واحد، زي `201 || 204` في Dart patterns.
- **ranges:** `in 400..499` بتشيك لو الرقم جوه الـ range. Dart 3 عندها `>= 400 && <= 499` كـ relational pattern، بس `in` أوضح.
- **مفيش fall-through ولا `break`:** كل فرع لوحده.
- **الـ `else` هي `_` أو `default`:** ولازمة لما `when` تكون expression ومش كل الحالات متغطية.

### القسم 3: when من غير subject 🆕 جديد عليك

<div dir="ltr">

```kotlin
fun ageGroup(age: Int) = when {
    age < 13 -> "Child"
    age < 20 -> "Teen"
    else -> "Adult"
}
```

</div>

من غير قوسين بعد `when`، كل فرع بيبقى **شرط Boolean كامل**. ده بديل أنضف لسلسلة `if / else if`. أول شرط يتحقق هو اللي بيتنفذ. مفيش مقابل مباشر ليها في Dart.

### القسم 4: when مع الأنواع

<div dir="ltr">

```kotlin
fun describe(x: Any): String = when (x) {
    is Int -> "Int"
    is String -> "String of length ${x.length}"
    is List<*> -> "List of ${x.size}"
    else -> "Something else"
}
```

</div>

زي `case int():` في Dart 3. جوه كل فرع، `x` بيتعمله smart cast للنوع ده.

- **الـ `List<*>`:** الـ `*` اسمها star projection، ومعناها list من أي نوع مش معروف. لازم تكتبها لأن الـ JVM بيمسح نوع الـ generic وقت التشغيل (type erasure)، فمينفعش تشيك على `is List<String>`. التفاصيل في T21.

لما تستخدم `when` مع `sealed class` (في T16)، الكومبايلر بيتأكد إنك غطيت كل الحالات ومش محتاج `else`. ده نفس exhaustiveness بتاع Dart 3 مع `sealed`.

### القسم 5: الـ ranges 🆕 جديد عليك

<div dir="ltr">

```kotlin
for (i in 1..5)            // 1 2 3 4 5
for (i in 1 until 5)       // 1 2 3 4
for (i in 1..<5)           // 1 2 3 4
for (i in 10 downTo 0 step 2)
for (c in 'a'..'e')
println("${5 in 1..10}   ${15 !in 1..10}")
```

</div>

Dart مفيهاش ranges، وكنت بتكتب `for (var i = 0; i < 5; i++)`. Kotlin **مفيهاش** الـ C-style loop ده أصلًا، وكل حاجة بالـ ranges:

- **`..`:** شامل الطرفين.
- **`until` أو `..<`:** من غير الطرف الأخير، وده اللي هتستخدمه مع indexes. الشكل `..<` أحدث وأوضح.
- **`downTo`:** للعد التنازلي.
- **`step`:** لتحديد الخطوة.
- **ranges للحروف:** `'a'..'e'`.
- **`in` و `!in`:** بتشيك لو قيمة جوه range، وبتنفع في `if` عادي.

الـ range object حقيقي، و `1..5` نوعها `IntRange`. وكلمات زي `until` و `downTo` و `step` مش keywords، دي **infix functions** (هتشوفها في T23).

### القسم 6: اللف على الـ collections

<div dir="ltr">

```kotlin
for (n in names) print("$n ")
for ((index, n) in names.withIndex()) println("$index: $n")
for (i in names.indices) print("[$i] ")
names.forEachIndexed { i, n -> print("$i=$n ") }
```

</div>

- **`withIndex()`:** بترجعلك index وقيمة مع بعض، و `(index, n)` اسمها **destructuring** (في T15). في Dart كنت بتعمل `asMap().entries` أو `indexed` في Dart 3.
- **`indices`:** هي range الـ indexes (`0..size-1`).
- **`forEachIndexed`:** نفس الفكرة بـ lambda.

### القسم 7: while و do-while و repeat

<div dir="ltr">

```kotlin
repeat(3) { println("Hi #${it + 1}") }
```

</div>

الـ `while` و `do-while` زي Dart بالظبط. الجديد `repeat(n) { }`، وده function عادية بتنفذ الـ lambda عدد من المرات، و `it` هو رقم المرة بادئًا من صفر.

### القسم 8: break و continue والـ labels

<div dir="ltr">

```kotlin
outer@ for (i in 1..3) {
    for (j in 1..3) {
        if (j == 2) continue@outer
        if (i == 3) break@outer
        print("($i,$j) ")
    }
}
```

</div>

Dart عندها labels برضه (`outer:`)، بس الشكل مختلف: الـ label هنا بيتكتب `name@` قبل الـ loop، وبتستخدمه `break@name`. هتشوف نفس الـ syntax ده مع `return@` جوه lambdas في T09، وده أهم استخدام ليه.

### القسم 9: خد بالك، 10..1 فاضية

<div dir="ltr">

```kotlin
for (i in 10..1) println("never printed")
println("${(10..1).count()}")          // 0
println("${(10 downTo 1).toList()}")
```

</div>

العلامة `..` بتعد **لفوق بس**. لو البداية أكبر من النهاية الـ range بتبقى فاضية، **من غير error ولا warning وقت التشغيل**، والـ loop مش بيتنفذ ولا مرة. للعد التنازلي لازم `downTo`.

## الفخاخ في الملف

- **ERROR:** `if` كـ expression من غير `else`.
- **WATCH OUT:** `10..1` فاضية.

## عادات Flutter اللي هتوقعك

- كتابة `cond ? a : b`، والصح `if (cond) a else b`.
- محاولة كتابة `for (var i = 0; ...)`، والصح ranges.
- استخدام `1..size` للـ indexes، والصح `0 until size` أو `indices`، وإلا هتاخد `IndexOutOfBoundsException`.
- كتابة `case` و `default:`، والصح `->` و `else`.

## الخلاصة في 3 سطور

1. الـ `if` و `when` expressions بترجع قيمة، ومفيش ternary.
2. الـ `when` بتعمل switch على قيم و ranges وأنواع، ومن غير subject بتبقى بديل `if-else`.
3. الـ ranges بدل الـ C-style loop: `..` و `until` و `downTo` و `step`، وخلي بالك إن `10..1` فاضية.

</div>
