package me.hgj.jetpackmvvm.demo.note.lifecycle

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

class ServiceComponent : LifecycleObserver {

    val TAG = "ServiceComponent"

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun start() {
        Log.d(TAG, "component start")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun stop() {
        Log.d(TAG, "component stop")
    }

}

