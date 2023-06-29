package me.hgj.jetpackmvvm.base.viewmodel

import androidx.lifecycle.ViewModel
import me.hgj.jetpackmvvm.callback.livedata.event.EventLiveData

/**
 * 作者　: hegaojian
 * 时间　: 2019/12/12
 * 描述　: ViewModel的基类 使用ViewModel类，放弃AndroidViewModel，原因：用处不大 完全有其他方式获取Application上下文
 *
 * ViewModel is a class that is responsible for preparing and managing the data for an Activity or a Fragment.
 * It also handles the communication of the Activity / Fragment with the rest of the application (e.g. calling
 * the business logic classes).
 * ViewModel是一个负责为Activity或Fragment准备和管理数据的类。
 * 它还处理活动/片段与应用程序的其余部分的通信(例如调用业务逻辑类)。
 * A ViewModel is always created in association with a scope (a fragment or an activity) and will be retained
 * as long as the scope is alive. E.g. if it is an Activity, until it is finished.
 * ViewModel总是与作用域(一个片段或一个活动)关联创建，并将被保留只要作用域是活着的。例如，它是一个活动，直到它被结束。
 * In other words, this means that a ViewModel will not be destroyed if its owner is destroyed for a configuration
 * change (e.g. rotation). The new owner instance just re-connects to the existing model.
 * 换句话说，这意味着如果ViewModel的所有者因为配置改变而被销毁，ViewModel也不会被销毁。新的所有者实例只是重新连接到现有的模型。
 * The purpose of the ViewModel is to acquire and keep the information that is necessary for an Activity or a Fragment.
 * The Activity or the Fragment should be able to observe changes in the ViewModel. ViewModels usually expose this
 * information via LiveData or Android Data Binding. You can also use any observability construct from you favorite
 * framework.
 * ViewModel的目的是获取和保存活动或片段所必需的信息。活动或片段应该能够观察到ViewModel中数据的变化。viewmodel通常会暴露这一点
 * 通过LiveData或Android Data binding 数据绑定的信息。您也可以使用您喜欢的任何可观察性构造框架。
 * ViewModel's only responsibility is to manage the data for the UI. It should never access your view hierarchy or
 * hold a reference back to the Activity or the Fragment.
 * ViewModel的唯一职责是为UI管理数据。它不应该访问你的视图层次结构或保留对Activity或Fragment的引用。
 *
 * ViewModel 类是一种业务逻辑或屏幕级状态容器。它用于将状态公开给界面，以及封装相关的业务逻辑。 它的主要优点是，它可以缓存状态，
 * 并可在配置更改后持久保留相应状态。这意味着在 activity 之间导航时或进行配置更改后（例如旋转屏幕时），界面将无需重新提取数据。
 */
open class BaseViewModel : ViewModel() {

    /*
     * lazy是属性委托的一种，是有kotlin标准库实现。它是属性懒加载的一种实现方式，在对属性使用时才对属性进行初始化，
     * 并且支持对属性初始化的操作时进行加锁，使属性的初始化在多线程环境下线程安全。lazy默认是线程安全的。
     *
     * Creates a new instance of the [Lazy] that uses the specified initialization function [initializer]
     * and the default thread-safety mode [LazyThreadSafetyMode.SYNCHRONIZED].
     * 创建一个懒加载实例，通过括号内指定的初始化方法，默认是线程安全的，即懒加载线程安全模式-同步的。
     *
     * If the initialization of a value throws an exception, it will attempt to reinitialize the value at next access.
     * 假如初始化的过程中抛出异常，将在下次调用该属性时尝试重新初始化。
     *
     * Note that the returned instance uses itself to synchronize on. Do not synchronize from external code on
     * the returned instance as it may cause accidental deadlock. Also this behavior can be changed in the future.
     * 需要注意的是，返回的实例是通过该方法本身实现同步的，不要在在返回该实例的外部代码再进行同步，否则可能会造成死锁。
     * 而且，这个行为后续也可能改变。
     *
     * //lazy 方法
     * public actual fun <T> lazy(initializer: () -> T): Lazy<T> = SynchronizedLazyImpl(initializer)
     *
     * actual关键字
     * 这个Kt的多平台项目的特性，这个特性方便我们进行代码复用，比如同一份业务逻辑的代码可以用来允许js项目，也可以用来运行java项目。
     * 在多平台项目中，主要存在三大模块
     * 公共模块（common module）该模块的代码平台无关，或者是一些api的声明（声明的实现在platform module中）
     * 平台模块（platform module）该模块的代码与平台特性相关。
     * 一般模块 （regular module） ，依赖平台模块或被平台模块依赖
     *
     * 当我们为某一个平台编译多模块项目时，只有该平台对应的平台模块和公共模块被编译
     * 以上仅仅是项目结构，那么如何在语言层面上进行支持呢？kotlin提供了expected和actual两个关键字。
     * expected一般在公共模块中进行api的声明，actual关键字用来在平台模块进行具体的实现。
     */
    val loadingChange: UiLoadingChange by lazy { UiLoadingChange() }

    /**
     * 内置封装好的可通知Activity/fragment 显示隐藏加载框 因为需要跟网络请求显示隐藏loading配套才加的，不然我加他个鸡儿加
     */
    inner class UiLoadingChange {
        //显示加载框
        val showDialog by lazy { EventLiveData<String>() }
        //隐藏
        val dismissDialog by lazy { EventLiveData<Boolean>() }
    }

}