<div dir="rtl">

# شرح T04: الـ Strings

**الملف:** `src/main/kotlin/T04_Strings.kt`، وسلايدز 23–24.

## الخلاصة

الـ strings في Kotlin قريبة جدًا من Dart: نفس الـ templates ونفس الفكرة. الفرق في علامات التنصيص، وفي raw strings، وفي إن مكتبة الـ functions أكبر بكتير.

## خريطة Dart ← Kotlin

</div>

<div dir="ltr">

| Dart | Kotlin |
| --- | --- |
| `'$name'` و `'${a + b}'` | `"$name"` و `"${a + b}"` |
| `'''multi-line'''` | `"""raw"""` + `trimIndent()` |
| `r'C:\path'` | `"""C:\path"""` |
| `StringBuffer` | `StringBuilder` / `buildString { }` |
| `list.join(', ')` | `joinToString(", ")` |
| `s.isEmpty` (getter) | `s.isEmpty()` (function) |
| `s.toUpperCase()` | `s.uppercase()` |

</div>

<div dir="rtl">

## الشرح

### القسم 1: الـ templates

<div dir="ltr">

```kotlin
println("Hi $name")
println("Sum: ${1 + 2}")
println("WATCH OUT: $items.size")   // -> "[a, b, c].size"
println("Price: \$99")
```

</div>

زي Dart بالظبط، وفيها نفس الفخ: `$items.size` بتاخد `items` بس، والباقي `.size` بيتطبع كنص. لو فيه نقطة أو أي expression، لازم `${}`.

لطباعة علامة `$` نفسها: `\$`، أو `${'$'}`.

### القسم 2: الـ raw strings 🆕 فيها جديد

<div dir="ltr">

```kotlin
val json = """
    {
      "name": "$name",
      "track": "Android"
    }
""".trimIndent()

val poem = """
    |Roses are red,
    |Kotlin is neat.
""".trimMargin()
```

</div>

علامة `"""` في Kotlin بتجمع ميزتين من Dart في حاجة واحدة:

- **multi-line:** زي `'''` في Dart.
- **raw:** مفيش escaping. يعني `\n` جوه `"""` بتتطبع زي ما هي، زي `r''` في Dart. وده مفيد جدًا في regex و Windows paths.
- **الـ templates لسه شغالة:** `$name` جوه `"""` بيتبدل عادي.

والجديد عليك:

- **`trimIndent()`:** بتشيل المسافات المشتركة من أول كل سطر، فتقدر تكتب الـ string متساوية مع الكود من غير ما المسافات تدخل في النتيجة.
- **`trimMargin()`:** بتشيل كل حاجة لحد علامة `|`. مفيدة لما عايز تتحكم بالظبط في بداية كل سطر.

### القسم 3: functions مفيدة

<div dir="ltr">

```kotlin
println("isEmpty    : ${"   ".isEmpty()}   isBlank: ${"   ".isBlank()}")
println("first/last : ${"Kotlin"[0]} / ${"Kotlin".last()}")
println("capitalize : ${"kotlin".replaceFirstChar { it.uppercase() }}")
```

</div>

أغلبها زي Dart، بس فيه شوية حاجات لازم تخلي بالك منها:

- **الـ properties والـ functions:** `length` و `size` properties من غير أقواس، لكن `isEmpty()` و `last()` functions بأقواس. في Dart كانوا getters كلهم.
- **الفرق بين isEmpty و isBlank:** `isEmpty()` بترجع `true` لو الطول صفر بس، و `isBlank()` بترجع `true` لو فاضية أو كلها مسافات. مع input اليوزر، استخدم `isBlank()` تقريبًا دايمًا.
- **الـ index:** `"Kotlin"[0]` بترجع `Char` مش `String`، يعني `'K'` (راجع T01).
- **الـ capitalize:** كانت موجودة واتشالت. البديل الحالي: `replaceFirstChar { it.uppercase() }`. لاحظ إن `uppercase()` على `Char` بترجع `String`.
- **الـ padStart:** نفس اسم Dart بالظبط، بس الـ padding هنا `Char` (`'0'`) مش String.

### القسم 4: مقارنة الـ strings 🆕 جديد عليك

<div dir="ltr">

```kotlin
println("a == b             : ${a == b}")   // content
println("equals ignoreCase  : ${a.equals(b, ignoreCase = true)}")
println("compareTo          : ${a.compareTo(b)}")
```

</div>

العلامة `==` في Kotlin بتنادي `equals()` تحت الغطاء، يعني بتقارن **المحتوى**. ده نفس سلوك Dart، فمش هتحس بفرق. الفرق الحقيقي لو جيت من Java: هناك `==` بتقارن المرجع.

ولو عايز تقارن المرجع نفسه في Kotlin، استخدم `===`، وده المقابل لـ `identical()` في Dart.

لاحظ كمان `ignoreCase = true`: ده named argument (T06)، وهتلاقيه كتير في الـ standard library.

### القسم 5: بناء الـ strings

<div dir="ltr">

```kotlin
val built = buildString {
    append("Built ")
    append("with buildString")
}
println(listOf("Mona", "Ali", "Omar").joinToString(separator = " | ", prefix = "[", postfix = "]"))
```

</div>

- **الـ `StringBuilder`:** زي `StringBuffer` في Dart.
- **الـ `buildString { }`:** 🆕 أول مرة تشوف **lambda with receiver**. جوه الأقواس، `this` هو `StringBuilder`، فبتكتب `append` على طول من غير `sb.append`. ده نفس الأسلوب اللي Compose مبني عليه، وشرحه الكامل في T10.
- **الـ `joinToString`:** زي `join` في Dart، وفيها `prefix` و `postfix` و `transform` كمان.

### القسم 6: functions الـ Char

<div dir="ltr">

```kotlin
println("code=${ch.code} isLetter=${ch.isLetter()} isDigit=${'7'.isDigit()} digitToInt=${'7'.digitToInt()}")
```

</div>

- **`code`:** رقم الحرف في Unicode، زي `codeUnitAt(0)` في Dart.
- **`digitToInt()`:** بتحوّل `'7'` لـ `7`. خلي بالك إن `'7'.code` بترجع `55`، ودي غلطة مشهورة.

## الفخاخ في الملف

- **WATCH OUT:** `$items.size` بيطبع الـ list وبعدها `.size` كنص.
- **الفرق بين isEmpty و isBlank:** على string كلها مسافات.

## عادات Flutter اللي هتوقعك

- كتابة `s.isEmpty` من غير أقواس، والصح `isEmpty()`.
- البحث عن `toUpperCase()`، والصح `uppercase()` (القديمة deprecated).
- استخدام `'''` للـ multi-line، والصح `"""`.

## الخلاصة في 3 سطور

1. الـ templates زي Dart بالظبط، واستخدم `${}` لأي حاجة أكتر من اسم.
2. علامة `"""` بتعمل raw و multi-line مع بعض، ونضّفها بـ `trimIndent()`.
3. العلامة `==` بتقارن المحتوى، و `===` بتقارن المرجع، و `buildString` أول لمحة من أسلوب DSL.

</div>
