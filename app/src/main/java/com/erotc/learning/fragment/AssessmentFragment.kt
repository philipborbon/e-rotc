package com.erotc.learning.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.erotc.learning.R
import com.erotc.learning.activity.AssessmentActivity
import com.erotc.learning.repository.LearnRepository
import kotlinx.android.synthetic.main.fragment_assessment.*

/**
 * A simple [Fragment] subclass.
 */
class AssessmentFragment : Fragment() {
    private lateinit var repository: LearnRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val context = context ?: return
        repository = LearnRepository.getInstance(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_assessment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        button_start.setOnClickListener {
            startAssessment()
        }
    }

    private fun startAssessment(){
        val context = context ?: return
        val name = input_name.editText?.text.toString()

        if (name.isBlank()) {
            val builder = AlertDialog.Builder(context)
            builder.setTitle(R.string.dialog_assessment_name_required_title)
            builder.setMessage(R.string.dialog_assessment_name_required_message)
            builder.setPositiveButton(R.string.button_text_ok, null)
            builder.show()
        } else {
            val intent = Intent(context, AssessmentActivity::class.java)
            intent.putExtra(AssessmentActivity.DATA_NAME, name)
            startActivity(intent)
        }
    }

    companion object {
        private val LOG_TAG = AssessmentFragment::class.java.simpleName
        fun newInstance(): AssessmentFragment {
            return AssessmentFragment()
        }
    }
}