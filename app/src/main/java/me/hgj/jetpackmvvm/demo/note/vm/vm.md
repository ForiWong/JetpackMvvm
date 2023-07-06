# “终于懂了“系列：Jetpack AAC完整解析（三）ViewModel 完全掌握！
# 作者：胡飞洋 链接：https://juejin.cn/post/6915012483421831175

ViewModel是Jetpack AAC的重要组件，同时也有一个同名抽象类。
ViewModel，意为 视图模型，即 为界面准备数据的模型。简单理解就是，ViewModel为UI层提供数据。
官方文档定义如下：

ViewModel 以注重生命周期的方式存储和管理界面相关的数据。(作用)
ViewModel 类让数据可在发生屏幕旋转等配置更改后继续留存。(特点)

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

Typical usage from an Activity standpoint would be:
//viewModel在activity的一个典型用法：

public class UserActivity extends Activity {

        @Override
       protected void onCreate(Bundle savedInstanceState) {
           super.onCreate(savedInstanceState);
           setContentView(R.layout.user_activity_layout);
           final UserModel viewModel = new ViewModelProvider(this).get(UserModel.class);
           viewModel.getUser().observe(this, new Observer<User>() {
                @Override
               public void onChanged(@Nullable User data) {
                   // update ui.
               }
           });
           findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
                @Override
               public void onClick(View v) {
                    viewModel.doAction();
               }
           });
       }
}

ViewModel would be:

public class UserModel extends ViewModel { 
       
       private final MutableLiveData<User> userLiveData = new MutableLiveData<>();

       public LiveData<User> getUser() {
           return userLiveData;
       }
  
       public UserModel() {
           // trigger user load.
       }
  
       void doAction() {
           // depending on the action, do necessary business logic calls and update the
           // userLiveData.
       }
}

ViewModels can also be used as a communication layer between different Fragments of an Activity. Each
Fragment can acquire the ViewModel using the same key via their Activity. This allows communication between
Fragments in a de-coupled fashion such that they never need to talk to the other Fragment directly.

public class MyFragment extends Fragment {
    public void onStart() {

        UserModel userModel = new ViewModelProvider(requireActivity()).get(UserModel.class);

    }
}


1.1 出场背景
在详细介绍ViewModel前，先来看下背景和问题点。

Activity可能会在某些场景（例如屏幕旋转）销毁和重新创建界面，那么存储在其中的界面相关数据都会丢失。例如，界面含用户信息
列表 ，因配置更改而重新创建 Activity 后，新 Activity 必须重新请求用户列表，这会造成资源的浪费。能否直接恢复之前的数
据呢？对于简单的数据，Activity 可以使用 onSaveInstanceState() 方法保存 然后从 onCreate() 中的Bundle恢复数据，
但此方法仅适合可以序列化再反序列化的少量数据（IPC对Bundle有1M的限制），而不适合数量可能较大的数据，如用户信息列表或位
图 。 那么如何做到 因配置更改而新建Activity后的数据恢复呢？

UI层（如 Activity 和 Fragment）经常需要通过逻辑层（如MVP中的Presenter）进行异步请求，可能需要一些时间才能返回结果，
如果逻辑层持有UI层应用（如context），那么UI层需要管理这些请求，确保界面销毁后清理这些调用以避免潜在的内存泄露，但此项管
理需要大量的维护工作。 那么如何更好的避免因异步请求带来的内存泄漏呢？

这时候ViewModel就闪亮出场了——ViewModel用于代替MVP中的Presenter，为UI层准备数据，用于解决上面两个问题。

1.2 特点
具体地，相比于Presenter，ViewModel有以下特点：

1.2.1 生命周期长于Activity
ViewModel最重要的特点是 生命周期长于Activity。来看下官网的一张图：

看到在因屏幕旋转而重新创建Activity后，ViewModel对象依然会保留。 只有Activity真正Finish的时ViewModel才会被清除。

也就是说，因系统配置变更Activity销毁重建，ViewModel对象会保留并关联到新的Activity。而Activity的正常销毁（系统不会
重建Activity）时，ViewModel对象是会清除的。

那么很自然的，因系统配置变更Activity销毁重建，ViewModel内部存储的数据 就可供重新创建的Activity实例使用了。这就解决了
第一个问题。

1.2.2 不持有UI层引用
我们知道，在MVP的Presenter中需要持有IView接口来回调结果给界面。
而ViewModel是不需要持有UI层引用的，那结果怎么给到UI层呢？答案就是使用上一篇中介绍的基于观察者模式的LiveData。 并且，
ViewModel也不能持有UI层引用，因为ViewModel的生命周期更长。

所以，ViewModel不需要也不能 持有UI层引用，那么就避免了可能的内存泄漏，同时实现了解耦。这就解决了第二个问题。

二、ViewModel使用

2.1 基本使用

了解了ViewModel作用解特点，下面来看看如何结合LivaData使用的。（gradle依赖在第一篇中已经介绍过了。）

步骤：
继承ViewModel自定义MyViewModel
在MyViewModel中编写获取UI数据的逻辑
使用LiveData将获取到的UI数据抛出
在Activity/Fragment中使用ViewModelProvider获取MyViewModel实例
观察MyViewModel中的LiveData数据，进行对应的UI更新。

举个例子，如果您需要在Activity中显示用户信息，那么需要将获取用户信息的操作分放到ViewModel中，代码如下：

public class UserViewModel extends ViewModel {

    private MutableLiveData<String> userLiveData ;
    private MutableLiveData<Boolean> loadingLiveData;

    public UserViewModel() {
        userLiveData = new MutableLiveData<>();
        loadingLiveData = new MutableLiveData<>();
    }

    //获取用户信息,假装网络请求 2s后 返回用户信息
    public void getUserInfo() {
        
        loadingLiveData.setValue(true);

        new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPostExecute(String s) {
                loadingLiveData.setValue(false);
                userLiveData.setValue(s);//抛出用户信息
            }
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String userName = "我是胡飞洋，公众号名字也是胡飞洋，欢迎关注~";
                return userName;
            }
        }.execute();
    }
    
    public LiveData<String> getUserLiveData() {
        return userLiveData;
    }
    public LiveData<Boolean> getLoadingLiveData() {
        return loadingLiveData;
    }
}

UserViewModel继承ViewModel，然后逻辑很简单：假装网络请求 2s后 返回用户信息，其中userLiveData用于抛出用户信息，
loadingLiveData用于控制进度条显示。

再看UI层：
public class UserActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ...
    Log.i(TAG, "onCreate: ");

        TextView tvUserName = findViewById(R.id.textView);
        ProgressBar pbLoading = findViewById(R.id.pb_loading);
        //获取ViewModel实例
        ViewModelProvider viewModelProvider = new ViewModelProvider(this);
        UserViewModel userViewModel = viewModelProvider.get(UserViewModel.class);
        //观察 用户信息
        userViewModel.getUserLiveData().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                // update ui.
                tvUserName.setText(s);
            }
        });

        userViewModel.getLoadingLiveData().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                pbLoading.setVisibility(aBoolean?View.VISIBLE:View.GONE);
            }
        });
        //点击按钮获取用户信息
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userViewModel.getUserInfo();
            }
        });
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop: ");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: ");
    }
}

页面有个按钮用于点击获取用户信息，有个TextView展示用户信息。 在onCreate()中先 创建ViewModelProvider实例，传入的参
数是ViewModelStoreOwner，Activity和Fragment都是其实现。然后通过ViewModelProvider的get方法 获取ViewModel实例，
然后就是 观察ViewModel中的LiveData。

运行后，点击按钮 会弹出进度条，2s后展示用户信息。接着旋转手机，我们发现用户信息依然存在。来看下效果：

总结下：

ViewModel的使用很简单，作用和原来的Presenter一致。只是要结合LiveData，UI层观察即可。
ViewModel的创建必须通过ViewModelProvider。
注意到ViewModel中没有持有任何UI相关的引用。
旋转手机重建Activity后，数据确实恢复了。


2.2 Fragment间数据共享
Activity 中的多个Fragment需要相互通信是一种很常见的情况。假设有一个ListFragment，用户从列表中选择一项，会有另一个
DetailFragment显示选定项的详情内容。在之前 你可能会定义接口或者使用EventBus来实现数据的传递共享。

现在就可以使用 ViewModel 来实现。这两个 Fragment 可以使用其 Activity 范围共享 ViewModel 来处理此类通信，如以下示例代码所示：

//ViewModel
public class SharedViewModel extends ViewModel {
//被选中的Item
private final MutableLiveData<UserContent.UserItem> selected = new MutableLiveData<UserContent.UserItem>();

    public void select(UserContent.UserItem user) {
        selected.setValue(user);
    }
    public LiveData<UserContent.UserItem> getSelected() {
        return selected;
    }
}

//ListFragment
public class MyListFragment extends Fragment {
    ...
    private SharedViewModel model;
    ...
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    //获取ViewModel，注意ViewModelProvider实例传入的是宿主Activity
    model = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
    adapter.setListner(new MyItemRecyclerViewAdapter.ItemCLickListner(){
    @Override
    public void onClickItem(UserContent.UserItem userItem) {
    model.select(userItem);
    }
    });
    }
}

//DetailFragment
public class DetailFragment extends Fragment {

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView detail = view.findViewById(R.id.tv_detail);
        //获取ViewModel,观察被选中的Item
        SharedViewModel model = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        model.getSelected().observe(getViewLifecycleOwner(), new Observer<UserContent.UserItem>() {
            @Override
            public void onChanged(UserContent.UserItem userItem) {
                //展示详情
                detail.setText(userItem.toString());
            }
        });
    }
}

代码很简单，ListFragment中在点击Item时更新ViewModel的LiveData数据，然后DetailFragment监听这个LiveData数据即可。

要注意的是，这两个 Fragment 通过ViewModelProvider获取ViewModel时 传入的都是它们宿主Activity。这样，当这两个 Fragment
各自获取 ViewModelProvider 时，它们会收到相同的 SharedViewModel 实例（其范围限定为该 Activity）。

此方法具有以下 优势：
Activity 不需要执行任何操作，也不需要对此通信有任何了解。
除了 SharedViewModel 约定之外，Fragment 不需要相互了解。如果其中一个 Fragment 消失，另一个 Fragment 将继续照常工作。
每个 Fragment 都有自己的生命周期，而不受另一个 Fragment 的生命周期的影响。如果一个 Fragment 替换另一个 Fragment，界面将继
续工作而没有任何问题。

四、对比onSaveInstanceState()
系统提供了onSaveInstanceState()用于让开发者保存一些数据，以方便界面销毁重建时恢复数据。那么和 使用ViewModel恢复数据 有哪些区别呢？

4.1 使用场景
在我很久之前一篇文章《Activity生命周期》中有提到：

onSaveInstanceState调用时机：
当某个activity变得“容易”被系统销毁时，该activity的onSaveInstanceState就会被执行，除非该activity是被用户主动销毁的，
例如当用户按BACK键的时候。 注意上面的双引号，何为“容易”？言下之意就是该activity还没有被销毁，而仅仅是一种可能性。

这种可能性有哪些？有这么几种情况：
1、当用户按下HOME键时。 这是显而易见的，系统不知道你按下HOME后要运行多少其他的程序，自然也不知道activity A是否会被销毁，
故系统会调用onSaveInstanceState，让用户有机会保存某些非永久性的数据。以下几种情况的分析都遵循该原则 。

2、长按HOME键，选择运行其他的程序时。
3、按下电源按键（关闭屏幕显示）时。
4、从activity A中启动一个新的activity时。
5、屏幕方向切换时，例如从竖屏切换到横屏时。 在屏幕切换之前，系统会销毁activity A，在屏幕切换之后系统又会自动地创建
activity A，所以onSaveInstanceState一定会被执行。

总而言之，onSaveInstanceState的调用遵循一个重要原则，即当系统“未经你许可”时销毁了你的activity，则onSaveInstanceState
会被系统调用，这是系统的责任，因为它必须要提供一个机会让你保存你的数据（当然你不保存那就随便你了）。

而使用ViewModel恢复数据 则 只有在 因配置更改界面销毁重建 的情况。

4.2 存储方式
ViewModel是存在内存中，读写速度快，而通过onSaveInstanceState是在序列化到磁盘中。

4.3 存储数据的限制
ViewModel，可以存复杂数据，大小限制就是App的可用内存。而 onSaveInstanceState只能存可序列化和反序列化的对象，且大小有
限制（一般Bundle限制大小1M）。



## ViewModel 官方文档 https://developer.android.google.cn/topic/libraries/architecture/viewmodel#additional-resources

ViewModel 类是一种业务逻辑或屏幕级状态容器。它用于将状态公开给界面，以及封装相关的业务逻辑。 它的主要优点是，它可以缓存状态，
并可在配置更改后持久保留相应状态。这意味着在 activity 之间导航时或进行配置更改后（例如旋转屏幕时），界面将无需重新提取数据。

# 1、ViewModel的优势

1）持久性
ViewModel 允许数据在 ViewModel 持有的状态和 ViewModel 触发的操作结束后继续存在。这种缓存意味着在常见的配置更改（例如屏幕旋转）
完成后，您无需重新提取数据。

2）作用域
实例化 ViewModel 时，您会向其传递实现 ViewModelStoreOwner 接口的对象。
# 它可能是 Navigation 目的地、Navigation 图表、activity、fragment 或实现接口的任何其他类型。然后，ViewModel 的作用域将限定
为 ViewModelStoreOwner 的 Lifecycle。它会一直保留在内存中，直到其 ViewModelStoreOwner 永久消失。

有一系列类是 ViewModelStoreOwner 接口的直接或间接子类。直接子类为 ComponentActivity、Fragment 和 NavBackStackEntry。

3）SavedStateHandle

#问题 借助 SaveStateHandle，您不仅可以在更改配置后持久保留数据，还可以在进程重新创建过程中持久保留数据。
也就是说，即使用户关闭应用，稍后又将其打开，您的界面状态也可以保持不变。

4）对业务逻辑的访问权限
尽管绝大多数业务逻辑都存在于数据层中，但界面层也可以包含业务逻辑。当您合并多个代码库中的数据以创建屏幕界面状态时，或特定类型
的数据不需要数据层时，情况就是如此。

ViewModel 是在界面层处理业务逻辑的正确位置。当需要应用业务逻辑来修改应用数据时，ViewModel 还负责处理事件并将其委托给层次结构中的其他层。

# 2、ViewModel 的生命周期
ViewModel 的生命周期与其作用域直接关联。ViewModel 会一直保留在内存中，直到其作用域 ViewModelStoreOwner 消失。
以下上下文中可能会发生这种情况：

对于 activity，是在 activity 完成时。
对于 fragment，是在 fragment 分离时。
对于 Navigation 条目，是在 Navigation 条目从返回堆栈中移除时。
这使得 ViewModels 成为了存储在配置更改后仍然存在的数据的绝佳解决方案。

您通常在系统首次调用 activity 对象的 onCreate() 方法时请求 ViewModel。系统可能会在 activity 的整个生命周期内多次调用 
onCreate()，如在旋转设备屏幕时。ViewModel 存在的时间范围是从您首次请求 ViewModel 直到 activity 完成并销毁。

# 3、创建具有依赖项的 ViewModel   
按照依赖项注入的最佳实践，ViewModel 可以在其构造函数中将依赖项作为参数。这大多是网域层或数据层中的类型。由于框架提供 ViewModel，
因此需要一种特殊机制来创建 ViewModel 的实例。该机制是 ViewModelProvider.Factory 接口。
只有此接口的实现才能在适当的作用域内实例化 ViewModel。

注意：如果 ViewModel 不接受任何依赖项，或只将 SavedStateHandle 类型作为依赖项，您便无需为框架提供工厂来实例化该 ViewModel 类型。

注意：如果在注入 ViewModel 时使用 Hilt 作为依赖项注入解决方案，您便无需手动定义 ViewModel 工厂。Hilt 会生成一个工厂，它知道如何在
编译时为您创建所有带有 @HiltViewModel 注解的 ViewModel。调用常规 ViewModel API 时，带有 @AndroidEntryPoint 注解的类可以直接
访问 Hilt 生成的工厂。

# ViewModelProvider.Factory的使用
# Hilt的使用


# 包含 CreationExtras 的 ViewModel

如果 ViewModel 类在其构造函数中接收依赖项，请提供用于实现 ViewModelProvider.Factory 接口的工厂。替换 create(Class<T>, CreationExtras)
函数以提供 ViewModel 的新实例。
借助 CreationExtras，您可以访问有助于实例化 ViewModel 的相关信息。

# 4、ViewModel 作用域 API   
Android Jetpack 的一部分。
作用域是有效使用 ViewModel 的关键。每个 ViewModel 的作用域都限定为一个实现 ViewModelStoreOwner 接口的对象。有多个
API 可帮助您更轻松地管理 ViewModel 的作用域。本文档简要介绍了您应该了解的一些关键技术。

注意：如需详细了解作用域和 ViewModel 生命周期，请参阅 ViewModel 概览。

借助 ViewModelProvider.get() 方法，您可以获取作用域限定为任何 ViewModelStoreOwner 的 ViewModel 实例。对于 Kotlin 用户，
我们针对最常见的用例提供了不同的扩展函数。所有 Kotlin 扩展函数实现在后台都使用 ViewModelProvider API。

# 1）ViewModel 的作用域限定为最近的 ViewModelStoreOwner
您可以将 ViewModel 的作用域限定为 Navigation 图的 activity、fragment 或目的地。借助 Activity 库、Fragment 库和 
Navigation 库提供的 viewModels() 扩展函数，以及 Compose 中的 viewModel() 函数，您可以获取作用域限定为最近的 ViewModelStoreOwner
的 ViewModel 实例。

# 2）ViewModel 的作用域限定为 Navigation 图
Navigation 图也是 ViewModel Store Owner。如果您使用的是 Navigation Fragment 或 Navigation Compose，可以使用
navGraphViewModels(graphId) View 扩展函数获取作用域限定为某个 Navigation 图的 ViewModel 实例。

# 5、 ViewModel 的已保存状态模块   
保存界面状态这篇文章提到过，ViewModel 对象可以处理配置更改，因此您无需担心旋转时或其他情况下的状态。但是，如果需要处理系统发起的进程终止，
则可以使用 SavedStateHandle API 作为备用方式。

界面状态通常会在 ViewModel 对象（而非 activity）中存储或引用，因此，使用 onSaveInstanceState() 或 rememberSaveable 时需要已保存的
状态模块可以为您处理的某个样板文件。

使用此模块时，ViewModel 对象会通过其构造函数接收 SavedStateHandle 对象。此对象是一个键值对映射，用于向已保存状态写入对象以及从其中检索对
象。这些值会在进程被系统终止后继续保留，并通过同一对象保持可用状态。

保存的状态与您的任务堆栈相关联。如果任务堆栈消失，保存的状态也会消失。强制停止应用、从“最近用过”菜单中移除应用或重新启动设备时，可能会发生这种
情况。在这种情况下，任务堆栈会消失，并且您无法恢复保存的状态中的信息。在用户发起的界面状态解除情景中，不会恢复保存的状态。在系统发起的界面状态
解除情景中，则会恢复。

要点：通常，保存的实例状态中存储的数据是临时状态，根据用户的输入或导航而定。这方面的例子包括：列表的滚动位置、用户想详细了解的项目的 ID、正在进
行的用户偏好设置选择或文本字段的输入。
重要提示：要使用的 API 取决于状态存储的位置及其所需的逻辑。对于业务逻辑中使用的状态，请将其存储在 ViewModel 中，并使用 SavedStateHandle 保存
。对于界面逻辑中使用的状态，请使用 View 系统中的 onSaveInstanceState API 或 Compose 中的 rememberSaveable。

注意：状态必须简单轻省。对于复杂或大型数据，应使用本地持久性存储。

# 使用 SavedStateHandle
SavedStateHandle 类是一个键值对映射，用于通过 set() 和 get() 方法向已保存的状态写入数据以及从中检索数据。

通过使用 SavedStateHandle，查询值会在进程终止后保留下来，从而确保用户在重新创建前后看到同一组过滤后的数据，而无需 
activity 或 fragment 手动保存、恢复该值并将其重新转给 ViewModel。

注意：仅当 Activity 停止时，SavedStateHandle 才会保存写入其中的数据。在 Activity 停止时对 SavedStateHandle 的写入，
只有在 Activity 收到 onStop 后，又再次收到 onStart 时，才会被保存。

此外，SavedStateHandle 还包含其他您与键值对映射互动时可能会用到的方法：

contains(String key) - 检查是否存在给定键的值。
remove(String key) - 移除给定键的值。
keys() - 返回 SavedStateHandle 中包含的所有键。
此外，您还可以使用可观察数据存储器从 SavedStateHandle 检索值。支持的类型包括：
