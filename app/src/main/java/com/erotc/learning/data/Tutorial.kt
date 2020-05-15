package com.erotc.learning.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

/**
 * Created on 5/15/2020.
 */
@Parcelize
@Entity(foreignKeys = [
    ForeignKey (
            entity = Lecture::class,
            parentColumns = ["id"],
            childColumns = ["topicid"],
            onDelete = ForeignKey.CASCADE
    )
])
data class Tutorial (
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    var topicid: Long = 0,
    var title: String? = null,
    var description: String? = null,
    var file: String? = null,
    var sort: Int = 0
) : Parcelable