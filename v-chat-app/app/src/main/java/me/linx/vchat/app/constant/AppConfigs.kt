package me.linx.vchat.app.constant

object AppConfigs {
    val connectTimeout by lazy { 30000L }
    val readTimeout by lazy { 10000L }
    val writeTimeout by lazy { 10000L }

    val databaseName by lazy { "vchat_db" }

}