package me.hgj.jetpackmvvm.demo.note.moshi

data class BaseResp<T>(
    val code: Int,
    val msg: String,
    val data: T
)
