package me.hgj.jetpackmvvm.demo.ui.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_test.*
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.demo.app.base.BaseActivity
import me.hgj.jetpackmvvm.demo.databinding.ActivityTestBinding
import me.hgj.jetpackmvvm.demo.ui.adapter.TestAdapter
import me.hgj.jetpackmvvm.demo.viewmodel.request.RequestLoginRegisterViewModel
import me.hgj.jetpackmvvm.ext.util.logd
import me.hgj.jetpackmvvm.ext.view.clickNoRepeat

/**
 * @author : hgj
 * @date   : 2020/8/26
 *
 * activity除了在泛型里边的BaseViewModel，其他的viewModel直接创建
 */
class TestActivity : BaseActivity<BaseViewModel, ActivityTestBinding>() {

    /*
     * by viewModels()
     * kotlin 的属性委托是by的一种用法
     *
     * 属性委托指的是一个类的某个属性值不是在类中直接进行定义，而是将其托付给一个代理类。
     * 属性委托语法格式：
     * val/var <属性名>: <类型> by <表达式>
     *
     * 这里的val只能是val，不可以是var
     * 只能用于常量val
     * 相当于单例模式（如果为空，则初始化，否则直接得到值）。
     * 只有在第一次调用的时才执行 by 后的表达式，对变量赋值，
     * 后面再调用就直接获取到变量的值了，不会再次执行by 后的表达式了。
     *
     * 而viewModels() 其实是一个ComponentActivity的扩展函数
     *
     * //java
     * MyViewModel model = new ViewModelProvider(this).get(MyViewModel.class);
     **/
    val viewModel: RequestLoginRegisterViewModel by viewModels()

    val testAdapter: TestAdapter by lazy { TestAdapter(arrayListOf("a1", "b2", "c3")) }


    override fun initView(savedInstanceState: Bundle?) {

        //强烈注意：使用addLoadingObserve 将非绑定当前activity的viewmodel绑定loading回调 防止出现请求时不显示 loading 弹窗bug
        addLoadingObserve(viewModel)

        testAdapter.run {
            clickAction = { position, item ->
                "海王收到了点击事件，并准备发一个红包".logd()
            }
        }

        test_recycler.apply {
            context?.let {
                layoutManager = LinearLayoutManager(it)
                adapter = testAdapter
            }
        }

        button1.clickNoRepeat {
            showLoading("111")
            "点击了".logd()
        }

        button2.clickNoRepeat {
            showLoading("222")
        }

    }

    override fun createObserver() {


    }

    override fun onDestroy() {
        super.onDestroy()
        "testActivity onDestroy".logd()
    }
}

