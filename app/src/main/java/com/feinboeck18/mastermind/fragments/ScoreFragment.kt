package com.feinboeck18.mastermind.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.feinboeck18.mastermind.MainActivity
import com.feinboeck18.mastermind.R
import kotlinx.android.synthetic.main.fragment_score.*

class ScoreFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        listView.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_expandable_list_item_1, MainActivity.scores)
    }
}