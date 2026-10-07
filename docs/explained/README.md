<div dir="rtl">

# شرح Kotlin لمطور Flutter

الفولدر ده بيشرح كل ملف في `src/main/kotlin` بالتفصيل، وكل ملف شرح جنب الملف بتاعه بنفس الاسم. الشرح مكتوب لحد عارف Dart و Flutter كويس، فالحاجات المشابهة لـ Dart بتاخد سطر أو اتنين، والحاجات الجديدة بتاخد شرح مفصّل.

## إزاي تقرا الملفات

- **علامة 🆕:** حاجة مالهاش مقابل في Dart، أو مختلفة عنها بشكل مهم. دي اللي تركز عليها.
- **كل ملف بيمشي بنفس ترتيب أقسام الكود** (`section("1. ...")`)، فتقدر تفتح الاتنين جنب بعض.
- **مقاطع الكود:** مأخوذة من الملف الأصلي، وأحيانًا مختصرة عشان تركز على النقطة. لو عايز السياق الكامل، ارجع للملف.
- **قسم "عادات Flutter اللي هتوقعك":** الحاجات اللي إيدك هتكتبها بطريقة Dart وتطلع غلط في Kotlin.

## الفهرس

</div>

<div dir="ltr">

| الملف | الموضوع | أهم جديد عليك |
| --- | --- | --- |
| [T01](T01_HelloWorld.md) | Hello world والـ JVM | `Char` vs `String`، الـ `FileKt` class |
| [T02](T02_Variables.md) | val و var و const | `const val` محدودة، مفيش const objects |
| [T03](T03_Types.md) | الأنواع والأرقام | `Int` 32-bit، `7 / 2 = 3`، مفيش تحويل تلقائي، `as?` |
| [T04](T04_Strings.md) | الـ Strings | `"""` + `trimIndent()`، `isBlank()` |
| [T05](T05_NullSafety.md) | Null safety | `?: return`، `?.let`، platform types من Java |
| [T06](T06_Functions.md) | Functions | أي param ممكن named، `vararg`، `Unit` |
| [T07](T07_ControlFlow.md) | Control flow | `if` expression، `when`، ranges |
| [T08](T08_Collections.md) | Collections | `List` vs `MutableList`، `map` eager، `Sequence` |
| [T09](T09_Lambdas.md) | Lambdas | trailing lambda، `it`، `return` في `forEach` |
| [T10](T10_LambdaWithReceiver.md) | Lambda with receiver | `T.() -> Unit`، DSLs |
| [T11](T11_ComposeStyleDsl.md) | Compose صغير | `Modifier`، scopes، `weight` |
| [T12](T12_Classes.md) | Classes | primary constructor، `init`، `internal` |
| [T13](T13_Properties.md) | Properties | `field`، `private set`، `by lazy` |
| [T14](T14_Inheritance.md) | Inheritance | `final` افتراضي، `open`، `super<X>` |
| [T15](T15_DataClasses.md) | Data classes | `copy`، destructuring، UI state |
| [T16](T16_EnumAndSealed.md) | Enum و Sealed | `entries`، `data object`، متستخدمش `else` |
| [T17](T17_Objects.md) | object و companion | مفيش `static`، anonymous objects، SAM |
| [T18](T18_Delegation.md) | Delegation | `by`، إزاي `mutableStateOf` شغالة |
| [T19](T19_Extensions.md) | Extensions | nullable receiver، extension على companion |
| [T20](T20_ScopeFunctions.md) | Scope functions | `let` / `run` / `with` / `apply` / `also` |
| [T21](T21_Generics.md) | Generics | type erasure + `reified`، `out` / `in` |
| [T22](T22_Exceptions.md) | Exceptions | `try` expression، `require` / `check`، `runCatching` |
| [T23](T23_OperatorsAndMore.md) | Operators و value classes | `infix`، `invoke`، `value class` |
| [JavaUser](JavaUser.md) | Java interop | getters كـ properties، platform types |

</div>

<div dir="rtl">

## أهم 10 حاجات جديدة عليك كمطور Flutter

لو مش هتقرا غير ده، اقرا ده:

1. **القسمة:** `7 / 2` بتساوي `3` مش `3.5` (في T03).
2. **مفيش ternary:** `if` و `when` و `try` كلهم expressions بترجع قيمة (في T07 و T22).
3. **الـ Elvis مع return:** `val x = y ?: return` بتعمل null check وتعيين في سطر واحد (في T05).
4. **الـ trailing lambda:** آخر parameter لو lambda بيطلع بره الأقواس، وده سر شكل Compose (في T09).
5. **`return` جوه `forEach`:** بيخرج من الـ function كلها، و `return@forEach` هو الـ skip (في T09).
6. **الـ lambda with receiver:** `T.() -> Unit` هي اللي ورا `apply` و `Row { }` و `Column { }` (في T10 و T11).
7. **الـ classes مقفولة افتراضيًا:** لازم `open` للوراثة، و `override` إجبارية (في T14).
8. **الـ `data class`:** بتديك `==` و `copy` و `toString` من غير freezed (في T15).
9. **كلمة `by`:** delegation للـ classes والـ properties، وده اللي ورا `by mutableStateOf` (في T18).
10. **الـ scope functions:** خمس functions هتشوفهم في كل سطر Kotlin (في T20).

## اللي هيبقى مألوف ليك

الحاجات دي تقريبًا زي Dart، فمتقلقش منها: null safety (`?` و `?.` و `!!`)، والـ string templates، والـ smart casts (type promotion)، والـ sealed classes مع exhaustive `when`، والـ extensions، والـ enhanced enums، والـ closures.

</div>
