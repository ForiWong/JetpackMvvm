package me.hgj.jetpackmvvm.demo.note.flow

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.*
import me.hgj.jetpackmvvm.demo.R

class FlowActivity : AppCompatActivity() {

    /**
     * 使用官方库的 MainScope()获取一个协程作用域用于创建协程
     * public fun MainScope(): CoroutineScope = ContextScope(SupervisorJob() + Dispatchers.Main)
     */
    private val mScope = MainScope()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        // 创建一个默认参数的协程，其默认的调度模式为Main 也就是说该协程的线程环境是Main线程
        val job1 = mScope.launch {
            // 这里就是协程体

            // 延迟1000毫秒  delay是一个挂起函数
            // 在这1000毫秒内该协程所处的线程不会阻塞
            // 协程将线程的执行权交出去，该线程该干嘛干嘛，到时间后会恢复至此继续向下执行
            delay(1000)
            Toast.makeText(this@FlowActivity, "1000秒以后。。。", Toast.LENGTH_SHORT).show()
        }

        // 创建一个指定了调度模式的协程，该协程的运行线程为IO线程
        val job2 = mScope.launch(Dispatchers.IO) {

            // 此处是IO线程模式

            // 切线程 将协程所处的线程环境切至指定的调度模式Main
            withContext(Dispatchers.Main) {
                // 现在这里就是Main线程了  可以在此进行UI操作了
                Toast.makeText(this@FlowActivity, "job2。。。", Toast.LENGTH_SHORT).show()
            }
        }

        // 下面直接看一个例子： 从网络中获取数据  并更新UI
        // 该例子不会阻塞主线程
        mScope.launch(Dispatchers.IO) {
            // 执行getUserInfo方法时会将线程切至IO去执行
            val userInfo = getUserInfo()
            // 获取完数据后 切至Main线程进行更新UI
            withContext(Dispatchers.Main) {
                // 更新UI
                Toast.makeText(this@FlowActivity, "$userInfo。。。", Toast.LENGTH_SHORT).show()
            }
        }


        asyncTest1()
        mScope.launch{
            println(asyncTest2())
        }
        asyncTest3()

        testCoroutine()
    }

    /**
     * 获取用户信息 该函数模拟IO获取数据
     * @return String
     */
    private suspend fun getUserInfo(): String {
        return withContext(Dispatchers.IO) {
            delay(2000)
            "Kotlin"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // 取消协程 防止协程泄漏  如果使用lifecycleScope或viewModelScope则不需要手动取消 //todo 这个lifecycleScope怎么使用啊
        mScope.cancel()
    }

    fun asyncTest1(){
        mScope.launch {
            // 开启一个IO模式的线程 并返回一个Deferred，Deferred可以用来获取返回值
            // 代码执行到此处时会新开一个协程 然后去执行协程体  父协程的代码会接着往下走
            val deferred = mScope.async(Dispatchers.IO) {
                // 模拟耗时
                delay(2000)
                // 返回一个值
                "Quyunshuo"
            }
            // 等待async执行完成获取返回值 此处并不会阻塞线程  而是挂起 将线程的执行权交出去
            // 等到async的协程体执行完毕后  会恢复协程继续往下执行
            deferred.await()
        }
    }


    suspend fun asyncTest2(): String{
        // 开启一个IO模式的线程 并返回一个Deferred，Deferred可以用来获取返回值
        // 代码执行到此处时会新开一个协程 然后去执行协程体  父协程的代码会接着往下走
        val deferred = mScope.async(Dispatchers.IO) {
            // 模拟耗时
            delay(2000)
            // 返回一个值
            "Quyunshuo"
        }
        // 等待async执行完成获取返回值 此处并不会阻塞线程  而是挂起 将线程的执行权交出去
        // 等到async的协程体执行完毕后  会恢复协程继续往下执行
        return deferred.await()
    }


    fun asyncTest3() {

        mScope.launch {
            // 此处有一个需求  同时请求5个接口  并且将返回值拼接起来

            val job1 = async {
                // 请求1
                delay(1000)
                "1"
            }
            val job2 = async {
                // 请求2
                delay(2000)
                "2"
            }
            val job3 = async {
                // 请求3
                delay(3000)
                "3"
            }
            val job4 = async {
                // 请求4
                delay(4000)
                "4"
            }
            val job5 = async {
                // 请求5
                delay(5000)
                "5"
            }

            // 代码执行到此处时  5个请求已经同时在执行了
            // 等待各job执行完 将结果合并
            Log.d(
                "TAG",
                "asyncTest2: ${job1.await()} ${job2.await()} ${job3.await()} ${job4.await()} ${job5.await()}"
            )

            // 因为我们设置的模拟时间都是5000毫秒  所以当job1执行完时  其他job也均执行完成
        }
    }

    fun testCoroutine(){
        val coroutineContext = Job() + Dispatchers.Default + CoroutineName("myContext")
        println("$coroutineContext,${coroutineContext[CoroutineName]}")
        val newCoroutineContext = coroutineContext.minusKey(CoroutineName)
        println("$newCoroutineContext")
    }
}