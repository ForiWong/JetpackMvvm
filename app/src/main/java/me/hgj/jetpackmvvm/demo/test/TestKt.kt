package me.hgj.jetpackmvvm.demo.test

/**
 * Created by wlp on 2023/7/6
 * Description:
 */
class TestKt {

    val myVersion = VERSION
    val myVersion2 = StaticClass.version

    fun test(){
        var b = StaticClass.A.bbb

        StaticClass.A.foo()
    }
}