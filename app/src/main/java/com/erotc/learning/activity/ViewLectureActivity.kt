package com.erotc.learning.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.erotc.learning.R
import com.erotc.learning.data.Lecture
import com.erotc.learning.util.ApplicationUtil
import kotlinx.android.synthetic.main.activity_view_lecture.*
import kotlinx.android.synthetic.main.content_view_lecture.*

class ViewLectureActivity : AppCompatActivity() {

    private lateinit var lecture: Lecture

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_lecture)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        lecture = intent.getParcelableExtra(DATA_LECTURE)

        showLecture()
    }

    private fun showLecture(){
        title = lecture.title

        lecture.file?.let {
            val content = ApplicationUtil.getStringFromAsset(this, it)
            web_view.loadDataWithBaseURL("file:///android_asset/", content, "text/html", "utf-8", null)
        }
    }

    companion object {
        private const val LOG_TAG = "ViewLectureActivity"
        const val DATA_LECTURE = "data-lecture"
    }
}