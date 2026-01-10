package com.example.mushroomhuntgame.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mushroomhuntgame.database.Record
import com.example.mushroomhuntgame.databinding.ItemRecordBinding

class RecordAdapter(private val records: List<Record>, private val onItemClick: (Record) -> Unit) :
    RecyclerView.Adapter<RecordAdapter.ScoreViewHolder>() {
    class ScoreViewHolder(val binding: ItemRecordBinding) : RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScoreViewHolder {
        val binding = ItemRecordBinding.inflate(LayoutInflater.from(parent.context), parent,false)
        return ScoreViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ScoreViewHolder, position: Int) {
        val record = records[position]

        holder.binding.apply {
            recordRank.text = "${position + 1}."
            recordName.text = record.name
            recordScore.text = record.score.toString()
            recordTime.text = record.timestamp

            root.setOnClickListener {
                onItemClick(record)
            }
        }

    }

    override fun getItemCount() = records.size
}