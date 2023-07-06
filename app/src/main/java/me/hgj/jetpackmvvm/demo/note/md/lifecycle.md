# 参考 XBaron -> Android-Lifecycle超能解析-生命周期的那些事儿
       https://www.jianshu.com/p/2c9bcbf092bc

Android凡是需要展示给用户看的，都包含着生命周期这个概念，例如Activity、Fragment、View等都与生命周期息息相关，在生命周期函数里，
它们各自完成创建、渲染、销毁等工作。
但是一旦我们往某个生命周期函数中，加入我们自己的逻辑，若是处理不当，就有可能破坏其原有的生命周期，造成内存泄漏甚至应用崩溃等问题。

为什么要引进Lifecycle？
我们在处理Activity或者Fragment组件的生命周期相关时，不可避免会遇到这样的问题：
我们在Activity的onCreate()中初始化某些成员（比如MVP架构中的Presenter，或者AudioManager、MediaPlayer等），然后在onStop中对
这些成员进行对应处理，在onDestroy中释放这些资源。
如果在实际项目中，由于业务逻辑复杂，太多类似的调用，特别容易导致生命周期回调方法变得臃肿了。

Lifecycle提供了一个解决方案，它持有关于组件（如 Activity 或 Fragment）生命周期状态的信息，并且允许其他对象观察此状态。
如此，就将独立的业务逻辑闭合到这个自定义的lifecycleObserver组件了。

Lifecycle是什么？

Lifecycle是一个生命周期感知组件，一般用来响应Activity、Fragment等组件的生命周期变化，并将变化通知到已注册的观察者。
有助于更好地组织代码，让代码逻辑符合生命周期规范，减少内存泄漏，增强稳定性。
Lifecycle已经纳入新版本的AppCompatActivity和Fragment中了，并且Lifecycle还是Android Jetpack中其他两个组件LiveData
和ViewModel的基础。

虽然此示例看起来没问题，但在真实的应用中，最终会有太多管理界面和其他组件的调用，以响应生命周期的当前状态。管理多个组件会在生命周期方法
（如 onStart() 和 onStop()）中放置大量的代码，这使得它们难以维护。
此外，无法保证组件会在 Activity 或 Fragment 停止之前启动。在我们需要执行长时间运行的操作（如 onStart() 中的某种配置检查）时尤其如此。
这可能会导致出现一种竞态条件，在这种条件下，onStop() 方法会在 onStart() 之前结束，这使得组件留存的时间比所需的时间要长。

activity的生命周期内有大量管理组件的代码，难以维护。
无法保证组件会在 Activity/Fragment停止后不执行启动
Lifecycle库 则可以 以弹性和隔离的方式解决这些问题。
所有方法调用操作都由组件本身管理：Presenter类自动感知生命周期，如果需要在其他的Activity/Fragment也使用这个Presenter，
只需添加其为观察者即可。
让各个组件存储自己的逻辑，减轻Activity/Fragment中代码，更易于管理；


# 1、LifeCycle简单使用

1）自定义组件 继承LifeCycleObserver接口
使自定义组件实现LifeCycleObserver接口 //生命周期的观察者

open class LocationComponent(
var context: Activity,
var onLocationChangedListener: OnLocationChangedListener
) : LifecycleObserver {
val TAG = "LocationComponent"
}

/**
* Marks a class as a LifecycleObserver. It does not have any methods, instead, relies on
* {@link OnLifecycleEvent} annotated methods.
* 标记类为生命周期观察者，它没有任何的方法，而是通过注解方法来实现的。
*/
@SuppressWarnings("WeakerAccess")
public interface LifecycleObserver {

}

2）注解生命周期相关方法
使用@OnLifecycleEvent注解对自定义组件的方法进行注解，使被注解的方法能感知Activity/Fragment/Service/Application的生命周期。

public enum Event {//生命周期事件EVENT
/**
* Constant for onCreate event of the {@link LifecycleOwner}.
*/
ON_CREATE,
/**
* Constant for onStart event of the {@link LifecycleOwner}.
*/
ON_START,
/**
* Constant for onResume event of the {@link LifecycleOwner}.
*/
ON_RESUME,
/**
* Constant for onPause event of the {@link LifecycleOwner}.
*/
ON_PAUSE,
/**
* Constant for onStop event of the {@link LifecycleOwner}.
*/
ON_STOP,
/**
* Constant for onDestroy event of the {@link LifecycleOwner}.
*/
ON_DESTROY,
/**
* An {@link Event Event} constant that can be used to match all events.
*/
ON_ANY;
}

3）绑定
lifecycle.addObserver(LifecycleObserver())
在Activity/Fragment/Service/Application中调用 lifecycle.addObserver(observer:LifeCycleObserver) 方法对
实现LifeCycleObserver接口的自定义组件进行绑定。

# 2、 LifecycleService
LifecycleService用于替换Android原生Service，使Service和自定义组件实现解耦。

# 3、自定义LifecycleOwner
# from https://developer.android.google.cn/topic/libraries/architecture/lifecycle

支持库 26.1.0 及更高版本中的 Fragment 和 Activity 已实现 LifecycleOwner 接口。

如果您有一个自定义类并希望使其成为 LifecycleOwner，您可以使用 LifecycleRegistry 类，但需要将事件转发到该类，如以下代码示例中所示：

class MyActivity : Activity(), LifecycleOwner {

    private lateinit var lifecycleRegistry: LifecycleRegistry

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleRegistry = LifecycleRegistry(this)
        lifecycleRegistry.markState(Lifecycle.State.CREATED)
    }

    public override fun onStart() {
        super.onStart()
        lifecycleRegistry.markState(Lifecycle.State.STARTED)
    }

    override fun getLifecycle(): Lifecycle {
        return lifecycleRegistry
    }
}

# 4、生命周期感知型组件的最佳实践
使界面控制器（activity 和 fragment）尽可能保持精简。它们不应试图获取自己的数据，而应使用 ViewModel 执行此操作，并观察
LiveData 对象以将更改体现到视图中。

设法编写数据驱动型界面，对于此类界面，界面控制器的责任是随着数据更改而更新视图，或者将用户操作通知给 ViewModel。

将数据逻辑放在 ViewModel 类中。ViewModel 应充当界面控制器与应用其余部分之间的连接器。不过要注意，ViewModel 不负责获
取数据（例如，从网络获取）。但是，ViewModel 应调用相应的组件来获取数据，然后将结果提供给界面控制器。

使用数据绑定在视图与界面控制器之间维持干净的接口。这样一来，您可以使视图更具声明性，并尽量减少需要在 activity 和 fragment
中编写的更新代码。如果您更愿意使用 Java 编程语言执行此操作，请使用 Butter Knife 之类的库，以避免样板代码并实现更好的抽象化。

如果界面很复杂，不妨考虑创建 presenter 类来处理界面的修改。这可能是一项艰巨的任务，但这样做可使界面组件更易于测试。

避免在 ViewModel 中引用 View 或 Activity 上下文。如果 ViewModel 存在的时间比 activity 更长（在配置更改的情况下），
activity 将泄漏并且不会获得垃圾回收器的妥善处置。

使用 Kotlin 协程管理长时间运行的任务和其他可以异步运行的操作。

# 5、生命周期感知型组件的用例
生命周期感知型组件可使您在各种情况下更轻松地管理生命周期。下面是几个例子：

停止和开始视频缓冲。使用生命周期感知型组件可尽快开始视频缓冲，但会推迟播放，直到应用完全启动。此外，应用销毁后，您还可以
使用生命周期感知型组件终止缓冲。

开始和停止网络连接。借助生命周期感知型组件，可在应用位于前台时启用网络数据的实时更新（流式传输），并在应用进入后台时自动暂停。

暂停和恢复动画可绘制资源。借助生命周期感知型组件，可在应用位于后台时暂停动画可绘制资源，并在应用位于前台后恢复可绘制资源。

# 6、Lifecycle
State和Event，贯穿整个Lifecycle的两个概念：状态和事件。
Lifecycle将Activity的生命周期函数对应成State，生命周期改变，会造成State改变，而State变化将触发Event事件，从而被
LifeCycleObserver接收。

State：状态，是Lifecycle中对应Activity生命周期的一种状态标识，从图中可以看到，它有INITIALIZED、DESTROYED、CREATED、、
STARTED、RESUMED这5中状态。

INITIALIZED：对应Activity的onCreate之前的生命周期
DESTROYED：对应Activity的onDestroy
CREATED：对应Activity的onCreate到onStop之间的生命周期
STARTED：对应Activity的onStart到onPause之间的生命周期
RESUMED：对应Activity的onResume

Event：事件，当State发生变化时，Lifecycle会向已注册的LifecycleObserver发送事件，例如：当State从INITIALIZED变化到
CREATED时，就会发出ON_CREATE事件。

因此，弄懂Lifecycle，其实也就是需要弄懂两件事：

State是如何与Activity/Fragment的生命周期绑定的？
Event事件是如何分发到LifecycleObserver的？

#7、捋源码的顺序
从AppCompatActivity的getLifecycle()开始
LifecycleRegistry 生命周期登记表：这个类是Lifecycle中最重要的一个类，它是Lifecycle的子类，起着添加观察者，响应生命
    周期事件，分发生命周期事件的作用
ReportFragment
为什么不直接在SupportActivity的生命周期函数中给Lifecycle分发生命周期事件，而是要加一个Fragment呢？
因为不是所有的页面都继承AppCompatActivity，为了兼容非AppCompatActivity，所以封装一个同样具有生命周期的Fragment来给
Lifecycle分发生命周期事件。
那我们不继承新版本AppCompatActivity时，Lifecycle是如何通过ReportFragment来分发生命周期事件的呢？
这里我们直接使用AndroidStudio强大的搜索功能，alt+F7搜索ReportFragment的调用者，我们发现：除了SupportActivity以外，还
有两个地方使用到了ReportFragment：LifecycleDispatcher和ProcessLifecycleOwner

# 相关的重要的类
LifecycleObserver接口（ Lifecycle观察者）：实现该接口的类，通过注解的方式，可以通过被LifecycleOwner类的addObserver
  (LifecycleObserver o)方法注册,被注册后，LifecycleObserver便可以观察到LifecycleOwner的生命周期事件。
LifecycleOwner接口（Lifecycle持有者）：实现该接口的类持有生命周期(Lifecycle对象)，该接口的生命周期(Lifecycle对象)的改
   变会被其注册的观察者LifecycleObserver观察到并触发其对应的事件。
Lifecycle(生命周期)：和LifecycleOwner不同的是，LifecycleOwner本身持有Lifecycle对象，LifecycleOwner通过其Lifecycle 
   getLifecycle()的接口获取内部Lifecycle对象。

