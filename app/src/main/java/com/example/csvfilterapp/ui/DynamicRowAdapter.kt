package com.example.csvfilterapp.ui

import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.csvfilterapp.data.entity.CsvRowEntity
import com.example.csvfilterapp.utils.JsonUtils

class DynamicRowAdapter(
    private val rows: MutableList<CsvRowEntity> = mutableListOf()
) : RecyclerView.Adapter<DynamicRowAdapter.RowVH>() {

    fun setData(newRows: List<CsvRowEntity>) {
        rows.clear()
        rows.addAll(newRows)
        notifyDataSetChanged()
    }

    fun addRows(newRows: List<CsvRowEntity>) {
        val start = rows.size
        rows.addAll(newRows)
        notifyItemRangeInserted(start, newRows.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowVH {
        val container = LinearLayout(parent.context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)
        }
        return RowVH(container)
    }

    override fun onBindViewHolder(holder: RowVH, position: Int) {
        holder.bind(rows[position])
    }

    override fun getItemCount() = rows.size

    class RowVH(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val container = itemView as LinearLayout

        fun bind(entity: CsvRowEntity) {
            container.removeAllViews()

            // 🔥 THIS IS NOW 100% SAFE
            val map: Map<String, String> =
                JsonUtils.jsonToMap(entity.dataJson)

            map.forEach { entry ->
                val tv = TextView(container.context).apply {
                    text = "${entry.key}: ${entry.value}"
                    textSize = 14f
                }
                container.addView(tv)
            }
        }
    }
}
