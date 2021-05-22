package com.android.locationtracking.logs

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.databinding.DataBindingUtil
import com.android.locationtracking.R
import com.android.locationtracking.data.model.Logs
import com.android.locationtracking.databinding.ItemLogsBinding
import com.android.locationtracking.helpers.Utils

class LogsAdapter(private val context: Activity, private val logsList: List<Logs>) :
    BaseAdapter() {

    override fun getCount(): Int {
        return logsList.size
    }

    override fun getItem(position: Int): Any {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
//        var convertView = LayoutInflater.from(context).inflate(R.layout.item_logs, parent, false)
        val binding =
            DataBindingUtil.inflate<ItemLogsBinding>(
                LayoutInflater.from(parent.context),
                R.layout.item_logs, parent, false
            )

        val loc = "" + logsList[position].lat + ", " + logsList[position].lng
        binding.tvLocation.text = loc

        val time = Utils.getDate(logsList[position].time)
        binding.tvTime.text = time

        return binding.root
    }


}