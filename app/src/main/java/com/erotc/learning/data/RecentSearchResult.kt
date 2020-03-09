package com.erotc.learning.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Created on 11/7/2018.
 */
@Entity(foreignKeys = [
    ForeignKey (
            entity = DictionaryEntry::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("entryId"),
            onDelete = ForeignKey.CASCADE
    )
])
data class RecentSearchResult (
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    var entryId: Long = 0
)