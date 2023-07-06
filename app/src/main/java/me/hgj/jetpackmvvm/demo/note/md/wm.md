# WorkManager详解 https://juejin.cn/post/6844903679531679758

# WorkManager的推出

WorkManager 是一个 Android 库, 它在工作的触发器 (如适当的网络状态和电池条件) 满足时, 优雅地运行可推迟的后台工作。
WorkManager 尽可能使用框架 JobScheduler , 以帮助优化电池寿命和批处理作业。在 Android 6.0 (API 级 23) 下面的设备
上, 如果 WorkManager 已经包含了应用程序的依赖项, 则尝试使用Firebase JobDispatcher 。否则, WorkManager 返回到自
定义 AlarmManager实现, 以优雅地处理您的后台工作。
    
# 也就是说，WorkManager可以自动维护后台任务，同时可适应不同的条件，同时满足后台Service和静态广播，内部维护着
# JobScheduler，而在6.0以下系统版本则可自动切换为AlarmManager，好神奇！


# WorkManager详解
1.引入
implementation "android.arch.work:work-runtime:1.0.0-alpha06" // use -ktx for Kotlin

2.1 Worker
Worker是一个抽象类，用来指定需要执行的具体任务。我们需要继承Worker类，并实现它的doWork方法：
class MyWorker:Worker() {

    override fun doWork(): Result {
        Log.d(tag,"任务执行完毕！")
        return Worker.Result.SUCCESS
    }
}

# 向任务添加参数

在Request中传参： 
    val data=Data.Builder()
        .putInt("A",1)
        .putString("B","2")
        .build()

    val request2 = PeriodicWorkRequestBuilder<MyWorker>(24,TimeUnit.SECONDS)
    .setInputData(data)
    .build()


在Worker中使用：
class MyWorker:Worker() {

    val tag = javaClass.simpleName

    override fun doWork(): Result {
        val A = inputData.getInt("A",0)
        val B = inputData.getString("B")
        return Worker.Result.SUCCESS
    }
}

很类似的，任务的返回值也很简单：
    override fun doWork(): Result {

    val A = inputData.getInt("A",0)
    val B = inputData.getString("B")

    val data = Data.Builder()
            .putBoolean("C",true)
            .putFloat("D",0f)
            .build()
    outputData = data//返回值
    return Worker.Result.SUCCESS
}

doWork要求最后返回一个Result，这个Result是一个枚举，它有几个固定的值：
    FAILURE 任务失败。
    RETRY 遇到暂时性失败，此时可使用WorkRequest.Builder.setBackoffCriteria(BackoffPolicy, long, TimeUnit)来重试。
    SUCCESS 任务成功。
    
2.2WorkRequest
也是一个抽象类，可以对Work进行包装，同时装裱上一系列的约束（Constraints），这些Constraints用来向系统指明什么条件下，或者什么时候开始执行任务。
    WorkManager向我们提供了WorkRequest的两个子类：
        OneTimeWorkRequest 单次任务。
        PeriodicWorkRequest 周期任务。
    
    val request1 = PeriodicWorkRequestBuilder<MyWorker>(60,TimeUnit.SECONDS).build()

    val request2 = OneTimeWorkRequestBuilder<MyWorker>().build()

从代码中可以看到，我们应该使用不同的构造器来创建对应的WorkRequest。

接下来我们看看都有哪些约束：
    public boolean requiresBatteryNotLow ()：执行任务时电池电量不能偏低。
    public boolean requiresCharging ()：在设备充电时才能执行任务。
    public boolean requiresDeviceIdle ()：设备空闲时才能执行。
    public boolean requiresStorageNotLow ()：设备储存空间足够时才能执行。

# 给任务加约束：
val myConstraints = Constraints.Builder()
    .setRequiresDeviceIdle(true)//指定{@link WorkRequest}运行时设备是否为空闲
    .setRequiresCharging(true)//指定要运行的{@link WorkRequest}是否应该插入设备
    .setRequiredNetworkType(NetworkType.NOT_ROAMING)
    .setRequiresBatteryNotLow(true)//指定设备电池是否不应低于临界阈值
    .setRequiresCharging(true)//网络状态
    .setRequiresDeviceIdle(true)//指定{@link WorkRequest}运行时设备是否为空闲
    .setRequiresStorageNotLow(true)//指定设备可用存储是否不应低于临界阈值
    .addContentUriTrigger(myUri,false)//指定内容{@link android.net.Uri}时是否应该运行{@link WorkRequest}更新
    .build()

val request = PeriodicWorkRequestBuilder<MyWorker>(24,TimeUnit.SECONDS)
    .setConstraints(myConstraints)//注意看这里！！！
    .build()

# 给任务加标签分组
val request1 = OneTimeWorkRequestBuilder<MyWorker>()
    .addTag("A")//标签
    .build()

val request2 = OneTimeWorkRequestBuilder<MyWorker>()
    .addTag("A")//标签
    .build()

上述代码我给两个相同任务的request都加上了标签，使他们成为了一个组：A组。这样的好处是以后可以直接控制整个组就行了，组内的每个成员都会受到影响。

# 2.3 WorkManager
经过上面的操作，相信我们已经能够成功创建request了，接下来我们就需要把任务放进任务队列，我们使用WorkManager。
WorkManager是个单例，它负责调度任务并且监听任务状态。
    WorkManager.getInstance().enqueue(request)

当我们的request入列后，WorkManager会给它分配一个work ID，之后我们可以使用这个work id来取消或者停止任务：
WorkManager.getInstance().cancelWorkById(request.id)
注意：WorkManager并不一定能结束任务，因为任务有可能已经执行完毕了。

同时，WorkManager还提供了其他结束任务的方法：
    cancelAllWork():取消所有任务。
    cancelAllWorkByTag(tag:String):取消一组带有相同标签的任务。
    cancelUniqueWork(uniqueWorkName:String):取消唯一任务。
    
# 2.4WorkStatus
当WorkManager把任务加入队列后，会为每个WorkRequest对象提供一个LiveData（如果这个东东不了解的话赶紧去学）。
LiveData持有WorkStatus;通过观察该 LiveData, 我们可以确定任务的当前状态, 并在任务完成后获取所有返回的值。

val liveData: LiveData<WorkStatus> = WorkManager.getInstance().getStatusById(request.id)

我们来看这个WorkStatus到底都包涵什么，我们点进去看它的源码：
public final class WorkStatus {

    private @NonNull UUID mId;
    private @NonNull State mState;
    private @NonNull Data mOutputData;
    private @NonNull Set<String> mTags;

    public WorkStatus(
            @NonNull UUID id,
            @NonNull State state,
            @NonNull Data outputData,
            @NonNull List<String> tags) {
        mId = id;
        mState = state;
        mOutputData = outputData;
        mTags = new HashSet<>(tags);
    }

我们需要关注的只有State和Data这两个属性，首先看State:
public enum State {
    ENQUEUED,//已加入队列
    RUNNING,//运行中
    SUCCEEDED,//已成功
    FAILED,//已失败
    BLOCKED,//已刮起
    CANCELLED;//已取消

    public boolean isFinished() {
        return (this == SUCCEEDED || this == FAILED || this == CANCELLED);
    }
}

这特么又一个枚举。看过代码之后，State枚举其实就是用来给我们做最后的结果判断的。但是要注意其中有个已挂起BLOCKED，这是啥子情况？
通过看它的注释，我们得知，如果WorkRequest的约束没有通过，那么这个任务就会处于挂起状态。
接下来，Data当然就是我们在任务中doWork的返回值了
看到这里，我感觉谷歌大佬的设计思维还是非常之强的，把状态和返回值同时输出，非常方便我们做判断的同时来取值，并且这样的设计就可以达到
‘多次返回’的效果，有时间一定要去看一下源码，先立个flag！

# 3. 任务链
在很多场景中，我们需要把不同的任务弄成一个队列，比如在用户注册的时候，我们要先验证手机短信验证码，验证成功后再注册，注册
成功后再调登陆接口实现自动登陆。类似这样相似的逻辑比比皆是，实话说笔者以前都是在service里面用rxjava来实现的。但是现在
service在Android8.0版本以上系统不能用了怎么办？当然还是用我们今天学到的WorkManager来实现，接下来我们就一起看一下
WorkManager的任务链。

# 3.1链式启动-并发
val request1 = OneTimeWorkRequestBuilder<MyWorker1>().build()
val request2 = OneTimeWorkRequestBuilder<MyWorker2>().build()
val request3 = OneTimeWorkRequestBuilder<MyWorker3>().build()

WorkManager.getInstance().beginWith(request1,request2,request3)
.enqueue()
这样等同于WorkManager把一个个的WorkRequest enqueue进队列，但是这样写明显更整齐！同时队列中的任务是并行的。

# 3.2 then操作符-串发
val request1 = OneTimeWorkRequestBuilder<MyWorker>().build()
val request2 = OneTimeWorkRequestBuilder<MyWorker>().build()
val request3 = OneTimeWorkRequestBuilder<MyWorker>().build()

WorkManager.getInstance().beginWith(request1)
.then(request2)
.then(request3)
.enqueue()
上述代码的意思就是先1，1成功后再2，2成功后再3，这期间如果有任何一个任务失败（返回Worker.WorkerResult.FAILURE),则整个队列就会被中断。
在任务链的串行中，也就是两个任务使用了then操作符连接，那么上一个任务的返回值就会自动转为下一个任务的参数！

# 3.3 combine操作符-组合
现在我们有个复杂的需求：共有A、B、C、D、E这五个任务，要求AB串行，CD串行，但两个串之间要并发，并且最后要把两个串的结果汇总到E。
我们看到这种复杂的业务逻辑，往往都会吓一跳，但是牛X的谷歌提供了combine操作符专门应对这种奇葩逻辑，不得不说：谷歌是我亲哥！

val chuan1 = WorkManager.getInstance()
    .beginWith(A)
    .then(B)
val chuan2 = WorkManager.getInstance()
    .beginWith(C)
    .then(D)
WorkContinuation
    .combine(chuan1, chuan2)
    .then(E)
    .enqueue()

# 4. 唯一链
什么是唯一链，就是同一时间内队列里不能存在相同名称的任务。

val request = OneTimeWorkRequestBuilder<MyWorker>().build()

WorkManager.getInstance().beginUniqueWork("tag"，ExistingWorkPolicy.REPLACE,request,request,request)

从上面代码我们可以看到，首先与之前不同的是，这次我们用的是beginUniqueWork方法，这个方法的最后一个参数是一个可变长度的
数组，那就证明这一定是一根链条。
然后我们看这个方法的第一个参数，要求输入一个名称，这个名称就是用来标识任务的唯一性。那如果两个不同的任务我们给了相同的名
称也是可以的，但是这两个任务在队列中只能存活一个。

最后我们再来看第二个参数ExistingWorkPolicy,点进去果然又双叒是枚举：

public enum ExistingWorkPolicy {
    REPLACE,
    KEEP,
    APPEND
}

REPLACE：如果队列里面已经存在相同名称的任务，并且该任务处于挂起状态则替换之。
KEEP：如果队列里面已经存在相同名称的任务，并且该任务处于挂起状态，则什么也不做。
APPEND：如果队列里面已经存在相同名称的任务，并且该任务处于挂起状态，则会缓存新任务。当队列中所有任务执行完毕后，以这个新任务做为序列的第一个任务。

