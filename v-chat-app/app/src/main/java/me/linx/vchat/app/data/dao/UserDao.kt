package me.linx.vchat.app.data.dao

import androidx.room.*
import io.reactivex.Single
import me.linx.vchat.app.data.entity.User

@Dao
interface UserDao {

    @Query("SELECT * FROM user WHERE bizId = :userId")
    fun findByBizId(userId: Long): Single<User>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: User) : Long

    @Delete
    fun delete(user: User)
}