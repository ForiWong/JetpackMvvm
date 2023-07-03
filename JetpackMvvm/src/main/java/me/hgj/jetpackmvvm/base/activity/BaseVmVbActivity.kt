package me.hgj.jetpackmvvm.base.activity

import android.view.View
import androidx.viewbinding.ViewBinding
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.ext.inflateBindingWithGeneric

/**
 * 作者　: hegaojian
 * 时间　: 2019/12/12
 * 描述　: 包含 ViewModel 和 ViewBinding ViewModelActivity基类，把ViewModel 和 ViewBinding 注入进来了
 * 需要使用 ViewBinding 的请继承它

ViewBinding 视图绑定
在模块中启用视图绑定之后，系统会为该模块中的每个 XML 布局文件生成一个绑定类。绑定类的实例包含对在相应布局中
具有 ID 的所有视图的直接引用。

buildFeatures {//编译特征
    viewBinding true
}

val binding = ActivityMainBinding.inflate(layoutInflater)
setContentView(binding.root)

binding.tv.text = "setText"

 */
abstract class BaseVmVbActivity<VM : BaseViewModel, VB : ViewBinding> : BaseVmActivity<VM>() {

    override fun layoutId(): Int = 0

    lateinit var mViewBind: VB

    /**
     * 创建DataBinding
     */
    override fun initDataBind(): View? {
        mViewBind = inflateBindingWithGeneric(layoutInflater)
        return mViewBind.root

    }
}