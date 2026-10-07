<div dir="rtl">

# شرح T06: الـ Functions والـ named args والـ varargs

**الملف:** `src/main/kotlin/T06_Functions.kt`، وسلايدز 32–35 و40.

## الخلاصة

الـ functions في Kotlin أبسط من Dart في حاجة مهمة: **أي parameter ممكن يتبعت بالاسم**، من غير ما تقرر ده وقت التعريف بـ `{}`. وفيه حاجتين جداد: `vararg` والنوع `Nothing`، وإن كان `Nothing` شبه `Never` في Dart.

## خريطة Dart ← Kotlin

</div>

<div dir="ltr">

| Dart | Kotlin |
| --- | --- |
| `int add(int a, int b) { }` | `fun add(a: Int, b: Int): Int { }` |
| `int sq(int x) => x * x;` | `fun sq(x: Int) = x * x` |
| `void log(String m)` | `fun log(m: String)` (بترجع `Unit`) |
| `{String g = 'Hi'}` | `g: String = "Hi"` |
| `[int x = 0]` | `x: Int = 0` |
| `required` | أي param من غير default |
| — | `vararg nums: Int` |
| `Never` | `Nothing` |
| tear-off: `add` | `::add` |

</div>

<div dir="rtl">

## الشرح

### القسم 1: block body و expression body و Unit

<div dir="ltr">

```kotlin
fun add(a: Int, b: Int): Int {
    return a + b
}

fun square(x: Int) = x * x

val result: Unit = log("even Unit is a real value")
```

</div>

- **الـ block body:** بأقواس `{}`، ولازم تكتب الـ return type و `return` صريحين.
- **الـ expression body:** بعلامة `=`، ودي `=>` بتاعة Dart. الـ return type بيتعرف لوحده.
- **الـ `Unit`:** هو المقابل لـ `void`، بس الفرق إنه **نوع حقيقي ليه قيمة واحدة** اسمها `Unit`. عشان كده تقدر تحطه في متغير وتطبعه. الفايدة الحقيقية إن الـ generics بتشتغل معاه عادي: `() -> Unit` نوع function سليم. في Dart `void` نوع غريب شوية في الـ generics.

### القسم 2: الـ default والـ named arguments 🆕 فيها جديد

<div dir="ltr">

```kotlin
fun greet(name: String, greeting: String = "Hello", punctuation: String = "!") = ...

greet("Ali")
greet("Ali", "Hi")
greet("Ali", punctuation = "?")
greet(punctuation = ".", name = "Mona")
```

</div>

في Dart لازم تقرر وقت التعريف: positional، أو named بين `{}`، أو optional بين `[]`. في Kotlin **كل parameter ينفع يتبعت بالاسم أو بالترتيب**، والقرار عند اللي بينادي:

- **بالترتيب:** `greet("Ali", "Hi")`.
- **بالاسم وتتخطى اللي في النص:** `greet("Ali", punctuation = "?")`.
- **بالاسم وبأي ترتيب:** `greet(punctuation = ".", name = "Mona")`.

والـ parameter اللي ملوش default هو تلقائيًا `required`.

**ليه ده مهم في Compose؟** كل composable فيه عشرات الـ parameters بـ defaults، زي `Text(text = "Hi", fontSize = 18.sp)`، وانت بتبعت اللي محتاجه بالاسم بس. ده نفس إحساس widgets Flutter، بس من غير `{}` في التعريف.

### القسم 3: الـ varargs 🆕 جديد عليك

<div dir="ltr">

```kotlin
fun sum(vararg nums: Int): Int = nums.sum()

sum()
sum(1, 2, 3)
val more = intArrayOf(4, 5)
sum(1, *more)
sum(*fromList.toIntArray())
```

</div>

Dart مفيهاش varargs خالص. كنت بتبعت `List` وخلاص. هنا:

- **`vararg`:** معناها إن الـ function بتاخد أي عدد من القيم. جوه الـ function، `nums` بيبقى **array**، ولـ `Int` بالذات بيبقى `IntArray`.
- **الـ spread `*`:** لو معاك array جاهز، بتفرده بـ `*`، زي `...` في Dart. بس `*` بتشتغل مع **arrays بس**، فالـ list لازم تتحول الأول بـ `toIntArray()` أو `toTypedArray()`.
- **بتستخدمها كل يوم:** `listOf(1, 2, 3)` نفسها تعريفها `listOf(vararg elements: T)`.

### القسم 4: الـ local functions

<div dir="ltr">

```kotlin
fun isValid(username: String): Boolean {
    fun notBlank() = username.isNotBlank()
    fun shortEnough() = username.length <= 10
    return notBlank() && shortEnough()
}
```

</div>

موجودة في Dart برضه. الـ function الداخلية بتشوف متغيرات الـ function الخارجية (closure). مفيدة لما يكون عندك helper مالوش لازمة بره function واحدة.

### القسم 5: الـ Nothing

<div dir="ltr">

```kotlin
fun fail(message: String): Nothing = throw IllegalStateException(message)
```

</div>

دي `Never` بتاعة Dart. النوع ده معناه إن الـ function **مش هترجع أبدًا**: يا إما بترمي exception يا إما loop لا نهائي.

الفايدة إن الكومبايلر بيفهم إن اللي بعدها مش هيتنفذ، وبيستخدم ده في الـ smart cast. ده اللي بيخلي `name ?: throw ...` في T05 تشتغل: نوع `throw` هو `Nothing`، و `Nothing` نوع فرعي من كل الأنواع.

مثال من الـ standard library: `TODO()` بترجع `Nothing`، فتقدر تكتب `fun f(): Int = TODO()` والكود يـcompile.

### القسم 6: الـ recursion

<div dir="ltr">

```kotlin
fun factorial(n: Int): Long = if (n <= 1) 1 else n * factorial(n - 1)
```

</div>

لاحظ إن الـ return type هنا **لازم** يتكتب. الـ functions اللي بتنادي نفسها لازم نوعها يبقى صريح، لأن الكومبايلر مش هيعرف يستنتجه. ولاحظ كمان إنه `Long` عشان `factorial(13)` مش هيكفيه `Int` (راجع T03).

### القسم 7: الـ functions قيم

<div dir="ltr">

```kotlin
val op: (Int, Int) -> Int = ::add
println("op(10, 5) = ${op(10, 5)}")
```

</div>

- **الـ `(Int, Int) -> Int`:** ده **نوع function**، ويقابله في Dart `int Function(int, int)`.
- **الـ `::add`:** ده **function reference**. في Dart بتكتب `add` على طول (tear-off)، وفي Kotlin لازم `::` قبلها. لو كتبت `add` من غير `::` هياخدها كأنها اسم متغير.

التفاصيل في T09.

### القسم 8: خد بالك

<div dir="ltr">

```kotlin
// Math.max(a = 1, b = 2)   // ERROR: Java methods can't be called with named arguments
println(tag("a", "b", "c", separator = "-"))
```

</div>

- **أولًا:** الـ named arguments **مش بتشتغل مع Java methods**، لأن الـ Java bytecode مش دايمًا بيحفظ أسماء الـ parameters.
- **ثانيًا:** أي parameter بعد `vararg` **لازم** يتبعت بالاسم، وإلا هيتبلع جوه الـ vararg.
- **ثالثًا:** مسموح بـ `vararg` **واحد بس** في كل function.

## الفخاخ في الملف

- **ERROR:** named arguments مع Java method.
- **CRASH:** `fail("Boom")` بترمي `IllegalStateException`.

## عادات Flutter اللي هتوقعك

- البحث عن `{}` عشان تعمل named params، والصح إنهم موجودين تلقائيًا.
- كتابة `void`، والصح إنك متكتبش حاجة أو تكتب `Unit`.
- كتابة اسم الـ function من غير `::` لما عايز تبعتها كقيمة.
- تمرير list لـ vararg من غير `*` وتحويل لـ array.

## الخلاصة في 3 سطور

1. استخدم `=` للـ functions اللي سطر واحد، و `Unit` هو المقابل لـ `void` بس نوع حقيقي.
2. أي parameter ممكن يتبعت بالاسم، والـ defaults بتغنيك عن الـ overloads، ودي أساس Compose.
3. الـ `vararg` مع `*` للفرد، والـ `Nothing` للـ functions اللي مش بترجع.

</div>
