package com.erotc.learning.data

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.erotc.learning.repository.DictionaryRepository
import java.util.*

/**
 * Created on 10/15/2018.
 */
@Entity
data class QuestionSet (
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    var label: String? = null,
    var level: Int = 0,
    var isLocked: Boolean = false,
    var pointsToProceed: Int = 0,
    var highScore: Int = 0,
    var answerSet: String? = null
) {
    @Ignore
    private var answerSetInList: List<String?>? = null

    @Ignore
    private var random: Random? = null

    @Ignore
    private var nextPulled = false

    @Ignore
    private var nextLevel: QuestionSet? = null

    fun shouldUnlockNextLevel(): Boolean {
        return highScore >= pointsToProceed
    }

    fun getRandomAnswerSet(count: Int): ArrayList<String?>? {
        if (answerSetInList == null) {
            answerSetInList = answerSet?.split(",")
        }

        if (random == null) {
            random = Random()
        }

        val selectedRandomIndex = ArrayList<Int>()
        while (selectedRandomIndex.size < count) {
            val index = random?.nextInt(answerSetInList?.size ?: 0) ?: 0

            if (selectedRandomIndex.size != 0) {
                val lastIndex = selectedRandomIndex[selectedRandomIndex.size - 1]
                if (lastIndex == index) {
                    continue  // prevent duplicate random
                }
            }

            selectedRandomIndex.add(index)
        }
        val randomAnswerSet = ArrayList<String?>()
        for (integer in selectedRandomIndex) {
            val answer = answerSetInList?.get(integer)
            randomAnswerSet.add(answer)
        }
        return randomAnswerSet
    }

    fun getNextLevel(repository: DictionaryRepository): QuestionSet? {
        if (nextLevel == null && !nextPulled) {
            nextLevel = repository.getNextLevel(this)
            nextPulled = true
        }

        return nextLevel
    }
}