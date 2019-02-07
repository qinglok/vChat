package me.linx.vchat.app.constant

object AppConfigs {
    val connectTimeout by lazy { 8000L }
    val readTimeout by lazy { 8000L }
    val writeTimeout by lazy { 8000L }

    val databaseName by lazy { "vchat_db" }

}