<div dir="rtl">

# شرح T13: الـ Properties والـ getters والـ setters والـ lazy

**الملف:** `src/main/kotlin/T13_Properties.kt`، وسلايدز 27 و57.

## الخلاصة

في Dart عندك field عادي، ولو عايز منطق بتعمل `_field` + `get` + `set` منفصلين. في Kotlin **كل property ليها getter و setter من الأول**، وتقدر تكتب منطقهم في نفس مكان تعريفها. والكلمة السحرية `field` هي الـ storage المخفي.

## خريطة Dart ← Kotlin

</div>

<div dir="ltr">

| Dart | Kotlin |
| --- | --- |
| `int get area => w * h;` | `val area get() = w * h` |
| `int _count = 0;` + `get` + `set` | `var count = 0` + `set(value) { field = value }` |
| `int _total; int get total => _total;` | `var total = 0` + `private set` |
| `late final x = compute();` | `val x by lazy { compute() }` |
| `late String title;` | `lateinit var title: String` |

</div>

<div dir="rtl">

## الشرح

### القسم 1: الـ computed getter

<div dir="ltr">

```kotlin
class Rect(val width: Int, val height: Int) {
    val area: Int
        get() = width * height
    val isSquare get() = width == height
}
```

</div>

زي `int get area => ...` في Dart. القيمة **بتتحسب مع كل قراءة** ومش بتتخزن. ومن بره بتتقري زي أي property: `r.area` من غير أقواس.

**إمتى تستخدم property وإمتى function؟** لو الحساب رخيص ومفيش side effects، استخدم property. لو تقيل أو بيعمل حاجة، استخدم function.

خد بالك من الفرق: `val area = width * height` (من غير `get()`) **بتتحسب مرة واحدة** وقت الإنشاء، لكن `get() = ...` بتتحسب كل مرة.

### القسم 2: setter فيه validation و field 🆕 جديد عليك

<div dir="ltr">

```kotlin
var count = 0
    set(value) {
        if (value >= 0) field = value   // field = the hidden storage
        else println("  rejected $value")
    }
```

</div>

- **`set(value)`:** بيتنفذ مع أي `c.count = x`.
- **`field`:** keyword معناها **المكان الحقيقي اللي القيمة متخزنة فيه** (backing field). في Dart كنت بتعمل `_count` بنفسك. هنا Kotlin بتعمله وبتسميه `field`، ومتاح جوه الـ getter والـ setter بس.

### القسم 3: الـ private set 🆕 جديد عليك

<div dir="ltr">

```kotlin
var total = 0
    private set
// c.total = 99   // ERROR: cannot access 'total': its setter is private
```

</div>

أي حد يقدر **يقرأ**، والـ class بس تقدر **تكتب**. في Dart كنت هتحتاج `_total` و `get total`، يعني متغيرين. هنا سطر واحد.

ده نمط هتستخدمه كتير جدًا في ViewModels (القسم 7).

### القسم 4: property من غير storage

<div dir="ltr">

```kotlin
var fahrenheit: Double
    get() = celsius * 9 / 5 + 32
    set(value) {
        celsius = (value - 32) * 5 / 9
    }
```

</div>

الـ getter والـ setter مش بيستخدموا `field`، فمفيش storage خالص للـ property دي. هي مجرد "واجهة" على `celsius`. زي getter و setter في Dart من غير field.

### القسم 5: get و set مع بعض

<div dir="ltr">

```kotlin
var name: String = ""
    get() = field.uppercase()
    set(value) {
        field = value.trim()
    }
```

</div>

الـ setter بيخزّن نسخة نضيفة، والـ getter بيرجعها uppercase. النتيجة: `"   samy  "` بتتقري `SAMY`.

### خد بالك: setter بينادي نفسه للأبد

<div dir="ltr">

```kotlin
class Broken {
    var x = 0
        set(value) { x = value }   // should be: field = value
}
```

</div>

جوه الـ setter، كتابة `x = value` **بتنادي الـ setter نفسه تاني**، فيحصل loop لا نهائي و `StackOverflowError`. جوه الـ getter/setter استخدم `field` دايمًا. نفس الغلطة ممكن تحصل في Dart لو getter رجّع نفسه، بس هنا أسهل تقع فيها.

### القسم 6: الفرق بين lateinit و lazy

<div dir="ltr">

```kotlin
class Screen {
    lateinit var title: String
    val heavy: String by lazy {
        println("  computing heavy value...")
        "READY"
    }
    fun isReady() = this::title.isInitialized
}
```

</div>

الاتنين بيأجلوا القيمة، بس الفكرة مختلفة:

</div>

<div dir="ltr">

| | `lateinit var` | `val by lazy { }` |
| --- | --- | --- |
| مين بيعيّن؟ | انت، من بره، وقت ما تحب | الـ lambda نفسها، أول ما حد يقرأ |
| `val` أو `var` | `var` بس | `val` بس |
| primitives (`Int`) | لأ | آه |
| لو قريت بدري | crash | بيحسب على طول |
| Dart | `late String x;` | `late final x = compute();` |

</div>

<div dir="rtl">

- **`by lazy`:** 🆕 أول مرة تشوف كلمة `by`، ودي **delegation** (T18). معناها إن الـ getter بتاع `heavy` متوكّل لـ object تاني اسمه `Lazy`، وهو اللي بيحسب القيمة أول مرة ويحفظها.
- **thread-safe:** الـ `lazy` افتراضيًا thread-safe. لو اتنين threads قروها مع بعض، الحساب هيحصل مرة واحدة بس.
- **`this::title.isInitialized`:** نفس اللي في T05، بس على property جوه class.

### القسم 7: في Compose

<div dir="ltr">

```kotlin
var uiState by mutableStateOf(UiState())
    private set
```

</div>

ده نمط هتكتبه في كل ViewModel تقريبًا:

- **`by mutableStateOf(...)`:** delegation تاني. الـ property متوصلة بـ Compose state، فأي تغيير بيعمل recomposition. ده مقابل `setState` أو `notifyListeners`.
- **`private set`:** الشاشة تقرأ بس، والـ ViewModel هو اللي يعدّل.

في Flutter كنت بتعمل نفس الحكاية بـ `ChangeNotifier` + `_state` + `get state`.

## الفخاخ في الملف

- **ERROR:** الكتابة في property الـ setter بتاعها `private`.
- **WATCH OUT:** الـ setter اللي بينادي نفسه.
- **الفرق بين `get()` والقيمة المحسوبة مرة واحدة.**

## عادات Flutter اللي هتوقعك

- عمل `_field` منفصل + getter، والصح استخدام `field` و `private set`.
- كتابة `getX()` كـ function، والأحسن property في Kotlin.
- استخدام `lateinit` لحاجة ممكن تتحسب لوحدها، والأحسن `by lazy`.

## الخلاصة في 3 سطور

1. كل property ليها getter و setter، وتقدر تخصصهم في نفس مكان التعريف.
2. الكلمة `field` هي الـ storage المخفي، واستخدمها جوه الـ setter عشان متعملش loop.
3. الـ `private set` للقراءة من بره بس، و `lateinit` لـ var تتعين بعدين، و `by lazy` لـ val تتحسب أول مرة.

</div>
