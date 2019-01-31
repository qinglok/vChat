package me.linx.vchat.app.data.api

object Api{
    // 服务器地址
    val baseUrl by lazy { "https://192.168.0.5:8443/vchat/" }
    // 静态资源地址
    val baseFileDir by lazy { baseUrl }

    // 登录
    val login by lazy { "app/login" }
    // 注册
    val register by lazy { "app/register" }
    // 修改昵称
    val editNickName by lazy { "user/editNickName" }
    // 修改头像
    val editHeadImg by lazy { "biz/upload" }
}