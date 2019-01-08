package me.linx.vchat.app.common.net

data class JsonResult<T>(var code: Int, var msg: String?, var data: T?) {
}