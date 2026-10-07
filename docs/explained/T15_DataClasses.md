<div dir="rtl">

# شرح T15: الـ Data classes

**الملف:** `src/main/kotlin/T15_DataClasses.kt`، وسلايدز 62–64 و71.

## الخلاصة

في Flutter، كل model كنت بتكتبله `==` و `hashCode` و `toString` و `copyWith` بإيدك، أو بتستخدم `freezed` و `build_runner` و `equatable`. في Kotlin، **كلمة واحدة: `data`**، والكومبايلر بيكتب كل ده. 🆕 ودي من أكتر الحاجات اللي هتحبها.

## خريطة Dart ← Kotlin

</div>

<div dir="ltr">

| Dart (يدوي أو freezed) | Kotlin |
| --- | --- |
| `operator ==` + `hashCode` | تلقائي |
| `toString()` | تلقائي |
| `copyWith({...})` | `copy(...)` تلقائي |
| records destructuring `var (a, b) = r;` | `val (a, b) = user` |
| `@freezed class` + `build_runner` | `data class` |

</div>

<div dir="rtl">

## الشرح

### القسم 1: toString ببلاش

<div dir="ltr">

```kotlin
data class User(val name: String, val age: Int)
class PlainUser(val name: String, val age: Int)

println(User("Ali", 20))        // User(name=Ali, age=20)
println(PlainUser("Ali", 20))   // t15.PlainUser@1b6d3586
```

</div>

الـ class العادية بتطبع اسمها + hash، زي `Instance of 'PlainUser'` في Dart.

### القسم 2: equals بيقارن القيم

<div dir="ltr">

```kotlin
User("Ali", 20) == User("Ali", 20)            // true
PlainUser("Ali", 20) == PlainUser("Ali", 20)  // false
User("Ali", 20) === User("Ali", 20)           // false: different objects
```

</div>

- **`==`:** بتنادي `equals()`. في `data class` بتقارن كل الـ properties اللي في الـ constructor.
- **الـ class العادية:** `equals` بتقارن المرجع، زي Dart من غير override.
- **`===`:** دايمًا بتقارن المرجع.

**ليه ده مهم في Compose؟** Compose بيقارن الـ parameters بـ `equals` عشان يقرر يعمل recomposition ولا لأ. لو الـ state `data class`، القيم المتساوية مش هتعمل recomposition على الفاضي. وده نفس السبب اللي كنت بتستخدم عشانه `Equatable` مع Bloc.

### القسم 3: hashCode، عشان sets و map keys

<div dir="ltr">

```kotlin
setOf(User("Ali", 20), User("Ali", 20)).size            // 1
setOf(PlainUser("Ali", 20), PlainUser("Ali", 20)).size  // 2
```

</div>

الـ `Set` والـ `Map` بيعتمدوا على `hashCode` + `equals`. الـ data class بتعملهم صح مع بعض. لو كتبتهم بإيدك ونسيت واحد، الـ set هيتصرف غلط، وده bug مشهور في Dart.

### القسم 4: copy (المقابل لـ copyWith)

<div dir="ltr">

```kotlin
val u = User("Ali", 20)
val older = u.copy(age = 21)
```

</div>

- **object جديد:** بنفس القيم ما عدا اللي غيرته. الأصلي متغيرش.
- **أحسن من `copyWith` في Dart في نقطة مهمة:** في Dart لو عندك `String? error` ومحتاج ترجعه `null`، فـ `copyWith(error: null)` مش بتشتغل، لأن `null` معناها "متغيرش". لازم تحايل. هنا `copy(error = null)` **بتشتغل عادي**، لأن الـ default مش `null`، الـ default هو القيمة الحالية.

### القسم 5: الـ Destructuring 🆕 جديد عليك

<div dir="ltr">

```kotlin
val (name, age) = u
for ((n, a) in listOf(User("Mona", 22), User("Omar", 19))) println("$n is $a")
```

</div>

الـ data class بتعمل functions اسمها `component1()` و `component2()` بترتيب الـ constructor، والـ destructuring بيستخدمهم. Dart 3 عندها destructuring للـ records وللـ classes بـ object patterns، بس على class عادية لازم تكتب أسماء الـ fields.

**خد بالك:** الـ destructuring بالـ **ترتيب** مش بالاسم. لو كتبت `val (age, name) = u`، هتاخد `age = "Ali"`. وده من غير أي error لو الأنواع متوافقة.

### القسم 6: نمط الـ UI state (ViewModel و Compose)

<div dir="ltr">

```kotlin
data class LoginUiState(
    val email: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
)

var state = LoginUiState()
state = state.copy(email = "samy@mail.com", isLoading = true)
state = state.copy(isLoading = false, error = "Wrong password")
```

</div>

ده **النمط الأساسي** في Android الحديث، وهتكتبه في كل شاشة:

- **state واحد immutable للشاشة:** كله `val` بـ defaults.
- **كل تغيير بـ `copy`:** state جديد يتبعت للـ UI.

ده نفس اللي كنت بتعمله في Bloc/Cubit: `emit(state.copyWith(isLoading: true))`.

لاحظ الـ trailing comma بعد آخر parameter: مسموحة في Kotlin زي Dart، وبتخلي الـ diff أنضف.

### القسم 7: خد بالك، الـ properties اللي في الـ body بتتجاهل

<div dir="ltr">

```kotlin
data class Profile(val name: String) {
    var visits: Int = 0     // NOT part of equals / toString / copy
}

val p1 = Profile("Mona").apply { visits = 5 }
val p2 = Profile("Mona")
p1 == p2   // true
```

</div>

الـ `equals` و `hashCode` و `toString` و `copy` بيستخدموا **الـ properties اللي في الـ primary constructor بس**. أي حاجة في الـ body مش داخلة. أحيانًا ده مقصود (حاجة مش عايزها في المقارنة)، بس غالبًا بيبقى bug.

### القسم 8: خد بالك، copy() سطحي

<div dir="ltr">

```kotlin
data class Cart(val items: MutableList<String>)

val cart1 = Cart(mutableListOf("Milk"))
val cart2 = cart1.copy()
cart2.items.add("Eggs")
println(cart1)   // Cart(items=[Milk, Eggs])  <- changed too!
```

</div>

الـ `copy` بينسخ **المراجع** مش المحتوى (shallow copy). الاتنين بيشاوروا على نفس الـ list. نفس الحكاية في `copyWith` بتاع Dart.

الحل: في الـ state استخدم `List` (read-only)، ولما تعدّل اعمل list جديدة: `state.copy(items = state.items + "Eggs")`.

### القسم 9: القواعد

<div dir="ltr">

```kotlin
// data class Empty()               // ERROR: needs at least one constructor parameter
// open data class Base(val a: Int) // ERROR: data classes can't be open / abstract / sealed
```

</div>

- **لازم parameter واحد على الأقل** في الـ primary constructor. ولو محتاج حاجة من غير بيانات، استخدم `data object` (T16).
- **مينفعش `open` أو `abstract`:** لأن الوراثة بتبوظ `equals`. تقدر تخليها **تورث من** sealed class أو interface، وده الاستخدام الشائع في T16.

## عادات Flutter اللي هتوقعك

- كتابة `copyWith` أو `==` بإيدك، ومش محتاج.
- البحث عن package زي freezed، والصح إن الموضوع built-in.
- استخدام `MutableList` جوه state، والأحسن `List` ونسخة جديدة.

## الخلاصة في 3 سطور

1. الـ `data class` بتديك `equals` و `hashCode` و `toString` و `copy` و destructuring ببلاش.
2. كل ده بيشتغل على properties الـ constructor بس، والـ `copy` سطحي.
3. الـ UI state بيبقى `data class` بـ `val` و defaults، والتعديل بـ `copy`، زي `copyWith` في Bloc.

</div>
