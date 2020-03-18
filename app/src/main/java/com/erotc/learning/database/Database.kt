package com.erotc.learning.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.erotc.learning.data.Assessment
import com.erotc.learning.data.Lecture

/**
 * Created on 3/8/2020.
 */
@Database(entities = [
    Assessment::class,
    Lecture::class
], version = 1)
abstract class Database : RoomDatabase() {
    abstract fun assessmentDao(): AssessmentDao
    abstract fun lectureDao(): LectureDao
}