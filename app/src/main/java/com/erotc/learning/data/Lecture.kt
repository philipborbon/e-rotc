package com.erotc.learning.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

/**
 * Created on 3/14/2020.
 */
@Parcelize
@Entity
data class Lecture (
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    var title: String? = null,
    var file: String? = null,
    var sort: Int = 0
) : Parcelable