package com.example.csvfilterapp.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.csvfilterapp.R
import com.example.csvfilterapp.data.entity.CsvRowEntity
import com.example.csvfilterapp.utils.JsonUtils

class CsvRowAdapter : RecyclerView.Adapter<CsvRowAdapter.RowViewHolder>() {

    private val items = mutableListOf<CsvRowEntity>()

    // ================= Public API =================

    fun setData(newItems: List<CsvRowEntity>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    fun clearAll() {
        items.clear()
        notifyDataSetChanged()
    }

    // ================= Adapter =================

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_dynamic_row, parent, false)
        return RowViewHolder(view)
    }

    override fun onBindViewHolder(holder: RowViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    // ================= ViewHolder =================

    class RowViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val container: LinearLayout =
            itemView.findViewById(R.id.dynamicContainer)

        fun bind(entity: CsvRowEntity) {
            container.removeAllViews()

            val map: Map<String, String> =
                JsonUtils.jsonToMap(entity.dataJson)

            for ((key, value) in map) {
                val tv = TextView(container.context).apply {
                    text = "$key: $value"
                    textSize = 14f
                }
                container.addView(tv)
            }
        }
    }
}
