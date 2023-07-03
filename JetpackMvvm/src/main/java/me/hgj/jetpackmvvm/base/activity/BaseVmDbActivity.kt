package me.hgj.jetpackmvvm.base.activity

import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.ext.inflateBindingWithGeneric

/**
 * 作者　: hegaojian
 * 时间　: 2019/12/12
 * 描述　: 包含ViewModel 和Databind ViewModelActivity基类，把ViewModel 和Databind注入进来了
 * 需要使用Databind的请继承它

DataBinding
数据绑定库是一种支持库，借助该库，您可以使用声明性格式将布局中的界面组件绑定到应用中的数据源，可以绑
定数据、绑定点击事件。

buildFeatures {
    dataBinding true
}

布局文件：
<data>
    <variable name="user" type="com.example.User"/>
</data>

val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
binding.user = User("Test", "User")

 */
abstract class BaseVmDbActivity<VM : BaseViewModel, DB : ViewDataBinding> : BaseVmActivity<VM>() {

    override fun layoutId() = 0

    lateinit var mDatabind: DB

    /**
     * 创建DataBinding
     */
    override fun initDataBind(): View? {
        //mDatabind = DataBindingUtil.setContentView(this, layoutId())
        //mDatabind.lifecycleOwner = this
        //todo 为啥下面这个也可以
        mDatabind = inflateBindingWithGeneric(layoutInflater)
        return mDatabind.root
    }
}