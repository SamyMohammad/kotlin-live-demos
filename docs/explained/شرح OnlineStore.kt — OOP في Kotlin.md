# شرح OnlineStore.kt — OOP في Kotlin

Oct 7, 2026 · @samy

## ‏الفكرة

‏الملف `OnlineStore.kt` متجر أونلاين صغير بيغطي كل مفاهيم الـ OOP في Kotlin في 16 جزء مترقّم. الـ domain مألوف للطلبة: users و products و cart و payment و checkout.

‏الملف محتاج Kotlin 2.4 أو أحدث عشان الجزئين 11 (explicit backing field) و 16 (context parameters).

‏**ترتيب الشرح المقترح:** ابدأ من الـ data types الصغيرة (Money و Email و Role)، بعدين الـ interfaces، بعدين الـ abstract class، وآخر حاجة `main` اللي بتربط كل حاجة ببعض. كل قسم تحت فيه الكود، الشرح، والمقابل في Dart.

| ‏الجزء | ‏المفهوم | ‏المقابل في Dart |
| --- | --- | --- |
| 1 | value class | extension type |
| 2 | data class + operators | Equatable + copyWith |
| 3 | enum class | enhanced enum |
| 4-6 | interface و fun interface و by | abstract interface class و typedef و mixin |
| 7 | abstract class | abstract class |
| 8 | open class + override | class + @override |
| 9 | constructors + properties | constructors + getters |
| 10 | companion object | static + factory |
| 11 | backing field + lazy | late final + \_private |
| 12 | sealed interface | sealed class |
| 13 | object | singleton يدوي |
| 14 | generics + out | generics (covariant ضمنيًا) |
| 15 | extensions | extension |
| 16 | context parameters | ‏مفيش مقابل |

## ‏الأجزاء 1-3: Value class و Data class و Enum

### ‏1. Value class

```kotlin
@JvmInline
value class Email(val raw: String) {
    init { require("@" in raw) { "Invalid email: $raw" } }
}
```

‏غلاف حوالين `String` له نوع خاص بيه. وقت التشغيل بيتشال ويبقى `String` عادي، فمفيش تكلفة performance.

- ‏الفايدة: مينفعش تبعت اسم مكان إيميل بالغلط، لأن `User` مستني `Email` مش `String`
- ‏الـ `init` بيتأكد إن الإيميل فيه `@` أول ما يتعمل
- ‏لازم property واحدة بس في الـ constructor
- ‏في Dart: `extension type Email(String raw)`

### ‏2. Data class + Operator overloading

```kotlin
data class Money(val amount: Double) {
    operator fun plus(other: Money) = Money(amount + other.amount)
    operator fun times(qty: Int) = Money(amount * qty)
    override fun toString() = "%.2f EGP".format(amount)
}
```

‏كلمة `data` بتديك أوتوماتيك: `equals` و `hashCode` و `toString` و `copy` و destructuring. كلهم مبنيين على الـ properties اللي في الـ primary constructor بس.

- ‏`operator fun plus` بتخليك تكتب `price1 + price2` بدل `price1.plus(price2)`
- ‏`operator fun times` بتخليك تكتب `fee * 3`
- ‏عملنا override لـ `toString` عشان يطبع `50.00 EGP` بدل `Money(amount=50.0)`
- ‏في Dart: كنا بنحتاج `Equatable` و `copyWith` بإيدنا أو بـ code generation

### ‏3. Enum class

```kotlin
enum class Role(val discount: Double) {
    CUSTOMER(0.0), VIP(0.10), ADMIN(0.20);

    fun hasDiscount() = discount > 0
}
```

‏كل قيمة في الـ enum هي object ليه property اسمها `discount`. لاحظ الـ `;` بعد آخر قيمة: لازمة لما يكون فيه functions بعدها.

- ‏الاستخدام: `Role.VIP.discount` بيرجع `0.10`
- ‏في Dart: نفس فكرة enhanced enums بالظبط

## ‏الأجزاء 4-6: Interface و fun interface و Delegation

### ‏4. Interface

```kotlin
interface Payable {
    val provider: String
    fun pay(amount: Money): Boolean
    fun receipt(amount: Money) = "$provider paid $amount"
}
```

‏الـ interface عقد: أي حاجة `Payable` لازم تعرف تدفع. فيه 3 أنواع members:

- ‏`provider` ← abstract property: الكلاس اللي بيعمل implement هو اللي بيخزّن القيمة
- ‏`pay` ← abstract function: لازم يتكتب لها تنفيذ
- ‏`receipt` ← default method: جاهزة، والكلاس ياخدها من غير ما يكتب حاجة

‏الـ interface مينفعش يشيل state، يعني مينفعش `val x = 5` جواه. في Dart لو عملت `implements` لازم تعيد كتابة `receipt`، في Kotlin لأ.

### ‏5. fun interface (SAM)

```kotlin
fun interface PriceRule {
    fun apply(price: Money): Money
}
```

‏interface فيه function واحدة بس، فكلمة `fun` قدامه بتسمح تعمله بـ lambda:

```kotlin
val tenOff = PriceRule { Money(it.amount * 0.9) }
```

‏في الملف بنستخدمه مرتين: كـ default في `Cart.total` وهو `PriceRule { it }` (مفيش خصم)، وفي `checkout` عشان خصم الـ Role. في Dart أقرب حاجة `typedef PriceRule = Money Function(Money)`.

### ‏6. Logger للـ Delegation

```kotlin
interface Logger { fun log(msg: String) }

class ConsoleLogger : Logger {
    override fun log(msg: String) = println("[LOG] $msg")
}
```

‏interface بسيط و implementation واحد. أهميته هتظهر في جزء 11 (`Cart`) وجزء 16 (`checkout`). لاحظ إن `Logger` في سطر `class ConsoleLogger : Logger` مفيش بعده `()`، وده معناه إنه interface (implement مش extend).

## ‏الأجزاء 7-8: Abstract class والوراثة

### ‏7. Abstract class + Template method

```kotlin
abstract class PaymentMethod(override val provider: String) : Payable {
    protected var attempts = 0

    final override fun pay(amount: Money): Boolean {
        attempts++
        return process(amount)
    }

    protected abstract fun process(amount: Money): Boolean
}
```

‏ده أهم جزء في الملف. الـ abstract class بيعمل implement لـ `Payable` جزئيًا وبيسيب خطوة واحدة للـ children:

- ‏`override val provider` في الـ constructor ← بينفذ الـ abstract property بتاعة الـ interface مباشرة
- ‏`protected var attempts` ← state حقيقية، الـ children بس يشوفوها. ده مستحيل في interface
- ‏`final override fun pay` ← الـ flow ثابت: زوّد المحاولات ← نفّذ ← رجّع النتيجة. كلمة `final` ضرورية لأن أي `override` مفتوح افتراضيًا
- ‏`protected abstract fun process` ← الخطوة الوحيدة اللي الـ child يكتبها

‏ده اسمه **Template Method pattern**: الأب بيتحكم في الخطوات، والابن يكمّل الناقص.

```kotlin
class VodafoneCash(private val phone: String) : PaymentMethod("Vodafone Cash") {
    override fun process(amount: Money) = phone.length == 11 && attempts <= 3
}

class Card : PaymentMethod("Visa") {
    override fun process(amount: Money) = amount.amount < 50_000
}
```

‏لاحظ `PaymentMethod("Vodafone Cash")` بالـ `()` ← ده extend وبننادي الـ constructor بتاع الأب. و`VodafoneCash` بيستخدم `attempts` لأنها `protected`.

### ‏8. Open class + Override

```kotlin
open class Product(val id: Int, val title: String, open val price: Money) {
    open fun label() = "$title - $price"
}

class DiscountedProduct(
    id: Int, title: String,
    private val original: Money,
    private val percent: Int,
) : Product(id, title, original) {

    override val price: Money
        get() = Money(original.amount * (100 - percent) / 100)

    override fun label() = "${super.label()} (-$percent%)"
}
```

- ‏الكلاسات في Kotlin **final افتراضيًا**، فلازم `open` على الكلاس وعلى أي member عايز تسمح بالـ override بتاعه. ده أكبر فرق عن Dart
- ‏`id` و `title` في `DiscountedProduct` من غير `val` ← مجرد parameters بتتبعت للأب، مش properties جديدة
- ‏`override val price` بـ getter ← property محسوبة من غير backing field، بتتحسب كل مرة
- ‏`super.label()` بتنادي نسخة الأب، وهي هتطبع السعر بعد الخصم لأن `price` متعملها override (polymorphism)
- ‏`override` كلمة إجبارية مش annotation زي `@override` في Dart

## ‏الأجزاء 9-10: كلاس User

### ‏9. Constructors و Properties

```kotlin
class User(val name: String, val email: Email, var role: Role = Role.CUSTOMER) {

    init { require(name.isNotBlank() && name.length <= MAX_NAME) }

    constructor(email: Email) : this(email.raw.substringBefore("@"), email)

    var loginCount = 0
        private set

    val isVip: Boolean
        get() = role == Role.VIP

    lateinit var token: String

    fun login(newToken: String) {
        token = newToken
        loginCount++
    }
}
```

‏**الـ constructors:**

- ‏الـ primary في سطر التعريف. `val` و `var` بيخلوا الـ parameter property في نفس الوقت
- ‏`role` ليه default value، فممكن متبعتهاش
- ‏الـ `init` بيتنفذ مع الـ primary، وبيرمي exception لو الاسم فاضي
- ‏الـ secondary لازم ينادي الـ primary بـ `: this(...)`. هنا بيطلّع الاسم من الإيميل: `ali@store.eg` ← `ali`

‏**الـ properties:**

- ‏`loginCount` بـ `private set` ← أي حد يقراها، بس الكلاس بس اللي يغيّرها. ده الـ **encapsulation**
- ‏`isVip` ← computed getter، مفيش قيمة متخزنة. زي `bool get isVip => ...` في Dart
- ‏`lateinit` ← زي `late` في Dart. لو قريته قبل ما يتحط فيه قيمة هيرمي exception

### ‏10. Companion object

```kotlin
companion object {
    const val MAX_NAME = 30
    fun guest() = User("Guest", Email("guest@store.eg"))
}
```

‏Kotlin مفيهاش `static`. الـ companion object هو البديل:

- ‏`const val MAX_NAME` ← زي `static const` في Dart، وبيتستخدم في الـ `init` فوق
- ‏`User.guest()` ← زي named constructor أو factory في Dart

## ‏الجزء 11: Cart

```kotlin
class Cart(logger: Logger) : Logger by logger {

    val items: List<Product>
        field = mutableListOf()

    val createdAt by lazy { System.currentTimeMillis() }

    fun add(p: Product) {
        items.add(p)
        log("Added ${p.title}")
    }

    fun total(rule: PriceRule = PriceRule { it }): Money =
        rule.apply(items.fold(Money(0.0)) { acc, p -> acc + p.price })
}
```

‏الكلاس ده فيه 3 مفاهيم مهمة:

### ‏Delegation بـ by

‏`: Logger by logger` معناها: الـ `Cart` هو `Logger`، بس الشغل الفعلي بيتحوّل للـ object اللي اتبعت في الـ constructor. عشان كده `add` بتنادي `log(...)` مباشرة، من غير ما نكتب الـ function دي جوه `Cart`.

‏ده البديل لـ `mixin` في Dart. الفرق إنك تقدر تبعت `FileLogger` بدل `ConsoleLogger` من غير ما تغيّر أي حاجة في `Cart`.

### ‏Explicit backing field (Kotlin 2.4)

‏الـ `items` ليها شكلين:

- ‏من بره: `List<Product>` ← read-only، محدش يقدر يعمل `cart.items.add(...)`
- ‏من جوه: `MutableList` ← الكلاس بس اللي يزوّد عليها

‏قبل 2.4 كنا بنكتبها كده (زي الـ `_items` في Dart):

```kotlin
private val _items = mutableListOf<Product>()
val items: List<Product> get() = _items
```

### ‏by lazy

‏`createdAt` مش بتتحسب غير أول مرة حد يقراها، وبعد كده بتفضل نفس القيمة. زي `late final createdAt = ...` في Dart.

### ‏total

‏`fold` بتجمع الأسعار بالـ `+` اللي عرّفناه في `Money`، وبعدين الـ `PriceRule` بيطبّق الخصم. لاحظ إن `p.price` بترجّع السعر بعد الخصم لو المنتج `DiscountedProduct` (polymorphism).

## ‏الأجزاء 12-16: Sealed و object و Generics و Extensions و Context

### ‏12. Sealed interface

```kotlin
sealed interface CheckoutState {
    data object Idle : CheckoutState
    data object Processing : CheckoutState
    data class Paid(val receipt: String) : CheckoutState
    data class Failed(val reason: String) : CheckoutState
}
```

- ‏`sealed` ← الحالات محددة، والـ `when` مش محتاج `else`
- ‏حالة من غير بيانات ← `data object`. حالة معاها بيانات ← `data class`
- ‏اخترنا `sealed interface` بدل `sealed class` لأن الحالات مش محتاجة state مشتركة

```kotlin
fun render(state: CheckoutState) = when (state) {
    CheckoutState.Idle -> "Ready"
    CheckoutState.Processing -> "Processing..."
    is CheckoutState.Paid -> "OK: ${state.receipt}"
    is CheckoutState.Failed if state.reason.contains("rejected") -> "Try another payment method"
    is CheckoutState.Failed -> "Error: ${state.reason}"
}
```

- ‏`Idle` من غير `is` لأنه object واحد، و`Paid` بـ `is` لأنه class
- ‏بعد `is` الـ compiler بيعمل smart cast، فـ `state.receipt` متاحة من غير casting
- ‏`if` بعد الـ `is` اسمها guard condition، زي `when` في Dart patterns. الترتيب مهم: الـ guard لازم قبل الحالة العامة

### ‏13. Object (Singleton)

```kotlin
object StoreConfig {
    const val NAME = "Souq Samy"
    val deliveryFee = Money(50.0)
}
```

‏singleton في سطر واحد، بيتعمل مرة واحدة أول ما يتستخدم. الاستخدام: `StoreConfig.deliveryFee`. في Dart كنا بنعمل private constructor + static instance بإيدنا.

### ‏14. Generics + Variance

```kotlin
interface Repository<out T> {
    fun getAll(): List<T>
}
```

‏`out` معناها إن الـ interface بيطلّع `T` بس (مبياخدهاش كـ parameter). النتيجة: `Repository<Product>` ينفع يتحط مكان `Repository<Any>`.

‏في Dart الـ generics covariant ضمنيًا فمش بتكتب حاجة. في Kotlin لازم تقولها صراحة (`out` للإخراج، `in` للإدخال)، وده أأمن.

### ‏15. Extension functions

```kotlin
fun Money.withVat() = Money(amount * 1.14)
fun User.greeting() = if (isVip) "Welcome back, VIP $name" else "Hi $name"
```

‏بنضيف functions لكلاس من غير ما نعدّله. جواها بنوصل للـ public members بس (زي `amount` و `isVip`)، مش الـ private. نفس فكرة `extension` في Dart بس بسطر واحد.

### ‏16. Context parameters (Kotlin 2.4)

```kotlin
context(logger: Logger)
fun checkout(cart: Cart, user: User, method: Payable): CheckoutState {
    logger.log("Checkout started for ${user.name}")
    val roleDiscount = PriceRule { Money(it.amount * (1 - user.role.discount)) }
    val total = cart.total(roleDiscount).withVat() + StoreConfig.deliveryFee
    return if (method.pay(total)) CheckoutState.Paid(method.receipt(total))
    else CheckoutState.Failed("Payment rejected")
}
```

‏`context(logger: Logger)` معناها إن الـ function محتاجة `Logger` موجود في الـ scope، من غير ما تبعته في كل call. بنوفره بـ `with(logger) { ... }` في `main`.

‏الـ function دي بتجمع كتير: SAM lambda و extension (`withVat`) و operator (`+`) و object (`StoreConfig`). وأهم حاجة إن `method` نوعه `Payable`، فينفع تبعتلها أي طريقة دفع ← ده الـ **polymorphism**.

## ‏main: ربط كل حاجة

### ‏Object expressions

```kotlin
val cashOnDelivery = object : Payable {
    override val provider = "Cash on Delivery"
    override fun pay(amount: Money) = true
}

val promoWallet = object : PaymentMethod("Promo Wallet") {
    override fun process(amount: Money) = attempts == 1
}
```

‏object من غير اسم بنعمله في مكانه ونستخدمه مرة واحدة. Dart مفيهاش الحاجة دي.

- ‏الأول بيعمل implement لـ interface ← من غير `()`
- ‏التاني بيعمل extend لـ abstract class ← بـ `("Promo Wallet")` عشان الـ constructor

### ‏Polymorphism

```kotlin
val methods: List<Payable> = listOf(VodafoneCash("01012345678"), Card(), cashOnDelivery, promoWallet)

with(logger) {
    for (method in methods) {
        println(render(checkout(cart, samy, method)))
    }
}
```

‏ليستة واحدة نوعها `Payable`، فيها 4 أنواع مختلفة: class عادي، class تاني، object expression لـ interface، و object expression لـ abstract class. الـ `checkout` مش عارفة ولا محتاجة تعرف هي بتتعامل مع أنهي واحدة.

‏`with(logger)` هو اللي بيوفّر الـ context parameter لـ `checkout`.

### ‏باقي main

- ‏`User("Samy", ...)` و `User(Email(...))` و `User.guest()` ← الـ 3 طرق لإنشاء user
- ‏`val anyRepo: Repository<Any> = repo` ← شغالة بسبب `out`
- ‏`repo.getAll().forEach(cart::add)` ← `::add` اسمها function reference، زي `forEach(cart.add)` في Dart
- ‏`fee.copy(amount = ...)` و `val (amount) = doubleFee` ← copy و destructuring من الـ data class

### ‏المخرجات المتوقعة

```text
Welcome back, VIP Samy
ali, logins: 0, guest: Guest
Items in repo: 2
[LOG] Added Headphones
[LOG] Added Keyboard
Headphones - 1500.00 EGP
Keyboard - 1500.00 EGP (-25%)
Fee x3 = 150.00 EGP, doubled amount = 100.0
VIP has discount: true
[LOG] Checkout started for Samy
OK: Vodafone Cash paid 3128.00 EGP
[LOG] Checkout started for Samy
OK: Visa paid 3128.00 EGP
[LOG] Checkout started for Samy
OK: Cash on Delivery paid 3128.00 EGP
[LOG] Checkout started for Samy
OK: Promo Wallet paid 3128.00 EGP
Ready
```

‏حساب الـ total: 1500 + 1500 = 3000 ← خصم VIP 10% = 2700 ← VAT 14% = 3078 ← شحن 50 = 3128.

## ‏أسئلة وتمارين للطلبة

### ‏أسئلة سريعة

1. ‏ليه `PaymentMethod` اتعمل abstract class مش interface؟ ← لأن فيه state (`attempts`) و `protected` و flow ثابت
2. ‏لو شلنا `final` من `pay` إيه اللي هيحصل؟ ← أي child يقدر يعمل override ويكسر الـ flow
3. ‏ليه `Idle` اتعمل `data object` و `Paid` اتعمل `data class`؟ ← الأول ملوش بيانات، التاني معاه receipt
4. ‏ليه `cart.items.add(...)` من `main` هيدي error؟ ← لأنها من بره `List` مش `MutableList`
5. ‏إيه الفرق بين `object : Payable` و `object StoreConfig`؟ ← الأول نسخة جديدة مجهولة، التاني singleton باسم
6. ‏ليه `Product` محتاج `open`؟ ← لأن الكلاسات final افتراضيًا

### ‏تمارين

- [ ] ‏ضيف طريقة دفع `InstaPay` تورث من `PaymentMethod` وترفض أي مبلغ فوق 20,000
- [ ] ‏ضيف حالة `Cancelled` لـ `CheckoutState` وشوف الـ error اللي هيظهر في `render`، وبعدين صلّحه
- [ ] ‏اعمل `FileLogger` وابعته لـ `Cart` من غير ما تغيّر أي سطر في `Cart`
- [ ] ‏ضيف `operator fun minus` لـ `Money` واستخدمه في كوبون خصم ثابت
- [ ] ‏ضيف في `User.companion` دالة `admin(name: String)` ترجّع user بـ `Role.ADMIN`
- [ ] ‏اكتب extension اسمها `Cart.isEmpty()`
- [ ] ‏تحدي: اعمل `Repository<in T>` فيه `fun save(item: T)` واشرح ليه `Repository<Any>` ينفع يتحط مكان `Repository<Product>` مش العكس
