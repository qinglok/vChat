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

@Entity(indices = [Index(value = arrayOf("bizId"), unique = true)])
class User() : BaseObservable(), Parcelable {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @get:Bindable
    var id: Long = 0
        set(value) {
            field = value
            notifyPropertyChanged(BR.id)
        }

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
    var nickname: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.nickname)
        }

    @get:Bindable
    var avatar: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.avatar)
        }

    @get:Bindable
    var updateTime: Long? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.updateTime)
        }

    constructor(parcel: Parcel) : this() {
        id = parcel.readLong()
        bizId = parcel.readValue(Long::class.java.classLoader) as? Long
        email = parcel.readString()
        token = parcel.readString()
        nickname = parcel.readString()
        avatar = parcel.readString()
        updateTime = parcel.readValue(Long::class.java.classLoader) as? Long
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeValue(bizId)
        parcel.writeString(email)
        parcel.writeString(token)
        parcel.writeString(nickname)
        parcel.writeString(avatar)
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
