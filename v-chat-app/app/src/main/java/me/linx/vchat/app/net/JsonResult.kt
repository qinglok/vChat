package me.linx.vchat.app.net

/**
 *  服务器返回的Json实体封装
 */
data class JsonResult<T>(var code: Int, var msg: String?, var data: T?)