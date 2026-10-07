<div dir="rtl">

# شرح T17: الـ object والـ companion والـ object expressions

**الملف:** `src/main/kotlin/T17_Objects.kt`، وسلايدز 68–69.

## الخلاصة

كلمة `object` في Kotlin بتعمل 3 حاجات مختلفة، وكلها جديدة عليك بشكل ما 🆕:

1. **`object Logger`:** singleton بكلمة واحدة.
2. **`companion object`:** بديل `static`، لأن Kotlin **مفيهاش `static`**.
3. **`object : Interface { }`:** anonymous object، ودي حاجة Dart مفيهاش خالص.

وكمان `fun interface`، اللي بتخليك تبعت lambda مكان interface.

## خريطة Dart ← Kotlin

</div>

<div dir="ltr">

| Dart | Kotlin |
| --- | --- |
| singleton: `static final instance = Logger._();` | `object Logger` |
| `static const url = '...'` | `companion object { const val URL = "..." }` |
| `static Api create()` | `companion object { fun create() }` |
| `Api._()` private constructor | `class Api private constructor()` |
| anonymous class | `object : Listener { ... }` |
| `typedef OnTap = void Function(int)` | `fun interface OnTap` أو `(Int) -> Unit` |

</div>

<div dir="rtl">

## الشرح

### القسم 1: object هو singleton

<div dir="ltr">

```kotlin
object Logger {
    private var count = 0
    fun log(msg: String) {
        count++
        println("  [$count] $msg")
    }
}

Logger.log("first")
println("Logger === Logger: ${Logger === Logger}")
```

</div>

في Dart الـ singleton محتاج private constructor + static instance + factory. هنا كلمة `object` بدل `class`، وخلاص:

- **نسخة واحدة بس** في البرنامج كله.
- **بتتعمل lazily:** أول ما حد يستخدمها، وده thread-safe.
- **بتتنادى باسمها:** `Logger.log(...)`.

**تحذير:** الـ singletons اللي فيها state متغيرة (زي `count`) صعبة في الـ testing. في Android الحقيقي غالبًا هتستخدم dependency injection زي Hilt بدلها. الـ `object` مناسبة لحاجات من غير state، أو utilities.

### القسم 2: الـ companion object بديل static 🆕 جديد عليك

<div dir="ltr">

```kotlin
class ApiClient private constructor(val baseUrl: String) {
    companion object {
        const val DEFAULT_URL = "https://api.example.com"
        private var created = 0

        fun create(url: String = DEFAULT_URL): ApiClient {
            created++
            return ApiClient(url)
        }
    }
}
// ApiClient("x")   // ERROR: cannot access the constructor: it is private
```

</div>

- **Kotlin مفيهاش `static`:** أي حاجة عايزها على مستوى الـ class مش الـ object، بتحطها في `companion object`. وبتتنادى زي static بالظبط: `ApiClient.create()`.
- **`private constructor`:** محدش يقدر يعمل `ApiClient(...)` من بره، ولازم يعدي على `create()`. ده نمط الـ factory، زي `factory` constructor أو `ApiClient._()` في Dart.
- **الـ companion object هو object حقيقي:** يقدر يـimplement interface (زي `Modifier` في T11)، ويقدر يكون ليه extensions (T19).
- **بديل تاني:** في Kotlin كتير بتحط الـ functions دي **top-level** في الملف بدل companion، لأنها مش لازم تكون جوه class أصلًا.

### القسم 3: companion ليه اسم

<div dir="ltr">

```kotlin
class Config {
    companion object Factory {
        fun default() = Config()
    }
}

Config.default()
Config.Factory.default()
```

</div>

الاسم الافتراضي هو `Companion`، وتقدر تسميه زي ما تحب. نادرًا ما هتحتاج ده.

### القسم 4: object expression، anonymous object في مكانه 🆕 جديد عليك

<div dir="ltr">

```kotlin
registerListener(object : ClickListener {
    override fun onClick(id: Int) = println("  clicked item $id")
})

val watcher = object : TextWatcherLike {
    override fun before(s: String) = ...
    override fun on(s: String) = ...
    override fun after(s: String) = ...
}
```

</div>

Dart **مفيهاش anonymous classes**. لو عايز تـimplement interface، لازم تعمل class باسم. هنا تقدر تعمل object بيـimplement interface **في نفس المكان** من غير ما تسميه.

ده مهم في Android لأن APIs كتير قديمة (Java) بتطلب listeners بأكتر من method. مثال حقيقي: `TextWatcher` فيه 3 methods (`beforeTextChanged` و `onTextChanged` و `afterTextChanged`)، وده اللي `TextWatcherLike` بيقلده.

### القسم 5: object من غير نوع

<div dir="ltr">

```kotlin
val point = object {
    val x = 3
    val y = 4
}
println("x + y = ${point.x + point.y}")
```

</div>

object مؤقت فيه بيانات. بيشتغل كده جوه function واحدة بس (local)، وأقرب حاجة ليه في Dart هي record `(x: 3, y: 4)`. استخدامه قليل في الواقع.

### القسم 6: fun interface والـ SAM conversion 🆕 جديد عليك

<div dir="ltr">

```kotlin
fun interface OnTap {
    fun tap(x: Int)
}

fun registerTap(onTap: OnTap) = onTap.tap(3)

registerTap { println("  tapped at $it") }
val tap = OnTap { x -> println("  tap $x") }
```

</div>

- **SAM:** معناها Single Abstract Method، يعني interface فيها method واحدة بس.
- **`fun interface`:** بتسمحلك تبعت **lambda** مكان الـ interface، والكومبايلر بيعمل الـ object لوحده.
- **من غير `fun`:** هتضطر تكتب `object : OnTap { override fun tap... }`، زي القسم 4.

ليه مش `(Int) -> Unit` على طول؟ الـ `fun interface` ليها **اسم** واضح، وممكن تبقى فيها default methods، وبتبان أحسن في الـ API العامة. للحاجات البسيطة الـ function type كفاية.

### القسم 7: Java single-method interfaces بتشتغل بنفس الطريقة

<div dir="ltr">

```kotlin
val task = Runnable { println("  running a Java Runnable") }
val byLength = Comparator<String> { a, b -> a.length - b.length }
```

</div>

أي Java interface فيها method واحدة بتقبل lambda تلقائيًا من غير `fun`. ده هتستخدمه مع Android APIs كتير، زي `view.setOnClickListener { }`، و `OnClickListener` ده Java interface.

### القسم 8: خد بالك، object expressions مش singletons

<div dir="ltr">

```kotlin
fun makeListener(): ClickListener = object : ClickListener {
    override fun onClick(id: Int) {}
}
println("same instance? ${makeListener() === makeListener()}")   // false
```

</div>

نفس كلمة `object` بس معنى مختلف تمامًا. `object Logger` (declaration) معناها singleton. لكن `object : X { }` (expression) معناها **object جديد كل مرة** الكود ده يتنفذ.

## الفخاخ في الملف

- **ERROR:** نداء `private constructor` من بره.
- **WATCH OUT:** object expression بيعمل نسخة جديدة كل مرة.

## عادات Flutter اللي هتوقعك

- البحث عن `static`، والصح `companion object` أو top-level function.
- كتابة singleton بالطريقة الطويلة، والصح `object`.
- عمل class كاملة باسم عشان listener واحد، والأحسن `object :` أو lambda مع `fun interface`.

## الخلاصة في 3 سطور

1. الـ `object X` singleton، و `companion object` بديل `static`، و `object : I { }` anonymous object.
2. الـ `fun interface` وأي Java SAM interface بيقبلوا lambda مباشرة.
3. الـ object expression بيعمل نسخة جديدة كل مرة، مش singleton.

</div>
