- **基于MVVM模式集成谷歌官方推荐的JetPack组件库：LiveData、ViewModel、Lifecycle、Navigation组件**
- **使用kotlin语言，添加大量拓展函数，简化代码**
- **加入Retrofit网络请求,协程，帮你简化各种操作，让你快速请求网络**

|-base mvvm 基础类
    |-BaseActivity\BaseFragment\BaseViewModel
    |-BaseApp
|-callback 可观察的数据
    |-ObservableField
    |-LiveData
    |-UnPeekLiveData
|-ext 扩展方法属性
    |-extension method
|-navigation 导航
|-network 网络
    |-Interceptor 拦截器
    |-RetrofitManager 管理类
    |-Exception
    |-NetworkState
    |-ResultState
|-util 工具类
    |-utils