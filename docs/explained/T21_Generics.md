<div dir="rtl">

# شرح T21: الـ Generics (بونص)

**الملف:** `src/main/kotlin/T21_Generics.kt`، ومش في السلايدز.

## الخلاصة

الـ generics الأساسية زي Dart تقريبًا: `Box<T>` و `<T>` قبل الـ function. الفروق المهمة اتنين:

1. **Type erasure:** في Dart الـ generics **reified** يعني النوع موجود وقت التشغيل (`x is List<String>` شغالة). في Kotlin على الـ JVM النوع **بيتمسح**، إلا لو استخدمت `inline` + `reified` 🆕.
2. **Variance:** في Dart كل الـ generics covariant، وده unsound وبيدي runtime errors. في Kotlin فيه `out` و `in`، والكومبايلر بيمسك الغلط 🆕.

## خريطة Dart ← Kotlin

</div>

<div dir="ltr">

| Dart | Kotlin |
| --- | --- |
| `class Box<T>` | `class Box<T>` |
| `T first<T>(List<T> l)` | `fun <T> first(l: List<T>): T` |
| `T extends num` | `T : Number` |
| `T extends Comparable<T>` | `T : Comparable<T>` |
| `List<dynamic>` / `List<Object?>` | `List<*>` |
| `x is T` (شغالة) | `x is T` محتاجة `reified` |
| `whereType<String>()` | `filterIsInstance<String>()` |

</div>

<div dir="rtl">

## الشرح

### القسم 1: generic class

<div dir="ltr">

```kotlin
class Box<T>(val value: T) {
    fun <R> map(transform: (T) -> R): Box<R> = Box(transform(value))
}

val box = Box(42)                        // Box<Int>
val text = box.map { "value is $it" }    // Box<String>
```

</div>

- **`Box(42)`:** الكومبايلر استنتج `T = Int` لوحده، ومش محتاج تكتب `Box<Int>(42)`.
- **`fun <R> map`:** function جوه الـ class ليها generic تاني خاص بيها. بتحوّل `Box<T>` لـ `Box<R>`. نفس فكرة `map` في الـ collections.

### القسم 2: generic functions

<div dir="ltr">

```kotlin
fun <T> firstOrDefault(list: List<T>, default: T): T = list.firstOrNull() ?: default
firstOrDefault(emptyList(), "none")
```

</div>

الـ `<T>` بتيجي **قبل** اسم الـ function في Kotlin، وبعده في Dart. و `emptyList()` عرفت نوعها (`List<String>`) من الـ `default`.

### القسم 3: الـ constraints

<div dir="ltr">

```kotlin
fun <T : Comparable<T>> biggest(a: T, b: T, c: T): T = maxOf(a, maxOf(b, c))
fun <T : Number> sumAll(nums: List<T>): Double = nums.sumOf { it.toDouble() }
// biggest(listOf(1), listOf(2), listOf(3))  // ERROR: List is not Comparable
```

</div>

العلامة `:` بدل `extends`. `T : Comparable<T>` معناها أي نوع يتقارن بنفسه، زي `Int` و `String`. ولو عايز أكتر من constraint:

</div>

<div dir="ltr">

```kotlin
fun <T> f(x: T) where T : Comparable<T>, T : CharSequence
```

</div>

<div dir="rtl">

لاحظ `sumAll(listOf(1, 2.5, 3L))`: list فيها `Int` و `Double` و `Long`، فنوعها `List<Number>`، و `Number` هو الأب لكل الأرقام، زي `num` في Dart.

### القسم 4: generic sealed result 🆕 فيها جديد

<div dir="ltr">

```kotlin
sealed interface ApiResult<out T> {
    data class Ok<T>(val value: T) : ApiResult<T>
    data class Err(val error: String) : ApiResult<Nothing>
}

fun parseAge(text: String): ApiResult<Int> =
    text.toIntOrNull()?.let { ApiResult.Ok(it) } ?: ApiResult.Err("'$text' is not a number")

when (val r = parseAge(input)) {
    is ApiResult.Ok -> println("  ok: ${r.value}")
    is ApiResult.Err -> println("  error: ${r.error}")
}
```

</div>

ده نمط هتشوفه في كل repository في Android، زي `Either` في `dartz` أو sealed `Result` في Dart 3.

الجديد هنا:

- **`out T`:** معناها `ApiResult<Int>` ينفع يتعامل كـ `ApiResult<Number>` أو `ApiResult<Any>`، لأن `T` **بيطلع بس** (output) ومش بيدخل. التفاصيل في القسم 6.
- **`ApiResult<Nothing>`:** الـ `Err` مفيهوش قيمة، فبنقول إن نوعه `Nothing` (T06). ولأن `Nothing` نوع فرعي من كل حاجة، و `ApiResult` معاها `out`، يبقى `Err` ينفع يترجع من أي function بترجع `ApiResult<Int>` أو `ApiResult<User>` أو أي حاجة.
- **`when (val r = ...)`:** بتعرّف متغير جوه الـ `when` نفسها، ومتاح جواها بس. نظيفة جدًا.

### القسم 5: reified، النوع بيعيش وقت التشغيل 🆕 جديد عليك

<div dir="ltr">

```kotlin
inline fun <reified T> List<Any>.onlyOf(): List<T> = filterIsInstance<T>()

mixed.onlyOf<String>()
mixed.onlyOf<Int>()
```

</div>

**المشكلة:** على الـ JVM، `List<String>` و `List<Int>` نفس الحاجة وقت التشغيل (type erasure). فجوه function عادية `fun <T> f()`، مينفعش تكتب `x is T`، لأن `T` مش موجود وقت التشغيل.

**الحل:** `inline` + `reified`. الـ function بتتنسخ مكان كل نداء (T09)، والكومبايلر بيحط النوع الحقيقي (`String`) مكان `T` في النسخة دي. فـ `is T` بقت `is String`.

**في Dart:** المشكلة دي أصلًا مش موجودة، لأن الـ generics reified دايمًا. عشان كده هتستغرب أول مرة الكومبايلر يقولك "Cannot check for instance of erased type".

هتشوف `reified` كتير في Android: `viewModel<MyViewModel>()` و `hiltViewModel<MyViewModel>()`.

### القسم 6: الـ Variance (للعلم) 🆕 جديد عليك

<div dir="ltr">

```kotlin
val cats = listOf(Cat("Tom"), Cat("Kitty"))
printNames(cats)                  // List<Cat> works as List<Animal>
val mutableCats = mutableListOf(Cat("Tom"))
// val animals: MutableList<Animal> = mutableCats   // ERROR: MutableList is invariant
printSize(mutableCats)            // List<*>
```

</div>

السؤال: هل list القطط ينفع تتعامل كـ list حيوانات؟

- **الـ `List<Cat>` تتحول لـ `List<Animal>`:** آه، لأن `List` read-only ومعرّفة `List<out E>`. انت هتقرا منها حيوانات بس، وكل قطة حيوان.
- **الـ `MutableList<Cat>` تتحول لـ `MutableList<Animal>`:** لأ. لو سمح، تقدر تعمل `animals.add(Dog())`، وتبقى حاطط كلب في list القطط. فالكومبايلر بيرفض من الأول.

**في Dart:** `List<Animal> animals = <Cat>[];` مسموح، و `animals.add(Dog())` بيـcompile عادي و**بيكسر وقت التشغيل**. Kotlin بتمسك ده وقت الـ compile.

- **`out T`:** الـ T بيطلع بس (producer). مثال: `List` و `Flow`.
- **`in T`:** الـ T بيدخل بس (consumer). مثال: `Comparator`.
- **`List<*>`:** list من حاجة مش معروفة. تقدر تقرا منها كـ `Any?`، بس متقدرش تضيف.

القاعدة بالإنجليزي: "Producer `out`, Consumer `in`". مش محتاج تتقنها دلوقتي، بس لما تشوف `out` في كود مكتبة هتعرف معناها.

## الفخاخ في الملف

- **ERROR:** `List` مش `Comparable`.
- **ERROR:** `MutableList<Cat>` مش `MutableList<Animal>`.

## عادات Flutter اللي هتوقعك

- كتابة `x is T` أو `x is List<String>` وتتوقع إنها تشتغل.
- كتابة `T extends X`، والصح `T : X`.
- توقع إن أي list تتحول لـ list من النوع الأب.

## الخلاصة في 3 سطور

1. الـ syntax زي Dart تقريبًا، بس `<T>` قبل اسم الـ function و `:` بدل `extends`.
2. الأنواع بتتمسح وقت التشغيل، فاستخدم `inline` + `reified` لو محتاج `is T`.
3. الـ `out` و `in` بيخلوا الكومبايلر يمنع أخطاء الأنواع اللي Dart بتكتشفها وقت التشغيل بس.

</div>
