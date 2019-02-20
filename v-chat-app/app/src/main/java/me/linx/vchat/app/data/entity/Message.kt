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
class Message() : BaseObservable(), Parcelable {
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
    var fromId: Long? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.fromId)
        }

    @get:Bindable
    var fromName: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.fromName)
        }

    @get:Bindable
    var fromAvatar: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.fromAvatar)
        }

    @get:Bindable
    var toId: Long? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.toId)
        }

    @get:Bindable
    var toName: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.toName)
        }

    @get:Bindable
    var toAvatar: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.toAvatar)
        }

    @get:Bindable
    var content: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.content)
        }

    @get:Bindable
    var read: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.read)
        }

    @get:Bindable
    var sent: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.sent)
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
        fromId = parcel.readValue(Long::class.java.classLoader) as? Long
        fromName = parcel.readString()
        fromAvatar = parcel.readString()
        toId = parcel.readValue(Long::class.java.classLoader) as? Long
        toName = parcel.readString()
        toAvatar = parcel.readString()
        content = parcel.readString()
        read = parcel.readInt() == 1
        sent = parcel.readInt() == 1
        updateTime = parcel.readValue(Long::class.java.classLoader) as? Long
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeValue(bizId)
        parcel.writeValue(fromId)
        parcel.writeValue(fromName)
        parcel.writeValue(fromAvatar)
        parcel.writeValue(toId)
        parcel.writeValue(toName)
        parcel.writeValue(toAvatar)
        parcel.writeString(content)
        parcel.writeInt(if (read) 1 else 0)
        parcel.writeInt(if (sent) 1 else 0)
        parcel.writeValue(updateTime)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Message> {
        override fun createFromParcel(parcel: Parcel): Message {
            return Message(parcel)
        }

        override fun newArray(size: Int): Array<Message?> {
            return arrayOfNulls(size)
        }
    }

}