## 郭霖-Jetpack新成员，一篇文章带你玩转Hilt和依赖注入 https://juejin.cn/post/6902009428633698312

#1、依赖注入
（什么是依赖呢？）首先，Hilt是一个依赖注入框架。依赖就是一个对象的功能依赖于其他对象去实现。就比如我们要上网，那我们就
依赖于手机或者电脑， 而在项目中，ViewModel想要获取数据就依赖于数据仓库Repository。我们依赖于某个东西的功能去实现自己
的需求，这就是依赖。

（如何简单实现注入？）而想要使用某个对象的功能，通常是直接new一个对象出来然后使用它的功能即可。但是这样的话，当依赖的对象
很多的话，会导致类本身非常臃肿。因为要保持对每个依赖对象的创建和维护，而我们仅仅是想要使用它的功能而已，对于其他的并不关心。
因此可以提供一个方法setXXX，这样类本身并不去创建维护对象，而是交给外部去管理并传递进来，这就是注入。

（需要一个容器去管理？）将依赖对象的创建交给了外部去传递进来，那么这个外部又应该是谁呢？发现这个过程交给谁都不太合适，因此就
单独创建一个容器去创 建管理，这个容器就是依赖注入框架，也就是本章说的Hilt。

Hilt 是 Android 的依赖项注入库，可减少在项目中执行手动依赖项注入的样板代码,hilt就是对dagger2的进一步封装。
Hilt 通过为项目中的每个 Android 类提供容器并自动管理其生命周期，提供了一种在应用中使用 DI（依赖项注入）的标准方法。
Hilt 在热门 DI 库 Dagger 的基础上构建而成，因而能够受益于 Dagger 的编译时正确性、运行时性能、可伸缩性和 Android Studio 支持。

依赖项注入会为您的应用提供以下优势：
重用类以及分离依赖项：更容易换掉依赖项的实现。由于控制反转，代码重用得以改进，并且类不再控制其依赖项的创建方式，而是支持任何配置。
易于重构：依赖项成为 API Surface 的可验证部分，因此可以在创建对象时或编译时进行检查，而不是作为实现详情隐藏。
易于测试：类不管理其依赖项，因此在测试时，您可以传入不同的实现以测试所有不同用例。


#（1）为什么要使用依赖注入？
依赖注入的英文名是 Dependency Injection，简称 DI。
耦合度过高可能会是你的项目中一个比较严重的隐患，它会让你的项目到了后期变得越来越难以维护。
为了让大家更容易理解，这里我准备通过一个具体的例子来讲述一下。

（2）依赖注入框架的作用是什么？
就是满足解耦下的自动注入。

# Android常用的依赖注入框架 Dagger -> Dagger2 -> Hilt
接下来我们聊一聊 Android 有哪些常用的依赖注入框架。
在很早的时候，绝大部分的 Android 开发者都是没有使用依赖注入框架这种意识的。
大名鼎鼎的 Square 公司在 2012 年推出了至今仍然知名度极高的开源依赖注入框架：Dagger。

Dagger 的依赖注入理念虽然非常先进，但是却存在一个问题，它是基于 Java 反射去实现的，这就导致了两个潜在的隐患。
第一，我们都知道反射是比较耗时的，所以用这种方式会降低程序的运行效率。当然这个问题并不大，因为现在的程序中到处都在用反射。
第二，依赖注入框架的用法总体来说是非常有难度的，除非你能相当熟练地使用它，否则很难一次性编写正确。

而基于反射实现的依赖注入功能，使得在编译期我们无法得知依赖注入的用法到底对不对，只能在运行时通过程序有没有崩溃来判断。
这样测试的效率就很低，而且容易将一些 bug 隐藏得很深。
接下来就到了最有意思的地方，我们现在都知道 Dagger 的实现方式存在问题，那么 Dagger2 自然是要去解决这些问题的。但是 Dagger2 
并不是由 Square 开发的，而是由 Google 开发的。

大概是 Google Fork 了一份 Dagger 的源码，然后在此基础上进行修改，并发布了 Dagger2 版本。Square 看到了之后，认为 Google
的这个版本做得非常好，自己没有必要再重做一遍，也没有必要继续维护 Dagger1 了，所以就发布了这样一条声明：

那么 Dagger2 和 Dagger1 不同的地方在哪里呢？最重要的不同点在于，实现方式完全发生了变化。刚才我们已经知道，Dagger1 是
基于 Java 反射实现的，并且列举了它的一些弊端。而 Google 开发的 Dagger2 是基于 Java 注解实现的，这样就把反射的那些弊端全部解决了。

通过注解，Dagger2 会在编译时期自动生成用于依赖注入的代码，所以不会增加任何运行耗时。另外，Dagger2 会在编译时期检查开发
者的依赖注入用法是否正确，如果不正确的话则会直接编译失败，这样就能将问题尽可能早地抛出。也就是说，只要你的项目正常编译通过，
基本也就说明你的依赖注入用法没什么问题了。

事实上，Hilt 和 Dagger2 有着千丝万缕的关系。Hilt 就是 Android 团队联系了 Dagger2 团队，一起开发出来的一个专门面向 Android
的依赖注入框架。相比于 Dagger2，Hilt 最明显的特征就是：1. 简单。2. 提供了 Android 专属的 API。


# https://developer.android.google.cn/training/dependency-injection/hilt-android
# Hilt 应用类 @HiltAndroidApp
所有使用 Hilt 的应用都必须包含一个带有 @HiltAndroidApp 注解的 Application 类。
@HiltAndroidApp 会触发 Hilt 的代码生成操作，生成的代码包括应用的一个基类，该基类充当应用级依赖项容器。

@HiltAndroidApp
class ExampleApplication : Application() { ... }

生成的这一 Hilt 组件会附加到 Application 对象的生命周期，并为其提供依赖项。此外，它也是应用的父组件，这意味着，其他组件可以访问它提供的依赖项。


# 将依赖项注入 Android 类 @AndroidEntryPoint
在 Application 类中设置了 Hilt 且有了应用级组件后，Hilt 可以为带有 @AndroidEntryPoint 注解的其他 Android 类提供依赖项：

@AndroidEntryPoint
class ExampleActivity : AppCompatActivity() { ... }

Hilt 目前支持以下 Android 类：

Application（通过使用 @HiltAndroidApp）
ViewModel（通过使用 @HiltViewModel）
Activity
Fragment
View
Service
BroadcastReceiver

# 如果您使用 @AndroidEntryPoint 为某个 Android 类添加注解，则还必须为依赖于该类的 Android 类添加注解。例如，如果您为
# 某个 fragment 添加注解，则还必须为使用该 fragment 的所有 activity 添加注解。

# 注意：在 Hilt 对 Android 类的支持方面适用以下几项例外情况：
Hilt 仅支持扩展 ComponentActivity 的 activity，如 AppCompatActivity。
Hilt 仅支持扩展 androidx.Fragment 的 Fragment。
Hilt 不支持保留的 fragment。

@AndroidEntryPoint 会为项目中的每个 Android 类生成一个单独的 Hilt 组件。这些组件可以从它们各自的父类接收依赖项，如组件层次结构中所述。

# 如需从组件获取依赖项，请使用 @Inject 注解执行字段注入：

@AndroidEntryPoint
class ExampleActivity : AppCompatActivity() {

@Inject lateinit var analytics: AnalyticsAdapter
...

}

注意：由 Hilt 注入的字段不能为私有字段。尝试使用 Hilt 注入私有字段会导致编译错误。

Hilt 注入的类可以有同样使用注入的其他基类。如果这些类是抽象类，则它们不需要 @AndroidEntryPoint 注解。


# 定义 Hilt 绑定
为了执行字段注入，Hilt 需要知道如何从相应组件提供必要依赖项的实例。“绑定”包含将某个类型的实例作为依赖项提供所需的信息。

向 Hilt 提供绑定信息的一种方法是构造函数注入。在某个类的构造函数中使用 @Inject 注解，以告知 Hilt 如何提供该类的实例：

class AnalyticsAdapter @Inject constructor(
private val service: AnalyticsService
) { ... }

在一个类的代码中，带有注解的构造函数的参数即是该类的依赖项。在本例中，AnalyticsService 是 AnalyticsAdapter 的一个依赖项。
因此，Hilt 还必须知道如何提供 AnalyticsService 的实例。

注意：在构建时，Hilt 会为 Android 类生成 Dagger 组件。然后，Dagger 会走查您的代码，并执行以下步骤：
构建并验证依赖关系图，确保没有未满足的依赖关系且没有依赖循环。
生成它在运行时用来创建实际对象及其依赖项的类。

# Hilt 模块
有时，类型不能通过构造函数注入。发生这种情况可能有多种原因。例如，您不能通过构造函数注入接口。此外，您也不能通过构造函数
注入不归您所有的类型，如来自外部库的类。在这些情况下，您可以使用 Hilt 模块向 Hilt 提供绑定信息。

# Hilt 模块是一个带有 @Module 注解的类。与 Dagger 模块一样，它会告知 Hilt 如何提供某些类型的实例。与 Dagger 模块
# 不同的是，您必须使用 @InstallIn 为 Hilt 模块添加注解，以告知 Hilt 每个模块将用在或安装在哪个 Android 类中。

以 AnalyticsService 为例。如果 AnalyticsService 是一个接口，则您无法通过构造函数注入它，而应向 Hilt 提供绑定信息，
方法是在 Hilt 模块内创建一个带有 @Binds 注解的抽象函数。

@Binds 注解会告知 Hilt 在需要提供接口的实例时要使用哪种实现。

带有注解的函数会向 Hilt 提供以下信息：

函数返回类型会告知 Hilt 该函数提供哪个接口的实例。
函数参数会告知 Hilt 要提供哪种实现。

# 使用 @Provides 注入实例
接口不是无法通过构造函数注入类型的唯一一种情况。如果某个类不归您所有（因为它来自外部库，如 Retrofit、OkHttpClient 或 Room 数据库等类），
或者必须使用构建器模式创建实例，也无法通过构造函数注入。

接着前面的例子来讲。如果 AnalyticsService 类不直接归您所有，您可以告知 Hilt 如何提供此类型的实例，方法是在 Hilt 模
块内创建一个函数，并使用 @Provides 为该函数添加注解。

带有注解的函数会向 Hilt 提供以下信息：

函数返回类型会告知 Hilt 函数提供哪个类型的实例。
函数参数会告知 Hilt 相应类型的依赖项。
函数主体会告知 Hilt 如何提供相应类型的实例。每当需要提供该类型的实例时，Hilt 都会执行函数主体。

# 为同一类型提供多个绑定
如果您需要让 Hilt 以依赖项的形式提供同一类型的不同实现，必须向 Hilt 提供多个绑定。您可以使用限定符为同一类型定义多个绑定。

# 限定符是一种注解，当为某个类型定义了多个绑定时，您可以使用它来标识该类型的特定绑定。

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthInterceptorOkHttpClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class OtherInterceptorOkHttpClient

然后，Hilt 需要知道如何提供与每个限定符对应的类型的实例。在这种情况下，您可以使用带有 @Provides 的 Hilt 模块。这两种
方法具有相同的返回类型，但限定符将它们标记为两个不同的绑定：

# Hilt 中的预定义限定符
Hilt 提供了一些预定义的限定符。例如，由于您可能需要来自应用或 activity 的 Context 类，因此 Hilt 提供了 @ApplicationContext 
和 @ActivityContext 限定符。

假设本例中的 AnalyticsAdapter 类需要 activity 的上下文。以下代码演示了如何向 AnalyticsAdapter 提供 activity 上下文：

class AnalyticsAdapter @Inject constructor(
    @ActivityContext private val context: Context,
    private val service: AnalyticsService
) { ... }


# 为 Android 类生成的组件
对于您可以从中执行字段注入的每个 Android 类，都有一个关联的 Hilt 组件，您可以在 @InstallIn 注解中引用该组件。每个
Hilt 组件负责将其绑定注入相应的 Android 类。

前面的示例演示了如何在 Hilt 模块中使用 ActivityComponent。


# Hilt 提供了以下组件：
Hilt 组件	注入器面向的对象
SingletonComponent	Application
ActivityRetainedComponent	不适用
ViewModelComponent	ViewModel
ActivityComponent	Activity
FragmentComponent	Fragment
ViewComponent	View
ViewWithFragmentComponent	带有 @WithFragmentBindings 注解的 View
ServiceComponent	Service
注意：Hilt 不会为广播接收器生成组件，因为 Hilt 直接从 SingletonComponent 注入广播接收器。
组件生命周期


# Hilt 会按照相应 Android 类的生命周期自动创建和销毁生成的组件类的实例。
生成的组件	创建时机	销毁时机
SingletonComponent	Application#onCreate()	Application 已销毁
ActivityRetainedComponent	Activity#onCreate()	Activity#onDestroy()
ViewModelComponent	ViewModel 已创建	ViewModel 已销毁
ActivityComponent	Activity#onCreate()	Activity#onDestroy()
FragmentComponent	Fragment#onAttach()	Fragment#onDestroy()
ViewComponent	View#super()	View 已销毁
ViewWithFragmentComponent	View#super()	View 已销毁
ServiceComponent	Service#onCreate()	Service#onDestroy()

注意：ActivityRetainedComponent 在配置更改后仍然存在，因此它在第一次调用 Activity#onCreate() 时创建，在最后一次
调用 Activity#onDestroy() 时销毁。

# 组件作用域
默认情况下，Hilt 中的所有绑定都未限定作用域。这意味着，每当应用请求绑定时，Hilt 都会创建所需类型的一个新实例。
在本例中，每当 Hilt 提供 AnalyticsAdapter 作为其他类型的依赖项或通过字段注入提供它（如在 ExampleActivity 中）时，
Hilt 都会提供 AnalyticsAdapter 的一个新实例。

不过，Hilt 也允许将绑定的作用域限定为特定组件。Hilt 只为绑定作用域限定到的组件的每个实例创建一次限定作用域的绑定，对该
绑定的所有请求共享同一实例。

下表列出了生成的每个组件的作用域注解：

Android 类	生成的组件	作用域
Application	SingletonComponent	@Singleton
Activity	ActivityRetainedComponent	@ActivityRetainedScoped
ViewModel	ViewModelComponent	@ViewModelScoped
Activity	ActivityComponent	@ActivityScoped
Fragment	FragmentComponent	@FragmentScoped
View	ViewComponent	@ViewScoped
带有 @WithFragmentBindings 注解的 View	ViewWithFragmentComponent	@ViewScoped
Service	ServiceComponent	@ServiceScoped

在本例中，如果您使用 @ActivityScoped 将 AnalyticsAdapter 的作用域限定为 ActivityComponent，Hilt 会在相应 activity 
的整个生命周期内提供 AnalyticsAdapter 的同一实例：

@ActivityScoped
class AnalyticsAdapter @Inject constructor(
private val service: AnalyticsService
) { ... }

# 注意：将绑定的作用域限定为某个组件的成本可能很高，因为提供的对象在该组件被销毁之前一直保留在内存中。请在应用中尽量少用
限定作用域的绑定。如果绑定的内部状态要求在某一作用域内使用同一实例，绑定需要同步，或者绑定的创建成本很高，那么将绑定的作
用域限定为某个组件是一种恰当的做法。
假设 AnalyticsService 的内部状态要求每次都使用同一实例 - 不只是在 ExampleActivity 中，而是在应用中的任何位置。在
这种情况下，将 AnalyticsService 的作用域限定为 SingletonComponent 是一种恰当的做法。结果是，每当组件需要提供 
AnalyticsService 的实例时，都会提供同一实例。

以下示例演示了如何将绑定的作用域限定为 Hilt 模块中的某个组件。绑定的作用域必须与其安装到的组件的作用域一致，因此在本例中，
您必须将 AnalyticsService 安装在 SingletonComponent 中，而不是安装在 ActivityComponent 中。
