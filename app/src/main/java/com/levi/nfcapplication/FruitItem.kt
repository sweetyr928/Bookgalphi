package com.levi.nfcapplication
import android.os.Parcel
import android.os.Parcelable

open class FruitItem(var name: String?, var author: String?, var company: String?, var booked: String?, var resId: Int, var sheId: Int , var code: String? ) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(author)
        parcel.writeString(company)
        parcel.writeString(booked)
        parcel.writeInt(resId)
        parcel.writeInt(sheId)
        parcel.writeString(code)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FruitItem> {
        override fun createFromParcel(parcel: Parcel): FruitItem {
            return FruitItem(parcel)
        }

        override fun newArray(size: Int): Array<FruitItem?> {
            return arrayOfNulls(size)
        }
    }
}
