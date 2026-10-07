<div dir="rtl">

# شرح T12: الـ Classes والـ constructors والـ init

**الملف:** `src/main/kotlin/T12_Classes.kt`، وسلايدز 53–56 و58.

## الخلاصة

الـ class في Kotlin أقصر بكتير من Dart، لأن **الـ primary constructor بيتكتب في سطر الـ class نفسه** وبيعرّف الـ properties مع بعض. والجديد عليك: `init` blocks، ومفيش named constructors (بدالها `companion object`)، والـ visibility بالكلمات مش بـ `_`.

## خريطة Dart ← Kotlin

</div>

<div dir="ltr">

| Dart | Kotlin |
| --- | --- |
| `class Car { final String brand; Car(this.brand); }` | `class Car(val brand: String)` |
| `Car('BMW')` أو `new Car('BMW')` | `Car("BMW")` |
| initializer list `: x = 1` | `init { }` أو property initializer |
| `User.fromJson(...)` named constructor | `companion object { fun fromJson(...) }` |
| `factory User(...)` | `companion object` function |
| `_balance` (library-private) | `private` / `protected` / `internal` |
| `identical(a, b)` | `a === b` |
| `assert(...)` / `ArgumentError` | `require(...)` |

</div>

<div dir="rtl">

## الشرح

### القسم 1: الـ class والـ object 🆕 فيها جديد

<div dir="ltr">

```kotlin
class Car(val brand: String, var speed: Int = 0) {
    fun accelerate(by: Int) {
        speed += by
    }
    override fun toString() = "Car(brand=$brand, speed=$speed)"
}

val a = Car("BMW")
```

</div>

السطر الأول ده بيعمل كل اللي في Dart بيحتاج 5 سطور:

</div>

<div dir="ltr">

```dart
class Car {
  final String brand;
  int speed;
  Car(this.brand, [this.speed = 0]);
}
```

</div>

<div dir="rtl">

- **الـ primary constructor:** القوسين بعد اسم الـ class.
- **`val brand`:** بيعمل property read-only وبيعيّنها من الـ constructor.
- **`var speed`:** property قابلة للتعديل.
- **لو كتبت `brand: String` من غير `val`:** هيبقى parameter عادي للـ constructor بس، مش property، ومش هتقدر تقرأه بعدين بـ `a.brand`. دي غلطة شائعة.
- **`override fun toString()`:** لازم كلمة `override`، ومفيش annotation `@override` زي Dart.
- **مفيش `new`:** زي Dart الحديث.

وبالنسبة للمقارنة: `===` بتشيك لو نفس الـ object، زي `identical()` في Dart.

### القسم 2: init blocks و property initializers 🆕 جديد عليك

<div dir="ltr">

```kotlin
class Person(val name: String) {
    val greeting: String

    init {
        println("  init 1: name=$name")
        greeting = "Hi, $name"
    }

    val nameLength = name.length.also { println("  property initializer: nameLength=$it") }

    init {
        println("  init 2: runs after the property above (top to bottom)")
    }
}
```

</div>

- **الـ `init`:** كود بيتنفذ مع الـ primary constructor. هو مكان الـ validation أو الحسابات اللي في Dart كنت بتعملها في constructor body أو initializer list.
- **الترتيب:** الـ `init` blocks والـ property initializers **بيتنفذوا من فوق لتحت بترتيب كتابتهم**. لو `init` بتستخدم property معرّفة **تحتها**، هتلاقيها لسه متعينتش.
- **الـ `also`:** scope function (T20) مستخدمة هنا بس عشان نطبع وقت التنفيذ.

### القسم 3: الـ default parameters أحسن من constructors زيادة

<div dir="ltr">

```kotlin
class User(val name: String, val age: Int = 18)

User("Ali")
User("Ali", 25)
User(age = 30, name = "Mona")
```

</div>

بفضل الـ defaults والـ named args (T06) نادرًا ما هتحتاج أكتر من constructor واحد.

### القسم 4: الـ secondary constructor

<div dir="ltr">

```kotlin
constructor(json: Map<String, Any>) : this(json["name"] as String, json["age"] as Int) {
    println("  secondary constructor body runs AFTER init")
}
```

</div>

- **الشكل:** constructor إضافي جوه الـ class بكلمة `constructor`.
- **القاعدة:** **لازم** ينادي الـ primary بـ `this(...)`، زي redirecting constructor في Dart.
- **الترتيب:** الـ primary والـ `init` بيتنفذوا الأول، وبعدهم body الـ secondary.

في الواقع هتستخدمه قليل جدًا، غالبًا لما تعمل extend لـ Java class زي custom View في Android.

### القسم 5: مفيش named constructors، استخدم factory 🆕 جديد عليك

<div dir="ltr">

```kotlin
companion object {
    fun fromJson(json: Map<String, Any>): User =
        User(json["name"] as String, json["age"] as? Int ?: 18)
}

User.fromJson(mapOf("name" to "Sara"))
```

</div>

في Dart بتكتب `User.fromJson(...)` كـ named constructor أو `factory`. Kotlin مفيهاش ده. البديل `companion object`، وده object واحد مرتبط بالـ class. أي function جواه بتتنادى باسم الـ class، زي `static` في Dart. التفاصيل في T17.

لاحظ `json["age"] as? Int ?: 18`: لو مش موجود أو مش `Int` يرجع 18. ده T03 و T05 مع بعض.

### القسم 6: الـ Visibility 🆕 جديد عليك

<div dir="ltr">

```kotlin
class Account(private val owner: String) {
    private var balance = 0
    internal val bank = "Kotlin Bank"

    fun deposit(amount: Int) {
        require(amount > 0) { "amount must be positive, was $amount" }
        balance += amount
    }
}
// acc.balance   // ERROR: cannot access 'balance': it is private
```

</div>

في Dart عندك مستويين بس: public، أو `_` وده private على مستوى **الملف/library** كله. في Kotlin عندك 4:

</div>

<div dir="ltr">

| الكلمة | مين يشوفها |
| --- | --- |
| `public` (الافتراضي) | أي حد |
| `private` | جوه الـ class بس (أو الملف لو top-level) |
| `protected` | الـ class والـ subclasses |
| `internal` | أي حد في نفس الـ **module** (Gradle module) |

</div>

<div dir="rtl">

- **فرق مهم:** `private` في class معناها **الـ class دي بس**. في Dart، `_` كان بيخلي أي كود في نفس الملف يشوفها.
- **`internal`:** مفيدة في المشاريع الكبيرة متعددة الـ modules. الحاجة متاحة جوه الـ module ومخفية عن الباقي.
- **`require(cond) { msg }`:** بترمي `IllegalArgumentException` لو الشرط مش متحقق. استخدمها لـ validation الـ input. وفيه `check()` لحالة الـ object، وبترمي `IllegalStateException`. عكس `assert` في Dart، دول **شغالين دايمًا** مش في debug بس.

### القسم 7: خد بالك

- **الـ classes `final` افتراضيًا:** مش هتقدر تعمل منها `extends` إلا لو كتبت `open`. ده عكس Dart تمامًا (T14).
- **أي property لازم يكون ليها قيمة:** من الـ constructor، أو initializer، أو `init`، أو تكون `lateinit`. مفيش حاجة اسمها property متعينتش.

## الفخاخ في الملف

- **ERROR:** قراءة `private` property من بره.
- **CRASH:** `deposit(-5)` بيرمي `IllegalArgumentException` من `require`.

## عادات Flutter اللي هتوقعك

- كتابة الـ fields جوه الـ class والـ constructor لوحده، والصح إنهم الاتنين في سطر الـ class.
- نسيان `val`/`var` في الـ constructor، فالـ parameter مش بيبقى property.
- البحث عن `User.named()`، والصح `companion object`.
- استخدام `_` للـ private، وده مش بيعمل أي حاجة في Kotlin غير إنه جزء من الاسم.

## الخلاصة في 3 سطور

1. الـ `class Car(val brand: String)` بيعرّف constructor و property في سطر واحد.
2. الـ `init` بيتنفذ مع الـ constructor بترتيب الكتابة، ومفيش named constructors فاستخدم `companion object`.
3. عندك `private` و `protected` و `internal`، والـ classes مقفولة افتراضيًا.

</div>
