<div dir="rtl">

# شرح T02: المتغيرات val و var و const

**الملف:** `src/main/kotlin/T02_Variables.kt`، وسلايدز 20 و30.

## الخلاصة

الموضوع ده تقريبًا نفس Dart، الفرق في الأسماء بس. الاستثناء الوحيد المهم هو `const`، لأن معناها في Kotlin أضيق بكتير من Dart.

## خريطة Dart ← Kotlin

</div>

<div dir="ltr">

| Dart | Kotlin | ملاحظة |
| --- | --- | --- |
| `final x = 1;` | `val x = 1` | يتعيّن مرة واحدة |
| `var y = 1;` | `var y = 1` | نفس الكلمة ونفس المعنى |
| `const z = 1;` | `const val Z = 1` | top-level أو جوه `object` بس |
| `int year = 2026;` | `val year: Int = 2026` | النوع **بعد** الاسم |

</div>

<div dir="rtl">

## الشرح

### القسم 1: الفرق بين val و var

<div dir="ltr">

```kotlin
val name = "Samy"   // read-only
var count = 0       // mutable
// name = "Ali"     // ERROR: 'val' cannot be reassigned
```

</div>

اعتبر `val` هي `final` بتاعة Dart بالظبط. الـ `var` زي ما هي.

### القسم 2: الـ type inference والأنواع الصريحة

<div dir="ltr">

```kotlin
val city = "Alexandria"   // inferred: String
val year: Int = 2026      // name: Type
```

</div>

أكبر فرق في الشكل إن **النوع بييجي بعد الاسم** بعد `:`. في Dart بتكتب `int year`، وهنا بتكتب `year: Int`. نفس الأسلوب ده هتلاقيه في parameters الـ functions وفي return type: `fun f(x: Int): String`.

برضه مفيش `dynamic` هنا. محاولة `score = "high"` على متغير `Double` بتدي compile error، والأقرب لـ `dynamic` هو `Any` (في T03)، بس مش بيسمحلك تنادي أي method عليه من غير cast.

### القسم 3: تعلن دلوقتي وتعيّن بعدين

<div dir="ltr">

```kotlin
val grade: String
val mark = 75
grade = if (mark >= 50) "Pass" else "Fail"
```

</div>

ده شبه `final String grade;` في Dart. الكومبايلر بيتأكد إنك عيّنت `grade` **مرة واحدة بالظبط** قبل ما تستخدمه، وده اسمه definite assignment.

لاحظ إن `if` هنا **expression** بترجع قيمة، ودي حاجة 🆕 جديدة عليك. في Dart كنت هتستخدم `? :`، وهنا مفيش ternary operator أصلًا، والـ `if/else` نفسها هي اللي بترجع قيمة. التفاصيل في T07.

### القسم 4: الفرق بين const val و val 🆕 جديد عليك

<div dir="ltr">

```kotlin
const val APP_NAME = "Kotlin Diploma"
// const val STARTED = System.currentTimeMillis()  // ERROR
```

</div>

هنا الفرق الحقيقي عن Dart:

- **في Dart:** `const` حاجة قوية جدًا. تقدر تعمل `const` objects و `const` constructors وتكتب `const Text('Hi')`، والـ widget tree بيستفيد منها في performance.
- **في Kotlin:** `const val` محدودة جدًا. بتتكتب top-level أو جوه `object` أو `companion object` بس، ونوعها لازم يكون primitive (`Int` و `Double` و `Boolean` ...) أو `String`. القيمة بتتحط inline في كل مكان بيستخدمها وقت الـ compile.
- **مفيش const constructors** ولا const objects في Kotlin. لو عايز object ثابت، استخدم `val` عادي أو `object`.

القاعدة: `const val` للـ constants البسيطة زي keys و URLs، و `val` لأي حاجة تانية. وبالعرف بتتكتب `UPPER_SNAKE_CASE`.

### القسم 5: خد بالك، val مش immutable

<div dir="ltr">

```kotlin
val list = mutableListOf(1, 2)
list.add(3)                  // allowed
// list = mutableListOf()    // ERROR
```

</div>

نفس الحكاية في Dart: `final list = [1, 2]; list.add(3);` شغالة. المرجع (reference) ثابت، بس المحتوى ممكن يتغير.

**نقطة مهمة جدًا في Compose:** لو عندك `mutableListOf` وعملت `add`، الشاشة **مش هتتحدث**. ده زي ما في Flutter لو عدّلت list جوه state من غير `setState`. في Compose عندك حلين:

- استخدم `mutableStateListOf(...)`، وده list الـ Compose بيراقب تغييراته.
- أو اعمل **list جديدة** وحطها في state: `items = items + newItem`.

### القسم 6: القاعدة العامة

ابدأ دايمًا بـ `val`، وحوّلها لـ `var` لو احتجت فعلًا. الـ IDE هيقترح عليك ده لوحده لو لقى `var` مش بيتغير.

## الفخاخ في الملف

- **محاولة `const val` بقيمة runtime:** لأن `System.currentTimeMillis()` مش معروفة وقت الـ compile.
- **إعادة تعيين `val`:** في القسمين 1 و3.
- **تغيير نوع متغير:** `score = "high"`، لأن مفيش `dynamic`.

## عادات Flutter اللي هتوقعك

- كتابة `final` أو `int x`، والصح `val` و `x: Int`.
- محاولة عمل `const` لـ object أو list، وده مش موجود هنا.
- افتراض إن تعديل list بيحدّث الـ UI في Compose.

## الخلاصة في 3 سطور

1. الـ `val` تساوي `final`، والـ `var` زي ما هي، والنوع بييجي بعد الاسم.
2. الـ `const val` لـ primitives و String على مستوى top-level أو object بس، ومفيش const objects.
3. الـ `val` بيثبّت المرجع مش المحتوى، وفي Compose تعديل list عادية مش بيعمل recomposition.

</div>
