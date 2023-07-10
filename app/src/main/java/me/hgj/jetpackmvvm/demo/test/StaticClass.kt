package me.hgj.jetpackmvvm.demo.test

/**
 * Created by wlp on 2023/7/6
 * Description:
 */

val VERSION : String by lazy { StaticClass.version }

class StaticClass {

    companion object{
        var version = "1.0.0"
    }

    //object的实现和java中的饿汉式单例是一致的
    object A{
        val bbb = "str"
        fun foo(){
        }
    }

}