package me.hgj.jetpackmvvm.demo.note.lifecycle

import androidx.lifecycle.LifecycleService

open class LifeService : LifecycleService() {

    override fun onCreate() {
        super.onCreate()
        lifecycle.addObserver(ServiceComponent())
    }

}

