package me.hgj.jetpackmvvm.demo.note

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_test.button1
import kotlinx.android.synthetic.main.activity_test.button2
import kotlinx.android.synthetic.main.activity_test_one.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.demo.app.base.BaseActivity
import me.hgj.jetpackmvvm.demo.databinding.ActivityTestOneBinding
import me.hgj.jetpackmvvm.ext.util.logd
import me.hgj.jetpackmvvm.ext.view.clickNoRepeat
import java.lang.Runnable
import java.math.BigDecimal
import java.util.concurrent.Executors
import kotlin.concurrent.thread

/**
 * 测试1
 */
class Test1Activity : BaseActivity<BaseViewModel, ActivityTestOneBinding>() {

    val viewModel: Test1ViewModel by viewModels()
    var count = 0

    override fun initView(savedInstanceState: Bundle?) {

        with(mDatabind) {
            vm = viewModel

            val observer = Observer<String> {
                ("debug $count : " + System.currentTimeMillis() + it).logd()
                button1.text = "$count 次"
            }
            viewModel.message.observe(this@Test1Activity, observer)


            viewModel.message2.observe(this@Test1Activity, Observer<String> {//添加观察者
                ("debug2 $count : " + System.currentTimeMillis() + it).logd()
                button2.text = "$count 次"
            })

            /**
             * 延时10s改变,黑屏，没有调用onChange()方法
             * 在白屏之后，会调用onChange()
             */
            button2.postDelayed(Runnable {
                ("debug:" + Thread.currentThread().name + Thread.currentThread().id + ":" + System.currentTimeMillis()).logd()
                viewModel.message2.postValue("delay 10s post")
                count++
            }, 10000)

        }

        //StockLiveData
//        val myPriceListener: LiveData<BigDecimal> = StockLiveData("Test Stock ==")
//
//        myPriceListener.observe(this@Test1Activity, Observer<BigDecimal> { price: BigDecimal? ->
//            "价格是$price".logd()
//        })

        StockLiveData.get("symbol")
            .observe(this@Test1Activity, Observer<BigDecimal> { price: BigDecimal? ->
                "价格是$price".logd()
            })

        button1.clickNoRepeat {
            ("debug:" + Thread.currentThread().name + Thread.currentThread().id + ":" + System.currentTimeMillis()).logd()
            "点击了1".logd()
            count++
            viewModel.message.value = "在主线程中更新" //setValue()
        }

        button2.clickNoRepeat {
            "点击了2".logd()
            count++
            thread {
                ("debug:" + Thread.currentThread().name + Thread.currentThread().id + ":" + System.currentTimeMillis()).logd()

                // java.lang.IllegalStateException: Cannot invoke setValue on a background thread
                //非法线程状态异常，不能在后台线程调用LiveData.setValue()
                //viewModel.message2.value = "在子线程中更新"
                viewModel.message2.postValue("zai thread post")
            }
        }

        button3.clickNoRepeat {
            val flow: Flow<Int> = flow {
                delay(1000)
                emit(1)
                delay(1000)
                emit(2)
                delay(1000)
                emit(3)
            }

            val flow2 = flowOf(1, 2, 3).onEach {
                delay(1000)
            }

            val flow3 = listOf(1, 2, 3).asFlow().onEach {
                delay(1000)
            }

            runBlocking {
                flow.collect {
                    println("打印 $it")
                }
            }

            runBlocking {
                println("calling collect...")
                flow2.collect {
                    println("打印 $it")
                }
                println("calling collect again...")
                flow2.collect {
                    println("打印 $it")
                }
            }

            runBlocking {
                withTimeoutOrNull(150) { // 在 250 毫秒后超时
                    simple().collect { value -> println(value) }
                }
                println("Done")


                val sum = (1..5).asFlow().reduce { a, b ->
                    a + b
                }
                println("sum reduce $sum")

                val sum2 = (1..5).asFlow().fold(101) { a, b ->
                    a + b
                }
                println("sum2 fold $sum2")
            }

            val mDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()

            val scope = CoroutineScope(mDispatcher)
            (1..5).asFlow().onEach { println(it) }
                .onCompletion { mDispatcher.close() }
                .launchIn(scope)

            runBlocking<Unit> {
                launch {
                    (1..5).asFlow()
                        .onEach { delay(100) }
                        .flowOn(Dispatchers.IO)
                        .collect { println(it) }
                }
                launch {
                    flowOf("one", "two", "three", "four", "five")
                        .onEach { delay(200) }
                        .flowOn(Dispatchers.IO)
                        .collect { println(it) }
                }
            }

            runBlocking<Unit> {
                (1..5).asFlow()
                    .onEach { delay(100) }
                    .flowOn(Dispatchers.IO)
                    .onEach { println(it) }
                    .launchIn(this)

                flowOf("one", "two", "three", "four", "five")
                    .onEach { delay(200) }
                    .flowOn(Dispatchers.IO)
                    .onEach { println(it) }
                    .launchIn(this)
            }

            runBlocking {
                (1..5).asFlow()
                    .filter {
                        println("Filter $it")
                        it % 2 == 0
                    }
                    .map {
                        println("Map $it")
                        "string $it"
                    }.collect {
                        println("Collect $it")
                    }
            }

            runBlocking {
                (1..5).asFlow()
                    .onEach { delay(200) }
                    .onStart { println("onStart") }
                    .collect { println(it) }
            }

        }

    }

    fun simple(): Flow<Int> = flow {
        for (i in 1..3) {
            delay(100)
            println("Emitting $i")
            emit(i)
        }
    }

    fun main() = runBlocking {
        try {
            flow {
                for (i in 1..5) {
                    delay(100)
                    emit(i)
                }
            }.collect { println(it) }
        } finally {
            println("Done")
        }
    }

    fun main2() = runBlocking {
        flow {
            for (i in 1..5) {
                delay(100)
                emit(i)
            }
        }.onCompletion { println("Done") }
            .collect { println(it) }
    }

    fun main3() = runBlocking {
//        val cosTime = measureTimeMillis {
            (1..5).asFlow().onEach {
                delay(100)
                println("produce data: $it")

                //SUSPEND DROP_OLDEST DROP_LATEST
            }.buffer(2, BufferOverflow.SUSPEND)
                 .collect {
                    delay(700)
                    println("collect: $it")
                }
//        }
//        println("cosTime: $cosTime")
    }


    fun main4() = runBlocking {
//        val cosTime = measureTimeMillis {
            (1..5).asFlow().onEach {
                delay(100)
                println("produce data: $it")
            }.conflate()
                .collect {
                    delay(700)
                    println("collect: $it")
                }
//        }
//        println("cosTime: $cosTime")
    }


    fun main5() = runBlocking {
        (1..5).asFlow().onEach {
            if (it == 4) {
                throw Exception("test exception")
            }
            delay(100)
            println("produce data: $it")
        }.onCompletion {
            println("onCompletion")
        }.collect {
            println("collect: $it")
        }
    }


    fun main6() = runBlocking {
        (1..5).asFlow().onEach {
            if (it == 4) {
                throw Exception("test exception")
            }
            delay(100)
            println("produce data: $it")
        }.onCompletion { cause ->
            if (cause != null) {
                println("flow completed exception")
            } else {
                println("onCompletion")
            }
        }.collect {
            println("collect: $it")
        }
    }

    fun main7() = runBlocking {
        (1..5).asFlow().onEach {
            if (it == 4) {
                throw Exception("test exception")
            }
            delay(100)
            println("produce data: $it")
        }.onCompletion { cause ->
            if (cause != null) {
                println("flow completed exception")
            } else {
                println("onCompletion")
            }
        }.catch { ex ->
            println("catch exception: ${ex.message}")
        }.collect {
            println("collect: $it")
        }
    }

    fun main8() = runBlocking {
        (1..5).asFlow().onEach {
            if (it == 4) {
                throw Exception("test exception")
            }
            delay(100)
            println("produce data: $it")
        }.catch { ex ->
            println("catch exception: ${ex.message}")
        }.onCompletion { cause ->
            if (cause != null) {
                println("flow completed exception")
            } else {
                println("onCompletion")
            }
        }.collect {
            println("collect: $it")
        }
    }

    fun main9() = runBlocking {
        (1..5).asFlow().onEach {
            if (it == 4) {
                throw Exception("test exception")
            }
            delay(100)
            println("produce data: $it")
        }.retry(2) {
            it.message == "test exception"
        }.catch { ex ->
            println("catch exception: ${ex.message}")
        }.collect {
            println("collect: $it")
        }
    }

    fun main10() = runBlocking {
        (1..5).asFlow().onEach {
            if (it == 4) {
                throw Exception("test exception")
            }
            delay(100)
            println("produce data: $it")
        }.retryWhen { cause, attempt ->
            cause.message == "test exception" && attempt < 2
        }.catch { ex ->
            println("catch exception: ${ex.message}")
        }.collect {
            println("collect: $it")
        }
    }


    override fun createObserver() {


    }

    override fun onDestroy() {
        super.onDestroy()
        "Test1Activity onDestroy".logd()
    }
}

