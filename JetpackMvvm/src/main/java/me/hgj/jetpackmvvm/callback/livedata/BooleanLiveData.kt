package me.hgj.jetpackmvvm.callback.livedata

import androidx.lifecycle.MutableLiveData


/**
 * 作者　: hegaojian
 * 时间　: 2019/12/17
 * 描述　:自定义的Boolean类型 MutableLiveData 提供了默认值，避免取值的时候还要判空
 *
 * LiveData，它不仅可以实现与ObservableField相同的功能，而且有以下好处
 * ObservableField只有在数据发生改变时UI才会收到通知，而LiveData不同，只要你postValue或者setValue，UI
 * 都会收到通知，不管数据有无变化。
 * LiveData能感知Activity的生命周期，在Activity不活动的时候不会触发，例如一个Activity不在任务栈顶部。
 */
class BooleanLiveData : MutableLiveData<Boolean>() {

    override fun getValue(): Boolean {
        return super.getValue() ?: false
    }
}

