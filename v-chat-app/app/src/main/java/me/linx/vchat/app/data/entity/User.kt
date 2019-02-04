package me.linx.vchat.app.data.entity

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import me.linx.vchat.app.BR

@Entity(indices = arrayOf(Index(value = arrayOf("bizId"), unique = true)))
class User() : BaseObservable(), Parcelable {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    var id: Long = 0

    @get:Bindable
    var bizId: Long? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.bizId)
        }

    @get:Bindable
    var email: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.email)
        }

    @get:Bindable
    var token: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.token)
        }

    @get:Bindable
    var nickName: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.nickName)
        }

    @get:Bindable
    var headImg: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.headImg)
        }

    var createTime: Long? = null
    var updateTime: Long? = null

    constructor(parcel: Parcel) : this() {
        id = parcel.readLong()
        bizId = parcel.readValue(Long::class.java.classLoader) as? Long
        email = parcel.readString()
        token = parcel.readString()
        nickName = parcel.readString()
        headImg = parcel.readString()
        createTime = parcel.readValue(Long::class.java.classLoader) as? Long
        updateTime = parcel.readValue(Long::class.java.classLoader) as? Long
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeValue(bizId)
        parcel.writeString(email)
        parcel.writeString(token)
        parcel.writeString(nickName)
        parcel.writeString(headImg)
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
