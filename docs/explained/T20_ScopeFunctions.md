<div dir="rtl">

# شرح T20: الـ Scope functions

**الملف:** `src/main/kotlin/T20_ScopeFunctions.kt`، وسلايدز 74–76 و78.

## الخلاصة

دول 5 functions صغيرين (`let` و `run` و `with` و `apply` و `also`) هتلاقيهم في **كل** ملف Kotlin 🆕. مفيش ليهم مقابل في Dart غير الـ cascade `..` اللي بيقرب من `apply`. هما مش syntax جديد، دول functions عادية مبنية على اللي اتعلمته في T09 و T10.

الفرق بينهم كله في سؤالين بس:

1. **الـ object جوه الأقواس اسمه إيه؟** `it` ولا `this`؟
2. **بيرجع إيه؟** الـ object نفسه ولا نتيجة الـ lambda؟

## الجدول اللي لازم تحفظه

</div>

<div dir="ltr">

| الـ function | الـ object جواها | بترجع | الاستخدام |
| --- | --- | --- | --- |
| `let` | `it` | نتيجة الـ lambda | null check (`?.let`)، تحويل |
| `run` | `this` | نتيجة الـ lambda | تحسب حاجة من object |
| `with` | `this` | نتيجة الـ lambda | calls كتير على object واحد |
| `apply` | `this` | **الـ object** | تظبط object (زي `..` في Dart) |
| `also` | `it` | **الـ object** | side effects: log أو validate |

</div>

<div dir="rtl">

طريقة سهلة للحفظ:

- **`apply` و `also`:** بيرجعوا الـ object، يعني بيكمّلوا السلسلة.
- **`let` و `run` و `with`:** بيرجعوا النتيجة، يعني بيحوّلوا.
- **`it`:** مع `let` و `also`، وتقدر تسميه اسم تاني.
- **`this`:** مع `run` و `with` و `apply`، وتنادي الـ members على طول.

## الشرح

### القسم 1: let، للـ null check أو التحويل

<div dir="ltr">

```kotlin
val length = found?.let { it.length }
found?.let { println("Hello ${it.replaceFirstChar { c -> c.uppercase() }}") }
```

</div>

أشهر استخدام: `?.let { }` بينفذ بس لو القيمة مش null (T05).

لاحظ الـ `c ->`: عندنا lambda جوه lambda. لو الاتنين استخدموا `it` هيحصل لخبطة، فسمينا الداخلية `c`. قاعدة: **متعملش nested `it`**.

### القسم 2: apply، تظبط object

<div dir="ltr">

```kotlin
val p = Person("Ali").apply {
    age = 25
    city = "Alexandria"
}
```

</div>

دي الـ cascade بتاعة Dart:

</div>

<div dir="ltr">

```dart
final p = Person('Ali')
  ..age = 25
  ..city = 'Alexandria';
```

</div>

<div dir="rtl">

استخدامات حقيقية في Android: تظبيط `Intent` أو `Bundle` أو `Paint`، أو أي object بتعمله وتملا خصائصه.

### القسم 3: also، للـ side effects

<div dir="ltr">

```kotlin
val list = mutableListOf(1, 2)
    .also { println("  created: $it") }
    .apply { add(3) }
```

</div>

`also` معناها "وكمان اعمل كذا". بتعمل حاجة جانبية زي log أو validation، وبترجع الـ object زي ما هو، فالسلسلة بتكمل. الفرق بينها وبين `apply`: جواها `it` مش `this`، وده بيوضح إنك **مش بتعدّل** في الـ object، بتستخدمه بس.

### القسم 4: run، تحسب نتيجة

<div dir="ltr">

```kotlin
val bio = p.run { "$name, $age, from $city" }
val total = run {
    val a = 10
    val b = 20
    a + b
}
```

</div>

- **`p.run { }`:** جواها `this` هو `p`، وبترجع آخر سطر.
- **`run { }` من غير receiver:** مجرد block بيرجع قيمة، والمتغيرات جواه (`a` و `b`) مش بتطلع بره. مفيد لما تحسب قيمة في كذا خطوة.

### القسم 5: with

<div dir="ltr">

```kotlin
val report = with(StringBuilder()) {
    append("Name: ${p.name}\n")
    append("Age : ${p.age}")
    toString()
}
```

</div>

زي `run` بالظبط، بس الـ object بيتبعت كـ argument مش قبل النقطة. الاستخدام: لما يكون عندك object موجود وعايز تعمل عليه calls كتير. وفي Compose هتشوف `with(LocalDensity.current) { 16.dp.toPx() }`.

### القسم 6: takeIf و takeUnless

<div dir="ltr">

```kotlin
val adult = p.takeIf { it.age >= 18 }
val input = "   ".takeUnless { it.isBlank() } ?: "default"
```

</div>

- **`takeIf`:** بترجع الـ object لو الشرط متحقق، وإلا `null`.
- **`takeUnless`:** العكس.
- **مع `?:`:** بتعمل default value حلوة في سطر واحد.

### القسم 7: الكومبو الواقعي

<div dir="ltr">

```kotlin
val user = findName(false)?.let { Person(it) } ?: Person("Guest")
```

</div>

اقراها: لو فيه اسم اعمل Person بيه، ولو مفيش اعمل Guest. هتكتب الشكل ده كتير.

### القسم 8: خد بالك، let من غير ?

<div dir="ltr">

```kotlin
val nothing: String? = findName(false)
nothing.let { println("  let without ?. runs anyway: $it") }   // prints "null"
nothing?.let { println("  never printed") }
```

</div>

`let` لوحدها **مش null check**، هي مجرد function. الـ `?.` هو اللي بيعمل الـ check. لو نسيتها، الـ lambda هتشتغل و `it` هيبقى `null`.

### القسم 9: خد بالك، إيه اللي بيرجع؟

<div dir="ltr">

```kotlin
val a = "Hi".apply { length }   // "Hi"
val b = "Hi".let { it.length }  // 2
```

</div>

أكتر غلطة: تستخدم `apply` وانت عايز النتيجة، أو `let` وانت عايز الـ object. ارجع للجدول.

### القسم 10: خد بالك، الأسماء المحلية بتخبي members الـ this

<div dir="ltr">

```kotlin
val city = "Cairo"
val where = p.run { "lives in $city" }        // the LOCAL city wins
p.run { "lives in ${this.city}" }             // fix
```

</div>

جوه `run` أو `apply` أو `with`، لو فيه متغير محلي بنفس اسم property في الـ object، **المتغير المحلي هو اللي بيكسب**، من غير أي warning. الحل `this.city` صريح. وده واحد من أسباب إن `apply` مع objects فيها أسماء شائعة (زي `name` و `id`) ممكن تعمل bugs.

### القسم 11: خد بالك، متعملش nesting كتير

<div dir="ltr">

```kotlin
p.let { it.apply { also { run { } } } }   // unreadable
```

</div>

الـ scope functions بتغري إنك تكتب كل حاجة في سطر واحد. لو احتجت أكتر من مستوى، غالبًا متغير عادي بـ `val` أوضح. الهدف قراءة أسهل مش كود أقصر.

## عادات Flutter اللي هتوقعك

- البحث عن `..` cascade، والبديل `apply`.
- استخدام `let` من غير `?.` على أساس إنها null check.
- الإفراط في الـ scope functions في الأول لأنها "شكلها Kotlin".

## الخلاصة في 3 سطور

1. الـ `apply` و `also` بيرجعوا الـ object، والـ `let` و `run` و `with` بيرجعوا النتيجة.
2. الـ `?.let` للـ null، و `apply` للتظبيط، و `also` للـ log، و `run` للحساب.
3. خلي بالك من `let` من غير `?`، ومن الأسماء المحلية اللي بتخبي `this`، ومن الـ nesting.

</div>
