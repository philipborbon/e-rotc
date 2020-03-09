package com.erotc.learning.util

/**
 * Created on 11/19/2018.
 */
object ScoreUtil {
    private val LEVEL_SCORES = arrayOf(intArrayOf(300, 280, 260, 240, 220, 200, 180, 160, 140, 120), intArrayOf(400, 380, 360, 340, 320, 300, 280, 260, 240, 220), intArrayOf(500, 480, 460, 440, 420, 400, 380, 360, 340, 320))

    fun getScore(level: Int, seconds: Int): Int {
        if (seconds < 0) {
            throw Exception("Seconds should be greater than or equal to zero")
        } else if (level < 1 || level > 3) {
            throw Exception("Levels should be greater than or equal to 1 and less than or equal to 3")
        }
        if (seconds > 9) {
            return 0
        }
        val levelIndex = level - 1
        return LEVEL_SCORES[levelIndex][seconds]
    }
}