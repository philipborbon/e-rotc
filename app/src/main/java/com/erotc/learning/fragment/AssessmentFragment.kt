package com.erotc.learning.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.erotc.learning.R
import com.erotc.learning.repository.LearnRepository

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_assessment, container, false)
    }

    companion object {
        private val LOG_TAG = AssessmentFragment::class.java.simpleName
        fun newInstance(): AssessmentFragment {
            return AssessmentFragment()
        }
    }
}