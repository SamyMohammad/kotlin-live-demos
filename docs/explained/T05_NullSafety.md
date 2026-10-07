<div dir="rtl">

# شرح T05: الـ Null safety

**الملف:** `src/main/kotlin/T05_NullSafety.kt`، وسلايدز 25–30.

## الخلاصة

لو جاي من Dart 2.12 أو أحدث، فإنت عارف الـ sound null safety. Kotlin عندها نفس الفكرة وتقريبًا نفس العلامات. الجديد عليك حاجتين: **الـ Elvis مع `return`/`throw`**، و**الـ platform types** اللي جاية من Java، ودي ثغرة مش موجودة في Dart.

## خريطة Dart ← Kotlin

</div>

<div dir="ltr">

| Dart | Kotlin |
| --- | --- |
| `String?` | `String?` |
| `a?.b` | `a?.b` |
| `a ?? b` | `a ?: b` (Elvis) |
| `a!` | `a!!` |
| `late String x;` | `lateinit var x: String` |
| `late final x = compute();` | `val x by lazy { compute() }` |
| `if (a != null) { a.length }` | `if (a != null) { a.length }` |
| — | `a?.let { }` |

</div>

<div dir="rtl">

## الشرح

### القسم 1: الأنواع nullable والأنواع non-null

<div dir="ltr">

```kotlin
var city: String = "Alex"
// city = null              // ERROR
var nickname: String? = null
```

</div>

زي Dart بالظبط: من غير `?` النوع مش بيقبل `null`.

### القسم 2: الـ safe call

<div dir="ltr">

```kotlin
println("nickname?.length = ${nickname?.length}")   // null
```

</div>

نفس Dart. لو `nickname` قيمته `null`، الـ expression كله بيرجع `null` من غير crash، ونوع النتيجة `Int?`.

### القسم 3: الـ Elvis

<div dir="ltr">

```kotlin
println("length or 0   = ${empty?.length ?: 0}")
println("name or Guest = ${empty ?: "Guest"}")
```

</div>

هي `??` بتاعة Dart باسم وعلامة مختلفة. سموها Elvis لأن `?:` لو لفيتها شبه تسريحة شعر Elvis Presley.

### القسم 4: الـ not-null assertion

<div dir="ltr">

```kotlin
println("sure!!.length = ${sure!!.length}")
tryIt("empty!!.length") { empty!!.length }   // NullPointerException
```

</div>

دي `!` بتاعة Dart، بس بعلامتين عشان تبان وحشة في الكود، وده مقصود. لو القيمة طلعت `null` هتاخد `NullPointerException`. في code review، كل `!!` المفروض يتسأل عنها.

### القسم 5: الـ smart cast بعد null check

<div dir="ltr">

```kotlin
if (input != null) {
    println("input.length = ${input.length}")   // input is String here
}
```

</div>

دي الـ type promotion بتاعة Dart. الكومبايلر بيعتبر `input` من نوع `String` جوه الـ `if`.

### القسم 6: الـ ?.let 🆕 جديد عليك

<div dir="ltr">

```kotlin
input?.let { println("let got: $it") }
val label = empty?.let { "Hi $it" } ?: "No name"
```

</div>

دي أكتر idiom هتشوفها في كود Android:

- **إزاي بتشتغل:** `let` بتاخد lambda وبتنفذها، وجواها القيمة اسمها `it`. ولما تتكتب `?.let`، الـ lambda **بتتنفذ بس لو القيمة مش `null`**، وجواها `it` نوعه non-null.
- **بترجع قيمة:** `let` بترجع آخر سطر في الـ lambda. فالسطر التاني معناه: لو فيه اسم اعمل "Hi" + الاسم، ولو مفيش استخدم "No name".
- **في Dart:** كنت هتكتب `if (x != null) { ... }` أو `x == null ? 'No name' : 'Hi $x'`.

الـ `let` واحدة من الـ scope functions، وشرحها الكامل في T20.

### القسم 7: الـ Elvis مع return و throw 🆕 جديد عليك

<div dir="ltr">

```kotlin
fun greet(name: String?): String {
    val n = name ?: return "Hello, stranger"
    return "Hello, $n"
}

fun requireName(name: String?): String =
    name ?: throw IllegalArgumentException("name is required")
```

</div>

دي ميزة جميلة مش موجودة في Dart. في Kotlin، `return` و `throw` **expressions**، ونوعهم الخاص اسمه `Nothing`، فتقدر تحطهم على يمين `?:`.

السطر `val n = name ?: return "..."` معناه: لو `name` مش null حطه في `n`، ولو null اخرج من الـ function. وبعد السطر ده `n` نوعه `String` مش `String?`.

ده بيحل محل الـ guard clause:

</div>

<div dir="ltr">

```dart
// Dart
if (name == null) return 'Hello, stranger';
```

</div>

<div dir="rtl">

بس في Kotlin بتعمل الاتنين في سطر واحد: الـ check والـ assignment. هتستخدمه كتير في ViewModels: `val user = repo.getUser() ?: return`.

### القسم 8: الـ chained safe calls

<div dir="ltr">

```kotlin
users.forEach { println("upper = ${it?.name?.uppercase() ?: "unknown"}") }
```

</div>

زي Dart. أول `null` في السلسلة بيوقفها وبيرجع `null`، وبعدها الـ Elvis بيدي قيمة بديلة.

### القسم 9: خد بالك، مفيش smart cast على var properties

<div dir="ltr">

```kotlin
val user = User("Ali")
// if (user.name != null) println(user.name.length)
// ERROR: smart cast is impossible, 'name' is a mutable property
user.name?.let { println("fix 1, ?.let     : ${it.length}") }
val localName = user.name
if (localName != null) println("fix 2, local val : ${localName.length}")
```

</div>

السبب: `name` معرّفة `var` في class. الكومبايلر مش ضامن إن thread تاني مش هيغيرها لـ `null` بين الـ check والاستخدام، فبيرفض الـ smart cast.

Dart عندها نفس القيد بالظبط على fields، وكنت بتحلها بـ local variable. الحلول هنا:

- **الحل الأول:** `?.let { }`.
- **الحل التاني:** انسخ القيمة لـ `val` محلي واعمل الـ check عليه.

### القسم 10: الـ lateinit

<div dir="ltr">

```kotlin
lateinit var token: String
tryIt("read token before init") { println(token) }
println("isInitialized = ${::token.isInitialized}")
// lateinit var count: Int   // ERROR: not allowed on primitive types
```

</div>

دي `late` بتاعة Dart، مع فروق:

- **بتشتغل مع `var` بس:** للـ `val` المتأخرة استخدم `by lazy` (في T13).
- **مش بتشتغل مع primitives:** زي `Int` و `Boolean`، لأنها في الـ JVM مش بتقبل `null`، و `lateinit` بتستخدم `null` داخليًا كعلامة إنها لسه متعينتش.
- **🆕 `::token.isInitialized`:** تقدر تسأل لو اتعينت ولا لأ، ودي مش موجودة في Dart. والـ `::` معناها reference للـ property نفسها مش لقيمتها.
- **الـ exception:** لو قريتها قبل التعيين هتاخد `UninitializedPropertyAccessException`.

في Android هتشوفها كتير مع حاجات بتتعيّن في lifecycle زي `onCreate`، أو مع dependency injection.

### القسم 11: خد بالك، Java بترجع platform types 🆕 جديد عليك

<div dir="ltr">

```kotlin
val javaUser = JavaUser(null)
val fromJava = javaUser.name         // type is String! (Kotlin doesn't know)
tryIt("fromJava.length") { fromJava.length }
val safe: String? = javaUser.name
```

</div>

دي أهم حاجة جديدة في الملف. Java مفيهاش null safety، فلما Kotlin تاخد قيمة من Java مش بتعرف هي nullable ولا لأ. النوع ده اسمه **platform type** وبيتكتب في الـ IDE `String!`. الكومبايلر بيسيبك تتعامل معاه كأنه `String` عادي، ولو طلع `null` هتاخد crash.

الحل: لما تاخد قيمة من Java API، **حدد النوع بنفسك** `String?` لو مش متأكد.

ليه ده مهم؟ لأن جزء كبير من Android SDK مكتوب بـ Java. جوجل ضافت annotations زي `@Nullable` و `@NonNull` لأغلبه، والكومبايلر بيحترمها، بس مش كله. في Flutter الحاجة دي مكانتش بتقابلك لأن الـ platform channels بتعدي على serialization.

لاحظ كمان إن `javaUser.name` بتنادي `getName()`. Kotlin بتحول الـ Java getters لـ properties تلقائيًا. شوف `JavaUser.md`.

### القسم 12: الـ collections والـ null

<div dir="ltr">

```kotlin
println("filterNotNull = ${mixed.filterNotNull()}")
println("isNullOrBlank = ${empty.isNullOrBlank()}")
println("orEmpty       = '${empty.orEmpty()}'")
```

</div>

- **`filterNotNull()`:** بتحوّل `List<String?>` لـ `List<String>`، زي `whereType<String>()` في Dart. Dart 3 فيها كمان `nonNulls`.
- **🆕 `isNullOrBlank()` و `orEmpty()`:** extension functions شغالة على `String?` نفسه، يعني تقدر تناديها على قيمة null من غير `?.`. ده ممكن لأن الـ extension معرّفة على النوع nullable، وده موضوع T19.

## الفخاخ في الملف

- **ERROR:** إسناد `null` لنوع non-null.
- **ERROR:** smart cast على `var` property.
- **ERROR:** استخدام `lateinit` مع `Int`.
- **CRASH:** `!!` على null بيرمي `NullPointerException`.
- **CRASH:** قراءة `lateinit` قبل التعيين بترمي `UninitializedPropertyAccessException`.
- **CRASH:** platform type من Java طلع null.

## عادات Flutter اللي هتوقعك

- كتابة `??`، والصح `?:`.
- كتابة `!`، والصح `!!`، والأحسن إنك متكتبهاش أصلًا.
- كتابة `if (x == null) return;` على سطرين، والـ idiomatic هنا هو `val y = x ?: return`.
- الثقة في أي قيمة جاية من Java.

## الخلاصة في 3 سطور

1. نفس فكرة Dart: `?` و `?.` و `?:` و `!!`، والـ smart cast مش بيشتغل على `var` properties.
2. الـ `?.let { }` والـ `?: return` هما الـ idioms اللي هتكتبهم كل يوم.
3. القيم اللي جاية من Java ممكن تكون null من غير ما الكومبايلر يحذرك، فحدد نوعها `?` بنفسك.

</div>
