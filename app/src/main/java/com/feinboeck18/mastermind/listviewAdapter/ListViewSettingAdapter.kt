package com.feinboeck18.mastermind.listviewAdapter

import android.annotation.SuppressLint
import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.feinboeck18.mastermind.R
import com.feinboeck18.mastermind.Setting
import kotlinx.android.synthetic.main.setting_listviewitem.view.*

class ListViewSettingAdapter(settings: List<Setting>, private val currentContext: Activity): ArrayAdapter<Setting>(currentContext, R.layout.setting_listviewitem, settings) {
    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val currentLayout = currentContext.layoutInflater.inflate(R.layout.setting_listviewitem, null, true)

        currentLayout.settingName.text = getItem(position)?.settingName
        currentLayout.settingDescription.text = getItem(position)?.settingDescription

        return currentLayout
    }
}