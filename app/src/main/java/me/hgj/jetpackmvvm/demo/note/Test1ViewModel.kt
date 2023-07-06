package me.hgj.jetpackmvvm.demo.note

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.ext.util.logd

/**
 * 描述　:
 */
class Test1ViewModel : BaseViewModel() {

    val message : MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val message2 : MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    //map()
    private val userLiveData: LiveData<Int> = MutableLiveData<Int>(0)
    val userName: LiveData<String> = Transformations.map(userLiveData) {
            value -> "转变为数字${value}"
    }

    //switchMap()
    private fun getUser(id: String): LiveData<Int> {
        return MutableLiveData(id.toInt())
    }
    val userId: LiveData<String> = MutableLiveData("123")
    val user = Transformations.switchMap(userId) { id -> getUser(id) }

    //switchMap()
    private val addressInput = MutableLiveData<String>()

    val postalCode: LiveData<String> = Transformations.switchMap(addressInput) {
        //address -> repository.getPostCode(address)
        address -> MutableLiveData("code")
    }

    private fun setInput(address: String) {
        addressInput.value = address
    }

    //合并数据源
    private val mediatorLiveData:MediatorLiveData<String> = MediatorLiveData<String>()
    val liveData1 : MutableLiveData<String> = MutableLiveData<String>()
    val liveData2 : MutableLiveData<String> = MutableLiveData<String>()

    init {
        //添加 源 LiveData
        mediatorLiveData.addSource(liveData1) {
            "onChanged3: $it".logd()
            mediatorLiveData.setValue(it)
        }

        //添加 源 LiveData
        mediatorLiveData.addSource(liveData2) {
            "onChanged4: $it".logd()
            mediatorLiveData.setValue(it)
        }

        //通过以上的添加源操作，无论liveData5、liveData6更新，观察者都可以接收到
    }
}