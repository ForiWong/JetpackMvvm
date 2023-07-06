package me.hgj.jetpackmvvm.demo.note.lifecycle

import android.app.Activity
import android.location.Location
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

open class LocationComponent(
    var context: Activity,
    var onLocationChangedListener: OnLocationChangedListener
) : LifecycleObserver {
    val TAG = "LocationComponent"

    init {
        checkPermission()
    }

    private fun checkPermission() {
        //初始化操作
        Log.d(TAG, "initLocationManager")
//        RxPermissionsTool.with(context)
//            .addPermission(Manifest.permission_group.LOCATION)
//            .initPermission()
//        if (!RxLocationTool.isGpsEnabled(context) || !RxLocationTool.isLocationEnabled(context)) {
//            RxLocationTool.openGpsSettings(context)
//        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun startGetLocation() {
        Log.d(TAG, "startGetLocation")
//        RxLocationTool.registerLocation(
//            context,
//            1000,
//            5,
//            object : RxLocationTool.OnLocationChangeListener {
//                override fun getLastKnownLocation(location: Location) {
//
//                }
//
//                override fun onLocationChanged(location: Location) {
//                    Log.d(
//                        TAG,
//                        "onLocationChanged latitude：${location.latitude} longitude: ${location.longitude}"
//                    )
//                    onLocationChangedListener.onChanged(location)
//                }
//
//                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
//
//                }
//            })
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun stopGetLocation() {
        Log.d(TAG, "stopGetLocation")
//        RxLocationTool.unRegisterLocation()
    }

    interface OnLocationChangedListener {
        fun onChanged(location: Location)
    }
}

