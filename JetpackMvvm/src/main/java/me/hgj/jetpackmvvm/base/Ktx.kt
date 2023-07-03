package me.hgj.jetpackmvvm.base

import android.app.Application
import android.content.ContentProvider
import android.content.ContentValues
import android.content.IntentFilter
import android.database.Cursor
import android.net.ConnectivityManager
import android.net.Uri
import androidx.lifecycle.ProcessLifecycleOwner
import me.hgj.jetpackmvvm.ext.lifecycle.KtxAppLifeObserver
import me.hgj.jetpackmvvm.ext.lifecycle.KtxLifeCycleCallBack
import me.hgj.jetpackmvvm.network.manager.NetworkStateReceive

/**
 * 作者　: hegaojian
 * 时间　: 2019/12/14
 * 描述　:
 */

// Kotlin 中的类不能拥有静态成员，即在 Kotlin 中是没有 static 关键字的，所以 Kotlin 中没有静态方法和静态成员。
// 在 Kotlin 中想要表达这种概念，可使用 包级别函数（顶层函数） 和 伴生对象
// 这里声明的顶层属性就相当于Java中的静态变量
val appContext: Application by lazy { Ktx.app }

//manifest中配置ContentProvider
class Ktx : ContentProvider() {

    companion object {
        lateinit var app: Application
        private var mNetworkStateReceive: NetworkStateReceive? = null
        var watchActivityLife = true
        var watchAppLife = true
    }

    override fun onCreate(): Boolean {
        val application = context!!.applicationContext as Application
        install(application)
        return true
    }

    private fun install(application: Application) {
        app = application
        mNetworkStateReceive = NetworkStateReceive()
        app.registerReceiver(
            mNetworkStateReceive,
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )

        //Activity活动生命周期回调
        if (watchActivityLife) application.registerActivityLifecycleCallbacks(KtxLifeCycleCallBack())
        //ProcessLifecycleOwner:Class that provides lifecycle for the whole application process.
        //进程生命周期，也就是这个应用的生命周期
        if (watchAppLife) ProcessLifecycleOwner.get().lifecycle.addObserver(KtxAppLifeObserver)
    }


    override fun insert(uri: Uri, values: ContentValues?): Uri? = null

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? = null


    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int = 0

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int = 0

    override fun getType(uri: Uri): String? = null
}