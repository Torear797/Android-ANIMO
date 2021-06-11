package com.animo.ru.models

import android.os.Parcel
import android.os.Parcelable

class GuideData(
    val id: Int = 0,
    val name: String? = "",
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GuideData> {
        override fun createFromParcel(parcel: Parcel): GuideData {
            return GuideData(parcel)
        }

        override fun newArray(size: Int): Array<GuideData?> {
            return arrayOfNulls(size)
        }
    }
}