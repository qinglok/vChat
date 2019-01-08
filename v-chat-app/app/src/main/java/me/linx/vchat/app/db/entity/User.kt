package me.linx.vchat.app.db.entity

import android.os.Parcel
import android.os.Parcelable
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import me.linx.vchat.app.db.AppDatabase


@Table(database = AppDatabase::class)
data class User(
    @PrimaryKey(autoincrement = true) var id: Long? = 0,
    @Column var bizId: Long? = null,
    @Column var email: String? = null,
    @Column var token: String? = null,
    @Column var createTime: Long? = null,
    @Column var updateTime: Long? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readValue(Long::class.java.classLoader) as? Long
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(id)
        parcel.writeValue(bizId)
        parcel.writeString(email)
        parcel.writeString(token)
        parcel.writeValue(createTime)
        parcel.writeValue(updateTime)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}
