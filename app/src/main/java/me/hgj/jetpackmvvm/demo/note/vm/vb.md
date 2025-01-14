# 参考-视图绑定：https://developer.android.google.cn/topic/libraries/data-binding

# 视图绑定的作用
通过视图绑定功能，您可以更轻松地编写可与视图交互的代码。在模块中启用视图绑定之后，系统会为该模块中的每个 XML 布局文件生成一个绑定类。
绑定类的实例包含对在相应布局中具有 ID 的所有视图的直接引用。
在大多数情况下，视图绑定会替代 findViewById。

设置说明
注意：视图绑定在 Android Studio 3.6 Canary 11 及更高版本中可用。
# 视图绑定功能可按模块启用。要在某个模块中启用视图绑定，请将 viewBinding 元素添加到其 build.gradle 文件中，如下例所示：

android {
    ...
    viewBinding {
        enabled = true
    }
}

如果您希望在生成绑定类时忽略某个布局文件，请将 tools:viewBindingIgnore="true" 属性添加到相应布局文件的根视图中：

<LinearLayout
    ...
    tools:viewBindingIgnore="true" >
    ...
</LinearLayout>

# 用法
为某个模块启用视图绑定功能后，系统会为该模块中包含的每个 XML 布局文件生成一个绑定类。每个绑定类均包含对根视图以及具有 ID
的所有视图的引用。系统会通过以下方式生成绑定类的名称：将 XML 文件的名称转换为驼峰式大小写，并在末尾添加“Binding”一词。

例如，假设某个布局文件的名称为 result_profile.xml：

<LinearLayout ... >
    <TextView android:id="@+id/name" />
    <ImageView android:cropToPadding="true" />
    <Button android:id="@+id/button"
    android:background="@drawable/rounded_button" />
</LinearLayout>

所生成的绑定类的名称就为 ResultProfileBinding。此类具有两个字段：一个是名为 name 的 TextView，另一个是名为 button 的 Button。
该布局中的 ImageView 没有 ID，因此绑定类中不存在对它的引用。

每个绑定类还包含一个 getRoot() 方法，用于为相应布局文件的根视图提供直接引用。在此示例中，ResultProfileBinding 类中的 getRoot() 
方法会返回 LinearLayout 根视图。

以下几个部分介绍了生成的绑定类在 Activity 和 Fragment 中的使用。

# 在 Activity 中使用视图绑定
如需设置绑定类的实例以供 Activity 使用，请在 Activity 的 onCreate() 方法中执行以下步骤：

调用生成的绑定类中包含的静态 inflate() 方法。此操作会创建该绑定类的实例以供 Activity 使用。
通过调用 getRoot() 方法或使用 Kotlin 属性语法获取对根视图的引用。
将根视图传递到 setContentView()，使其成为屏幕上的活动视图。

private lateinit var binding: ResultProfileBinding

override fun onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    binding = ResultProfileBinding.inflate(layoutInflater)
    val view = binding.root
    setContentView(view)
}

您现在即可使用该绑定类的实例来引用任何视图：

binding.name.text = viewModel.name
binding.button.setOnClickListener { viewModel.userClicked() }


# 在 Fragment 中使用视图绑定
如需设置绑定类的实例以供 Fragment 使用，请在 Fragment 的 onCreateView() 方法中执行以下步骤：

调用生成的绑定类中包含的静态 inflate() 方法。此操作会创建该绑定类的实例以供 Fragment 使用。
通过调用 getRoot() 方法或使用 Kotlin 属性语法获取对根视图的引用。
从 onCreateView() 方法返回根视图，使其成为屏幕上的活动视图。
注意：inflate() 方法会要求您传入布局膨胀器。如果布局已膨胀，您可以调用绑定类的静态 bind() 方法。如需了解详情，请查看视图绑定 GitHub 示例中的例子。

    private var _binding: ResultProfileBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ResultProfileBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

您现在即可使用该绑定类的实例来引用任何视图：

binding.name.text = viewModel.name
binding.button.setOnClickListener { viewModel.userClicked() }

# 注意：Fragment 的存在时间比其视图长。请务必在 Fragment 的 onDestroyView() 方法中清除对绑定类实例的所有引用。

# 与 findViewById 的区别

1）与使用 findViewById 相比，视图绑定具有一些很显著的优点：

Null 安全：由于视图绑定会创建对视图的直接引用，因此不存在因视图 ID 无效而引发 Null 指针异常的风险。此外，如果视图仅出
现在布局的某些配置中，则绑定类中包含其引用的字段会使用 @Nullable 标记。
类型安全：每个绑定类中的字段均具有与它们在 XML 文件中引用的视图相匹配的类型。这意味着不存在发生类转换异常的风险。

这些差异意味着布局和代码之间的不兼容将会导致构建在编译时（而非运行时）失败。

2）与数据绑定的对比

视图绑定和数据绑定均会生成可用于直接引用视图的绑定类。但是，视图绑定旨在处理更简单的用例，与数据绑定相比，具有以下优势：
更快的编译速度：视图绑定不需要处理注释，因此编译时间更短。
易于使用：视图绑定不需要特别标记的 XML 布局文件，因此在应用中采用速度更快。在模块中启用视图绑定后，它会自动应用于该模块的所有布局。
反过来，与数据绑定相比，视图绑定也具有以下限制：

视图绑定不支持布局变量或布局表达式，因此不能用于直接在 XML 布局文件中声明动态界面内容。
视图绑定不支持双向数据绑定。
考虑到这些因素，在某些情况下，最好在项目中同时使用视图绑定和数据绑定。您可以在需要高级功能的布局中使用数据绑定，而在不需要
高级功能的布局中使用视图绑定。

