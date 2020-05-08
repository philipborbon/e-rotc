package com.erotc.learning.repository

import android.content.Context
import androidx.room.Room
import com.erotc.learning.data.Assessment
import com.erotc.learning.data.Leaderboard
import com.erotc.learning.data.Lecture
import com.erotc.learning.data.Topic
import com.erotc.learning.database.Database
import java.util.*

/**
 * Created on 11/7/2018.
 */
class LearnRepository private constructor(context: Context) {
    private val database = Room.databaseBuilder(context, Database::class.java, "com.rotc.learning.database")
            .allowMainThreadQueries()
            .build()

    private val topicDao = database.topicDao()
    private val lectureDao = database.lectureDao()
    private val assessmentDao = database.assessmentDao()
    private val leaderboardDao = database.leaderboardDao()

    fun searchLecture(keyword: String): List<Lecture> {
        return lectureDao.search("%$keyword%")
    }

    fun getAllLecture(): List<Lecture> {
        return lectureDao.getAll()
    }

    fun getAllTopic(): List<Topic> {
        return topicDao.getAll()
    }

    fun getLeaderboard(): List<Leaderboard> {
        return leaderboardDao.getAll()
    }

    fun saveLeaderboard(name: String?, score: Int): Leaderboard {
        val leaderboard = Leaderboard()
        leaderboard.name = name
        leaderboard.score = score
        leaderboard.date = Calendar.getInstance().time

        val id = leaderboardDao.create(leaderboard)
        leaderboard.id = id

        return leaderboard
    }

    fun getHighScore(): Leaderboard {
        return leaderboardDao.getHighScore()
    }

    fun saveTopics(topics: List<Topic>): List<Topic> {
        topics.forEachIndexed { index, topic ->
            topic.sort = index
        }

        topicDao.create(topics).forEachIndexed { index, value ->
            topics[index].id = value
        }

        return topics
    }

    fun saveLectures(lectures: List<Lecture>): List<Lecture> {
        lectures.forEachIndexed { index, lecture ->
            lecture.sort = index
        }

        lectureDao.create(lectures).forEachIndexed { index, value ->
            lectures[index].id = value
        }

        return lectures
    }

    fun saveAssessments(questions: List<Assessment>): List<Assessment> {
        assessmentDao.create(questions).forEachIndexed { index, value ->
            questions[index].id = value
        }

        return questions
    }

    fun getRandomQuestions(topicId: Long): List<Assessment> {
        return assessmentDao.getRandomQuestions(topicId, QUESTION_LIMIT_COUNT).shuffled()
    }

    val isTopicEmpty: Boolean
        get() = topicDao.countOf() == 0

    val isLectureEmpty: Boolean
        get() = lectureDao.countOf() == 0

    val isAssessmentEmpty: Boolean
        get() = assessmentDao.countOf() == 0

    companion object {
        private const val QUESTION_LIMIT_COUNT = 5

        private var learnRepository: LearnRepository? = null

        fun getInstance(context: Context): LearnRepository {
            if (learnRepository == null) {
                learnRepository = LearnRepository(context)
            }

            return learnRepository!!
        }
    }
}