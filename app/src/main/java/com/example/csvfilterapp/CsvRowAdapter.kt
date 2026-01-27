package com.example.csvfilterapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.csvfilterapp.data.entity.CsvRowEntity
import com.example.csvfilterapp.utils.JsonUtils

class CsvRowAdapter : RecyclerView.Adapter<CsvRowAdapter.VH>() {

    private val items = mutableListOf<CsvRowEntity>()

    fun setData(newItems: List<CsvRowEntity>) {
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    fun clearAll() {
        items.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_dynamic_row, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        private val container: LinearLayout =
            view.findViewById(R.id.dynamicContainer)

        fun bind(entity: CsvRowEntity) {
            container.removeAllViews()
            JsonUtils.jsonToMap(entity.dataJson).forEach { e ->
                val tv = TextView(container.context)
                tv.text = "${e.key}: ${e.value}"
                container.addView(tv)
            }
        }
    }
}
