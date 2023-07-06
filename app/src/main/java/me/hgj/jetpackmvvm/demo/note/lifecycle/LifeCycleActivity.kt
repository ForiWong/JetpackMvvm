package me.hgj.jetpackmvvm.demo.note.lifecycle

import android.location.Location
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import me.hgj.jetpackmvvm.demo.R
import me.hgj.jetpackmvvm.demo.databinding.ActivityTestOneBinding

class LifeCycleActivity : AppCompatActivity() {

    lateinit var binding: ActivityTestOneBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_test_one)

        lifecycle.addObserver(
            LocationComponent(this,
                object : LocationComponent.OnLocationChangedListener {
                    override fun onChanged(location: Location) {
                        //展示收到的位置信息
                        binding.tv.text =
                            "latitude:${location.latitude}  longitude:${location.longitude}"
                    }
                })
        )
    }
}

