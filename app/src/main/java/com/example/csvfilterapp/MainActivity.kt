package com.example.csvfilterapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.csvfilterapp.data.db.AppDatabase
import com.example.csvfilterapp.data.entity.CsvRowEntity
import com.example.csvfilterapp.ui.DynamicFilterAdapter
import com.example.csvfilterapp.utils.CsvZipImporter
import com.example.csvfilterapp.utils.DynamicFilter
import com.example.csvfilterapp.utils.JsonUtils
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.*
import org.json.JSONObject
import java.util.*

class MainActivity : AppCompatActivity() {

    companion object {
        private const val PICK_FILES_REQUEST = 1001
        private const val PAGE_SIZE = 50
        private const val MAX_FILTERS = 10
    }

    // UI
    private lateinit var recyclerView: RecyclerView
    private lateinit var rowAdapter: CsvRowAdapter
    private lateinit var loadFilesFab: FloatingActionButton

    private lateinit var filterToggleBtn: Button
    private lateinit var filterCard: View
    private lateinit var applyFiltersBtn: Button
    private lateinit var dynamicFilterRecycler: RecyclerView
    private lateinit var dynamicFilterAdapter: DynamicFilterAdapter

    private lateinit var filePanel: View
    private lateinit var fileRecycler: RecyclerView
    private lateinit var fileAdapter: FileStatusAdapter

    // Data
    private val dao by lazy {
        AppDatabase.getInstance(this).csvRowDao()
    }

    private val dynamicFilters = mutableListOf<DynamicFilter>()

    private var offset = 0
    private var isLoading = false

    private val activeJobs = mutableMapOf<String, Job>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindViews()
        setupRecycler()
        setupFilters()
        setupFilePanel()
        setupPagination()

        loadFilesFab.setOnClickListener { openFilePicker() }

        loadFirstPage()
    }

    private fun bindViews() {
        recyclerView = findViewById(R.id.recyclerView)
        loadFilesFab = findViewById(R.id.loadFilesFab)

        filterToggleBtn = findViewById(R.id.filterToggleBtn)
        filterCard = findViewById(R.id.filterCard)
        applyFiltersBtn = findViewById(R.id.applyFiltersBtn)
        dynamicFilterRecycler = findViewById(R.id.dynamicFilterRecycler)

        filePanel = findViewById(R.id.filePanel)
        fileRecycler = findViewById(R.id.fileStatusRecycler)

        findViewById<ImageButton>(R.id.closeFilePanelBtn)
            .setOnClickListener { filePanel.visibility = View.GONE }
    }

    private fun setupRecycler() {
        rowAdapter = CsvRowAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = rowAdapter
    }

    private fun setupFilters() {
        dynamicFilterAdapter = DynamicFilterAdapter(dynamicFilters)
        dynamicFilterRecycler.layoutManager = LinearLayoutManager(this)
        dynamicFilterRecycler.adapter = dynamicFilterAdapter

        filterToggleBtn.setOnClickListener {
            filterCard.visibility =
                if (filterCard.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }

        applyFiltersBtn.setOnClickListener {
            loadFirstPage()
        }
    }

    private fun setupDynamicFiltersFromDb() {
        lifecycleScope.launch(Dispatchers.IO) {
            val json = dao.getAnyJsonRow() ?: return@launch
            val keys = JSONObject(json).keys()

            dynamicFilters.clear()
            var count = 0
            while (keys.hasNext() && count < MAX_FILTERS) {
                dynamicFilters.add(DynamicFilter(keys.next()))
                count++
            }

            withContext(Dispatchers.Main) {
                dynamicFilterAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun setupFilePanel() {
        fileAdapter = FileStatusAdapter(
            onCancel = { state ->
                activeJobs[state.fileName]?.cancel()
                fileAdapter.markCancelled(state.fileName)
            },
            onRemove = { state ->
                lifecycleScope.launch(Dispatchers.IO) {
                    dao.deleteBySourceFile(state.fileName)
                    withContext(Dispatchers.Main) {
                        fileAdapter.remove(state.fileName)
                        loadFirstPage()
                    }
                }
            }
        )

        fileRecycler.layoutManager = LinearLayoutManager(this)
        fileRecycler.adapter = fileAdapter
    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "*/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
        startActivityForResult(intent, PICK_FILES_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_FILES_REQUEST && resultCode == Activity.RESULT_OK) {
            val uris = mutableListOf<Uri>()
            data?.clipData?.let {
                for (i in 0 until it.itemCount) uris.add(it.getItemAt(i).uri)
            } ?: data?.data?.let { uris.add(it) }

            if (uris.isNotEmpty()) startFileLoading(uris)
        }
    }

    private fun startFileLoading(uris: List<Uri>) {
        filePanel.visibility = View.VISIBLE

        uris.forEach { uri ->
            val name = uri.lastPathSegment ?: UUID.randomUUID().toString()
            fileAdapter.add(FileLoadState(name))

            val job = lifecycleScope.launch(Dispatchers.IO) {
                CsvZipImporter.import(
                    context = this@MainActivity,
                    uri = uri,
                    dao = dao,
                    onProgress = { p ->
                        runOnUiThread { fileAdapter.updateProgress(name, p) }
                    }
                )

                withContext(Dispatchers.Main) {
                    fileAdapter.markLoaded(name)
                    if (dynamicFilters.isEmpty()) setupDynamicFiltersFromDb()
                    loadFirstPage()
                }
            }
            activeJobs[name] = job
        }
    }

    private fun setupPagination() {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                if (!rv.canScrollVertically(1) && !isLoading) loadNextPage()
            }
        })
    }

    private fun loadFirstPage() {
        offset = 0
        rowAdapter.clearAll()
        loadNextPage()
    }

    private fun loadNextPage() {
        lifecycleScope.launch {
            if (isLoading) return@launch
            isLoading = true

            val rows = withContext(Dispatchers.IO) {
                dao.loadPaged(PAGE_SIZE, offset)
            }

            val filtered = applyDynamicFilters(rows)
            rowAdapter.setData(filtered)

            offset += rows.size
            isLoading = false
        }
    }

    private fun applyDynamicFilters(rows: List<CsvRowEntity>): List<CsvRowEntity> {
        val active = dynamicFilterAdapter.getActiveFilters()
        if (active.isEmpty()) return rows

        return rows.filter { row ->
            val map = JsonUtils.jsonToMap(row.dataJson)
            active.all { f ->
                map[f.column]?.contains(f.value, ignoreCase = true) == true
            }
        }
    }
}
