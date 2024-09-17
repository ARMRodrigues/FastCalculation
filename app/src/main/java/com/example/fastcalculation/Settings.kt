package com.example.fastcalculation

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Settings(
    val playerName : String = "",
    var rounds : Int = 0,
    var roundInterval : Long = 0L
) : Parcelable
