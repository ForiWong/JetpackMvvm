package me.hgj.jetpackmvvm.base.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.ext.getVmClazz
import me.hgj.jetpackmvvm.ext.util.logd
import me.hgj.jetpackmvvm.ext.util.notNull
import me.hgj.jetpackmvvm.network.manager.NetState
import me.hgj.jetpackmvvm.network.manager.NetworkStateManager

/**
 * 作者　: hegaojian
 * 时间　: 2019/12/12
 * 描述　: ViewModelActivity基类，把ViewModel注入进来了
 *
//ViewBinding 与 DataBinding的区别 todo 这两者怎么选择
目的不同。ViewBinding的出现仅仅是为了帮开发人员省去写findViewById的步骤；而DataBinding是用于绑定数据的，
能够把视图的数据和代码变量绑定起来，并且实现自动更新。这个特性使得DataBinding能和MVVM框架进行很好的配合。

初始化方式不同。ViewBinding通过生成的Binding类的inflate方法来加载布局，然后还需要用Activity的setContent
View()方法来绑定。而Databinding则是通过DataBindingUtil.setContentView()来绑定的。

包含关系。DataBinding也有ViewBinding的功能，也可以省去findViewById()方法。
public abstract class ViewDataBinding extends BaseObservable implements ViewBinding

 */
abstract class BaseVmActivity<VM : BaseViewModel> : AppCompatActivity() {

    lateinit var mViewModel: VM//每个activity对应一个ViewModel,查看其class 描述

    abstract fun layoutId(): Int//布局文件

    abstract fun initView(savedInstanceState: Bundle?)//初始化view，设置UI、点击事件

    abstract fun showLoading(message: String = "请求网络中...")//loading弹框

    abstract fun dismissLoading()//关闭弹框

    /**
     * savedInstanceState bundle
     * 从上面的代码可以看出，onCreate方法的参数是一个Bundle类型的参数。Bundle类型的数据与Map类型的数据相似，都是以
     * key-value的形式存储数据的。
     *
     * 从字面上看savedInstanceState，是保存实例状态的。实际上，savedInstanceState也就是保存Activity的状态的。
     * 那么，savedInstanceState中的状态数据是从何处而来的呢？
     *
     *    public void onSaveInstanceState(Bundle savedInstanceState){
     *      super.onSaveInstanceState(savedInsanceState);
     *   }
     *
     * 在实际应用中，当一个Activity结束前，如果需要保存状态，就在onsaveInstanceState中，将状态数据以key-value的
     * 形式放入到savedInstanceState中。这样，当一个Activity被创建时，就能从onCreate的参数savedInsanceState中
     * 获得状态数据。
     */
    override fun onCreate(savedInstanceState: Bundle?) {//savedInstanceState 保存的实例状态
        "onCreate ${javaClass.simpleName}".logd()
        super.onCreate(savedInstanceState)
        initDataBind().notNull({//内联函数
            setContentView(it)
        }, {
            setContentView(layoutId())
        })
        init(savedInstanceState)
    }

    override fun onStart() {
        "onStart ${javaClass.simpleName}".logd()
        super.onStart()
    }

    override fun onResume() {
        "onResume ${javaClass.simpleName}".logd()
        super.onResume()
    }

    override fun onPause() {
        "onPause ${javaClass.simpleName}".logd()
        super.onPause()
    }

    override fun onStop() {
        "onStop ${javaClass.simpleName}".logd()
        super.onStop()
    }

    override fun onDestroy() {
        "onDestroy ${javaClass.simpleName}".logd()
        super.onDestroy()
    }

    private fun init(savedInstanceState: Bundle?) {
        mViewModel = createViewModel()//创建viewModel
        registerUiChange()//注册UI事件
        initView(savedInstanceState)//初始化view，设置UI显示、点击事件
        createObserver()//观察者
        NetworkStateManager.instance.mNetworkStateCallback.observeInActivity(this, Observer {
            onNetworkStateChanged(it)
        })
    }

    /**
     * 网络变化监听 子类重写
     */
    open fun onNetworkStateChanged(netState: NetState) {}

    /**
     * 创建viewModel方式：todo 看源码
     * (1)val viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
     *
     * (2)val mainModel by viewModels<MainViewModel>()
     */
    private fun createViewModel(): VM {
        return ViewModelProvider(this).get(getVmClazz(this))
    }

    /**
     * 创建LiveData数据观察者
     */
    abstract fun createObserver()

    /**
     * 注册UI 事件
     */
    private fun registerUiChange() {
        //显示弹窗
        mViewModel.loadingChange.showDialog.observeInActivity(this, Observer {
            showLoading(it)
        })
        //关闭弹窗
        mViewModel.loadingChange.dismissDialog.observeInActivity(this, Observer {
            dismissLoading()
        })
    }

    /**
     * 将非该Activity绑定的ViewModel添加 loading回调 防止出现请求时不显示 loading 弹窗bug
     * @param viewModels Array<out BaseViewModel>
     */
    protected fun addLoadingObserve(vararg viewModels: BaseViewModel) {
        viewModels.forEach { viewModel ->
            //显示弹窗
            viewModel.loadingChange.showDialog.observeInActivity(this, Observer {
                showLoading(it)
            })
            //关闭弹窗
            viewModel.loadingChange.dismissDialog.observeInActivity(this, Observer {
                dismissLoading()
            })
        }
    }

    /**
     * 供子类BaseVmDbActivity 初始化Databinding操作
     * 创建dataBinding,并返回绑定的view
     */
    open fun initDataBind(): View? {
        return null
    }
}