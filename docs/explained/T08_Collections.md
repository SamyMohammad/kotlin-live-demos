<div dir="rtl">

# شرح T08: الـ Collections

**الملف:** `src/main/kotlin/T08_Collections.kt`، وسلايدز 42–43 و51.

## الخلاصة

أكبر فرق عن Dart إن **الـ read-only والـ mutable نوعين منفصلين**: `List` و `MutableList`. وكمان الـ operations زي `map` و `filter` **بترجع List على طول** من غير `.toList()`، لأنها مش lazy زي `Iterable` في Dart.

## خريطة Dart ← Kotlin

</div>

<div dir="ltr">

| Dart | Kotlin |
| --- | --- |
| `[1, 2]` | `listOf(1, 2)` (read-only) / `mutableListOf(1, 2)` |
| `{1, 2}` | `setOf(1, 2)` / `mutableSetOf` |
| `{'a': 1}` | `mapOf("a" to 1)` / `mutableMapOf` |
| `.where(...)` | `.filter { }` |
| `.map(...).toList()` | `.map { }` |
| `.firstWhere(...)` | `.first { }` |
| `.firstWhereOrNull` (collection pkg) | `.firstOrNull { }` |
| `.every(...)` | `.all { }` |
| `.fold(0, (a, b) => a + b)` | `.fold(0) { acc, n -> acc + n }` |
| `.expand(...)` | `.flatMap { }` |
| `List.unmodifiable(...)` | `List` (read-only view) |

</div>

<div dir="rtl">

## الشرح

### القسم 1: الـ lists الـ read-only والـ mutable 🆕 جديد عليك

<div dir="ltr">

```kotlin
val nums = listOf(3, 1, 4, 1, 5)
// nums.add(9)          // ERROR
val mutable = mutableListOf(1, 2)
mutable.add(3)
mutable[0] = 20
mutable += 30
```

</div>

في Dart، `[1, 2]` mutable دايمًا، ولو عايز read-only بتعمل `List.unmodifiable` وبتعرف وقت التشغيل لو حد حاول يعدل.

في Kotlin النوع نفسه بيقول: `List` مفيهوش `add` أصلًا، والكومبايلر بيرفض. لو عايز تعدّل لازم `MutableList`.

القاعدة في Android: **اعرض `List` واخفي `MutableList`**. في ViewModel مثلًا يكون عندك `private val _items = mutableListOf()` جوه، و `val items: List` بره.

لاحظ إن `mutable += 30` بتعمل `add`. ده operator overloading (في T23).

### القسم 2: الـ sets والـ maps

<div dir="ltr">

```kotlin
val ages = mapOf("Ali" to 20, "Mona" to 22)
println("${ages["Nobody"]}")                       // null
println("${ages.getOrDefault("Nobody", 0)}")
for ((name, age) in ages) println("$name is $age")
val pair = "Ali" to 20
```

</div>

- **الـ `to`:** 🆕 مش syntax خاص بالـ maps. دي **infix function** بتعمل `Pair`، فـ `"Ali" to 20` تساوي `Pair("Ali", 20)`. والـ `mapOf` بتاخد vararg من الـ pairs.
- **قراءة key مش موجود:** بترجع `null` زي Dart، والنوع `Int?`.
- **`for ((name, age) in ages)`:** destructuring لكل entry، أنضف من `ages.entries` و `entry.key`.
- **الـ `Pair`:** فيه `first` و `second`، وهو أقرب حاجة للـ records `(String, int)` في Dart 3. الفرق إن الـ records في Dart بتدعم named fields، وفي Kotlin الأحسن تعمل `data class` لو الحاجة ليها معنى.

### القسم 3: الوصول للعناصر

<div dir="ltr">

```kotlin
println("${nums.getOrNull(10)} ${nums.indexOf(4)} ${4 in nums}")
```

</div>

- **`getOrNull`:** بترجع `null` بدل crash لو الـ index بره.
- **`4 in nums`:** بتنادي `contains` (operator تاني من T23).

### القسم 4: التحويل

<div dir="ltr">

```kotlin
nums.filter { it > 2 }
nums.map { it * 10 }
nums.mapIndexed { i, n -> "$i:$n" }
```

</div>

دي أهم نقطة: في Dart، `.where()` و `.map()` بيرجعوا `Iterable` **lazy**، ولازم `.toList()` في الآخر. في Kotlin بيرجعوا **`List` جديدة على طول** (eager). كل خطوة بتعمل list جديدة. للبيانات الكبيرة جدًا فيه `Sequence` (القسم 9).

لاحظ الـ syntax: `filter { it > 2 }` من غير أقواس عادية. لما آخر parameter يكون lambda، بيطلع بره الأقواس، ولو هو الـ parameter الوحيد بتشيل الأقواس خالص. و `it` هو الاسم الافتراضي لما الـ lambda فيها parameter واحد. ده كله في T09.

### القسم 5: الأسئلة

<div dir="ltr">

```kotlin
nums.any { it > 4 }
nums.all { it > 0 }
nums.none { it < 0 }
nums.count { it == 1 }
```

</div>

- **`any`:** زي Dart.
- **`all`:** هي `every` في Dart.
- **`none`:** مش موجودة في Dart.
- **`count` بـ شرط:** بدل `where(...).length`.

### القسم 6: التجميع

<div dir="ltr">

```kotlin
println("sum=${nums.sum()} average=${nums.average()} max=${nums.max()} min=${nums.min()}")
nums.fold(0) { acc, n -> acc + n }
nums.reduce { acc, n -> acc * n }
```

</div>

في Dart مفيش `sum()` في الـ core، كنت بتحتاج `package:collection` أو `fold`. هنا كلهم جاهزين. خلي بالك إن `max()` و `reduce` بيرموا exception على list فاضية. البدائل الآمنة: `maxOrNull()` و `reduceOrNull`.

### القسم 7: أمثلة واقعية 🆕 فيها جديد

<div dir="ltr">

```kotlin
students.sortedByDescending { it.grade }.map { it.name }
students.groupBy { it.track }.mapValues { (_, list) -> list.map { it.name } }
val (passed, failed) = students.partition { it.grade >= 50 }
students.maxByOrNull { it.grade }?.name
students.sumOf { it.grade }
students.associateBy { it.name }["Sara"]
```

</div>

دي الـ functions اللي هتوفرلك سطور كتير، وأغلبها مش موجود في Dart core:

- **`sortedByDescending { }`:** بترجع list جديدة مترتبة. في Dart، `sort()` بتعدّل الـ list نفسها وبترجع `void`، ودي حاجة بتوقع ناس كتير.
- **`groupBy`:** بترجع `Map<String, List<Student>>`.
- **`mapValues { (_, list) -> }`:** الـ `_` معناها مش محتاج الـ key، والأقواس معناها destructuring للـ entry.
- **`partition`:** بتقسم لـ list بتحقق الشرط و list مبتحققوش، وبترجع `Pair`، فبنعمل destructuring على طول.
- **`maxByOrNull`:** العنصر صاحب أكبر قيمة.
- **`sumOf`:** المجموع على property.
- **`associateBy`:** بتحوّل list لـ map بمفتاح من اختيارك.

**الـ `data class Student`** في أول الملف: دي class بـ `equals` و `toString` جاهزين (T15)، عشان كده الطباعة شكلها حلو.

### القسم 8: functions تانية

<div dir="ltr">

```kotlin
nums.take(2); nums.drop(2); nums.takeLast(2)
nums.chunked(2)
listOf("a", "b", "c").zip(listOf(1, 2, 3))
listOf(listOf(1, 2), listOf(3)).flatMap { it }
nums.find { it > 3 }
```

</div>

- **`drop`:** هي `skip` في Dart.
- **`chunked`:** بتقسم لمجموعات.
- **`zip`:** بتعمل pairs من list-تين.
- **`flatMap`:** هي `expand` في Dart.
- **`find`:** زي `firstOrNull` بشرط.

### القسم 9: الـ Sequences (lazy)

<div dir="ltr">

```kotlin
val firstThree = (1..1_000_000).asSequence()
    .map { it * 2 }
    .filter { it % 3 == 0 }
    .take(3)
    .toList()
```

</div>

دي **المقابل الحقيقي لـ `Iterable` في Dart**: كل عنصر بيعدي على كل الخطوات واحد واحد، والشغل بيقف أول ما `take(3)` تكتفي. من غير `asSequence()`، كان هيعمل list بمليون عنصر في `map`.

امتى تستخدمها؟ لما البيانات كبيرة وفيه خطوات كتير، أو لما فيه `take` أو `first` بيوقف بدري. غير كده الـ lists العادية أسرع وأبسط.

### القسم 10: خد بالك

<div dir="ltr">

```kotlin
tryIt("first { it > 100 }") { nums.first { it > 100 } }   // NoSuchElementException
nums.firstOrNull { it > 100 }
tryIt("nums[99]") { nums[99] }                             // IndexOutOfBoundsException
val backing = mutableListOf(1, 2)
val readOnly: List<Int> = backing
backing.add(3)
println("readOnly now = $readOnly")   // [1, 2, 3]
```

</div>

- **`first { }`:** بترمي exception لو مفيش عنصر، زي `firstWhere` في Dart. استخدم `firstOrNull` في الغالب.
- **read-only مش immutable:** `List` معناها **إنت** مش هتقدر تعدّل، مش إن الـ list نفسها ثابتة. هنا `readOnly` و `backing` نفس الـ object، فلما `backing` اتعدّلت، `readOnly` اتغيرت معاها. لو محتاج نسخة ثابتة فعلًا، استخدم `toList()` عشان تعمل نسخة.

ملحوظة الـ Compose في الآخر: `LazyColumn { items(students) { StudentRow(it) } }` ده المقابل لـ `ListView.builder`.

## الفخاخ في الملف

- **ERROR:** `add` على `List` read-only.
- **CRASH:** `first { }` من غير نتيجة بترمي `NoSuchElementException`.
- **CRASH:** index برة الحدود بيرمي `IndexOutOfBoundsException`.
- **WATCH OUT:** read-only list ممكن تتغير من مرجع تاني.

## عادات Flutter اللي هتوقعك

- كتابة `.toList()` بعد كل `map`، ومش محتاجها.
- كتابة `where` و `every` و `expand`، والصح `filter` و `all` و `flatMap`.
- توقع إن `listOf` mutable زي `[]`.
- استخدام `sort()` وانت عايز نسخة جديدة، والصح `sorted()`. والعكس: `sort()` موجودة على `MutableList` وبتعدّل في مكانها.

## الخلاصة في 3 سطور

1. الـ `List` read-only والـ `MutableList` mutable، واعرض الأولى واخفي التانية.
2. الـ `map` و `filter` بيرجعوا `List` على طول، والـ `Sequence` هي النسخة الـ lazy.
3. استخدم دايمًا نسخ `OrNull` زي `firstOrNull` و `getOrNull` و `maxByOrNull` بدل الـ crash.

</div>
