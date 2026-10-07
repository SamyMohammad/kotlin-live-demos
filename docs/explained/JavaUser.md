<div dir="rtl">

# شرح JavaUser: الـ Java interop

**الملف:** `src/main/java/javainterop/JavaUser.java`، والملف اللي بيستخدمه هو `T05_NullSafety.kt` في القسم 11.

## الخلاصة

ده ملف Java عادي جدًا موجود في نفس المشروع مع ملفات Kotlin. وجوده بيوضح إن **Java و Kotlin بيعيشوا مع بعض في نفس المشروع** وبينادوا بعض مباشرة. في Flutter، لو عايز تكلم كود native كنت محتاج platform channel أو FFI. هنا مفيش أي حاجة في النص.

## الكود

</div>

<div dir="ltr">

```java
public class JavaUser {
    private final String name;

    public JavaUser(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
```

</div>

<div dir="rtl">

## الشرح

### ده بيبان إزاي في Kotlin؟

لو كتبت نفس الـ class دي بـ Kotlin، هتبقى سطر واحد:

<div dir="ltr">

```kotlin
class JavaUser(val name: String?)
```

</div>

السطر ده بيعمل نفس الحاجة: private field وconstructor وgetter. وده سبب كبير إن المطورين سابوا Java لـ Kotlin (التفاصيل في T12).

### الـ getters بتتحول لـ properties 🆕 جديد عليك

في Kotlin بتكتب `javaUser.name` مش `javaUser.getName()`. أي method في Java اسمها `getX()` بتتحول لـ property اسمها `x`، و `setX()` بتخليها `var`، و `isX()` للـ Boolean بتفضل `isX`. عشان كده هتكتب في Android `view.visibility = View.GONE` بدل `view.setVisibility(View.GONE)`.

### ليه الملف ده خطير؟

الـ field `name` ممكن يبقى `null`، وJava مش بتقول ده في النوع. فلما Kotlin تقرأه، بيبقى **platform type** (`String!`):

- **لو عاملته `String`:** هتاخد crash لو طلع null.
- **لو عاملته `String?`:** هتبقى في أمان.

الحل في Java نفسها إنك تضيف `@Nullable` من `androidx.annotation` أو `org.jetbrains.annotations`، وساعتها Kotlin هتشوف النوع `String?` على طول.

### ملحوظة على مكان الملف

الـ Kotlin Gradle plugin بيـcompile أي ملف في `src/main/java` و `src/main/kotlin` مع بعض، فالفولدرين بيشوفوا بعض. في مشاريع Android حقيقية هتلاقي الاتنين مخلوطين، خصوصًا في المشاريع القديمة اللي بتتنقل من Java لـ Kotlin تدريجيًا.

## الخلاصة في 3 سطور

1. ملفات Java و Kotlin بتنادي بعض مباشرة من غير أي bridge.
2. الـ Java getters بتظهر في Kotlin كـ properties.
3. أي قيمة من Java ممكن تكون null من غير ما الكومبايلر يعرف، فحدد نوعها nullable أو ضيف `@Nullable`.

</div>
