package com.example.overscroll_jetpack_compose

import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SimpleTextAdapter(
    private val items: List<String>,
    private val onItemClick: (Int) -> Unit,
) : RecyclerView.Adapter<SimpleTextAdapter.VH>() {

    class VH(val textView: TextView) : RecyclerView.ViewHolder(textView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val tv = TextView(parent.context).apply {
            layoutParams = RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setPadding(48, 36, 48, 36)
            textSize = 18f
            gravity = Gravity.START
            isClickable = true
            isFocusable = true
        }
        return VH(tv)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.textView.text = items[position]
        holder.textView.setOnClickListener { onItemClick(position) }
    }

    override fun getItemCount(): Int = items.size
}