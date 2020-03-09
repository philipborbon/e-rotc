package com.erotc.learning.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created on 10/15/2018.
 */
@Entity
data class DictionaryEntry (
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    var tagalog: String? = null,
    var hiligaynon: String? = null,
    var ilocano: String? = null,
    var definition: String? = null,
    var soundFile: String? = null,
    var tagalogExample: String? = null,
    var hiligaynonExample: String? = null,
    var ilocanoExample: String? = null
) {
    companion object {
        fun getIndexOfMatch(keyword: String, dataList: List<DictionaryEntry>): Int {
            var keyword = keyword
            keyword = keyword.toLowerCase().trim()
            for (i in dataList.indices) {
                val data = dataList[i]
                if (data.tagalog?.toLowerCase() == keyword) {
                    return i
                }
            }
            for (i in dataList.indices) {
                val data = dataList[i]
                if (data.hiligaynon?.toLowerCase() == keyword) {
                    return i
                }
            }
            for (i in dataList.indices) {
                val data = dataList[i]
                if (data.ilocano?.toLowerCase() == keyword) {
                    return i
                }
            }
            return 0
        }
    }
}