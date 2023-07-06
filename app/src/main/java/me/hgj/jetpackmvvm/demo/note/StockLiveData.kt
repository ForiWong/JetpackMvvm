package me.hgj.jetpackmvvm.demo.note

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import java.math.BigDecimal


class StockLiveData(symbol: String) : LiveData<BigDecimal>() {

    private val stockManager = StockManager(symbol)

    private val listener = { price: BigDecimal -> Unit
        //value = price//setValue()
        postValue(price)
    }

    /**
     * Called when the number of active observers change from 0 to 1.
     * This callback can be used to know that this LiveData is being used thus should be kept up to date.
     * 当活动观察者的数量从0变为1时调用。这个回调可以用来知道这个LiveData正在被使用，因此应该保持最新。
     */
    override fun onActive() {
        stockManager.requestPriceUpdates(listener)
    }

    /**
     * Called when the number of active observers change from 1 to 0.
     * This does not mean that there are no observers left, there may still be observers but their lifecycle
     * states aren't Lifecycle.State.STARTED or Lifecycle.State.RESUMED (like an Activity in the back stack).
     * You can check if there are observers via hasObservers().
     * 当活动观察者的数量从1变为0时调用。
    这并不意味着没有观察器，可能仍然有观察器，但它们的生命周期状态不是lifeccycle . state . started或lifeccycle . state . resume(就像后台堆栈中的一个Activity)。
    你可以通过hasObservers()检查是否有观察者。
     */
    override fun onInactive() {
        stockManager.removeUpdates(listener)
    }

    companion object {

        private lateinit var sInstance: StockLiveData

        @MainThread//只能再UI线程被调用
        fun get(symbol: String): StockLiveData {
            sInstance = if (Companion::sInstance.isInitialized) sInstance else StockLiveData(symbol)
            return sInstance
        }
    }
}