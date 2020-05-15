package com.erotc.learning.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.erotc.learning.data.*

/**
 * Created on 3/8/2020.
 */
@Database(entities = [
    Topic::class,
    Lecture::class,
    Tutorial::class,
    Assessment::class,
    Leaderboard::class
], version = 1)
@TypeConverters(Converter::class)
abstract class Database : RoomDatabase() {
    abstract fun topicDao(): TopicDao
    abstract fun lectureDao(): LectureDao
    abstract fun tutorialDao(): TutorialDao
    abstract fun assessmentDao(): AssessmentDao
    abstract fun leaderboardDao(): LeaderboardDao
}