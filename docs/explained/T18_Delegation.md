<div dir="rtl">

# شرح T18: الـ Delegation بـ by

**الملف:** `src/main/kotlin/T18_Delegation.kt`، وسلايدز 70–71.

## الخلاصة

كلمة `by` مالهاش مقابل في Dart 🆕. بتعمل حاجتين:

1. **Class delegation:** "الـ class دي بتـimplement الـ interface ده عن طريق object تاني"، والكومبايلر بيكتب كل الـ forwarding.
2. **Property delegation:** "الـ get والـ set بتوع الـ property دي متوكّلين لـ object تاني". دي اللي ورا `by lazy` و `by mutableStateOf` في Compose.

لو فهمت القسم 6 في الملف ده، هتفهم إزاي `var count by remember { mutableStateOf(0) }` شغالة.

## خريطة Dart ← Kotlin

</div>

<div dir="ltr">

| الهدف | Dart | Kotlin |
| --- | --- | --- |
| إعادة استخدام behavior | `with Mixin` أو forwarding يدوي | `class A(x: I) : I by x` |
| قيمة تتحسب أول مرة | `late final x = ...;` | `val x by lazy { }` |
| تراقب التغيير | setter يدوي | `by Delegates.observable` |
| state بيعمل rebuild | `ValueNotifier` + `.value` | `by mutableStateOf` |

</div>

<div dir="rtl">

## الشرح

### القسم 1: الـ class delegation

<div dir="ltr">

```kotlin
interface Logger {
    fun log(msg: String)
}

class ConsoleLogger(private val prefix: String) : Logger {
    override fun log(msg: String) = println("  $prefix $msg")
}

class Repo(logger: Logger) : Logger by logger {
    fun load() = log("Loading users...")
}
```

</div>

السطر `: Logger by logger` معناه: `Repo` بتـimplement `Logger`، وأي method في `Logger` حوّلها لـ `logger`. الكومبايلر بيكتب ده لوحده:

</div>

<div dir="ltr">

```kotlin
override fun log(msg: String) = logger.log(msg)
```

</div>

<div dir="rtl">

مع method واحدة مش فارقة، بس مع interface فيه 20 method الفرق ضخم.

**ليه ده مهم؟** فيه مبدأ معروف: "composition over inheritance". بدل ما `Repo` تورث من `ConsoleLogger` وتتربط بيه، **بتحتوي** على logger وتستخدمه، وتقدر تبدله بأي implementation تانية، زي fake logger في الـ tests. Kotlin خلت ده سهل زي الوراثة بالظبط.

**الفرق عن mixins في Dart:** الـ mixin بيتحقن في الـ class وقت الـ compile، ومش بتقدر تبدله. هنا الـ object الحقيقي بيتبعت في الـ constructor، فتقدر تبعت أي واحد.

### القسم 2: override لـ methods معينة والباقي delegation

<div dir="ltr">

```kotlin
class LoudRepo(logger: Logger) : Logger by logger {
    override fun log(msg: String) = println("  !!! ${msg.uppercase()}")
}
```

</div>

لو عملت override لـ method، الـ implementation بتاعتك هي اللي هتشتغل، والباقي يفضل delegation. ده Decorator pattern في سطرين.

### القسم 3: by lazy

<div dir="ltr">

```kotlin
val config by lazy {
    println("  loading config...")
    "config v1"
}
```

</div>

شفتها في T13. لاحظ إنها بتشتغل مع **local variable** كمان مش property بس.

### القسم 4: Delegates.observable و vetoable

<div dir="ltr">

```kotlin
var theme: String by Delegates.observable("light") { prop, old, new ->
    println("  ${prop.name}: $old -> $new")
}
var volume: Int by Delegates.vetoable(50) { _, _, new -> new in 0..100 }
```

</div>

- **`observable`:** بتنادي الـ lambda **بعد** كل تغيير، وبتديك القيمة القديمة والجديدة. مفيدة للـ logging أو تحديث حاجة تانية.
- **`vetoable`:** بتنادي الـ lambda **قبل** التغيير. لو رجعت `false`، التغيير **بيترفض**. هنا `volume = 150` اترفضت وفضلت 80.

في Dart كنت هتكتب setter يدوي بـ `_field`.

### القسم 5: delegation لـ map

<div dir="ltr">

```kotlin
class User(map: Map<String, Any?>) {
    val name: String by map
    val age: Int by map
}
val u = User(mapOf("name" to "Mona", "age" to 22))
```

</div>

الـ property بتقرا قيمتها من الـ map **باسمها**. يعني `name` بتجيب `map["name"]`. شكلها حلو مع JSON، بس في الواقع هتستخدم مكتبة serialization زي `kotlinx.serialization` أو Moshi، زي `json_serializable` في Dart. لو المفتاح مش موجود هتاخد `NoSuchElementException` وقت القراءة.

### القسم 6: إزاي by mutableStateOf شغالة في Compose 🆕 أهم قسم

<div dir="ltr">

```kotlin
class MyState<T>(private var value: T) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        println("    (get ${property.name} = $value)")
        return value
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, newValue: T) {
        println("    (set ${property.name} = $newValue -> Compose would redraw here)")
        value = newValue
    }
}

var count by myMutableStateOf(0)
count++                    // a get, then a set
```

</div>

دي كل الحكاية: أي class فيها `operator fun getValue` (و `setValue` لو `var`) ينفع تيجي بعد `by`. الكومبايلر بيحوّل:

</div>

<div dir="ltr">

```kotlin
count++
// becomes:
delegate.setValue(null, ::count, delegate.getValue(null, ::count) + 1)
```

</div>

<div dir="rtl">

- **`thisRef`:** الـ object اللي فيه الـ property. هنا `null` لأنها local variable.
- **`property: KProperty<*>`:** معلومات عن الـ property، زي اسمها `count`.
- **`operator`:** keyword إجبارية عشان الكومبايلر يعرف إن الـ function دي معمولة للاستخدام ده (T23).

**في Compose الحقيقية:** `mutableStateOf(0)` بترجع `MutableState<Int>`، و `getValue` بتاعتها بتسجّل إن الـ composable ده **قرأ** الـ state، و `setValue` بتقول لـ Compose **إعادة رسم** كل اللي قرأه. ده مقابل `setState` في Flutter، بس أذكى: بيعيد رسم اللي قرأ القيمة بس، مش الـ widget كله.

**خد بالك من الـ imports:** `getValue` و `setValue` بتوع `MutableState` معرّفين كـ **extension functions** (T19) في package منفصل. لو مضفتش:

</div>

<div dir="ltr">

```kotlin
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
```

</div>

<div dir="rtl">

هتاخد compile error غريب عن `getValue`. الـ IDE غالبًا بيقترحهم، بس ده سؤال بيتكرر جدًا.

والبديل من غير `by`:

</div>

<div dir="ltr">

```kotlin
val count = remember { mutableStateOf(0) }
count.value++
```

</div>

<div dir="rtl">

ده نفس `ValueNotifier` في Flutter بالظبط: `.value`. الـ `by` بتشيل الـ `.value` بس.

## عادات Flutter اللي هتوقعك

- البحث عن `mixin` لإعادة الاستخدام، والبديل الأحسن هنا class delegation.
- كتابة setter يدوي للمراقبة، والبديل `Delegates.observable`.
- نسيان imports الـ `getValue` و `setValue` في Compose.

## الخلاصة في 3 سطور

1. الـ `class A(x: I) : I by x` بيعمل forwarding تلقائي، وده composition سهل زي الوراثة.
2. الـ `val/var x by something` بيحوّل الـ get والـ set لـ `getValue` و `setValue` في الـ object ده.
3. الـ `by mutableStateOf` هي بالظبط ده: الـ get بيسجّل القراءة، والـ set بيعمل recomposition.

</div>
