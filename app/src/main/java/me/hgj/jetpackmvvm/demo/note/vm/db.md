# 数据绑定库 https://developer.android.google.cn/topic/libraries/data-binding/expressions  

# 1、数据绑定库
一种支持库，借助该库，您可以使用声明性格式（而非程序化地）将布局中的界面组件绑定到应用中的数据源。
使用数据绑定库将文本直接分配到微件。赋值表达式中 @{} 语法的使用：
<TextView android:text="@{viewmodel.userName}" />

借助布局文件中的绑定组件，您可以移除 Activity 中的许多界面框架调用，使其维护起来更简单、方便。还可以提高应用性能，并且有
助于防止内存泄漏以及避免发生 Null 指针异常。

在许多情况下，视图绑定可简化实现，提高性能，提供与数据绑定相同的好处。如果您使用数据绑定的主要目的是取代 findViewById() 调用，
请考虑改用视图绑定。

# 布局和绑定表达式--数据绑定库会自动生成将布局中的视图与您的数据对象绑定所需的类。该库提供了可在布局中使用的导入、变量和包含等功能。

android {
    ...
    dataBinding {
        enabled = true
    }
}

Layout Editor 中的 Preview 窗格显示数据绑定表达式的默认值（如果提供）。
<TextView android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="@{user.firstName, default=my_default}"/> //default

# 2、 布局和绑定表达式
借助表达式语言，您可以编写表达式来处理视图分派的事件。数据绑定库会自动生成将布局中的视图与您的数据对象绑定所需的类。
数据绑定布局文件略有不同，以根标记 layout 开头，后跟 data 元素和 view 根元素。此视图元素是非绑定布局文件中的根。

<?xml version="1.0" encoding="utf-8"?>
    <layout xmlns:android="http://schemas.android.com/apk/res/android">
       <data>
           <variable name="user" type="com.example.User"/>
       </data>
       <LinearLayout
           android:orientation="vertical"
           android:layout_width="match_parent"
           android:layout_height="match_parent">
           <TextView android:layout_width="wrap_content"
               android:layout_height="wrap_content"
               android:text="@{user.firstName}"/>
           <TextView android:layout_width="wrap_content"
               android:layout_height="wrap_content"
               android:text="@{user.lastName}"/>
       </LinearLayout>
    </layout>


系统会为每个布局文件生成一个绑定类。默认情况下，类名称基于布局文件的名称，末尾添加 Binding 后缀。

override fun onCreate(savedInstanceState: Bundle?) {
super.onCreate(savedInstanceState)

        val binding: ActivityMainBinding = DataBindingUtil.setContentView(
                this, R.layout.activity_main)

        binding.user = User("Test", "User")
    }


# 表达式语言支持各种运算表达式：
算术运算符 + - / * %
字符串连接运算符 +
逻辑运算符 && ||
二元运算符 & | ^
一元运算符 + - ! ~
移位运算符 >> >>> <<
比较运算符 == > < >= <=（请注意，< 需要转义为 &lt;）
instanceof
分组运算符 ()
字面量运算符 - 字符、字符串、数字、null
类型转换
方法调用
字段访问
数组访问 []
三元运算符 ?:

Null 合并运算符
android:text="@{user.displayName ?? user.lastName}"

属性引用
表达式可以使用以下格式在类中引用属性，这对于字段、getter 和 ObservableField 对象都一样：
android:text="@{user.lastName}"

视图引用
表达式可以通过以下语法按 ID 引用布局中的其他视图：
android:text="@{exampleText.text}"

集合

字符串字面量
您可以使用单引号括住特性值，这样就可以在表达式中使用双引号，如以下示例所示：
android:text='@{map["firstName"]}'

资源
表达式可以使用以下语法引用应用资源：
android:padding="@{large? @dimen/largePadding : @dimen/smallPadding}"
您可以通过提供参数来评估格式字符串和复数形式：
android:text="@{@string/nameFormat(firstName, lastName)}"
android:text="@{@plurals/banana(bananaCount)}"

# 方法引用 监听器绑定 
方法引用：在表达式中，引用符合监听器方法签名的方法。当表达式求值结果为方法引用时，数据绑定会将方法引用和所有者对象封装到
监听器中，并在目标视图上设置该监听器。
方法引用实际监听器实现是在绑定数据时创建的。
@{click::login} //传参数只有view

监听器绑定：这些是在事件发生时进行求值的 lambda 表达式。数据绑定始终会创建一个要在视图上设置的监听器。事件被分派后，监
听器会对 lambda 表达式进行求值。
@{(view)->myViewModel.onClickSecondButton(view,user)} //除了view，还有其他参数。
监听器绑定，就是用了 lambda 表达式。事件被触发后，监听器会对 lambda 表达式进行求值。

# 使用可观察的数据对象
# 可观察性是指一个对象将其数据变化告知其他对象的能力。通过数据绑定库，您可以让对象、字段或集合变为可观察。
任何 plain-old 对象都可用于数据绑定，但修改对象不会自动使界面更新。
通过数据绑定，数据对象可在其数据发生更改时通知其他对象，即监听器。可观察类有三种不同类型：对象、字段和集合。
当其中一个可观察数据对象绑定到界面并且该数据对象的属性发生更改时，界面会自动更新。

# 可观察字段
在创建实现 Observable 接口的类时要完成一些操作，但如果您的类只有少数几个属性，这样操作的意义不大。
在这种情况下，您可以使用通用 Observable 类和以下特定于基元的类，将字段设为可观察字段：

ObservableBoolean
ObservableByte
ObservableChar
ObservableShort
ObservableInt
ObservableLong
ObservableFloat
ObservableDouble
ObservableParcelable

ObservableInt ：BaseObservableField ： BaseObservable
# 实际上这些，可观察字段也是BaseObservable的子类。

可观察字段是具有单个字段的自包含可观察对象。原语版本避免在访问操作期间封箱和开箱。
# 如需使用此机制，请采用 Java 编程语言创建 public final 属性，或在 Kotlin 中创建只读属性，如以下示例所示：

# 可观察集合
某些应用使用动态结构来保存数据。可观察集合允许使用键访问这些结构。
ObservableArrayMap、ObservableArrayList。

# 可观察对象 实现 Observable 接口
实现 Observable 接口的类允许注册监听器，以便它们接收有关可观察对象的属性更改的通知。
Observable 接口具有添加和移除监听器的机制，但何时发送通知必须由您决定。为便于开发，数据绑定库提供了用于实现监听器注册机制的
BaseObservable 类。实现 BaseObservable 的数据类负责在属性更改时发出通知。具体操作过程是向 getter 分配 Bindable 注释，
然后在 setter 中调用 notifyPropertyChanged() 方法，如以下示例所示：

class User : BaseObservable() {

        @get:Bindable
        var firstName: String = ""
            set(value) {
                field = value
                notifyPropertyChanged(BR.firstName)
            }

        @get:Bindable
        var lastName: String = ""
            set(value) {
                field = value
                notifyPropertyChanged(BR.lastName)
            }
    }

# ObservableField 实际上也是实现Observable 接口。


# 绑定适配器 为控件提供自定义属性的 BindingAdapter！

    <ImageView
        android:layout_width="100dp"
        android:layout_height="100dp"
        app:imageUrl="@{user.avatar}"
        app:placeHolder="@{@drawable/dog}"/>

@BindingAdapter({"app:imageUrl", "app:placeHolder"})
public static void loadImageFromUri(ImageView imageView, String imageUri, Drawable placeHolder){
    Glide.with(imageView.getContext())
    .load(imageUri)
    .placeholder(placeHolder)
    .into(imageView);
}

在随便某个类中添加 public static 方法（方法名随意），增加注解@BindingAdapter，并且注明对应的"app:imageUrl", "app:placeHolder"，
然后方法参数是 控件类型 及 这两个属性对应 值。 然后在方法中写逻辑即可，这里就是使用Glide加载用户头像，其中placeHolder是占位图。


# 使用 LiveData 将数据变化通知给界面
您可以使用 LiveData 对象作为数据绑定来源，自动将数据变化通知给界面。如需详细了解此架构组件，请参阅 LiveData 概览。
与实现 Observable 的对象（例如可观察字段）不同，LiveData 对象了解订阅数据更改的观察器的生命周期。了解这一点有许多好处，
具体说明请参阅使用 LiveData 的优势。在 Android Studio 版本 3.1 及更高版本中，您可以在数据绑定代码中将可观察字段替换为 LiveData 对象。

# DataBinding 结合 LiveData 使用步骤很简单：
要使用LiveData对象作为数据绑定来源，需要设置LifecycleOwner
xml中 定义变量 ViewModel， 并使用 ViewModel 中的 LiveData 绑定对应控件
binding设置变量ViewModel

        //结合DataBinding使用的ViewModel
        //1. 要使用LiveData对象作为数据绑定来源，需要设置LifecycleOwner
        binding.setLifecycleOwner(this);

        ViewModelProvider viewModelProvider = new ViewModelProvider(this);
        mUserViewModel = viewModelProvider.get(UserViewModel.class);

        //3. 设置变量ViewModel
        binding.setVm(mUserViewModel);

<!-- 2. 定义ViewModel 并绑定-->
    <variable
        name="vm"
         type="com.hfy.demo01.module.jetpack.databinding.UserViewModel" />

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="@{vm.userLiveData.name}"/>
        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="@{vm.userLiveData.level}"/>

# 双向数据绑定
<CheckBox
    android:id="@+id/rememberMeCheckBox"
    android:checked="@={viewmodel.rememberMe}" />

@={} 表示法（其中重要的是包含“=”符号）可接收属性的数据更改并同时监听用户更新。


# 使用自定义特性的双向数据绑定
该平台为最常见的双向特性和更改监听器提供了双向数据绑定实现，您可以将其用作应用的一部分。如果您希望结合使用双向数据绑定和自定义特性，
则需要使用 @InverseBindingAdapter 和 @InverseBindingMethod 注释。


