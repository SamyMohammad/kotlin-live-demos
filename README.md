<div dir="rtl">

# ⁦Kotlin Live Demos⁩

كود السيشن الأولى في دبلومة الأندرويد. كل ملف عن topic واحد، وفيه ⁦`fun main()`⁩ لوحده، وماشي بنفس ترتيب السلايدز.

## إزاي تفتحه

1. افتح ⁦IntelliJ IDEA⁩ (الـ ⁦Community⁩ مجانية) ← ⁦File⁩ ← ⁦Open⁩ ← اختار فولدر ⁦`kotlin-live-demos`⁩.
2. استنى الـ ⁦Gradle sync⁩ يخلص (أول مرة بس، ومحتاج إنترنت).
3. افتح أي ملف من ⁦`src/main/kotlin`⁩ واضغط السهم الأخضر جنب ⁦`fun main()`⁩.

المشروع على ⁦Kotlin 2.2.20⁩، وأي ⁦JDK⁩ من ⁦17⁩ وطالع شغال.

## الملفات

</div>

<div dir="ltr">

| الملف | الموضوع | السلايدز |
| --- | --- | --- |
| `T01_HelloWorld.kt` | Hello world والـ JVM | 10–17 |
| `T02_Variables.kt` | val و var و const | 20، 30 |
| `T03_Types.kt` | الأنواع والأرقام والـ casts | 21–22 |
| `T04_Strings.kt` | Templates و raw strings | 23–24 |
| `T05_NullSafety.kt` | Null safety و lateinit و Java | 25–30 |
| `T06_Functions.kt` | Functions و named args و varargs | 32–35، 40 |
| `T07_ControlFlow.kt` | if و when و loops و ranges | 36–40 |
| `T08_Collections.kt` | List و Set و Map والعمليات | 42–43، 51 |
| `T09_Lambdas.kt` | Lambdas و function types | 44–46، 50–51 |
| `T10_LambdaWithReceiver.kt` | Lambda with receiver و DSL | 47 |
| `T11_ComposeStyleDsl.kt` | Compose صغير بـ Kotlin عادي | 45، 48، 77 |
| `T12_Classes.kt` | Classes و constructors و init | 53–56، 58 |
| `T13_Properties.kt` | Getters و setters و lazy | 27، 57 |
| `T14_Inheritance.kt` | Inheritance و abstract و interfaces | 59–61 |
| `T15_DataClasses.kt` | Data classes و copy | 62–64، 71 |
| `T16_EnumAndSealed.kt` | Enum و sealed | 65–67 |
| `T17_Objects.kt` | object و companion و object expressions | 68–69 |
| `T18_Delegation.kt` | Delegation بـ by | 70–71 |
| `T19_Extensions.kt` | Extensions | 73، 78 |
| `T20_ScopeFunctions.kt` | let و apply و also و run و with | 74–76، 78 |
| `T21_Generics.kt` | Generics (بونص) | — |
| `T22_Exceptions.kt` | Exceptions (بونص) | — |
| `T23_OperatorsAndMore.kt` | Operators و infix و value classes (بونص) | — |

</div>

<div dir="rtl">

## إزاي تستخدمه لايف

- **سطور ⁦`// ERROR:`⁩** شيل الـ ⁦`//`⁩ قدام الطلبة، وهيظهر الـ compile error اللي مكتوب في الكومنت بالظبط. كل السطور دي اتجربت.
- **سطور ⁦`CRASH`⁩** في الـ output: دي أخطاء runtime محطوطة جوه ⁦`tryIt { }`⁩ عشان الملف يكمل لآخره. الطلبة يشوفوا اسم الـ exception والرسالة من غير ما البرنامج يقف.
- **أقسام ⁦`WATCH OUT`⁩:** هي نفس الـ edge cases اللي في سلايدز Watch out.
- **⁦Tools ← Kotlin ← Show Kotlin Bytecode ← Decompile⁩:** جربها على ⁦`T01`⁩ عشان يشوفوا ⁦`T01_HelloWorldKt`⁩، وعلى ⁦`T15`⁩ عشان يشوفوا الـ ⁦`equals`⁩ والـ ⁦`copy`⁩ اللي الكومبايلر كتبها.
- **⁦View ← Appearance ← Enter Presentation Mode⁩** عشان الخط يبان على البروجكتور.

## للطلبة على Kotlin Playground

كل ملف قائم بذاته، فممكن ينسخوه كله في ⁦play.kotlinlang.org⁩ ويشغلوه. الاستثناء الوحيد هو القسم 11 في ⁦`T05`⁩، لأنه محتاج ملف الـ Java اللي في ⁦`src/main/java`⁩.

## مش موجود هنا

الـ ⁦Coroutines⁩ والـ ⁦Flow⁩ ليهم سيشن لوحدهم.

</div>