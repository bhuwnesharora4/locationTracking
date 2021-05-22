package com.android.locationtracking.logs

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.android.locationtracking.R
import com.android.locationtracking.data.model.Entry
import com.android.locationtracking.data.model.Logs
import com.android.locationtracking.databinding.ItemEntryBinding
import com.android.locationtracking.helpers.Utils

class EntriesAdapter(
    private val parent: LogsActivity,
    private val clickListener: LogsClickListener
) :
    RecyclerView.Adapter<EntriesAdapter.ViewHolder>() {

    private val entriesList = arrayListOf<Entry?>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EntriesAdapter.ViewHolder {
        val binding =
            DataBindingUtil.inflate<ItemEntryBinding>(
                LayoutInflater.from(parent.context),
                R.layout.item_entry, parent, false
            )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return entriesList.size

    }

    override fun onBindViewHolder(holder: EntriesAdapter.ViewHolder, position: Int) {
        holder.setData(entriesList[position]!!, position)
    }

    fun addItemList(list: List<Entry?>) {
        entriesList.clear()
        entriesList.addAll(list)
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemEntryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun setData(
            entry: Entry,
            position: Int
        ) {
            val startTime = "Start time: ${Utils.getDate(entry.startTime)}"
            binding.tvStartTime.text = startTime

            val stopTime = "Stop time: ${Utils.getDate(entry.stopTime)}"
            binding.tvEndTime.text = stopTime

            binding.lvLogs.adapter = LogsAdapter(parent, entry.logs)
            binding.lvLogs.setSelectionAfterHeaderView()

            binding.lvLogs.setOnItemClickListener { _, _, position, _ ->
                clickListener.logClick(entry.logs[position])
            }
        }

    }

    interface LogsClickListener {
        fun logClick(log: Logs)
    }

}
