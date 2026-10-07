<div dir="rtl">

# شرح T14: الـ Inheritance والـ abstract والـ interfaces

**الملف:** `src/main/kotlin/T14_Inheritance.kt`، وسلايدز 59–61 و71.

## الخلاصة

المفاهيم نفسها زي Dart. الفرق الكبير في **الفلسفة**: في Dart كل class مفتوحة للوراثة ما لم تقول غير كده. في Kotlin **كل class و method مقفولين (`final`) افتراضيًا**، ولازم تكتب `open` صريح. وكمان مفيش `implements` و `extends` منفصلين، الاتنين بـ `:`.

## خريطة Dart ← Kotlin

</div>

<div dir="ltr">

| Dart | Kotlin |
| --- | --- |
| `class Cat extends Animal` | `class Cat : Animal()` |
| `class B implements A` | `class B : A` |
| `@override` (annotation اختيارية) | `override` (keyword إجبارية) |
| كل class مفتوحة | `open class` بس |
| `final class` (Dart 3) | الافتراضي |
| `abstract class` | `abstract class` |
| `abstract interface class` / implicit interface | `interface` |
| `super.sound()` | `super.sound()` |
| `with Mixin` | مفيش، استخدم interfaces بـ default methods أو delegation (T18) |

</div>

<div dir="rtl">

## الشرح

### القسم 1: open و override والـ polymorphism 🆕 جديد عليك

<div dir="ltr">

```kotlin
open class Animal(val name: String) {
    open fun sound() = "..."
    fun describe() = "$name says ${sound()}"   // final: can't be overridden
    open val legs: Int = 4
}

class Cat(name: String) : Animal(name) {
    override fun sound() = "Meow"
}

class Bird(name: String) : Animal(name) {
    override fun sound() = "Tweet"
    override val legs = 2
}
```

</div>

حاجات كتير جديدة هنا:

- **`open class`:** من غير `open`، مفيش حد يقدر يورث منها.
- **`open fun`:** حتى لو الـ class مفتوحة، كل method **مقفولة لوحدها** ما لم تكتب `open`. يعني `describe()` محدش يقدر يغيرها.
- **`override` إجبارية:** في Dart `@override` مجرد annotation للـ linter. هنا لو نسيتها هتاخد compile error.
- **`: Animal(name)`:** الـ `:` بدل `extends`، والأقواس معناها **نادي constructor الأب** وابعتله `name`، زي `super(name)` في Dart.
- **override لـ property:** `override val legs = 2`، ودي حاجة Dart بتعملها بـ getter override.

**ليه Kotlin عملت كده؟** ده مبدأ من كتاب Effective Java: "design for inheritance or prohibit it". الوراثة غير المقصودة بتعمل bugs لما الأب يتغير. فالقرار لازم يبقى صريح.

### Puppy: سلسلة وراثة و super

<div dir="ltr">

```kotlin
open class Dog(name: String) : Animal(name) {
    override fun sound() = "Woof"
}

class Puppy(name: String) : Dog(name) {
    override fun sound() = super.sound() + " (tiny)"
}
```

</div>

- **`Dog` لازم يبقى `open`:** عشان `Puppy` تورث منه.
- **`override` نفسها مفتوحة:** الـ method اللي معمولها override بتفضل `open` للأبناء اللي بعد كده. لو عايز تقفلها اكتب `final override`.
- **`super.sound()`:** زي Dart.

### القسم 2: الـ abstract class

<div dir="ltr">

```kotlin
abstract class Shape(val name: String) {
    abstract fun area(): Double
    open fun describe() = "$name with area ${"%.2f".format(area())}"
}
// Shape("x")   // ERROR: cannot create an instance of an abstract class
```

</div>

زي Dart. الـ `abstract fun` مفتوحة تلقائيًا ومش محتاجة `open`.

لاحظ `"%.2f".format(area())`: ده بيقرّب لرقمين عشريين، زي `toStringAsFixed(2)` في Dart.

### القسم 3: الـ interfaces 🆕 فيها جديد

<div dir="ltr">

```kotlin
interface Clickable {
    fun click()
    fun showOff() = println("  I'm clickable")
}

interface Focusable {
    fun focus() = println("  focused")
    fun showOff() = println("  I'm focusable")
}

class AppButton : Clickable, Focusable {
    override fun click() = println("  clicked")
    override fun showOff() {
        super<Clickable>.showOff()
        super<Focusable>.showOff()
    }
}
```

</div>

- **Kotlin عندها `interface` keyword حقيقية:** في Dart أي class ممكن تبقى interface بـ `implements`. هنا لأ.
- **default methods:** الـ interface ممكن يبقى فيها implementation، زي `showOff`. في Dart، `implements` كانت بتجبرك تكتب كل حاجة من الأول، فالـ default methods أقرب لـ mixins.
- **أكتر من interface:** مسموح، بس class أب **واحد** بس.
- **🆕 `super<Clickable>.showOff()`:** لو اتنين interfaces فيهم نفس الـ method بـ implementation، الكومبايلر **بيجبرك** تعمل override، وتقدر تختار تنادي أنهي واحد. في Dart مع mixins، آخر mixin كان بيكسب بصمت.

### القسم 4: properties في الـ interface

<div dir="ltr">

```kotlin
interface Named {
    val title: String
}

class Page(override val title: String) : Named
```

</div>

الـ interface ممكن تطلب property، بس **من غير storage**. والـ class بتعملها `override`، وممكن ده يحصل في الـ constructor نفسه.

### القسم 5: فحص الأنواع

<div dir="ltr">

```kotlin
val a: Animal = Cat("Kitty")
if (a is Cat) println("a is a Cat (smart cast): ${a.sound()}")
println("Root of every class: ${Any::class.simpleName} (Dart: Object)")
```

</div>

زي T03. الـ `Any` هو الأب لكل الـ classes.

### القسم 6: abstract class ولا interface؟

</div>

<div dir="ltr">

| | abstract class | interface |
| --- | --- | --- |
| state (fields فيها قيم) | آه | لأ |
| constructor | آه | لأ |
| كام واحد للـ class؟ | واحد بس | أي عدد |
| default methods | آه | آه |

</div>

<div dir="rtl">

القاعدة: لو فيه state مشتركة أو constructor استخدم abstract class. لو مجرد عقد (contract) استخدم interface. وفي Android الحديث، الـ interfaces أكتر بكتير، زي repository interfaces في Clean Architecture.

### القسم 7: خد بالك

<div dir="ltr">

```kotlin
// class Lion : Cat("Leo")                // ERROR: this type is final, so it cannot be extended
// override fun describe() = "" (in Cat)  // ERROR: 'describe' in 'Animal' is final
```

</div>

دول أكتر two errors هتقابلهم أول أسبوع. السبب في الاتنين نسيان `open`.

## عادات Flutter اللي هتوقعك

- كتابة `extends` و `implements`، والصح `:`.
- نسيان الأقواس بعد اسم الأب: `: Animal()` للـ class لازم أقواس، و `: Clickable` للـ interface من غير أقواس، لأن الـ interface ملهاش constructor.
- افتراض إن أي class تقدر تورث منها.
- البحث عن `mixin` و `with`، ومش موجودين. البديل interface بـ default methods، أو class delegation في T18.

## الخلاصة في 3 سطور

1. كل حاجة `final` افتراضيًا، ولازم `open` للوراثة و `override` إجبارية.
2. الـ `:` بتستخدم للوراثة والـ implementation، مع أقواس للـ class ومن غيرها للـ interface.
3. الـ interface فيها default methods و properties من غير state، ولو اتعارضت لازم تختار بـ `super<X>`.

</div>
