package me.linx.vchat.app.data.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.blankj.utilcode.util.Utils
import me.linx.vchat.app.constant.AppConfigs
import me.linx.vchat.app.data.dao.UserDao
import me.linx.vchat.app.data.entity.User

@Database(entities = [User::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        val db by lazy { Room.databaseBuilder(
            Utils.getApp(),
            AppDatabase::class.java, AppConfigs.databaseName
        ).build() }
    }

}