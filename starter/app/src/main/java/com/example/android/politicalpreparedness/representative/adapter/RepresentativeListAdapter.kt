package com.example.android.politicalpreparedness.representative.adapter

import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.ListItemRepresentativeBinding
import com.example.android.politicalpreparedness.network.models.Channel
import com.example.android.politicalpreparedness.representative.model.Representative

class RepresentativeListAdapter: ListAdapter<Representative, RepresentativeViewHolder>(RepresentativeDiffCallback){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepresentativeViewHolder {
        return RepresentativeViewHolder(ListItemRepresentativeBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RepresentativeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object RepresentativeDiffCallback : DiffUtil.ItemCallback<Representative>() {
        override fun areItemsTheSame(oldItem: Representative, newItem: Representative): Boolean {
            return oldItem.official == newItem.official
        }

        override fun areContentsTheSame(oldItem: Representative, newItem: Representative): Boolean {
            return oldItem == newItem
        }
    }
}

class RepresentativeViewHolder(val binding: ListItemRepresentativeBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Representative) {
        binding.representative = item
        binding.representativePhoto.setImageResource(R.drawable.ic_profile)

        showSocialLinks(item.official.channels)
        showWWWLinks(item.official.urls)

        binding.executePendingBindings()
    }

    private fun showSocialLinks(channels: List<Channel>?) {
        val facebookUrl = getFacebookUrl(channels)
        if (!facebookUrl.isNullOrBlank()) { enableLink(binding.representativeFacebook, facebookUrl) }

        val twitterUrl = getTwitterUrl(channels)
        if (!twitterUrl.isNullOrBlank()) { enableLink(binding.representativeTwitter, twitterUrl) }
    }

    private fun showWWWLinks(urls: List<String>?) {
        if (urls != null) {
            enableLink(binding.representativeUrl, urls.first())
        }
    }

    private fun getFacebookUrl(channels: List<Channel>?): String? {
            return channels?.filter { channel -> channel.type == "Facebook" }
                    ?.map { channel -> "https://www.facebook.com/${channel.id}" }
                    ?.firstOrNull()

    }

    private fun getTwitterUrl(channels: List<Channel>?): String? {
        return channels?.filter { channel -> channel.type == "Twitter" }
                ?.map { channel -> "https://www.twitter.com/${channel.id}" }
                ?.firstOrNull()
    }

    private fun enableLink(view: ImageView, url: String) {
        view.visibility = View.VISIBLE
        view.setOnClickListener { setIntent(url) }
    }

    private fun setIntent(url: String) {
        val uri = Uri.parse(url)
        val intent = Intent(ACTION_VIEW, uri)
        itemView.context.startActivity(intent)
    }
}