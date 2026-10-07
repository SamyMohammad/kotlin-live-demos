<div dir="rtl">

# شرح T16: الـ Enum والـ Sealed

**الملف:** `src/main/kotlin/T16_EnumAndSealed.kt`، وسلايدز 65–67 و40.

## الخلاصة

لو بتستخدم Dart 3، فالموضوع ده مألوف ليك: enhanced enums من Dart 2.17، و `sealed class` من Dart 3. الفكرة تقريبًا واحدة، والفرق في الـ syntax، وفي حاجة جديدة صغيرة اسمها `data object`. أهم استخدام ليها في Android: **UI state** بـ `sealed interface`.

## خريطة Dart ← Kotlin

</div>

<div dir="ltr">

| Dart | Kotlin |
| --- | --- |
| `enum Status { active, blocked }` | `enum class Status { ACTIVE, BLOCKED }` |
| `Status.values` | `Status.entries` |
| `Status.values.byName('x')` | `Status.valueOf("X")` |
| `s.index` | `s.ordinal` |
| `s.name` | `s.name` |
| `sealed class UiState {}` | `sealed interface UiState` / `sealed class` |
| `switch (state) { Loading() => ... }` | `when (state) { UiState.Loading -> ... }` |
| `case Success(:final items)` | `is UiState.Success -> state.items` |

</div>

<div dir="rtl">

## الشرح

### القسم 1: أساسيات الـ enum

<div dir="ltr">

```kotlin
enum class Status { ACTIVE, BLOCKED, PENDING }

println("s=$s name=${s.name} ordinal=${s.ordinal}")
println("entries = ${Status.entries}")
println("valueOf(\"BLOCKED\") = ${Status.valueOf("BLOCKED")}")
tryIt("valueOf(\"DELETED\")") { Status.valueOf("DELETED") }
println("safe lookup = ${Status.entries.find { it.name == "DELETED" }}")
```

</div>

- **`enum class`:** كلمتين مش واحدة.
- **الـ convention:** القيم بتتكتب `UPPER_CASE`، عكس Dart اللي بيستخدم `camelCase`.
- **`entries`:** أحدث وأحسن من `values()` القديمة. `values()` بتعمل array جديد كل مرة.
- **`valueOf`:** بترمي `IllegalArgumentException` لو الاسم مش موجود، زي `byName` في Dart. والبديل الآمن هو `entries.find { }`، وده مهم مع بيانات جاية من API.

### القسم 2: enum فيه properties و functions

<div dir="ltr">

```kotlin
enum class Plan(val price: Int, val label: String) {
    FREE(0, "Free"),
    PRO(99, "Pro"),
    TEAM(299, "Team");

    fun isPaid() = price > 0
}
```

</div>

زي enhanced enums في Dart 2.17. الفرق الوحيد اللي هيلخبطك: **لازم `;`** بعد آخر قيمة لو فيه members بعدها. ده المكان الوحيد تقريبًا في Kotlin اللي الـ semicolon فيه إجبارية.

### القسم 3: الـ when على enum بيبقى exhaustive

<div dir="ltr">

```kotlin
fun statusColor(s: Status) = when (s) {
    Status.ACTIVE -> "green"
    Status.BLOCKED -> "red"
    Status.PENDING -> "orange"
}
```

</div>

مفيش `else` لأن كل القيم متغطية. لو ضفت قيمة جديدة للـ enum، الكومبايلر هيطلع error هنا ويجبرك تتعامل معاها. زي `switch` expression في Dart 3.

### القسم 4: sealed interface للـ UI state 🆕 فيها جديد

<div dir="ltr">

```kotlin
sealed interface UiState {
    data object Loading : UiState
    data class Success(val items: List<String>) : UiState
    data class Error(val message: String) : UiState
}

fun render(state: UiState): String = when (state) {
    UiState.Loading -> "Spinner..."
    is UiState.Success -> "List: ${state.items.joinToString()}"
    is UiState.Error -> "Error: ${state.message}"
}
```

</div>

ده **أهم نمط في الملف**، وهتكتبه في كل شاشة فيها loading و success و error:

- **`sealed`:** كل الأنواع الفرعية لازم تكون في نفس الـ package والـ module، فالكومبايلر يعرفهم كلهم ويقدر يتأكد إن `when` غطتهم. زي `sealed` في Dart 3، اللي بيشترط نفس الـ library.
- **كل حالة ليها شكل مختلف:** `Loading` من غير بيانات، و `Success` معاها list، و `Error` معاها رسالة.
- **الأنواع متعرّفة جوه الـ interface:** فبتكتب `UiState.Success`. ده مش إجباري، بس بيخلي الأسماء منظمة.
- **`is UiState.Success`:** بعد كده `state.items` متاحة مباشرة بفضل الـ smart cast. في Dart كنت بتكتب `Success(:final items)` أو `case Success s`.
- **`UiState.Loading` من غير `is`:** لأنه **object واحد** مش class، فبنقارن بالقيمة. لو كتبت `is UiState.Loading` هتشتغل برضه.

**sealed interface ولا sealed class؟** الـ interface أخف ومفيهاش constructor، وأي class تقدر تـimplement أكتر من واحدة. استخدمها افتراضيًا.

### القسم 5: sealed class فيها state مشتركة

<div dir="ltr">

```kotlin
sealed class Shape(val name: String) {
    class Circle(val r: Double) : Shape("Circle")
    class Rect(val w: Double, val h: Double) : Shape("Rect")
}
```

</div>

استخدم `sealed class` لما كل الأنواع محتاجة property مشتركة أو constructor، زي `name` هنا.

### القسم 6: enum ولا sealed؟

</div>

<div dir="ltr">

| | enum | sealed |
| --- | --- | --- |
| إيه الثابت؟ | مجموعة **قيم** | مجموعة **أنواع** |
| كل عنصر ليه بيانات مختلفة؟ | لأ، نفس الشكل | آه |
| عدد النسخ | نسخة واحدة لكل قيمة | أي عدد (`Error("a")` و `Error("b")`) |
| مثال | `Status` و `Plan` | `UiState` و `Result` |

</div>

<div dir="rtl">

### القسم 7: خد بالك، متستخدمش else مع sealed

<div dir="ltr">

```kotlin
fun renderLazy(state: UiState): String = when (state) {
    is UiState.Success -> "List"
    else -> "Something else"
}
```

</div>

الـ `else` بتلغي كل فايدة الـ sealed. لو ضفت بكرة `data object Empty : UiState`:

- **`render()`:** هتطلع compile error، وده **كويس** لأنه بيقولك فيه حالة جديدة لازم ترسمها.
- **`renderLazy()`:** هتكمل بصمت وهتعرض "Something else"، وده **bug** هتكتشفه من اليوزرز.

القاعدة: مع `sealed` و `enum`، غطي كل الحالات بالاسم ومتكتبش `else`. نفس النصيحة في Dart مع `_ =>`.

### القسم 8: ليه data object؟ 🆕 جديد عليك

<div dir="ltr">

```kotlin
println("data object : ${UiState.Loading}")   // Loading
println("plain object: $PlainLoading")         // t16.PlainLoading@4e50df2e
```

</div>

- **`object`:** singleton، وموضوعه T17.
- **`data object`:** نفس الحاجة بس بـ `toString` حلو (اسمه بس)، و `equals` و `hashCode` مظبوطين. اتضافت في Kotlin 1.9 عشان تبقى متسقة مع `data class` في الـ sealed hierarchies. مفيش ليها مقابل مباشر في Dart. أقرب حاجة `const Loading()`.

## الفخاخ في الملف

- **CRASH:** `valueOf` باسم مش موجود بيرمي `IllegalArgumentException`.
- **WATCH OUT:** `else` مع sealed بتخبي الحالات الجديدة.

## عادات Flutter اللي هتوقعك

- كتابة قيم الـ enum بـ camelCase، والـ convention هنا UPPER_CASE.
- كتابة `Status.values`، والصح `Status.entries`.
- نسيان `;` بعد آخر قيمة في enum فيه functions.
- كتابة `else` / `_` في `when` على sealed عشان "الأمان"، وهي بالعكس.

## الخلاصة في 3 سطور

1. الـ `enum class` لقيم ثابتة بنفس الشكل، واستخدم `entries` و `find` بدل `valueOf` مع بيانات خارجية.
2. الـ `sealed interface` مع `data object` و `data class` هو نمط الـ UI state القياسي.
3. الـ `when` من غير `else` على sealed أو enum بتخلي الكومبايلر يمسك أي حالة جديدة.

</div>
