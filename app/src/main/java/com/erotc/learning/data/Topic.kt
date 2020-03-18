package com.erotc.learning.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created on 3/18/2020.
 */
@Entity
data class Topic (
    @PrimaryKey var id: Long = 0,
    var name: String? = null,
    var order: Int = 0
)