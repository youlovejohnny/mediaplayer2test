package com.megastar.firebasetest.ui.main.list.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.StorageReference
import com.megastar.firebasetest.R

class FileAdapter(val clickAction: ((StorageReference)-> Unit)): RecyclerView.Adapter<FileViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        return FileViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_file,parent,false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        val currentItem = items[position]
        holder.bind(currentItem)
        holder.root.setOnClickListener { clickAction.invoke(currentItem) }
    }

    val items = mutableListOf<StorageReference>()
}