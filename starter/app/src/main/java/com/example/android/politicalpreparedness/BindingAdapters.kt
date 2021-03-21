package com.example.android.politicalpreparedness

import android.content.Intent
import android.net.Uri
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.network.models.Election
import java.text.SimpleDateFormat
import java.util.*


@BindingAdapter("date")
fun bindDate(textView: TextView, date: Date) {
    val dateFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.getDefault())
    textView.text = dateFormat.format(date)
}

@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<Election>?) {
    val adapter = recyclerView.adapter as ElectionListAdapter
    adapter.submitList(data?.toMutableList())
}

@BindingAdapter("url")
fun bindUrl(textView: TextView, url: String?) {
    textView.setOnClickListener {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
        }
        textView.context.startActivity(intent)
    }
}