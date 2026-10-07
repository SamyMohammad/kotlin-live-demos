<div dir="rtl">

# شرح T19: الـ Extensions

**الملف:** `src/main/kotlin/T19_Extensions.kt`، وسلايدز 73 و78.

## الخلاصة

الـ extensions موجودة في Dart من 2.7، فالفكرة مش جديدة عليك: تضيف functions لـ class مش بتاعتك. الفرق في الـ syntax: Kotlin **مفيهاش block** `extension X on Y { }`، كل extension بتتكتب لوحدها. والقواعد المهمة (الـ member بيكسب، والـ resolution static) **نفس Dart بالظبط**.

## خريطة Dart ← Kotlin

</div>

<div dir="ltr">

```dart
// Dart
extension StringX on String {
  String get initials => ...;
  bool isEmail() => ...;
}
```

```kotlin
// Kotlin
val String.initials: String get() = ...
fun String.isEmail(): Boolean = ...
```

</div>

<div dir="rtl">

## الشرح

### القسم 1: الـ extension functions

<div dir="ltr">

```kotlin
fun String.initials(): String =
    split(" ").filter { it.isNotBlank() }.map { it.first().uppercaseChar() }.joinToString("")

fun Int.isEven() = this % 2 == 0
```

</div>

- **`String.`:** قبل اسم الـ function، وده الـ **receiver type**.
- **جوه الـ function:** `this` هو الـ string. وتقدر تكتب `split(...)` على طول بدل `this.split(...)`.
- **تحت الغطاء:** دي static function عادية أول parameter فيها الـ string: `initials(receiver: String)`. مفيش أي تعديل على class الـ `String` نفسها.

**امتى تستخدمها؟** لما عايز تضيف utility لنوع موجود، والكود يتقري طبيعي: `name.initials()` بدل `StringUtils.initials(name)`.

### القسم 2: الـ extension properties

<div dir="ltr">

```kotlin
val String.wordCount: Int
    get() = trim().split(Regex("\\s+")).size
```

</div>

زي `get` في Dart extension. لازم يكون ليها `get()`، لأن الـ extensions **مش بتقدر تخزّن state**. مفيش `field` هنا، لأن مفيش مكان تتخزن فيه.

### القسم 3: الـ nullable receiver 🆕 فيه جديد

<div dir="ltr">

```kotlin
fun String?.orDash(): String = if (this.isNullOrBlank()) "-" else this

val missing: String? = null
missing.orDash()   // "-"   no ?. needed
```

</div>

- **`String?.`:** الـ extension شغالة على النوع الـ nullable، فتقدر تناديها على `null` **من غير `?.`**. جواها `this` ممكن يكون `null`.
- **ده اللي بيخلي `isNullOrBlank()` و `orEmpty()` (من T05) تشتغل.**
- **الـ smart cast:** بعد `isNullOrBlank()`، الكومبايلر عارف إن `this` مش null في الـ `else`. ده بسبب feature اسمها **contracts** في الـ standard library.

Dart بتدعم ده برضه (`extension on String?`)، بس نادرًا ما بيستخدم هناك. في Kotlin هتشوفه كتير.

### القسم 4: الـ generic extension

<div dir="ltr">

```kotlin
fun <T> List<T>.secondOrNull(): T? = if (size >= 2) this[1] else null
```

</div>

extension على `List` من أي نوع. أغلب الـ collection functions اللي في T08 (`filter` و `map` ...) معرّفة **بالظبط بالطريقة دي**، يعني extensions على `Iterable<T>`.

### القسم 5: زي Compose، 16.dp

<div dir="ltr">

```kotlin
@JvmInline
value class Dp(val value: Int)

val Int.dp: Dp get() = Dp(this)
```

</div>

شفتها في T11. extension property على `Int` بترجع `Dp`. في Compose الحقيقية هي `val Int.dp: Dp` في `androidx.compose.ui.unit`، وفيه زيها `sp` للخطوط.

### القسم 6: extension على companion 🆕 جديد عليك

<div dir="ltr">

```kotlin
class Api {
    companion object
}

fun Api.Companion.defaultTimeout() = 30

Api.defaultTimeout()
```

</div>

لو الـ class عندها `companion object` (حتى لو فاضي)، تقدر تضيفله extensions، فتتنادى كأنها **static** على الـ class. Dart مفيهاش طريقة تضيف static members بالـ extensions.

### القسم 7: خد بالك، الـ member دايمًا بيكسب

<div dir="ltr">

```kotlin
class Box {
    private val secret = "hidden"
    fun show() = "member"
}

@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
fun Box.show() = "extension"          // never called
fun Box.paint(color: String) = "Box painted $color"
// fun Box.reveal() = secret          // ERROR: extensions can't see private members
```

</div>

- **الـ member بيكسب:** لو الـ class فيها method بنفس الاسم والـ parameters، **الـ method الأصلية هي اللي هتشتغل** والـ extension هتتجاهل. الكومبايلر بيدي warning، و `@Suppress` هنا عشان نخبيه للعرض. نفس القاعدة في Dart.
- **مفيش وصول للـ private:** الـ extension شغالة **من بره**، فمش بتشوف `private` ولا `protected`. هي مجرد function عادية.

### القسم 8: خد بالك، الـ extensions بتتحدد static

<div dir="ltr">

```kotlin
open class Shape
class Circle : Shape()

fun Shape.kind() = "Shape"
fun Circle.kind() = "Circle"

val s: Shape = Circle()
s.kind()   // "Shape"
```

</div>

الـ extensions **مش virtual**. الكومبايلر بيختار أنهي extension هيشتغل على حسب **النوع المعلن** للمتغير (`Shape`) وقت الـ compile، مش النوع الحقيقي (`Circle`) وقت التشغيل. لو عايز polymorphism، استخدم `open fun` جوه الـ class (T14). نفس سلوك Dart.

## عادات Flutter اللي هتوقعك

- كتابة `extension X on Y { }`، والصح إن كل function بتتكتب لوحدها بـ `fun Y.name()`.
- توقع إن extension تقدر تعمل override لـ method موجودة.
- توقع إن extension تقدر تخزّن قيمة.

## الخلاصة في 3 سطور

1. الـ `fun Type.name()` و `val Type.name get()` بيضيفوا لأي نوع من غير وراثة، وهما static functions تحت الغطاء.
2. الـ nullable receiver `String?.` بيخليك تنادي على `null` مباشرة، وده سر `isNullOrBlank()`.
3. الـ member بيكسب دايمًا، ومفيش وصول للـ private، والاختيار بالنوع المعلن مش الحقيقي.

</div>
