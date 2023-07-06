#1、UnPeekLiveData的使用
LiveData
observable

2、关于信息弹框、loading弹框的设计
dialog怎么能这样缓存呢？另外，也会导致溢出吧。 
LeakCanary检测到果然是泄漏了

#3、LoadService的使用

4、网络请求管理类
同时异步请求2个接口，请求完成后合并数据todo 这种方法可以参考
参考关于网络请求、状态的管理

#5、点赞红心控件 CollectView ✅

#6、viewpager2的使用
viewpager及recyclerview

# 7、navigation的使用

#8、SettingFragment : PreferenceFragmentCompat(), todo extends PreferenceFragmentCompat 这个方式还是可以看看的

#9、CoroutineScope
   CoroutineScope可以理解为协程的作用域，可以管理其域内的所有协程。

10、Mvvm的设计 获取当前类绑定的泛型 ViewModel -> clazz
   通过泛型填充binding

#12、logUtil工具不好，没法跟踪代码
   StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
   //此方法用于返回表示线程的堆栈转储的堆栈跟踪元素数组。

13、Android 轻量级存储方案（SharedPreferences、MMKV、Jetpack DataStore）