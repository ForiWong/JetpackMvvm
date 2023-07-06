# 对kotlin友好的现代 JSON 库 moshi 基本使用和实战 https://blog.csdn.net/yuzhiqiang_1993/article/details/124076400
# https://blog.csdn.net/yuzhiqiang_1993/article/details/123953523
gson在处理kotlin data class时的一些坑，感兴趣的可以了解一下。
总结一下有一下两点:
属性声明时值不能为null，结果反序列化后值为null，跟预期不符
默认值可能不生效，可能被null覆盖
在文章末尾也介绍了解决办法就是不要使用gson，因为gson主要还是针对java的库，没有对kotlin做单独的支持。
我们可以使用moshi或jackson来解决上面所说的问题。

# moshi解决了gson使用在kotlin中遇到的问题
# moshi使用、对象、集合、泛型、复杂数据类型等
# moshi与retrofit

moshi是square出品。
做Android的对square都不陌生，我们常用的 retrofit、okhttp、leakcanary等都是square在维护的。那moshi显然在搭配
retrofit上有着得天独厚的优势，况且moshi还针对Android。

val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
retrofit = Retrofit.Builder()
.baseUrl(ServerConstants.apiUrl)
.client(initOkhttpClient())
.addConverterFactory(MoshiConverterFactory.create(moshi))
.build()


