package me.linx.vchat.app.data.api

object Api{
    // 服务器地址
    val baseUrl by lazy { "https://192.168.0.5:8443/vchat/" }
    // 静态资源地址
    val baseFileDir by lazy { baseUrl }

    // 登录
    val login by lazy { "app/login" }
    // 登录，并验证密保
    val loginAndVerifySecret by lazy { "app/loginAndVerifySecret" }
    // 注册
    val register by lazy { "app/register" }
    // 修改昵称
    val editNickName by lazy { "user/editNickName" }
    // 修改头像
    val editHeadImg by lazy { "biz/upload" }
    // 退出登录
    val logout by lazy { "user/logout" }
    // 登录超时测试
    val loginTimeoutTest by lazy { "user/loginTimeoutTest" }

}