
# https://blog.csdn.net/stephen_sun_/article/details/123051995
Android Jetpack Navigation组件（一）：入门使用

1、Navigation组件
Navigation组件是为了那些使用"单Activity多Fragment"架构模式的app而设计，在这个系列教程里我们只使用Fragment作为目的
地去管理界面和实现界面跳转。

2、NavHost是一个接口，实现了NavHost接口的类称为导航宿主。
导航宿主是一个空的容器，当用户在你的app中导航时，目的地会在其中换入和换出。例如：我们在app里从A目的地导航到B目的地的时候，
导航组件会把在导航宿主中的A目的地换出，然后把B目的地换入导航宿主。
总结翻译：导航宿主(NavHost)是一个目的地容器

3、如何实现NavHost？
直接使用NavHostFragment
A navigation host must derive from NavHost. The Navigation component’s default NavHost implementation,
NavHostFragment, handles swapping fragment destinations.
导航宿主必须派生自NavHost接口。在导航组件里有一个默认的实现了NavHost接口的类叫NavHostFragment，这个NavHostFragment
作为导航宿主，它既作为Fragment目的地的容器，又处理Fragment目的地之间的交换
总结：当我们只使用Fragment作为目的地时，不需要再写一个类去实现NavHost接口，直接使用NavHostFragment即可满足需求。

4、使用NavController+NavDirections导航到目的地。
通过前面的学习我们知道导航宿主（NavHostFragment）是一个容纳和交换目的地的容器，其实它本身并不会执行导航的操作，那我们想
导航到目的地应该怎么做？
答案是使用NavController的navigate(navDirections:NavDirections)方法。
顾名思义NavController是一个导航控制管理器。每个导航宿主都有其对应的NavController。

5、xml标签
导航图元素：
<navigation> 导航图的根元素
<fragment>目的地类型
<dialog>对话框，目的地
<action>切换到下一个目的地
1）局部action
2）全局action

6、嵌套导航图
1）使用<navigation>元素
2）使用<include>元素，包含导航试图

7、NavOptions定义
类似Activity，Fragment也有返回栈。我们可以通过NavOptions保存和恢复Fragment状态，灵活地管理返回栈。
NavOpstions是一个类，Android官方给它的注释只有一句话：
NavOptions stores special options for navigate actions
意思是NavOpstions存储导航操作的特殊选项。
在这里解释一下：除了目的地跳转和参数传递，其他都是特殊选项。

NavOptions类属性	<action>属性	作用
singleTop: Boolean	app:launchSingleTop="<boolean>"	       保证返回栈栈顶只有一个目的地实例。类似Activity的singleTop启动模式
popUpToId: Integer	app:popUpTo="@id/<目的地id>"	           将返回栈中popUpToId（默认不包括自己）之上的所有目的地弹出
popUpToInclusive: Boolean	app:popUpToInclusive="<boolean>"      与popUpToId配套使用，true表示popUpToId自己也要弹出返回栈
popUpToSaveState: Boolean	app:popUpToSaveState="<boolean>"      与popUpToId配套使用，true表示保存所有弹出的目的地的状态
restoreState: Boolean	app:restoreState="<boolean>"              true表示恢复之前保存的目的地状态

8、DeepLink深层链接
Navigation中的DeepLink又叫做深层链接。
在 Android 中，深层链接是指：将用户直接转到应用内特定目的地的链接。

在日常生活中很容易看见的应用：微信消息通知，点击后直接进入某人或者群聊的界面。借助 Navigation 组件可以比较轻松的完成这个效果。
在Navigation 组件中根据其使用方式的不同，可以分为两种不同类型的深层链接：显式深层链接和隐式深层链接。其分类如下面表格所示：

深层链接	说明
显式深层链接	使用 PendingIntent 将用户转到应用内的特定位置。
隐式深层链接	通过 URI、intent操作和 MIME类型匹配深层链接。可以为单个深层链接指定多个匹配类型，但请注意，匹配的优先顺序依次
是 URI 参数、intent操作和 MIME 类型。

9、 动态创建NavHostFragment、设置导航图
相比于之前在xml文件中指定NavHostFragment，也可以在代码中创建NavHostFragment，然后，动态设置导航图。

10、NavBackStackEntry
引用NavBackStackEntry类的注释：
Representation of an entry in the back stack of a NavController
翻译：NavBackStackEntry是NavController管理的返回栈的元素。

这个返回栈与Fragment返回栈是联动的

NavBackStackEntry关联了一个目的地，当目的地在返回栈时，NavBackStackEntry可以提供限定于目的地的Lifecycle、ViewModelStore
和SavedStateRegistry。

可以通过NavController获取指定目的地的NavBackStackEntry。

1）返回结果给前目的地
通过NavBackStackEntry的SavedStateHandle的LiveData返回结果给前目的地
假设我们想从B目的地（BFragment）返回数据给A目的地（AFragment）。
第一步：在A目的地（AFragment）通过LiveData监听数据
第二步：在B目的地（BFragment）通过LiveData返回数据

2）获取导航图范围的ViewModel
由于NavBackStackEntry实现了ViewModelStoreOwner接口，所以可以直接通过它获取ViewModel
导航图内的目的地之间可以通过导航图范围的ViewModel共享数据。
