package com.erotc.learning.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.erotc.learning.data.Assessment
import com.erotc.learning.data.Lecture
import com.erotc.learning.data.Topic

/**
 * Created on 3/8/2020.
 */
@Database(entities = [
    Topic::class,
    Assessment::class,
    Lecture::class
], version = 1)
@TypeConverters(Converter::class)
abstract class Database : RoomDatabase() {
    abstract fun topicDao(): TopicDao
    abstract fun assessmentDao(): AssessmentDao
    abstract fun lectureDao(): LectureDao
}