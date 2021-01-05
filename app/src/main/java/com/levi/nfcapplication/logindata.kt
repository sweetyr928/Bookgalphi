package com.levi.nfcapplication
import android.os.Parcel
import android.os.Parcelable
class logindata(var id: String?, var pass:String?,var book:String?,var book1:String?,var book2: String?,var book3: String?) {

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString()
    ) {
    }

    fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(pass)
        parcel.writeString(book)
        parcel.writeString(book1)
        parcel.writeString(book2)
        parcel.writeString(book3)
    }

    fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<logindata> {
        override fun createFromParcel(parcel: Parcel): logindata{
            return logindata(parcel)
        }

        override fun newArray(size: Int): Array<logindata?> {
            return arrayOfNulls(size)
        }
    }
}