package com.example.android.politicalpreparedness.election.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.databinding.ListItemElectionBinding
import com.example.android.politicalpreparedness.network.models.Election


class ElectionListAdapter(val clickListener: ElectionListener): ListAdapter<Election, ElectionListAdapter.ElectionViewHolder>(ElectionDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ElectionViewHolder {
        return ElectionViewHolder(ListItemElectionBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ElectionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ElectionViewHolder(val binding: ListItemElectionBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(election: Election) {
            binding.election = election
            binding.executePendingBindings()
            itemView.setOnClickListener {
                clickListener.onClick(election)
            }
        }
    }

    companion object ElectionDiffCallback : DiffUtil.ItemCallback<Election>() {
        override fun areItemsTheSame(oldItem: Election, newItem: Election): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Election, newItem: Election): Boolean {
            return oldItem == newItem
        }
    }
}

class ElectionListener(val clickListener: (election: Election) -> Unit) {
    fun onClick(election: Election)  = clickListener(election)
}