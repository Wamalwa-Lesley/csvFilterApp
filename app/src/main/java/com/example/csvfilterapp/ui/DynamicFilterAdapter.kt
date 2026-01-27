package com.example.csvfilterapp.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.example.csvfilterapp.R
import com.example.csvfilterapp.utils.DynamicFilter

class DynamicFilterAdapter(
    private val filters: List<DynamicFilter>
) : RecyclerView.Adapter<DynamicFilterAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val label: TextView = view.findViewById(R.id.filterLabel)
        val input: EditText = view.findViewById(R.id.filterInput)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_dynamic_filter, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val filter = filters[position]

        holder.label.text = filter.column
        holder.input.setText(filter.value)

        holder.input.addTextChangedListener {
            filter.value = it?.toString()?.trim() ?: ""
        }
    }

    override fun getItemCount(): Int = filters.size

    fun getActiveFilters(): List<DynamicFilter> =
        filters.filter { it.value.isNotBlank() }
}
