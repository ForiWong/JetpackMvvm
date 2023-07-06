先看看 Google 官方对 DataStore 的描述吧：
以异步、一致的事务方式存储数据，克服了 SharedPreferences 的一些缺点
这说的，一句话把 SP 都给搞死了，这意思不就是让我们抛弃 SP 来拥抱 DataStore 嘛！Google 都这样说了，那咱们还是来使用吧，
官方肯定有自己的道理，再来贴一段上面文章中对 DataStore 优点的描述吧：

DataStore 是基于 Flow 实现的，所以保证了在主线程的安全性
以事务方式处理更新数据，事务有四大特性（原子性、一致性、 隔离性、持久性）
没有 apply() 和 commit() 等等数据持久的方法
自动完成 SharedPreferences 迁移到 DataStore，保证数据一致性，不会造成数据损坏
可以监听到操作成功或者失败结果


Jetpack DataStore 是一种数据存储解决方案，允许您使用协议缓冲区存储键值对或类型化对象。DataStore 使用 Kotlin 协程和
Flow 以异步、一致的事务方式存储数据。
所以如果想要使用DataStore，就必须使用Kotlin，因为DataStore用到了flow，flow用到了协程，协程是Kotlin的特性。
但是，如果将DataStore封装起来，那么直接使用Java调用的话，也是可以正常使用的，所以大家也不用担心。
DataStore 提供两种不同的实现：Preferences DataStore 和 Proto DataStore。

Preferences DataStore 使用键存储和访问数据。此实现不需要预定义的架构，也不确保类型安全。
Proto DataStore 将数据作为自定义数据类型的实例进行存储。此实现要求您使用协议缓冲区来定义架构，但可以确保类型安全。

DataStore的基本使用

# 1、创建 Preferences DataStore

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = "settings"
    )
        
    name参数是 Preferences DataStore 的名称。类似SP中的name。

# 2、将内容写入 Preferences DataStore

    val EXAMPLE_COUNTER = intPreferencesKey("example_counter")
    suspend fun putIntData() {
        context.dataStore.edit { settings ->
            val currentCounterValue = settings[EXAMPLE_COUNTER] ?: 0
            settings[EXAMPLE_COUNTER] = currentCounterValue + 1
        }
    }

我们通过intPreferencesKey来定义我们的Key的name以及存储的数据类型，如果是存放String类型就是stringPreferencesKey。
通过settings[EXAMPLE_COUNTER]是可以取到数据的，通过赋值可以用来存数据。

# 3、从 Preferences DataStore 读取内容
    val EXAMPLE_COUNTER = intPreferencesKey("example_counter")
    val exampleCounterFlow: Flow<Int> = context.dataStore.data
        .map { preferences ->
            // No type safety.
            preferences[EXAMPLE_COUNTER] ?: 0
        }
通过这种方式取到的是一个被Flow包裹的，通过exampleCounterFlow.first()就可以拿到真实的数据。



# DataStore https://developer.android.google.cn/topic/libraries/architecture/datastore 
Jetpack DataStore 是一种数据存储解决方案，允许您使用协议缓冲区存储键值对或类型化对象。DataStore 使用 Kotlin 协程和 
Flow 以异步、一致的事务方式存储数据。

DataStore 提供两种不同的实现：Preferences DataStore 和 Proto DataStore。

Preferences DataStore 使用键存储和访问数据。此实现不需要预定义的架构，也不确保类型安全。
Proto DataStore 将数据作为自定义数据类型的实例进行存储。此实现要求您使用协议缓冲区来定义架构，但可以确保类型安全。

# 正确使用 DataStore
为了正确使用 DataStore，请始终谨记以下规则：
1、请勿在同一进程中为给定文件创建多个 DataStore 实例，否则会破坏所有 DataStore 功能。如果给定文件在同一进程中有多个
有效的 DataStore，DataStore 在读取或更新数据时将抛出 IllegalStateException。
2、DataStore 的通用类型必须不可变。更改 DataStore 中使用的类型会导致 DataStore 提供的所有保证失效，并且可能会造成
严重的、难以发现的 bug。强烈建议您使用可保证不可变性、具有简单的 API 且能够高效进行序列化的协议缓冲区。
3、切勿在同一个文件中混用 SingleProcessDataStore 和 MultiProcessDataStore。如果您打算从多个进程访问 DataStore，
请始终使用 MultiProcessDataStore。

使用 Preferences DataStore 存储键值对
Preferences DataStore 实现使用 DataStore 和 Preferences 类将简单的键值对保留在磁盘上。
#（1）创建 Preferences DataStore
使用由 preferencesDataStore 创建的属性委托来创建 Datastore<Preferences> 实例。在您的 Kotlin 文件顶层调用该实例一次，
便可在应用的所有其余部分通过此属性访问该实例。这样可以更轻松地将 DataStore 保留为单例。此外，如果您使用的是 RxJava，请使用 
RxPreferenceDataStoreBuilder。必需的 name 参数是 Preferences DataStore 的名称。

// At the top level of your kotlin file:
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

#（2）从 Preferences DataStore 读取内容
由于 Preferences DataStore 不使用预定义的架构，因此您必须使用相应的键类型函数为需要存储在 DataStore<Preferences> 
实例中的每个值定义一个键。例如，如需为 int 值定义一个键，请使用 intPreferencesKey()。然后，使用 DataStore.data 属性，
通过 Flow 提供适当的存储值。

val EXAMPLE_COUNTER = intPreferencesKey("example_counter")
val exampleCounterFlow: Flow<Int> = context.dataStore.data
.map { preferences ->
// No type safety.
preferences[EXAMPLE_COUNTER] ?: 0
}

#（3）将内容写入 Preferences DataStore
Preferences DataStore 提供了一个 edit() 函数，用于以事务方式更新 DataStore 中的数据。该函数的 transform 参数接
受代码块，您可以在其中根据需要更新值。转换块中的所有代码均被视为单个事务。

suspend fun incrementCounter() {
context.dataStore.edit { settings ->
val currentCounterValue = settings[EXAMPLE_COUNTER] ?: 0
settings[EXAMPLE_COUNTER] = currentCounterValue + 1
}
}


创建 Proto DataStore
#（1）创建 Proto DataStore 来存储类型化对象涉及两个步骤：
定义一个实现 Serializer<T> 的类，其中 T 是 proto 文件中定义的类型。此序列化器类会告知 DataStore 如何读取和写入您的
数据类型。请务必为该序列化器添加默认值，以便在尚未创建任何文件时使用。
使用由 dataStore 创建的属性委托来创建 DataStore<T> 的实例，其中 T 是在 proto 文件中定义的类型。在您的 Kotlin 文件
顶层调用该实例一次，便可在应用的所有其余部分通过此属性委托访问该实例。filename 参数会告知 DataStore 使用哪个文件存储数
据，而 serializer 参数会告知 DataStore 第 1 步中定义的序列化器类的名称。

object SettingsSerializer : Serializer<Settings> {
override val defaultValue: Settings = Settings.getDefaultInstance()

override suspend fun readFrom(input: InputStream): Settings {
try {
return Settings.parseFrom(input)
} catch (exception: InvalidProtocolBufferException) {
throw CorruptionException("Cannot read proto.", exception)
}
}

override suspend fun writeTo(
t: Settings,
output: OutputStream) = t.writeTo(output)
}

val Context.settingsDataStore: DataStore<Settings> by dataStore(
fileName = "settings.pb",
serializer = SettingsSerializer
)

#（2）从 Proto DataStore 读取内容
使用 DataStore.data 显示所存储对象中相应属性的 Flow。
val exampleCounterFlow: Flow<Int> = context.settingsDataStore.data
.map { settings ->
// The exampleCounter property is generated from the proto schema.
settings.exampleCounter
}

#（3）将内容写入 Proto DataStore
Proto DataStore 提供了一个 updateData() 函数，用于以事务方式更新存储的对象。updateData() 为您提供数据的当前状态，
作为数据类型的一个实例，并在原子读-写-修改操作中以事务方式更新数据。

suspend fun incrementCounter() {
context.settingsDataStore.updateData { currentSettings ->
currentSettings.toBuilder()
.setExampleCounter(currentSettings.exampleCounter + 1)
.build()
}
}
