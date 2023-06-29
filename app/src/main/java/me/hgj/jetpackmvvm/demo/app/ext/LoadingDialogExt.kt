package me.hgj.jetpackmvvm.demo.app.ext

import android.content.Context
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import me.hgj.jetpackmvvm.demo.R
import me.hgj.jetpackmvvm.demo.app.util.SettingUtil
import me.hgj.jetpackmvvm.ext.util.logd

/**
 * @author : hgj
 * @date : 2020/6/28
 */

/*
 * loading框
 * 这个弹框是不能缓存的
 * android.view.WindowManager$BadTokenException: Unable to add window -- token android.os.BinderProxy@76165e3
 *  is not valid; is your activity running?
 * todo 怎么能这样缓存呢？另外，也会导致溢出吧。
 *  LeakCanary检测到果然是泄漏了
 */
//private var loadingDialog: MaterialDialog? = null

/**
 * 以上，相当于是静态的了
 * 可以改为扩展属性吗？
 * 其实，可扩展属性其实也是静态的
 * 而且get()方法怎么写呢？每调用一次就创建一个，也是没法用的
 *
 * 所以，最好直接还是放到BaseActivity中
 */

val AppCompatActivity.loadingDialog : MaterialDialog
    get() {
        "创建一下 dialog".logd()
        return MaterialDialog(this)
            .cancelable(true)
            .cancelOnTouchOutside(false)
            .cornerRadius(12f)
            .customView(R.layout.layout_custom_progress_dialog_view)
            .lifecycleOwner(this)
    }
//    set(value) {
//        loadingDialog = value
//    }

val Fragment.loadingDialog : MaterialDialog
    get() = activity.let {
        MaterialDialog(it as Context)
            .cancelable(true)
            .cancelOnTouchOutside(false)
            .cornerRadius(12f)
            .customView(R.layout.layout_custom_progress_dialog_view)
            .lifecycleOwner(this)
    }
//    set(value) {
//        loadingDialog = value
//    }

/**
 * 打开等待框
 */
fun AppCompatActivity.showLoadingExt(message: String = "请求网络中") {
    loadingDialog.toString() + "--1".logd()
    if (!this.isFinishing) {
//        if (loadingDialog == null) {
//            loadingDialog = MaterialDialog(this)
//                    .cancelable(true)
//                    .cancelOnTouchOutside(false)
//                    .cornerRadius(12f)
//                    .customView(R.layout.layout_custom_progress_dialog_view)
//                    .lifecycleOwner(this)
//        }
        loadingDialog?.getCustomView()?.run {
            this.findViewById<TextView>(R.id.loading_tips).text = message
            this.findViewById<ProgressBar>(R.id.progressBar).indeterminateTintList = SettingUtil.getOneColorStateList(this@showLoadingExt)
        }
        loadingDialog?.show()
        loadingDialog.toString() + "--2".logd()
    }
}

/**
 * 打开等待框
 */
fun Fragment.showLoadingExt(message: String = "请求网络中") {
    activity?.let {
        if (!it.isFinishing) {
//            if (loadingDialog == null) {
//                loadingDialog = MaterialDialog(it)
//                    .cancelable(true)
//                    .cancelOnTouchOutside(false)
//                    .cornerRadius(12f)
//                    .customView(R.layout.layout_custom_progress_dialog_view)
//                    .lifecycleOwner(this)
//            }
            loadingDialog?.getCustomView()?.run {
                this.findViewById<TextView>(R.id.loading_tips).text = message
                this.findViewById<ProgressBar>(R.id.progressBar).indeterminateTintList = SettingUtil.getOneColorStateList(it)
            }
            loadingDialog?.show()
        }
    }
}

/**
 * 关闭等待框
 */
fun AppCompatActivity.dismissLoadingExt() {
    loadingDialog.dismiss()
//    loadingDialog = null
}

/**
 * 关闭等待框
 */
fun Fragment.dismissLoadingExt() {
    loadingDialog.dismiss()
//    loadingDialog = null
}
