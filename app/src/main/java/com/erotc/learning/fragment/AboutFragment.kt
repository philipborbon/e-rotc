package com.erotc.learning.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.erotc.learning.BuildConfig
import com.erotc.learning.R

/**
 * A simple [Fragment] subclass.
 */
class AboutFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_about, container, false)
        val about = view.findViewById<TextView>(R.id.text_version)
        about.text = String.format(getString(R.string.label_version), BuildConfig.VERSION_NAME)
        return view
    }

    companion object {
        fun newInstance(): AboutFragment {
            return AboutFragment()
        }
    }
}