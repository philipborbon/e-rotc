package com.erotc.learning.activity

import android.net.Uri
import android.os.Bundle
import android.widget.MediaController
import androidx.appcompat.app.AppCompatActivity
import com.erotc.learning.R
import com.erotc.learning.data.Tutorial
import com.erotc.learning.util.ApplicationUtil
import kotlinx.android.synthetic.main.activity_view_tutorial.*


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class ViewTutorialActivity : AppCompatActivity() {

    private lateinit var tutorial: Tutorial

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_tutorial)

        tutorial = intent.getParcelableExtra(DATA_TUTORIAL)
        title = tutorial.title

        showTutorial()
    }

    private fun showTutorial(){
        //Set MediaController  to enable play, pause, forward, etc options.
        val mediaController = MediaController(this)
        mediaController.setAnchorView(video_view)

        val filename = tutorial.file?.replace(".mp4", "")

        //Location of Media File
        val uri = Uri.parse("android.resource://$packageName/" + ApplicationUtil.getResId(filename, R.raw::class.java))

        //Starting VideoView By Setting MediaController and URI
        video_view.setMediaController(mediaController)
        video_view.setVideoURI(uri)
        video_view.requestFocus()
        video_view.start()
    }

    companion object {
        const val DATA_TUTORIAL = "data-tutorial"
    }
}
