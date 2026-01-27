package com.example.csvfilterapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FileStatusAdapter(
    private val onCancel: (FileLoadState) -> Unit,
    private val onRemove: (FileLoadState) -> Unit
) : RecyclerView.Adapter<FileStatusAdapter.VH>() {

    private val items = mutableListOf<FileLoadState>()

    fun add(state: FileLoadState) {
        items.add(state)
        notifyItemInserted(items.size - 1)
    }

    fun updateProgress(fileName: String, progress: Int) {
        val index = items.indexOfFirst { it.fileName == fileName }
        if (index != -1) {
            items[index].progress = progress
            notifyItemChanged(index)
        }
    }

    fun markLoaded(fileName: String) {
        updateStatus(fileName, FileLoadState.Status.LOADED)
    }

    fun markCancelled(fileName: String) {
        updateStatus(fileName, FileLoadState.Status.CANCELLED)
    }

    private fun updateStatus(fileName: String, status: FileLoadState.Status) {
        val index = items.indexOfFirst { it.fileName == fileName }
        if (index != -1) {
            items[index].status = status
            items[index].progress = 100
            notifyItemChanged(index)
        }
    }

    fun remove(fileName: String) {
        val index = items.indexOfFirst { it.fileName == fileName }
        if (index != -1) {
            items.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    fun allFinished(): Boolean =
        items.all { it.status != FileLoadState.Status.LOADING }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_file_status, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        private val name: TextView = view.findViewById(R.id.fileNameText)
        private val progress: ProgressBar = view.findViewById(R.id.fileProgress)
        private val cancel: Button = view.findViewById(R.id.cancelBtn)
        private val remove: Button = view.findViewById(R.id.removeBtn)

        fun bind(state: FileLoadState) {
            name.text = state.fileName
            progress.progress = state.progress

            when (state.status) {
                FileLoadState.Status.LOADING -> {
                    progress.visibility = View.VISIBLE
                    cancel.visibility = View.VISIBLE
                    remove.visibility = View.GONE
                    cancel.setOnClickListener { onCancel(state) }
                }
                else -> {
                    progress.visibility = View.GONE
                    cancel.visibility = View.GONE
                    remove.visibility = View.VISIBLE
                    remove.setOnClickListener { onRemove(state) }
                }
            }
        }
    }
}
