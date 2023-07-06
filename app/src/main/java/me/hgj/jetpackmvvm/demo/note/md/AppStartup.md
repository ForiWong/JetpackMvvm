# 程序员DHL Jetpack 最新成员 AndroidX App Startup 实践以及原理分析 https://juejin.cn/post/6844904190440013837

来自 Google  文档： App Startup 是 Android Jetpack 最新成员，提供了在 App 启动时初始化组件简单、高效的方法，无论是
library 开发人员还是 App 开发人员都可以使用 App Startup 显示的设置初始化顺序。

简单的说就是 App Startup 提供了一个 ContentProvider 来运行所有依赖项的初始化，避免每个第三方库单独使用 ContentProvider
进行初始化，从而提高了应用的程序的启动速度。

# AndroidX App Startup 为我们解决了什么问题？
刚才我们说到无论是 Google 提供的库还是第三方库，App 启动运行时会初始化一些逻辑，它们为了方便开发者使用，避免开发者手动调用，
使用 ContentProvider 进行初始化，例如 WorkManager 在应用启动时使用 ContentProvider 进行初始化。

假设你的 App 有很多类似于 WorkManager 这样的库，都在 ContentProvider 里面进行一些初始化工作，在 App 启动时运行多个 
ContentProvider，这样会带来一些问题，多个 ContentProvider 会增加了 App 启动运行的时间。

ContentProvider 的 onCreate 方法会先于 Application 的 OnCreate 方法执行，这是在冷启动阶段自动运行初始化的。

这是在 App 冷启动时自动运行初始化的，这样只会增加 App 的加载时间，用户希望 App 加载得快，启动慢会带来糟糕的用户体验，
AndroidX App Startup 正是为了解决这个问题而出现的。

# 注意： ContentProvider Application 等组件等初始化顺序
# 这个组件是启动优化的一个方法，和其他启动优化的关系呢？


# 如何正确使用 AndroidX App Startup？
使用 AndroidX App Startup 来运行所有依赖项的初始化有两种方式：
自动初始化。
手动初始化（也是延迟初始化）。

# 自动初始化
在 build.gradle 文件内添加依赖。

implementation "androidx.startup:startup-runtime:1.0.0-alpha01"

实现 Initializer 接口，并重写两个方法，来初始化组件。

class LibaryC : Initializer<LibaryC.Dependency> {
    override fun create(context: Context): Dependency {
        // 初始化工作
        Log.e(TAG, "init LibaryC ")
        return Dependency()
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return mutableListOf(LibaryB::class.java)
    }
    ......
}

create(Context): 这里进行组件初始化工作。
dependencies(): 返回需要初始化的列表，同时设置 App 启动时依赖库运行的顺序，假设
LibaryC 依赖于 LibaryB，LibaryB 依赖于 LibaryA，App 启动运行时，会先运行 LibaryA 然后运行 LibaryB 最后运行 LibaryC。

正如 GitHub 上的 AppStartupSimple 示例项目，它依赖结构就是 LibaryC 依赖于 LibaryB，LibaryB 依赖于 LibaryA，输出结果如下所示：
com.hi.dhl.startup.simple E/LibaryA: init LibaryA
com.hi.dhl.startup.simple E/LibaryB: init LibaryB
com.hi.dhl.startup.simple E/LibaryC: init LibaryC

在 AndroidManifest.xml 文件中注册 InitializationProvider。

<application>
    <provider
        android:name="androidx.startup.InitializationProvider"
        android:authorities="${applicationId}.androidx-startup"
        android:exported="false"
        tools:node="merge">

        <!-- 自动初始化 -->
        <meta-data
            android:name="com.hi.dhl.startup.library.LibaryC"
            android:value="androidx.startup" />
    </provider>
</application>

App 启动的时 App Startup 会读取 AndroidManifest.xml 文件里面的 InitializationProvider 下面的 <meta-data> 
声明要初始化的组件，完成自动初始化工作。

# 手动初始化（也是延迟初始化）
在 build.gradle 文件内添加依赖，和上文一样。
创建一个类 LibaryD 实现 Initializer 接口，并重写两个方法，来初始化组件，和上文一样。
在 AndroidManifest.xml 文件中注册 InitializationProvider。

<application>
    <provider
        android:name="androidx.startup.InitializationProvider"
        android:authorities="${applicationId}.androidx-startup"
        android:exported="false"
        tools:node="merge">
        <!-- 
            手动初始化（也是延迟初始化） 
            在 `<meta-data>` 标签内添加 `tools:node="remove"`
        -->
        <meta-data
            android:name="com.hi.dhl.startup.library.LibaryD"
            android:value="androidx.startup"
            tools:node="remove" />
    </provider>
</application>

禁用单个组件的自动初始化，需要在 <meta-data> 标签内添加 tools:node="remove" 清单合并工具会将它从清单文件中删除。

禁用所有组件初始化，需要在 provider 标签内添加 tools:node="remove" 清单合并工具会将它从清单文件中删除。

<!-- 禁用所有组件初始化 -->
<provider
    android:name="androidx.startup.InitializationProvider"
    android:authorities="${applicationId}.androidx-startup"
    android:exported="false"
    tools:node="remove">
    ......
</provider>

在需要的地方进行初始化，调用以下代码进行初始化。

AppInitializer.getInstance(context).initializeComponent(MyInitializer::class.java)

如果组件初始化之后，再次调用 AppInitializer.initializeComponent() 方法不会再次初始化。
手动初始化（也是延迟初始化）是非常有用的，组件不需要在 App 启动时运行，只需要在需要它地方运行，可以减少 App 的启动时间，提高启动速度。

# 这篇文章主要介绍了以下内容：
ContentProvider 启动顺序源码分析。
App Startup 是 Jetpack 的新成员，是为了解决因 App 启动时运行多个 ContentProvider 会增加 App 的启动时间的问题。
使用了一个 InitializationProvider 管理多个依赖项，消除了每个库单独使用 ContentProvider 成本，减少初始化时间。
App Startup 允许你自定义组件初始化顺序。
App Startup 可以自动初始化 AndroidManifest.xml 文件中 InitializationProvider 下面的 <meta-data> 声明要初始化的组件。
App Startup 提供了一种延迟初始化组件的方法，减少 App 初始化时间。


# 作者：彭旭锐 来源：稀土掘金 链接：https://juejin.cn/post/6898738809895125006
# 1. App Startup 解决了什么问题？
App Startup 是 Google 提供的 Android 轻量级初始化框架：
优点：使用 App Startup 框架，可以简化启动序列并显式设置初始化依赖顺序，在简单、高效这方面，App Startup 基本满足需求。
#不足：App Startup 框架的不足也是因为它太简单了，提供的特性太过简单，往往并不能完美契合商业化需求。
例如以下特性 App Startup 就无法满足：
缺乏异步等待： 同步等待指的是在当前线程先初始化所依赖的组件，再初始化当前组件，App Startup 是支持的，但是异步等待就不支持了。
             举个例子，所依赖的组件需要执行一个耗时的异步任务才能完成初始化，那么 App Startup 就无法等待异步任务返回；
缺乏依赖回调： 当前组件所依赖的组件初始化完成后，未发出回调。

# 2. App Startup 如何实现自动初始化？
App Startup 利用了 ContentProvider 在应用启动的时候初始化的特性，提供了一个自定义 ContentProvider 来实现自动初始化。
很多库都利用了 ContentProvider 的启动机制，来实现无侵入初始化，例如 LeakCanary 等。
使用 AppStartup 还能够合并所有用于初始化的 ContentProvider ，减少创建 ContentProvider，并提供全局管理。
