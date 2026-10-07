<div dir="rtl">

# شرح T11: Compose صغير بـ Kotlin عادي

**الملف:** `src/main/kotlin/T11_ComposeStyleDsl.kt`، وسلايدز 45 و48 و77.

## الخلاصة

الملف ده بيجمع كل اللي فات في مثال واحد: بيبني نسخة صغيرة من Compose بتطبع شجرة في الـ console بدل ما ترسم. الهدف إنك تشوف إن **Compose مش لغة جديدة**، هي Kotlin عادية بتستخدم 5 features: trailing lambda، و lambda with receiver، و extension functions، و extension properties، و default arguments.

ملحوظة: Compose الحقيقية فيها `@Composable` وcompiler plugin بيعمل recomposition. ده مش موجود هنا، والملف بيركز على **شكل اللغة** بس.

## مقارنة سريعة مع Flutter

</div>

<div dir="ltr">

| Flutter | Compose |
| --- | --- |
| `Padding(padding: EdgeInsets.all(16), child: ...)` | `Modifier.padding(16.dp)` |
| `Container(color: Colors.yellow)` | `Modifier.background(Color.Yellow)` |
| `Expanded(flex: 2, child: ...)` | `Modifier.weight(2f)` |
| `Column(children: [...])` | `Column { ... }` |
| `ElevatedButton(onPressed: ..., child: ...)` | `Button(onClick = ...) { ... }` |
| `16` (logical pixels) | `16.dp` |

</div>

<div dir="rtl">

## الشرح

### الجزء الأول: Modifier، سلسلة مبنية من extension functions

<div dir="ltr">

```kotlin
interface Modifier {
    val parts: List<String>

    companion object : Modifier {
        override val parts: List<String> = emptyList()
    }
}

private class ChainedModifier(override val parts: List<String>) : Modifier

fun Modifier.then(part: String): Modifier = ChainedModifier(parts + part)
fun Modifier.padding(all: Dp): Modifier = then("padding($all)")
fun Modifier.background(color: String): Modifier = then("background($color)")
```

</div>

دي أذكى حتة في الملف، فخلينا نفكها:

- **الـ `companion object : Modifier`:** 🆕 الـ companion object (T17) هو object واحد مرتبط بالـ interface، وهنا هو نفسه **implements** `Modifier`. النتيجة إن كلمة `Modifier` لوحدها بقت قيمة: الـ modifier الفاضي. عشان كده تقدر تكتب `Modifier.padding(...)` و `modifier: Modifier = Modifier`.
- **كل function بترجع Modifier جديد:** فيه الأجزاء القديمة + الجزء الجديد. مفيش تعديل على القديم (immutable)، وده اللي بيخلي السلسلة `Modifier.a().b().c()` ممكنة.
- **دي extension functions:** `fun Modifier.padding(...)` معناها إنك ضفت function على `Modifier` من بره (T19). Compose الحقيقية بتعمل كده بالظبط، وده بيخلي أي مكتبة تقدر تضيف modifiers جديدة.

**الفرق عن Flutter:** في Flutter الـ padding والـ background widgets بتلف الـ child (`Padding(child: Container(child: ...))`)، فالشجرة بتعمق. في Compose دول **خصائص** على الـ element نفسه في سلسلة واحدة.

### الجزء التاني: Dp، extension property على Int

<div dir="ltr">

```kotlin
@JvmInline
value class Dp(val value: Int) {
    override fun toString() = "$value.dp"
}

val Int.dp: Dp get() = Dp(this)
```

</div>

- **`val Int.dp`:** 🆕 **extension property**. ضفنا property اسمها `dp` على **كل** `Int`، فـ `16.dp` بقت تدي `Dp(16)`. Dart 2.7 وبعدها عندها extension getters برضه، فالفكرة ممكن تكون مألوفة.
- **`value class`:** 🆕 class بتلف قيمة واحدة. وقت الـ compile بتبقى type جديد، فمينفعش تبعت `Int` مكان `Dp` بالغلط. ووقت التشغيل بتبقى `Int` عادي من غير object إضافي، يعني من غير تكلفة. وده بيحمي من غلطة تبعت pixels مكان dp. Dart 3.3 فيها نفس الفكرة باسم `extension type`. التفاصيل في T23.

### الجزء التالت: الـ Scopes، ليه weight موجودة جوه Row بس

<div dir="ltr">

```kotlin
class RowScope {
    fun Modifier.weight(weight: Float): Modifier = then("weight(${weight}f)")
}

fun Row(modifier: Modifier = Modifier, content: RowScope.() -> Unit) {
    ...
    RowScope().content()
    ...
}
```

</div>

دي أهم فكرة في الملف 🆕:

- **`weight` متعرّفة جوه `RowScope`:** يعني extension function **عضو** في class. متاحة بس لما `RowScope` يكون `this`.
- **`content: RowScope.() -> Unit`:** lambda with receiver (T10)، فجوه أقواس `Row { }` الـ `this` هو `RowScope`.
- **النتيجة:** `Modifier.weight(1f)` بتشتغل جوه `Row { }` وبس.

في Flutter، `Expanded` widget عادي، وتقدر تحطه في أي حتة. لو حطيته بره `Row` أو `Column`، هتاخد **runtime error** ("Incorrect use of ParentDataWidget"). في Compose الغلطة دي **compile error**، وده فرق كبير.

### الجزء الرابع: الـ "Composables"

<div dir="ltr">

```kotlin
fun Text(text: String, modifier: Modifier = Modifier) = ...

fun Button(onClick: () -> Unit, modifier: Modifier = Modifier, content: () -> Unit) { ... }
```

</div>

- **أسماء بحرف كبير:** دي functions بس اسمها بـ capital letter، ده convention في Compose عشان تبان زي widgets.
- **`modifier: Modifier = Modifier`:** default argument (T06). الـ convention إن كل composable ياخد `modifier` كأول parameter اختياري.
- **`content` آخر parameter:** عشان يبقى trailing lambda (T09).

### القسم 1: شاشة شبه Compose

<div dir="ltr">

```kotlin
Column(Modifier.padding(16.dp)) {
    Text("Welcome, Samy", Modifier.background("Yellow").padding(8.dp))
    Row(Modifier.fillMaxWidth()) {
        Text("1f", Modifier.weight(1f))
        Text("2f", Modifier.weight(2f))
    }
    Button(onClick = { line("-> clicked!") }) {
        Text("Tap me")
    }
}
```

</div>

لو حطيت `import androidx.compose...` بدل الـ classes بتاعتنا، الكود ده يكاد يكون Compose حقيقي حرف بحرف.

### القسم 2: خد بالك، weight موجودة جوه Row و Column بس

<div dir="ltr">

```kotlin
// Text("x", Modifier.weight(1f))   // ERROR: unresolved reference 'weight'
```

</div>

جرّب تشيل الكومنت قدام الطلبة عشان يشوفوا إن الغلطة بتتمسك وقت الـ compile.

### القسم 3: خد بالك، ترتيب الـ Modifier مهم

<div dir="ltr">

```kotlin
Modifier.padding(16.dp).background("Yellow")   // padding first, yellow only inside it
Modifier.background("Yellow").padding(16.dp)   // yellow everywhere, content inset by 16
```

</div>

الـ modifiers بتتطبق **بالترتيب من الشمال لليمين**. كل واحد بيلف اللي بعده. في Flutter الترتيب كان واضح من تداخل الـ widgets. هنا هو ترتيب السلسلة. مثلًا `clickable` قبل `padding` غير `clickable` بعدها: مساحة الضغط هتختلف.

### القسم 4: features الـ Kotlin اللي ورا ده

</div>

<div dir="ltr">

| الشكل | الـ feature | الملف |
| --- | --- | --- |
| `Column { }` | trailing lambda | T09 |
| `content: RowScope.() -> Unit` | lambda with receiver | T10 |
| `Modifier.padding(...)` | extension function | T19 |
| `16.dp` | extension property | T19 |
| `modifier: Modifier = Modifier` | default argument + companion object | T06، T17 |

</div>

<div dir="rtl">

## ملحوظات صغيرة في الكود

- **`private var depth`:** متغير top-level بيتحكم في المسافات عشان الطباعة تبان شجرة. ده مجرد helper للعرض.
- **`ChainedModifier` معرّفة `private`:** يعني محدش بره الملف يشوفها. كله بيتعامل مع interface `Modifier` بس، وده encapsulation.

## الخلاصة في 3 سطور

1. الـ Compose عبارة عن Kotlin عادية: trailing lambdas و receivers و extensions و defaults.
2. الـ Modifier سلسلة immutable من extension functions، وترتيبها بيفرق.
3. الـ scopes زي `RowScope` بتخلي استخدام `weight` الغلط compile error بدل runtime error زي Flutter.

</div>
