# navigation使用入门
# https://developer.android.google.cn/guide/navigation/navigation-getting-started


导航是指支持用户导航、进入和退出应用中不同内容片段的交互。

导航组件由以下三个关键部分组成：
•	navigation.xml导航图：在一个集中位置包含所有导航相关信息的 XML 资源。这包括应用内所有单个内容区域（称为目标）以及用户可以通过应用获取的可能路径。
•	NavHost导航主机：显示导航图中目标的空白容器。导航组件包含一个默认 NavHost 实现 (NavHostFragment)，可显示 Fragment 目标。
•	NavController导航控制器：在 NavHost 中管理应用导航的对象。当用户在整个应用中移动时，NavController 会安排 NavHost 中目标内容的交换。

在应用中导航时，您告诉 NavController，您想沿导航图中的特定路径导航至特定目标，或直接导航至特定目标。NavController 便会在 NavHost 中显示相应目标。
导航组件提供各种其他优势，包括以下内容：
•	处理 Fragment 事务。
•	默认情况下，正确处理往返操作。
•	为动画和转换提供标准化资源。
•	实现和处理深层链接。
•	包括导航界面模式（例如抽屉式导航栏和底部导航），用户只需完成极少的额外工作。
•	Safe Args - 可在目标之间导航和传递数据时提供类型安全的 Gradle 插件。
•	ViewModel 支持 - 您可以将 ViewModel 的范围限定为导航图，以在图表的目标之间共享与界面相关的数据。

# 入门使用、传递数据、view model支持
# navigation 真的好用吗？之前用的不是好好的吗？有啥确定？
    当前Android开发中使用Fragment来开发页面已经成为主流做法。Fragment轻量、可控性强等优点让人感觉很香。但是Fragment也有自己的硬伤，
    那就是回退栈与页面参数传递。
    虽然当前有例如Fragmentation这样的开源库解决了这类问题，而且这些三方开源库也经受住了时间与项目的检验。但是总让Android开发者心中觉
    得少了点什么（尤其是学了iOS开发之后。。。）Navigation的横空出世，让Android开发者终于看到了一线曙光。
    利用Navigation的三大组件，我们可以自由控制管理fragment的切换和数据传递和回退栈，不要再想以前一样通过FragmentManager进行replace
    或者show了，以及事务的提交，在数据传递方面，也不会通过fragmentID和接口回调的方式进行传递，大大方便了我们的代码编写。可以说，
    Navigation其实就是通过编写XML文件来管理导航fragment，当然如果使用熟悉了的话，也可以在AS中直接进行拖拽的方式进行快速编写代码。

    Navigation是一个可简化Android导航的库和插件。
    更确切的来说，Navigation是用来管理Fragment的切换，并且可以通过可视化的方式，看见App的交互流程。这完美的契合了Jake Wharton大神单
    Activity的建议。

# 1、创建导航图
导航发生在应用中的各个目的地（即您的应用中用户可以导航到的任意位置）之间。这些目的地是通过操作连接的。
导航图是一种资源文件，其中包含您的所有目的地和操作。该图表会显示应用的所有导航路径。

(1)“目的地”是指应用中的不同内容区域。
(2)“操作”是指目的地之间的逻辑连接，表示用户可以采取的路径。

创建navigation.xml
<navigation> 元素是导航图的根元素。当您向图表添加目的地和连接操作时，可以看到相应的 <destination> 和 <action> 元素
在此处显示为子元素。如果您有嵌套图表，它们将显示为子 <navigation> 元素。

# 2、向 Activity 添加 NavHost
导航宿主是 Navigation 组件的核心部分之一。导航宿主是一个空容器，用户在您的应用中导航时，目的地会在该容器中交换进出。
导航宿主必须派生于 NavHost。Navigation 组件的默认 NavHost 实现 (NavHostFragment) 负责处理 fragment 目的地的交换。
# 注意：Navigation 组件旨在用于具有一个主 activity 和多个 fragment 目的地的应用。主 activity 与导航图相关联，且包含
一个负责根据需要交换目的地的 NavHostFragment。在具有多个 activity 目的地的应用中，每个 activity 均拥有其自己的导航图。

<androidx.fragment.app.FragmentContainerView
    android:id="@+id/nav_host_fragment"
    android:name="androidx.navigation.fragment.NavHostFragment"
    android:layout_width="0dp"
    android:layout_height="0dp"
    app:layout_constraintLeft_toLeftOf="parent"
    app:layout_constraintRight_toRightOf="parent"
    app:layout_constraintTop_toTopOf="parent"
    app:layout_constraintBottom_toBottomOf="parent"
    app:defaultNavHost="true"
    app:navGraph="@navigation/nav_graph" />

	• android:name 属性包含 NavHost 实现的类名称。
	• app:navGraph 属性将 NavHostFragment 与导航图相关联。导航图会在此 NavHostFragment 中指定用户可以导航到的所有目的地。
	• app:defaultNavHost="true" 属性确保您的 NavHostFragment 会拦截系统返回按钮。请注意，只能有一个默认 NavHost。
          如果同一布局（例如，双窗格布局）中有多个宿主，请务必仅指定一个默认 NavHost。

# 3、向导航图添加目的地
您可以从现有的 Fragment 或 Activity 创建目的地。

目的地详解
点击一个目的地以将其选中，并注意 Attributes 面板中显示的以下属性：
•	Type 字段指示在您的源代码中，该目的地是作为 fragment、activity 还是其他自定义类实现的。
•	Label 字段包含该目的地的用户可读名称。例如，如果您使用 setupWithNavController() 将 NavGraph 连接到 Toolbar，
    就可能在界面上看到此字段。因此，我们建议您对此值使用资源字符串。
•	ID 字段包含该目的地的 ID，它用于在代码中引用该目的地。
•	Class 下拉列表显示与该目的地相关联的类的名称。您可以点击此下拉列表，将相关联的类更改为其他目的地类型。
点击 Text 标签页可查看导航图的 XML 视图。XML 中同样包含该目的地的 id、name、label 和 layout 属性，如下所示：

<?xml version="1.0" encoding="utf-8"?>
<navigation xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    xmlns:android="http://schemas.android.com/apk/res/android"
    app:startDestination="@id/blankFragment">
    <fragment
        android:id="@+id/blankFragment"
        android:name="com.example.cashdog.cashdog.BlankFragment"
        android:label="@string/label_blank"
        tools:layout="@layout/fragment_blank" />
</navigation>

# 4、将某个屏幕指定为起始目的地 startDestination
起始目的地是用户打开您的应用时看到的第一个屏幕，也是用户退出您的应用时看到的最后一个屏幕。

# 5、连接目的地 action -> destination
操作是指目的地之间的逻辑连接。操作在导航图中以箭头表示。操作通常会将一个目的地连接到另一个目的地，不过您也可以创建全局操作，
此类操作可让您从应用中的任意位置转到特定目的地。
借助操作，您可以表示用户在您的应用中导航时可以采取的不同路径。请注意，如需实际导航到各个目的地，您仍然需要编写代码以执行导航操作。
如需了解详情，请参阅本主题后面的导航到目的地部分。

<?xml version="1.0" encoding="utf-8"?>
<navigation xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    xmlns:android="http://schemas.android.com/apk/res/android"
    app:startDestination="@id/blankFragment">
    <fragment
        android:id="@+id/blankFragment"
        android:name="com.example.cashdog.cashdog.BlankFragment"
        android:label="@string/label_blank"
        tools:layout="@layout/fragment_blank" >
        <action
            android:id="@+id/action_blankFragment_to_blankFragment2"
            app:destination="@id/blankFragment2" /> 
    </fragment>
    <fragment
        android:id="@+id/blankFragment2"
        android:name="com.example.cashdog.cashdog.BlankFragment2"
        android:label="@string/label_blank_2"
        tools:layout="@layout/fragment_blank_fragment2" />
</navigation>

	•	Type 字段包含“Action”。
	•	ID 字段包含该操作的 ID。
	•	Destination 字段包含目的地 Fragment 或 Activity 的 ID。

# 6、导航到目的地
导航到目的地是使用 NavController 完成的，它是一个在 NavHost 中管理应用导航的对象。每个 NavHost 均有自己的相应 NavController。
您可以使用以下方法之一检索 NavController：
    Kotlin：
    •	Fragment.findNavController()
    •	View.findNavController()
    •	Activity.findNavController(viewId: Int)

# 7、使用 Safe Args 确保类型安全
如需在目的地之间导航，建议使用 Safe Args Gradle 插件。该插件可以生成简单的对象和构建器类，这些类支持在目的地之间进行类型安全的导航和参数传递。

