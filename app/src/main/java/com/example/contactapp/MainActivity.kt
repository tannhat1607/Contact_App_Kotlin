package com.example.contactapp
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var storage: ContactStorage
    private lateinit var adapter: ContactAdapter
    private lateinit var edtSearch: EditText
    private lateinit var spinnerFilter: Spinner

    private var allContacts: List<Contact> = emptyList()

    private val filters = listOf(
        "Tất cả",
        "Yêu thích",
        "Gia đình",
        "Bạn bè",
        "Công việc",
        "Khác"
    )

    private val formLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                loadContacts()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        storage = ContactStorage(this)

        edtSearch = findViewById(R.id.edtSearch)
        spinnerFilter = findViewById(R.id.spinnerFilter)

        val rvContacts =
            findViewById<RecyclerView>(R.id.rvContacts)

        val btnAdd =
            findViewById<Button>(R.id.btnAddContact)

        spinnerFilter.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            filters
        )

        adapter = ContactAdapter(
            contacts = emptyList(),

            onClick = { contact ->
                openEditScreen(contact)
            },

            onFavoriteClick = { contact ->
                val contacts = storage.loadContacts()
                val index = contacts.indexOfFirst {
                    it.id == contact.id
                }

                if (index != -1) {
                    contacts[index] = contacts[index].copy(
                        isFavorite =
                            !contacts[index].isFavorite
                    )

                    storage.saveContacts(contacts)
                    loadContacts()
                }
            },

            onLongClick = { contact ->
                confirmDelete(contact)
            }
        )

        rvContacts.layoutManager =
            LinearLayoutManager(this)

        rvContacts.adapter = adapter

        btnAdd.setOnClickListener {
            val intent = Intent(
                this,
                AddEditContactActivity::class.java
            )

            formLauncher.launch(intent)
        }

        edtSearch.addTextChangedListener(
            object : android.text.TextWatcher {

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {}

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    applyFilters()
                }

                override fun afterTextChanged(
                    s: android.text.Editable?
                ) {}
            }
        )

        spinnerFilter.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    applyFilters()
                }

                override fun onNothingSelected(
                    parent: AdapterView<*>?
                ) {}
            }

        loadContacts()
    }

    private fun loadContacts() {
        allContacts = storage.loadContacts()
        applyFilters()
    }

    private fun applyFilters() {
        val keyword = edtSearch.text.toString()
            .trim()
            .lowercase()

        val selectedFilter =
            spinnerFilter.selectedItem?.toString() ?: "Tất cả"

        val filtered = allContacts.filter { contact ->

            val matchesSearch =
                contact.name.lowercase().contains(keyword) ||
                        contact.phone.contains(keyword) ||
                        contact.email.lowercase().contains(keyword)

            val matchesGroup = when (selectedFilter) {
                "Tất cả" -> true
                "Yêu thích" -> contact.isFavorite
                else -> contact.groupName == selectedFilter
            }

            matchesSearch && matchesGroup
        }

        adapter.updateData(filtered)
    }

    private fun openEditScreen(contact: Contact) {
        val intent = Intent(
            this,
            AddEditContactActivity::class.java
        ).apply {
            putExtra("contact_id", contact.id)
        }

        formLauncher.launch(intent)
    }

    private fun confirmDelete(contact: Contact) {
        AlertDialog.Builder(this)
            .setTitle("Xóa liên hệ")
            .setMessage(
                "Bạn có chắc muốn xóa ${contact.name} không?"
            )
            .setNegativeButton("Hủy", null)
            .setPositiveButton("Xóa") { _, _ ->

                val contacts = storage.loadContacts()

                contacts.removeAll {
                    it.id == contact.id
                }

                storage.saveContacts(contacts)
                loadContacts()

                Toast.makeText(
                    this,
                    "Đã xóa liên hệ",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .show()
    }
}
