用途、使用
（为什么会出现,解决什么问题？怎么使用？之前的方案？）
    其实，liveData是
特征
API
高阶用法（联合其他组件使用）
源码实现原理
    参考 https://blog.csdn.net/qb15119612897/article/details/125166882
UnPeekLiveData的使用
LiveDataBus
文章 参考 LiveData奇思妙用总结 https://www.jianshu.com/p/3421df16c0dd
Activity销毁重建导致LiveData数据倒灌 参考 https://juejin.cn/post/6986895858239275015

## liveData 源码注释
/* LiveData is a data holder class that can be observed within a given lifecycle.
* This means that an {@link Observer} can be added in a pair with a {@link LifecycleOwner}, and
* this observer will be notified about modifications of the wrapped data only if the paired
* LifecycleOwner is in active state. LifecycleOwner is considered as active, if its state is
* {@link Lifecycle.State#STARTED} or {@link Lifecycle.State#RESUMED}. An observer added via
* {@link #observeForever(Observer)} is considered as always active and thus will be always notified
* about modifications. For those observers, you should manually call
* {@link #removeObserver(Observer)}.
* 
* <p> An observer added with a Lifecycle will be automatically removed if the corresponding
* Lifecycle moves to {@link Lifecycle.State#DESTROYED} state. This is especially useful for
* activities and fragments where they can safely observe LiveData and not worry about leaks:
* they will be instantly unsubscribed when they are destroyed.
* 
* <p>
* In addition, LiveData has {@link LiveData#onActive()} and {@link LiveData#onInactive()} methods
* to get notified when number of active {@link Observer}s change between 0 and 1.
* This allows LiveData to release any heavy resources when it does not have any Observers that
* are actively observing.
* <p>
* This class is designed to hold individual data fields of {@link ViewModel},
* but can also be used for sharing data between different modules in your application
* in a decoupled fashion.
* 
* @param <T> The type of data held by this instance
* @see ViewModel
*/


## LiveData核心方法
方法名	作用
observe(LifecycleOwner owner,Observer observer)	   注册和宿主生命周期关联的观察者
observeForever(Observer observer)	               注册观察者,不会反注册,需自行维护
setValue(T data)	    发送数据,没有活跃的观察者时不分发。只能在主线程。
postValue(T data)	    和setValue一样。不受线程环境限制,
onActive	当且仅当有一个活跃的观察者时会触发
inActive	不存在活跃的观察者时会触发


##来源 官网 https://developer.android.google.cn/topic/libraries/architecture/livedata#transform_livedata
1、介绍
LiveData 概览 Android Jetpack 的一部分。
LiveData 是一种可观察的数据存储器类。与常规的可观察类不同，LiveData 具有生命周期感知能力，意指它遵循其他应用组件（如 
activity、fragment 或 service）的生命周期。这种感知能力可确保 LiveData 仅更新处于活跃生命周期状态的应用组件观察者。

## 小结
-- liveData是被观察者 -> 数据存储 + 可观察的 + 可感知观察者的生命周期 -> 数据变化会通知活跃的观察者
-- 接收数据的地方是观察者 -> LifecycleOwner的实现类即可为观察者，在ON_DESTROY状态时，就会被移除（这个好处是，不必担心内存泄漏了）

如果观察者（由 Observer 类表示）的生命周期处于 STARTED 或 RESUMED 状态，则 LiveData 会认为该观察者处于活跃状态。
LiveData 只会将更新通知给活跃的观察者。为观察 LiveData 对象而注册的非活跃观察者不会收到更改通知。

您可以注册与实现 LifecycleOwner 接口的对象配对的观察者。有了这种关系，当相应的 Lifecycle 对象的状态变为 DESTROYED 时，
便可移除此观察者。这对于 activity 和 fragment 特别有用，因为它们可以放心地观察 LiveData 对象，而不必担心泄露（当 activity
和 fragment 的生命周期被销毁时，系统会立即退订它们）。

2、使用 LiveData 的优势

使用 LiveData 具有以下优势：

1）确保界面符合数据状态
LiveData 遵循观察者模式。当底层数据发生变化时，LiveData 会通知 Observer 对象。您可以整合代码以在这些 Observer 对象中更新界面。
这样一来，您无需在每次应用数据发生变化时更新界面，因为观察者会替您完成更新。
2）不会发生内存泄漏
观察者会绑定到 Lifecycle 对象，并在其关联的生命周期遭到销毁后进行自我清理。
3）不会因 Activity 停止而导致崩溃
如果观察者的生命周期处于非活跃状态（如返回堆栈中的 activity），它便不会接收任何 LiveData 事件。
4）不再需要手动处理生命周期
界面组件只是观察相关数据，不会停止或恢复观察。LiveData 将自动管理所有这些操作，因为它在观察时可以感知相关的生命周期状态变化。
5）数据始终保持最新状态
如果生命周期变为非活跃状态，它会在再次变为活跃状态时接收最新的数据。例如，曾经在后台的 Activity 会在返回前台后立即接收最新的数据。
##这个特点：在后台的界面返回前台后，立即会更新到最新的数据，你会看到UI的变化，交互体验挺好的
6）适当的配置更改
如果由于配置更改（如设备旋转）而重新创建了 activity 或 fragment，它会立即接收最新的可用数据。
7）共享资源
您可以使用单例模式扩展 LiveData 对象以封装系统服务，以便在应用中共享它们。LiveData 对象连接到系统服务一次，然后需要相应资源的
任何观察者只需观察 LiveData 对象。如需了解详情，请参阅扩展 LiveData。
##之前使用一些管理类时，通过接口回调，现在通过这个方式就不需要额外的addListener()、removeListener()了。

3、使用 LiveData 对象
（1）创建 LiveData 对象
LiveData 是一种可用于任何数据的封装容器，其中包括可实现 Collections 的对象，如 List。LiveData 对象通常存储在 ViewModel 对象中，
并可通过 getter 方法进行访问。

 class Test1ViewModel : BaseViewModel() {

    val message : MutableLiveData<String> by lazy {//livedata
        MutableLiveData<String>()
    }

 }

注意：请确保用于更新界面的 LiveData 对象存储在 ViewModel 对象中，而不是将其存储在 activity 或 fragment 中，
原因如下：
避免 Activity 和 Fragment 过于庞大。现在，这些界面控制器负责显示数据，但不负责存储数据状态。
将 LiveData 实例与特定的 Activity 或 Fragment 实例分离开，并使 LiveData 对象在配置更改后继续存在。

（2）观察 LiveData 对象
 val observer = Observer<String> {//观察者，重新 void onChanged(T t);
   ("debug $count : " + System.currentTimeMillis() + it).logd()
   button1.text = "$count 次"
 }

 viewModel.message.observe(this@Test1Activity, observer)// this@Test1Activity 就是 LifecycleOwner 

注意：您可以使用 observeForever(Observer) 方法在没有关联的 LifecycleOwner 对象的情况下注册一个观察者。在这种情况下，
观察者会被视为始终处于活跃状态，因此它始终会收到关于修改的通知。您可以通过调用 removeObserver(Observer) 方法来移除这些观察者。
## observeForever(Observer)  removeObserver(Observer) 

在大多数情况下，应用组件的 onCreate() 方法是开始观察 LiveData 对象的正确着手点，原因如下：
确保系统不会从 Activity 或 Fragment 的 onResume() 方法进行冗余调用。
确保 activity 或 fragment 变为活跃状态后具有可以立即显示的数据。一旦应用组件处于 STARTED 状态，就会从它正在观察的 LiveData 对象接收最新值。
只有在设置了要观察的 LiveData 对象时，才会发生这种情况。
通常，LiveData 仅在数据发生更改时才发送更新，并且仅发送给活跃观察者。此行为的一种例外情况是，观察者从非活跃状态更改为活跃状态时也会收到更新。
此外，如果观察者第二次从非活跃状态更改为活跃状态，则只有在自上次变为活跃状态以来值发生了更改时，它才会收到更新。

（3）更新 LiveData 对象
LiveData 没有公开可用的方法来更新存储的数据。MutableLiveData 类将公开 setValue(T) 和 postValue(T) 方法，如果您需要修改存储在 LiveData 对象
中的值，则必须使用这些方法。通常情况下会在 ViewModel 中使用 MutableLiveData，然后 ViewModel 只会向观察者公开不可变的 LiveData 对象。

注意：必须调用 setValue(T) 方法以从主线程更新 LiveData 对象。
     在工作线程中执行代码，改用 postValue(T) 方法来更新 LiveData 对象。

4、其他使用：
（1）将 LiveData 与 Room 一起使用
Room 持久性库支持返回 LiveData 对象的可观察查询。可观察查询属于数据库访问对象 (DAO) 的一部分。
当数据库更新时，Room 会生成更新 LiveData 对象所需的所有代码。在需要时，生成的代码会在后台线程上异步运行查询。此模式有助于
使界面中显示的数据与存储在数据库中的数据保持同步。

（2）将协程与 LiveData 一起使用
LiveData 支持 Kotlin 协程。

如果在viewmodel中使用livedata并绑定到,然后postdata


5、应用架构中的 LiveData
LiveData 具有生命周期感知能力，遵循 activity 和 fragment 等实体的生命周期。您可以使用 LiveData 在这些生命周期所有者和生命周期不同的其他对象
（例如 ViewModel 对象）之间传递数据。ViewModel 的主要责任是加载和管理与界面相关的数据，因此非常适合作为用于保留 LiveData 对象的备选方法。
您可以在 ViewModel 中创建 LiveData 对象，然后使用这些对象向界面层公开状态。

activity 和 fragment 不应保留 LiveData 实例，因为它们的用途是显示数据，而不是保持状态。此外，如果 activity 和 fragment 无需保留数据，
还可以简化单元测试的编写。
##livedata单元测试怎么用？
-- livedata非常适合在viewModel中使用
-- 在数据层中不适合使用
-- 考虑使用 Kotlin Flow

您可能会想在数据层类中使用 LiveData 对象，但 LiveData 并不适合用于处理异步数据流。虽然您可以使用 LiveData 转换和 MediatorLiveData 来实现此目的，
但此方法的缺点在于：用于组合数据流的功能非常有限，并且所有 LiveData 对象（包括通过转换创建的对象）都会在主线程中观察到。

如果您需要在应用的其他层中使用数据流，请考虑使用 Kotlin Flow，然后使用 asLiveData() 在 ViewModel 中将 Kotlin Flow 转换成 LiveData。
如需详细了解如何搭配使用 Kotlin Flow 与 LiveData，请学习此 Codelab。对于使用 Java 构建的代码库，请考虑将执行器与回调或 RxJava 结合使用。

6、扩展 LiveData
例子 --> StockLiveData

class StockLiveData(symbol: String) : LiveData<BigDecimal>() {

    private val stockManager = StockManager(symbol)

    private val listener = { price: BigDecimal -> Unit
        //value = price//setValue()
        postValue(price)
    }

    /**
     * 当活动观察者的数量从0变为1时调用。这个回调可以用来知道这个LiveData正在被使用，因此应该保持最新。
     */
    override fun onActive() {
        stockManager.requestPriceUpdates(listener)
    }

    /**
     * 当活动观察者的数量从1变为0时调用。
     * 这并不意味着没有观察器，可能仍然有观察器，但它们的生命周期状态不是lifeccycle . state . started或
     * lifeccycle . state . resume(就像后台堆栈中的一个Activity)。
     * 你可以通过hasObservers()检查是否有观察者。
     */
    override fun onInactive() {
        stockManager.removeUpdates(listener)
    }

    companion object {

        private lateinit var sInstance: StockLiveData

        @MainThread//只能再UI线程被调用
        fun get(symbol: String): StockLiveData {
            sInstance = if (::sInstance.isInitialized) sInstance else StockLiveData(symbol)
            return sInstance
        }
    }
}

当 LiveData 对象具有活跃观察者时，会调用 onActive() 方法。这意味着，您需要从此方法开始观察股价更新。
当 LiveData 对象没有任何活跃观察者时，会调用 onInactive() 方法。由于没有观察者在监听，因此没有理由与 StockManager 服务保持连接。
setValue(T) 方法将更新 LiveData 实例的值，并将更改告知活跃观察者。

使用：
StockLiveData.get("symbol").observe(this@Test1Activity, Observer<BigDecimal> { price: BigDecimal? ->
"价格是$price".logd()
})

observe() 方法将与 Fragment 视图关联的 LifecycleOwner 作为第一个参数传递。这样做表示此观察者已绑定到与所有者关联的 Lifecycle 对象，
这意味着：
如果 Lifecycle 对象未处于活跃状态，那么即使值发生更改，也不会调用观察者。
销毁 Lifecycle 对象后，会自动移除观察者。

LiveData 对象具有生命周期感知能力，这一事实意味着您可以在多个 Activity、Fragment 和 Service 之间共享这些对象。
多个 Fragment 和 Activity 可以观察 MyPriceListener 实例。仅当一个或多项系统服务可见且处于活跃状态时，LiveData 才会连接到该服务。


7、转换 LiveData
您可能希望在将 LiveData 对象分派给观察者之前对存储在其中的值进行更改，或者您可能需要根据另一个实例的值返回不同的 LiveData 实例。
Lifecycle 软件包会提供 Transformations 类，该类包括可应对这些情况的辅助程序方法。

1）Transformations.map()
对存储在 LiveData 对象中的值应用函数，并将结果传播到下游。
val userLiveData: LiveData<User> = UserLiveData()
val userName: LiveData<String> = Transformations.map(userLiveData) {
user -> "${user.name} ${user.lastName}"
}

2）Transformations.switchMap()
与 map() 类似，对存储在 LiveData 对象中的值应用函数，并将结果解封和分派到下游。传递给 switchMap() 的函数必须返回 LiveData 对象，
如以下示例中所示：

private fun getUser(id: String): LiveData<User> {
...
}
val userId: LiveData<String> = ...
val user = Transformations.switchMap(userId) { id -> getUser(id) }

您可以使用转换方法在观察者的生命周期内传送信息。除非观察者正在观察返回的 LiveData 对象，否则不会计算转换。因为转换是以延迟的方式计算，
所以与生命周期相关的行为会隐式传递下去，而不需要额外的显式调用或依赖项。

如果您认为 ViewModel 对象中需要有 Lifecycle 对象，那么进行转换或许是更好的解决方案。例如，假设您有一个界面组件，该组件接受地址并返回
该地址的邮政编码。您可以为此组件实现简单的 ViewModel，如以下示例代码所示：

您也可以将邮政编码查询实现为地址输入的转换，如以下示例中所示：

class MyViewModel(private val repository: PostalCodeRepository) : ViewModel() {
    private val addressInput = MutableLiveData<String>()
    val postalCode: LiveData<String> = Transformations.switchMap(addressInput) {
        address -> repository.getPostCode(address) }

    private fun setInput(address: String) {
         addressInput.value = address
    }
 }
 在这种情况下，postalCode 字段定义为 addressInput 的转换。只要您的应用具有与 postalCode 字段关联的活跃观察者，就会在每次 addressInput
 发生更改时重新计算并检索该字段的值。

8、合并多个 LiveData 源
MediatorLiveData 是 LiveData 的子类，允许您合并多个 LiveData 源。只要任何原始的 LiveData 源对象发生更改，就会触发 MediatorLiveData 对象
的观察者。
例如，如果界面中有可以从本地数据库或网络更新的 LiveData 对象，则可以向 MediatorLiveData 对象添加以下源：
    与存储在数据库中的数据关联的 LiveData 对象。
    与从网络访问的数据关联的 LiveData 对象。
您的 Activity 只需观察 MediatorLiveData 对象即可从这两个源接收更新。

9、其他资源
示例代码
    Sunflower，这是一个演示版应用，演示了架构组件相关的最佳做法
    Android 架构组件基本示例

Codelab
    带 View 的 Android Room (Java) (Kotlin)
    学习采用 Kotlin Flow 和 LiveData 的高级协程


