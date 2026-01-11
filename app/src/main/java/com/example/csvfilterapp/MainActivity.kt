package com.example.csvfilterapp

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: UserAdapter
    private var users: List<User> = emptyList()

    // Views
    private lateinit var genderSpinner: Spinner
    private lateinit var searchInput: EditText
    private lateinit var filterBtn: Button
    private lateinit var recycler: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Bind views
        genderSpinner = findViewById(R.id.genderSpinner)
        searchInput = findViewById(R.id.searchInput)
        filterBtn = findViewById(R.id.filterBtn)
        recycler = findViewById(R.id.recycler)

        // Spinner data
        val genders = listOf("Men", "Women")
        genderSpinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            genders
        )

        // RecyclerView setup
        adapter = UserAdapter(users)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        // Load default CSV
        loadData("all_user_men_new.csv")

        // Spinner selection listener
        genderSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val fileName = if (position == 0)
                        "all_user_men_new.csv"
                    else
                        "all_users_women.csv"

                    loadData(fileName)
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }

        // Filter button
        filterBtn.setOnClickListener {
            val text = searchInput.text.toString()
            val filtered = users.filter {
                it.city.contains(text, ignoreCase = true)
            }
            adapter.updateData(filtered)
        }
    }

    private fun loadData(fileName: String) {
        users = CsvReader.readUsers(this, fileName)
        adapter.updateData(users)
    }
}
