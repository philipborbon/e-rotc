package com.erotc.learning.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.erotc.learning.R
import com.erotc.learning.data.Lecture
import kotlinx.android.synthetic.main.activity_view_lecture.*

class ViewLectureActivity : AppCompatActivity() {

    private lateinit var lecture: Lecture

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_lecture)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        lecture = intent.getParcelableExtra(DATA_LECTURE)

        title = lecture.title
        // TODO: show lecture
    }

    companion object {
        private const val LOG_TAG = "ViewLectureActivity"
        const val DATA_LECTURE = "data-lecture"
    }
}