<div dir="rtl">

# شرح T23: الـ Operators والـ infix والـ value classes (بونص)

**الملف:** `src/main/kotlin/T23_OperatorsAndMore.kt`، ومش في السلايدز.

## الخلاصة

الملف ده بيكشف "السحر" اللي ورا حاجات استخدمتها طول السيشن: ليه `a + b` شغالة على `Money`، وليه `"Ali" to 20` شغالة، وليه `x in list`. الإجابة: **operator functions** و **infix functions**. وفي الآخر `typealias` و `value class`.

## خريطة Dart ← Kotlin

</div>

<div dir="ltr">

| Dart | Kotlin |
| --- | --- |
| `Money operator +(Money o)` | `operator fun plus(o: Money)` |
| `operator [](int i)` | `operator fun get(i: Int)` |
| `call()` method | `operator fun invoke()` |
| `implements Iterable` عشان `for-in` | `operator fun iterator()` |
| — | `operator fun contains()` → `in` |
| — | `infix fun` |
| `typedef OnClick = void Function();` | `typealias OnClick = () -> Unit` |
| `extension type Email(String v)` (Dart 3.3) | `@JvmInline value class Email(val value: String)` |

</div>

<div dir="rtl">

## الشرح

### القسم 1: الـ Operator overloading

<div dir="ltr">

```kotlin
data class Money(val amount: Int, val currency: String = "EGP") {
    operator fun plus(other: Money) = Money(amount + other.amount, currency)
    operator fun times(factor: Int) = Money(amount * factor, currency)
    operator fun compareTo(other: Money) = amount.compareTo(other.amount)
}

println("a + b = ${a + b}   a * 3 = ${a * 3}   a > b = ${a > b}")
```

</div>

الفرق عن Dart: في Dart بتكتب العلامة نفسها (`operator +`). في Kotlin بتكتب **اسم** ثابت لكل علامة مع كلمة `operator`:

</div>

<div dir="ltr">

| العلامة | الـ function |
| --- | --- |
| `a + b` | `a.plus(b)` |
| `a - b` | `a.minus(b)` |
| `a * b` | `a.times(b)` |
| `a / b` | `a.div(b)` |
| `a > b` و `<` و `>=` و `<=` | `a.compareTo(b) > 0` ... |
| `a[i]` | `a.get(i)` |
| `a[i] = v` | `a.set(i, v)` |
| `x in a` | `a.contains(x)` |
| `a()` | `a.invoke()` |
| `a += b` | `a.plusAssign(b)` أو `a = a.plus(b)` |

</div>

<div dir="rtl">

لاحظ إن `compareTo` واحدة بتغطي **الأربع** علامات المقارنة مع بعض. في Dart لازم تعرّف `<` و `>` و `<=` و `>=` كل واحدة لوحدها.

**امتى تستخدمه؟** لما العلامة ليها معنى واضح، زي فلوس أو vectors أو تواريخ. متعملش `plus` على `User`، لأن محدش هيفهم معناه.

### القسم 2: الـ infix functions 🆕 جديد عليك

<div dir="ltr">

```kotlin
infix fun Int.percentOf(total: Int) = total * this / 100

println("20 percentOf 300 = ${20 percentOf 300}")
"Ali" to 20
1 until 4
```

</div>

function فيها parameter واحد بالظبط، ومعلّمة `infix`، ممكن تتنادى **من غير نقطة وأقواس**: `20 percentOf 300` بدل `20.percentOf(300)`. مفيش حاجة زيها في Dart.

ودلوقتي تعرف إن `to` (T08)، و `until` و `downTo` و `step` (T07)، كلهم مجرد infix functions في الـ standard library، مش keywords.

لاحظ ترتيب الحساب: `total * this / 100`، يعني الضرب الأول عشان القسمة الصحيحة متضيعش الكسر (T03). لو كتبت `this / 100 * total`، هتطلع `20 / 100 = 0`.

### القسم 3: invoke، object تنادي عليه زي function

<div dir="ltr">

```kotlin
class MinValidator(private val min: Int) {
    operator fun invoke(value: Int) = value >= min
}

val isAdult = MinValidator(18)
isAdult(20)
```

</div>

زي `call()` في Dart بالظبط. هتشوفها كتير في Clean Architecture على Android: الـ **use cases** بتتكتب بـ `operator fun invoke`، فبتنادي `getUserUseCase(id)` كأنها function.

### القسم 4: get و contains و iterator

<div dir="ltr">

```kotlin
class Team(private val members: List<String>) {
    operator fun get(index: Int) = members[index]
    operator fun contains(name: String) = name in members
    operator fun iterator() = members.iterator()
}

team[0]
"Ali" in team
for (m in team) print("$m ")
```

</div>

- **`get`:** زي `operator []` في Dart.
- **`contains`:** بتشغّل `in` و `!in`. Dart مفيهاش `in` operator.
- **`iterator`:** 🆕 أي class فيها `operator fun iterator()` بتشتغل مع `for`، من غير ما تـimplement `Iterable`. في Dart لازم تبقى `Iterable`.

### القسم 5: الـ typealias

<div dir="ltr">

```kotlin
typealias OnClick = () -> Unit
typealias TracksByStudent = Map<String, List<String>>
```

</div>

زي `typedef` في Dart. **اسم تاني** لنفس النوع، ومش نوع جديد. يعني `TracksByStudent` و `Map<String, List<String>>` متبادلين تمامًا. مفيد لأنواع الـ functions الطويلة. ولو عايز **نوع جديد فعلًا** يتمنع خلطه، استخدم `value class`.

### القسم 6: value class، type safety من غير تكلفة 🆕

<div dir="ltr">

```kotlin
@JvmInline
value class Email(val value: String) {
    init {
        require("@" in value) { "invalid email: $value" }
    }
}

fun sendWelcome(email: Email) = ...
sendWelcome(Email("samy@mail.com"))
// sendWelcome("samy@mail.com")      // ERROR: a String is not an Email
tryIt("Email(\"nope\")") { Email("nope") }
```

</div>

- **المشكلة:** لو function بتاخد `(userId: String, email: String)`، ممكن تبعتهم بالعكس والكومبايلر مش هيقول حاجة.
- **الحل:** `value class Email` بتعمل نوع جديد. الكومبايلر بيمنع `String` مكان `Email`.
- **من غير تكلفة:** وقت التشغيل الـ JVM بيشوف `String` عادي، من غير object إضافي (في أغلب الحالات). ده معنى `@JvmInline`.
- **الـ `init` بيضمن الصحة:** أي `Email` موجود في البرنامج اتعمله validation. اسمها "make illegal states unrepresentable".
- **القيود:** property واحدة بس في الـ constructor، ولازم `val`.
- **في Dart 3.3:** `extension type` هي نفس الفكرة.

وشفنا `Dp` في T11 و T19، ودي value class حقيقية في Compose.

### القسم 7: السيشن الجاية

الـ Coroutines: `suspend fun` و `launch` و `async/await` و `Flow`. المقابل في Dart: `async` و `Future` و `Stream`.

## الفخاخ في الملف

- **ERROR:** `String` مكان `Email`.
- **CRASH:** `Email("nope")` بيرمي `IllegalArgumentException` من الـ `init`.

## عادات Flutter اللي هتوقعك

- كتابة `operator +`، والصح `operator fun plus`.
- استخدام `typedef` على أساس إنه بيعمل نوع جديد، والصح إن `typealias` مجرد اسم، و `value class` هو النوع الجديد.

## الخلاصة في 3 سطور

1. كل علامة ليها function باسم ثابت (`plus` و `get` و `contains` و `invoke` ...) مع `operator`.
2. الـ `infix` بيخلي functions زي `to` و `until` تتكتب من غير نقطة وأقواس.
3. الـ `typealias` اسم بديل بس، والـ `value class` نوع جديد آمن من غير تكلفة وقت التشغيل.

</div>
