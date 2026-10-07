<div dir="rtl">

# شرح T01: أول برنامج Kotlin والـ JVM

**الملف:** `src/main/kotlin/T01_HelloWorld.kt`، وسلايدز 10–17.

## الخلاصة

الملف ده بيعرّفك على حاجتين: شكل الكود في Kotlin، والأهم منه **فين الكود ده بيشتغل**. في Flutter كان عندك Dart VM أو AOT بيطلع native code. هنا الكود بيتحول لـ JVM bytecode، وده نفس العالم اللي Java عايشة فيه، وعلى Android بيتحول تاني لـ DEX ويشتغل على ART.

## خريطة Dart ← Kotlin

</div>

<div dir="ltr">

| Dart | Kotlin |
| --- | --- |
| `void main() { }` | `fun main() { }` |
| `print('x')` | `println("x")` |
| `;` في آخر السطر | مفيش، السطر الجديد كفاية |
| `'text'` أو `"text"` | `"text"` بس، و `'K'` ده `Char` |

</div>

<div dir="rtl">

## الشرح

### القسم 1: Hello world

<div dir="ltr">

```kotlin
println("Hello, Kotlin!")
print("print stays on the same line... ")
print("see?\n")
```

</div>

الفرق بين الاتنين: `println` بتطبع وتنزل سطر، و `print` بتطبع وتفضل على نفس السطر. في Dart الـ `print` كانت بتنزل سطر دايمًا، فخلي بالك إن `print` هنا مش زي `print` بتاعة Dart.

كمان `fun main()` مش محتاجة `String[] args` زي Java، وده من أول الحاجات اللي Kotlin بسّطتها.

### القسم 2: من غير semicolons، والـ double quotes

<div dir="ltr">

```kotlin
val name = "Android"
println("Hello, $name!")
```

</div>

السطر الجديد بينهي الـ statement. تقدر تكتب `;` والكومبايلر مش هيزعل، بس الـ IDE هيقولك إنها زيادة. الـ string template زي Dart بالظبط.

### القسم 3: الفرق بين Char و String 🆕 جديد عليك

<div dir="ltr">

```kotlin
val letter: Char = 'K'   // single quotes = ONE character
val word: String = "K"   // double quotes = String
// val wrong: String = 'K'   // ERROR: Char is not a String
```

</div>

دي أول فخ لأي حد جاي من Dart. في Dart مفيش نوع اسمه `Char` أصلًا، و `'K'` و `"K"` الاتنين String. في Kotlin:

- الـ single quote معناها **حرف واحد** من نوع `Char`، ولو كتبت `'Ab'` هتاخد compile error.
- الـ double quote معناها `String`.

والاتنين مش بيتحولوا لبعض لوحدهم. عادة إيدك هتكتب `'hello'` من Dart، والكومبايلر هيوقفك على طول.

### القسم 4: الكود ده شغال فين؟

<div dir="ltr">

```kotlin
println("Kotlin version : ${KotlinVersion.CURRENT}")
println("Java version   : ${System.getProperty("java.version")}")
println("Class name     : ${object {}.javaClass.enclosingClass?.name}")
```

</div>

دي أهم نقطة في الملف من ناحية الفهم:

- **أولًا:** `System.getProperty` دي Java API. تقدر تنده أي مكتبة Java من Kotlin مباشرة، من غير platform channels ولا FFI زي Flutter. ده سبب كبير إن Kotlin نجحت على Android: كل مكتبات Java بتشتغل من أول يوم.
- **ثانيًا:** الـ JVM مش بيعرف يشغّل function لوحدها، هو بيشغّل **classes** بس. فلما تكتب `fun main()` على مستوى الملف (top-level)، الكومبايلر بيعمل class اسمها على اسم الملف ومعاها `Kt`. عشان كده هيطبعلك `t01.T01_HelloWorldKt`.
- **ثالثًا:** التعبير `object {}` بيعمل anonymous object (هتشوفه في T17)، و `javaClass.enclosingClass` بيجيب الـ class اللي حواليه، وده مجرد trick عشان نطبع اسم الـ class.

### تجربة لايف: Decompile

من `Tools` ← `Kotlin` ← `Show Kotlin Bytecode` ← `Decompile` هتشوف الملف متحوّل لـ Java فيها `public final class T01_HelloWorldKt` وجواها `public static final void main()`. اتعود تستخدم الأداة دي، لأنها أحسن طريقة تفهم بيها أي feature في Kotlin بتعمل إيه تحت الغطاء.

## الفخاخ في الملف

- **الـ ERROR في القسم 3:** إسناد `'K'` لـ `String`. السبب إن `Char` نوع مستقل.

## عادات Flutter اللي هتوقعك

- كتابة strings بـ single quotes، والصح في Kotlin double quotes دايمًا.
- افتراض إن `print` بتنزل سطر، والصح إنك تستخدم `println`.
- كتابة `void`، والصح إن الـ function اللي مش بترجع حاجة مش محتاجة نوع، أو تكتب `: Unit` لو حابب.

## ملحوظة عن `section`

<div dir="ltr">

```kotlin
private fun section(title: String) = println("\n=== $title ===")
```

</div>

ده helper بيتكرر في كل الملفات. لاحظ حاجتين: الـ `=` بدل `{ return ... }` اسمها **expression body** (هتتشرح في T06)، و `private` على مستوى الملف معناها إنها مرئية **جوه الملف ده بس**، وده أضيق من الـ `_` في Dart اللي بتخلي الحاجة private على مستوى الـ library.

## الخلاصة في 3 سطور

1. الكود بيتحول لـ JVM bytecode، وكل top-level function بتتحط في class اسمها `FileNameKt`.
2. علامة `'x'` معناها `Char` و `"x"` معناها `String`، ومش بيتحولوا لبعض.
3. الأمر `println` بينزل سطر والأمر `print` لأ، وأي مكتبة Java تقدر تنادي عليها مباشرة.

</div>
