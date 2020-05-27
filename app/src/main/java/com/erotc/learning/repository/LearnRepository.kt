package com.erotc.learning.repository

import android.content.Context
import androidx.room.Room
import com.erotc.learning.data.*
import com.erotc.learning.database.Database
import java.util.*

/**
 * Created on 11/7/2018.
 */
class LearnRepository private constructor(context: Context) {
    private val database = Room.databaseBuilder(context, Database::class.java, "com.rotc.learning.database")
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()

    private val topicDao = database.topicDao()
    private val lectureDao = database.lectureDao()
    private val tutorialDao = database.tutorialDao()
    private val assessmentDao = database.assessmentDao()
    private val leaderboardDao = database.leaderboardDao()

    fun searchLecture(keyword: String): List<Lecture> {
        return lectureDao.search("%$keyword%")
    }

    fun searchTutorial(keyword: String): List<Tutorial> {
        return tutorialDao.search("%$keyword%")
    }

    fun getAllLecture(): List<Lecture> {
        return lectureDao.getAll()
    }

    fun getAllTutorial(): List<Tutorial> {
        return tutorialDao.getAll()
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

    fun saveTutorials(tutorials: List<Tutorial>): List<Tutorial> {
        tutorials.forEachIndexed { index, lecture ->
            lecture.sort = index
        }

        tutorialDao.create(tutorials).forEachIndexed { index, value ->
            tutorials[index].id = value
        }

        return tutorials
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

    val isTutorialEmpty: Boolean
        get() = tutorialDao.countOf() == 0

    val isAssessmentEmpty: Boolean
        get() = assessmentDao.countOf() == 0

    val hasMissingData: Boolean
        get() = isTopicEmpty || isLectureEmpty || isTutorialEmpty || isAssessmentEmpty

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