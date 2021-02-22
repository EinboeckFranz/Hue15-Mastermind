package com.feinboeck18.mastermind.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.feinboeck18.mastermind.MainActivity
import com.feinboeck18.mastermind.R
import com.feinboeck18.mastermind.listviewAdapter.ListViewSettingAdapter
import kotlinx.android.synthetic.main.fragment_setting.*

class SettingFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        listView.adapter = ListViewSettingAdapter(MainActivity.settings, requireActivity())
    }
}