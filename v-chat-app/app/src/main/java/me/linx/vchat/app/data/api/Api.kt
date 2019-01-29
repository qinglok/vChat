package me.linx.vchat.app.data.api

object Api{
    // 服务器地址
    val baseUrl by lazy { "http://192.168.0.5:8443/vchat/" }
    // 静态资源地址
    val baseFileDir by lazy { baseUrl }

    val login by lazy { "app/login" }
    val register by lazy { "app/register" }
    val editHeadImg by lazy { "biz/upload" }
}