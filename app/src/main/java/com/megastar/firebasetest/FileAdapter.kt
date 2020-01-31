package com.megastar.firebasetest

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class FileAdapter(val clickAction: ((SongListItem)-> Unit)): RecyclerView.Adapter<FileViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        return FileViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_file, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        val currentItem = items[position]
        holder.bind(currentItem)
        holder.root.setOnClickListener { clickAction.invoke(currentItem) }
    }

    var items = mutableListOf<SongListItem>()
}