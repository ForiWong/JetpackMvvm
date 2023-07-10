package me.hgj.jetpackmvvm.demo.test;

import me.hgj.jetpackmvvm.base.KtxKt;

/**
 * Created by wlp on 2023/7/6
 * Description:
 */
public class TestJava {

    public static void main(String[] args) {
        KtxKt.getAppContext();

        //获取kotlin中的顶层变量
        StaticClassKt.getVERSION();
        StaticClass.Companion.getVersion();

        String b = StaticClass.A.INSTANCE.getBbb();
        StaticClass.A.INSTANCE.foo();
    }
}
