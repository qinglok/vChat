package me.linx.vchat.app.data.api

object Api {
    // 服务器地址
//    val baseUrl by lazy { "https://192.168.0.5:8443/vchat/" }
    val baseUrl by lazy { "http://192.168.1.68:8080/vchat/" }
//    val baseUrl by lazy { "https://120.78.94.116:8080/" }
    // 静态资源地址
    val baseFileDir by lazy { baseUrl }
    // netty
//    val imHost by lazy { "192.168.0.5" }
    val imHost by lazy { "192.168.1.68" }
//    val imHost by lazy { "120.78.94.116" }
    val imPort by lazy { 8888 }

    // 登录
    val login by lazy { "app/login" }
    // 登录，并验证密保
    val loginAndVerifySecret by lazy { "app/loginAndVerifySecret" }
    // 注册
    val register by lazy { "app/register" }
    // 修改昵称
    val editNickname by lazy { "user/editNickname" }
    // 修改头像
    val editAvatar by lazy { "biz/upload" }
    // 退出登录
    val logout by lazy { "user/logout" }
    // 登录超时测试
    val loginTimeoutTest by lazy { "user/loginTimeoutTest" }
    // 获取用户最新信息
    val getUserProfile by lazy { "user/getUserProfile" }
    // 获取某个用户最新信息
    val getUserProfileById by lazy { "user/getUserProfileById" }
    // 获取在线用户
    val getActiveUserProfile by lazy { "user/getActiveUserProfile" }

}