<div dir="rtl">

# شرح T10: الـ Lambda with receiver

**الملف:** `src/main/kotlin/T10_LambdaWithReceiver.kt`، وسلايد 47.

## الخلاصة

دي **أهم feature جديدة عليك في السيشن كلها** 🆕، ومفيش ليها مقابل في Dart. هي اللي بتخلي `apply` و `buildString` و `Row { }` و `Column { }` يشتغلوا. لو فهمت الملف ده، Compose هيبقى منطقي بدل ما يبان سحر.

الفكرة في سطر واحد: lambda **جواها `this` هو object معين**، فبتنادي الـ methods بتاعته على طول من غير ما تكتب اسمه.

## المقارنة

</div>

<div dir="ltr">

| النوع | جوه الـ lambda | مثال |
| --- | --- | --- |
| `(T) -> Unit` | الـ object اسمه `it` | `{ it.append("x") }` |
| `T.() -> Unit` | الـ object هو `this` | `{ append("x") }` |

</div>

<div dir="rtl">

أقرب حاجة ليها في Dart هي الـ cascade `..`:

</div>

<div dir="ltr">

```dart
final sb = StringBuffer()
  ..write('Hello, ')
  ..write('Kotlin');
```

</div>

<div dir="rtl">

بس الـ cascade بيشتغل على سلسلة calls بس. الـ lambda with receiver بتسمحلك تكتب **أي كود**: `if` و loops ومتغيرات، وكل ده و `this` هو الـ object.

## الشرح

### القسم 1: الفرق بين lambda عادية و lambda with receiver

<div dir="ltr">

```kotlin
val normal: (StringBuilder) -> Unit = { it.append("normal ") }
val withReceiver: StringBuilder.() -> Unit = { append("receiver ") }
val sb = StringBuilder()
normal(sb)
sb.withReceiver()          // call it like a member...
withReceiver(sb)           // ...or pass the receiver as the first argument
```

</div>

- **الـ syntax:** `StringBuilder.() -> Unit` اقراها: function **على** StringBuilder مش بتاخد parameters. الجزء اللي قبل النقطة هو الـ **receiver**.
- **جوه الـ lambda:** `append(...)` هي في الحقيقة `this.append(...)`.
- **طريقتين للنداء:** `sb.withReceiver()` كأنها method على الـ object، أو `withReceiver(sb)`. الاتنين نفس الحاجة، لأن تحت الغطاء الـ receiver مجرد أول parameter.

### القسم 2: buildString

<div dir="ltr">

```kotlin
val s = buildString {
    append("Hello, ")       // this is a StringBuilder
    append("Kotlin")
}
```

</div>

شفتها في T04. دلوقتي تقدر تفهم تعريفها: `fun buildString(builderAction: StringBuilder.() -> Unit): String`. بتعمل `StringBuilder`، وبتنفذ الـ lambda عليه، وبترجع الـ string.

### القسم 3: apply مبنية على ده، ودي نسختنا myApply

<div dir="ltr">

```kotlin
inline fun <T> T.myApply(block: T.() -> Unit): T {
    block()
    return this
}

val settings = Settings().myApply {
    theme = "dark"          // this.theme
    fontSize = 18
}
```

</div>

دي تقريبًا نفس كود `apply` الحقيقي في الـ standard library. خلينا نفكها:

- **`fun <T> T.myApply(...)`:** extension function (T19) على **أي نوع** `T`، فتقدر تناديها على أي object.
- **`block: T.() -> Unit`:** بتاخد lambda with receiver من نفس النوع.
- **`block()`:** بتنفذها، و `this` جوه الـ lambda هو نفس الـ object.
- **`return this`:** بترجع الـ object نفسه، فتقدر تكمل عليه.
- **`inline`:** عشان الـ lambda متتعملش object (راجع T09).

الاستخدام ده بالظبط زي الـ cascade في Dart: `Settings()..theme = 'dark'..fontSize = 18`.

### القسم 4: with و run

<div dir="ltr">

```kotlin
val summary = with(settings) { "theme=$theme size=$fontSize" }
```

</div>

`with` بتاخد `T.() -> R` يعني بترجع **نتيجة الـ lambda** مش الـ object. هنا رجعت string. الفرق بين `apply` و `with` و `run` و `let` و `also` هو موضوع T20.

### القسم 5: DSL صغير زي Compose 🆕

<div dir="ltr">

```kotlin
class Html {
    fun h1(text: String) { ... }
    fun p(text: String) { ... }
    fun ul(block: Ul.() -> Unit) {
        val ul = Ul()
        ul.block()
        sb.append(ul.render())
    }
}

class Ul {
    fun li(text: String) { items += text }
}

fun html(block: Html.() -> Unit): Html = Html().apply(block)

val page = html {
    h1("Kotlin for Flutter devs")
    p("Lambdas with receivers build DSLs.")
    ul {
        li("val and var")
        li("when")
    }
}
// li("oops")               // ERROR: li() only exists inside ul { }
```

</div>

**DSL** معناها Domain Specific Language، يعني كود Kotlin عادي شكله كأنه لغة مخصوصة. خطوات الشغل:

1. **`html { ... }`:** الـ `this` جوه الأقواس هو `Html`، فـ `h1` و `p` و `ul` متاحين.
2. **`ul { ... }`:** جواها `this` بقى `Ul`، فـ `li` متاحة.
3. **بره `ul`:** مفيش `li`. الكومبايلر بيرفض، وده **type safety**: مستحيل تحط `li` في مكان غلط.

ده بالظبط إزاي Compose شغالة: `Row { }` بتديك `RowScope` كـ `this`، وجواه `Modifier.weight()` موجودة. وبره `Row` مش موجودة. كمّل في T11.

ملحوظة: `Html().apply(block)` بتشتغل لأن `apply` بتاخد `T.() -> Unit`، و `block` نوعه `Html.() -> Unit`، يعني نفس النوع.

### القسم 6: ليه ده مهم؟

الـ DSLs الحقيقية بتضيف annotation معمولة بـ `@DslMarker`، زي `@LayoutScopeMarker` في Compose. وظيفتها إنها تمنعك تنادي function من الـ scope الخارجي بالغلط. مثلًا تكتب `h1` جوه `ul` وتلاقيها اشتغلت لأن `Html` لسه receiver خارجي. ده تفصيل متقدم، بس كويس تعرف إنه موجود.

## الفخاخ في الملف

- **ERROR:** `li` بره `ul { }` بتدي unresolved reference.

## عادات Flutter اللي هتوقعك

- البحث عن `children: [...]`، والصح إن المحتوى lambda بتتكتب جواها الـ composables.
- الاستغراب من functions زي `weight` موجودة في مكان ومش موجودة في مكان تاني، والسبب الـ receiver scope.

## الخلاصة في 3 سطور

1. النوع `T.() -> Unit` معناه lambda جواها `this` هو الـ object من نوع `T`.
2. الـ `apply` و `with` و `buildString` كلهم functions عادية مبنية على الفكرة دي.
3. أي DSL زي Compose أو Gradle بيستخدمها عشان كل scope يكون فيه functions مخصوصة ليه بس.

</div>
