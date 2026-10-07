<div dir="rtl">

# شرح T09: الـ Lambdas والـ higher-order functions

**الملف:** `src/main/kotlin/T09_Lambdas.kt`، وسلايدز 44–46 و50–51.

## الخلاصة

الفكرة نفسها مألوفة ليك من Dart: functions بتتبعت كقيم، و callbacks زي `onPressed`. الجديد هو **الـ syntax**: الـ lambda كلها جوه `{}`، وفيه `it`، والأهم **الـ trailing lambda**، وده السر اللي بيخلي Compose شكلها كده. وفيه فخ كبير: **`return` جوه `forEach` بتخرج من الـ function كلها**، عكس Dart.

## خريطة Dart ← Kotlin

</div>

<div dir="ltr">

| Dart | Kotlin |
| --- | --- |
| `(x) => x * 2` | `{ x -> x * 2 }` أو `{ it * 2 }` |
| `(a, b) { ...; return a + b; }` | `{ a, b -> ...; a + b }` |
| `VoidCallback` / `void Function()` | `() -> Unit` |
| `int Function(int)` | `(Int) -> Int` |
| `void Function()?` | `(() -> Unit)?` |
| `onTap?.call()` | `onDismiss?.invoke()` |
| tear-off `multiply` | `::multiply` |

</div>

<div dir="rtl">

## الشرح

### القسم 1: شكل الـ lambda

<div dir="ltr">

```kotlin
val double: (Int) -> Int = { x -> x * 2 }
val add = { a: Int, b: Int -> a + b }
val hello = { println("Hello from a lambda") }
val inc: (Int) -> Int = { it + 1 }
val describe = { x: Int ->
    val y = x * 2
    "x=$x, doubled=$y"
}
```

</div>

- **كل الـ lambda جوه `{}`:** الـ parameters الأول، وبعدها `->`، وبعدها الـ body.
- **من غير parameters:** بتكتب الـ body على طول.
- **🆕 الـ `it`:** لو فيه parameter واحد بس، ممكن متسميهوش، واسمه تلقائيًا `it`. هتشوفه في كل مكان: `filter { it > 2 }`.
- **🆕 مفيش `return` في الـ lambda:** آخر سطر هو القيمة اللي بترجع. لو كتبت `return` هيحصل حاجة تانية خالص (القسم 9).
- **الأنواع:** يا إما على المتغير `(Int) -> Int`، يا إما جوه الـ lambda `a: Int`. لازم الكومبايلر يعرفهم من مكان ما.

### القسم 2: الـ higher-order functions

<div dir="ltr">

```kotlin
fun operate(a: Int, b: Int, op: (Int, Int) -> Int): Int = op(a, b)

operate(6, 3) { x, y -> x - y }
operate(6, 3, ::multiply)
```

</div>

نفس Dart: function بتاخد function. لاحظ إن الـ lambda اتكتبت **بره الأقواس**، وده موضوع القسم الجاي. و `::multiply` هو الـ function reference من T06.

### القسم 3: الـ trailing lambdas، سر شكل Compose 🆕 جديد عليك

<div dir="ltr">

```kotlin
twice({ print("A ") })
twice() { print("B ") }
twice { print("C ") }        // all three are the same call

button("Tap me", onClick = { println("  clicked!") }) { label ->
    println("  drawing button: $label")
}
```

</div>

دي **أهم قاعدة في الملف**: لو آخر parameter في الـ function هو lambda، تقدر تطلّعه بره الأقواس. ولو هو الـ parameter الوحيد، تشيل الأقواس خالص. التلات سطور الأولانيين نفس الـ call بالظبط.

ده بيفسر Compose كله:

</div>

<div dir="ltr">

```kotlin
Column {                         // Column(content = { ... })
    Text("Hi")
}
Button(onClick = { }) {          // Button(onClick = { }, content = { ... })
    Text("Tap")
}
```

</div>

<div dir="rtl">

قارنه بـ Flutter:

</div>

<div dir="ltr">

```dart
Column(children: [Text('Hi')])
ElevatedButton(onPressed: () {}, child: Text('Tap'))
```

</div>

<div dir="rtl">

في Flutter الـ children **list من objects**، وفي Compose المحتوى **lambda بتتنفذ**. فـ `Column { }` مش syntax خاص بـ Compose، دي مجرد function عادية آخر parameter فيها lambda.

### القسم 4: أنواع الـ functions كـ parameters

<div dir="ltr">

```kotlin
val onClick: () -> Unit = { println("clicked") }
val onValueChange: (String) -> Unit = { println("typed: $it") }
val isValid: (String) -> Boolean = { it.length >= 3 }
```

</div>

دي بالظبط أسماء الـ callbacks اللي هتقابلها في Compose:

- **`onClick: () -> Unit`:** زي `VoidCallback`.
- **`onValueChange: (String) -> Unit`:** زي `ValueChanged<String>`، وهي اللي `TextField` بتاخدها.

### القسم 5: أنواع functions بتقبل null

<div dir="ltr">

```kotlin
var onDismiss: (() -> Unit)? = null
onDismiss?.invoke()
onDismiss = { println("dismissed") }
onDismiss?.invoke()
```

</div>

- **الأقواس حوالين النوع مهمة:** `(() -> Unit)?` معناها الـ function نفسها ممكن تكون null. لكن `() -> Unit?` معناها function **بترجع** `Unit?`، وده حاجة تانية.
- **`invoke()`:** هي `call()` في Dart. مينفعش تكتب `onDismiss?()`، لازم `?.invoke()`.

### القسم 6: function بترجع function

<div dir="ltr">

```kotlin
fun multiplier(factor: Int): (Int) -> Int = { it * factor }
val triple = multiplier(3)
```

</div>

زي Dart. الـ lambda اللي راجعة "فاكرة" قيمة `factor`.

### القسم 7: الـ closures

<div dir="ltr">

```kotlin
var counter = 0
val increment = { counter++ }
repeat(3) { increment() }
println("counter = $counter")   // 3
```

</div>

زي Dart بالظبط: الـ lambda بتمسك المتغير نفسه مش نسخة منه، وتقدر تعدّل فيه. (Java مكانتش بتسمح بده، وKotlin بتعمله تحت الغطاء بـ wrapper object.)

### القسم 8: الـ destructuring و `_`

<div dir="ltr">

```kotlin
mapOf("a" to 1, "b" to 2).forEach { (key, value) -> println("$key=$value") }
mapOf("x" to 10).forEach { (_, value) -> println("value only: $value") }
```

</div>

- **`(key, value)` جوه أقواس:** معناها parameter واحد (الـ `Map.Entry`) اتفك لجزئين، ودي `forEach` بتاعة Kotlin stdlib.
- **من غير أقواس:** `{ key, value -> }` معناها **اتنين parameters**. دي كمان بتـcompile على الـ JVM، بس بتنادي function تانية: `Map.forEach(BiConsumer)` بتاعة Java عن طريق SAM conversion (T17)، وعلى Android محتاجة API 24 أو أعلى. الاتنين شغالين، والفرق في أنهي function بتتنادي. الشكل اللي بالأقواس هو الأكثر أمانًا لأنه Kotlin خالص.
- **الـ `_`:** معناها مش محتاج الجزء ده، زي Dart.

### القسم 9: خد بالك، return جوه forEach 🆕 جديد عليك ومهم جدًا

<div dir="ltr">

```kotlin
fun printPositivesSkip(nums: List<Int>) {
    nums.forEach {
        if (it < 0) return@forEach        // skips this item only
        print("$it ")
    }
    println("<- reached the end")
}

fun printUntilNegative(nums: List<Int>) {
    nums.forEach {
        if (it < 0) return                // exits the WHOLE function!
        print("$it ")
    }
    println("never printed")
}
```

</div>

في Dart، `return` جوه closure الـ `forEach` بترجع من الـ closure بس، يعني بتعمل skip للعنصر ده. **في Kotlin لأ.** الـ `return` العادي جوه lambda بيرجع من **الـ function اللي حوالين الـ lambda**، يعني `printUntilNegative` كلها، وده اسمه **non-local return**.

ليه ده بيحصل؟ لأن `forEach` معرّفة `inline`. الكومبايلر بيـ"لزق" كود الـ lambda جوه الـ function مكان الـ call، فكأنك كاتب `for` loop عادي، والـ `return` بيخرج منها. ولو الـ function مش `inline`، مش هيسمحلك تكتب `return` من غير label أصلًا.

الحلول:

- **عشان تعمل skip:** `return@forEach`، وده **label return** زي `continue`.
- **عشان توقف:** استخدم `for` loop عادي مع `break`، أوضح من `return`.

**ما هو `inline`؟** دي keyword بتخلي الكومبايلر ينسخ جسم الـ function مكان الاستدعاء. فايدتها إن الـ lambda مبتتعملش object، وده مهم في الأداء مع functions زي `map` و `filter` اللي بتتنادى كتير. هتشوفها في `tryIt` وفي T10.

## عادات Flutter اللي هتوقعك

- كتابة `(x) => x * 2`، والصح `{ x -> x * 2 }`.
- كتابة `return` جوه lambda على أساس إنه بيرجع قيمة الـ lambda.
- استخدام `return` جوه `forEach` على أساس إنه `continue`.
- كتابة `callback?.call()`، والصح `?.invoke()`.

## الخلاصة في 3 سطور

1. الـ lambda كلها في `{}`، والـ parameter الوحيد اسمه `it`، وآخر سطر هو القيمة.
2. لو آخر parameter lambda بيطلع بره الأقواس، وده كل سر شكل Compose.
3. الـ `return` جوه `forEach` بيخرج من الـ function كلها، فاستخدم `return@forEach` للـ skip.

</div>
