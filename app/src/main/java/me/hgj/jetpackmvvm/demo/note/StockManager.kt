package me.hgj.jetpackmvvm.demo.note

import me.hgj.jetpackmvvm.ext.util.logd
import java.math.BigDecimal
import java.util.*

class StockManager(val symbol: String) {
    var count = BigDecimal(0)
    private var isStart = false
    val timer by lazy {
        Timer()//todo 计时器的使用
    }

    private val listeners : LinkedList<(BigDecimal) -> Unit> by lazy {
        LinkedList<(BigDecimal) -> Unit>()
    }

    fun requestPriceUpdates(listener: (BigDecimal) -> Unit) {
        "${symbol}开始请求价格变化".logd()
        if(!listeners.contains(listener))listeners.add(listener)
        if(!isStart) getPrice()
        isStart = true
    }

    private fun getPrice(){
        //java.lang.IllegalStateException: Timer already cancelled.
        timer.schedule(object : TimerTask(){
            override fun run() {
                count ++
                "价格变化：$count".logd()
                for(lis in listeners){
                    lis.invoke(count)
                }
            }
        }, 0, 2000)
    }

    fun removeUpdates(listener: (BigDecimal) -> Unit) {
        "${symbol}不再请求价格变化".logd()
        if(listeners.contains(listener)) listeners.remove(listener)
        if(listeners.isEmpty()) {
            //isStart = false
            //timer.cancel()
        }
    }

}