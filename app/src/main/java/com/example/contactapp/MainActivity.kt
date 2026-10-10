package com.example.contactapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.ChipGroup

class MainActivity : AppCompatActivity() {

    private lateinit var storage: ContactStorage
    private lateinit var contactAdapter: ContactAdapter
    private lateinit var favoriteAdapter: FavoriteAdapter

    private lateinit var edtSearch: EditText
    private lateinit var chipGroupFilter: ChipGroup
    private lateinit var txtContactCount: TextView
    private lateinit var btnSort: ImageView
    private lateinit var rvContacts: RecyclerView
    private lateinit var rvFavorites: RecyclerView
    private lateinit var btnAddContact: View

    private lateinit var navTabContacts: LinearLayout
    private lateinit var navTabRecent: LinearLayout
    private lateinit var navTabDialpad: LinearLayout
    private lateinit var navTabManage: LinearLayout

    private lateinit var imgTabContacts: ImageView
    private lateinit var txtTabContacts: TextView
    private lateinit var imgTabRecent: ImageView
    private lateinit var txtTabRecent: TextView
    private lateinit var imgTabDialpad: ImageView
    private lateinit var txtTabDialpad: TextView
    private lateinit var imgTabManage: ImageView
    private lateinit var txtTabManage: TextView

    private lateinit var includeDialpad: View
    private lateinit var txtDialNumber: TextView
    private lateinit var txtDialMatch: TextView
    private lateinit var btnDialCall: ImageView
    private lateinit var btnDialBackspace: ImageView

    private var allContacts: List<Contact> = emptyList()
    private var selectedCategory: String = "Tất cả"
    private var isAscending: Boolean = true
    private var dialpadBuffer = StringBuilder()

    private val formLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                loadContacts()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        storage = ContactStorage(this)

        initViews()
        setupAdapters()
        setupChipGroup()
        setupSearch()
        setupSort()
        setupBottomNav()
        setupDialpad()

        btnAddContact.setOnClickListener {
            openAddScreen()
        }

        loadContacts()
    }

    private fun initViews() {
        edtSearch = findViewById(R.id.edtSearch)
        chipGroupFilter = findViewById(R.id.chipGroupFilter)
        txtContactCount = findViewById(R.id.txtContactCount)
        btnSort = findViewById(R.id.btnSort)
        rvContacts = findViewById(R.id.rvContacts)
        rvFavorites = findViewById(R.id.rvFavorites)
        btnAddContact = findViewById(R.id.btnAddContact)

        navTabContacts = findViewById(R.id.navTabContacts)
        navTabRecent = findViewById(R.id.navTabRecent)
        navTabDialpad = findViewById(R.id.navTabDialpad)
        navTabManage = findViewById(R.id.navTabManage)

        imgTabContacts = findViewById(R.id.imgTabContacts)
        txtTabContacts = findViewById(R.id.txtTabContacts)
        imgTabRecent = findViewById(R.id.imgTabRecent)
        txtTabRecent = findViewById(R.id.txtTabRecent)
        imgTabDialpad = findViewById(R.id.imgTabDialpad)
        txtTabDialpad = findViewById(R.id.txtTabDialpad)
        imgTabManage = findViewById(R.id.imgTabManage)
        txtTabManage = findViewById(R.id.txtTabManage)

        includeDialpad = findViewById(R.id.includeDialpad)
        txtDialNumber = findViewById(R.id.txtDialNumber)
        txtDialMatch = findViewById(R.id.txtDialMatch)
        btnDialCall = findViewById(R.id.btnDialCall)
        btnDialBackspace = findViewById(R.id.btnDialBackspace)
    }

    private fun setupAdapters() {
        contactAdapter = ContactAdapter(
            items = emptyList(),
            onClick = { contact -> openEditScreen(contact) },
            onCallClick = { contact -> makeCall(contact.phone) },
            onLongClick = { contact -> confirmDelete(contact) }
        )
        rvContacts.layoutManager = LinearLayoutManager(this)
        rvContacts.adapter = contactAdapter

        favoriteAdapter = FavoriteAdapter(
            favoriteContacts = emptyList(),
            onAddNewClick = { openAddScreen() },
            onContactClick = { contact -> openEditScreen(contact) }
        )
        rvFavorites.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvFavorites.adapter = favoriteAdapter
    }

    private fun setupChipGroup() {
        chipGroupFilter.setOnCheckedStateChangeListener { _, checkedIds ->
            if (checkedIds.isEmpty()) return@setOnCheckedStateChangeListener
            selectedCategory = when (checkedIds.first()) {
                R.id.chipAll -> "Tất cả"
                R.id.chipPhone -> "Số điện thoại"
                R.id.chipFamily -> "Gia đình"
                R.id.chipWork -> "Công việc"
                R.id.chipFriends -> "Bạn bè"
                R.id.chipOther -> "Khác"
                else -> "Tất cả"
            }
            applyFilters()
        }
    }

    private fun setupSearch() {
        edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                applyFilters()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupSort() {
        btnSort.setOnClickListener { view ->
            val popup = PopupMenu(this, view)
            popup.menu.add(0, 1, 0, "Sắp xếp từ A đến Z (Tăng dần)")
            popup.menu.add(0, 2, 1, "Sắp xếp từ Z đến A (Giảm dần)")

            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    1 -> {
                        isAscending = true
                        Toast.makeText(this, "Đã sắp xếp từ A đến Z", Toast.LENGTH_SHORT).show()
                        applyFilters()
                        true
                    }
                    2 -> {
                        isAscending = false
                        Toast.makeText(this, "Đã sắp xếp từ Z đến A", Toast.LENGTH_SHORT).show()
                        applyFilters()
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }
    }

    private fun setupBottomNav() {
        navTabContacts.setOnClickListener {
            switchTab(0)
        }
        navTabRecent.setOnClickListener {
            switchTab(1)
        }
        navTabDialpad.setOnClickListener {
            switchTab(2)
        }
        navTabManage.setOnClickListener {
            switchTab(3)
        }
    }

    private fun switchTab(tabIndex: Int) {
        val activeColor = ContextCompat.getColor(this, android.R.color.holo_blue_dark)
        val inactiveColor = ContextCompat.getColor(this, android.R.color.darker_gray)

        imgTabContacts.setColorFilter(inactiveColor)
        txtTabContacts.setTextColor(inactiveColor)
        imgTabRecent.setColorFilter(inactiveColor)
        txtTabRecent.setTextColor(inactiveColor)
        imgTabDialpad.setColorFilter(inactiveColor)
        txtTabDialpad.setTextColor(inactiveColor)
        imgTabManage.setColorFilter(inactiveColor)
        txtTabManage.setTextColor(inactiveColor)

        when (tabIndex) {
            0 -> {
                imgTabContacts.setColorFilter(activeColor)
                txtTabContacts.setTextColor(activeColor)
                includeDialpad.visibility = View.GONE
                btnAddContact.visibility = View.VISIBLE
            }
            1 -> {
                imgTabRecent.setColorFilter(activeColor)
                txtTabRecent.setTextColor(activeColor)
                includeDialpad.visibility = View.GONE
                btnAddContact.visibility = View.GONE
                Toast.makeText(this, "Tính năng chưa hoàn thiện", Toast.LENGTH_SHORT).show()
            }
            2 -> {
                imgTabDialpad.setColorFilter(activeColor)
                txtTabDialpad.setTextColor(activeColor)
                includeDialpad.visibility = View.VISIBLE
                btnAddContact.visibility = View.GONE
            }
            3 -> {
                imgTabManage.setColorFilter(activeColor)
                txtTabManage.setTextColor(activeColor)
                includeDialpad.visibility = View.GONE
                btnAddContact.visibility = View.GONE
                Toast.makeText(this, "Tính năng chưa hoàn thiện", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupDialpad() {
        val keyMap = mapOf(
            R.id.btnKey1 to "1", R.id.btnKey2 to "2", R.id.btnKey3 to "3",
            R.id.btnKey4 to "4", R.id.btnKey5 to "5", R.id.btnKey6 to "6",
            R.id.btnKey7 to "7", R.id.btnKey8 to "8", R.id.btnKey9 to "9",
            R.id.btnKey0 to "0", R.id.btnKeyStar to "*", R.id.btnKeyHash to "#"
        )

        for ((viewId, digit) in keyMap) {
            findViewById<View>(viewId).setOnClickListener {
                dialpadBuffer.append(digit)
                updateDialpadDisplay()
            }
        }

        btnDialBackspace.setOnClickListener {
            if (dialpadBuffer.isNotEmpty()) {
                dialpadBuffer.deleteCharAt(dialpadBuffer.length - 1)
                updateDialpadDisplay()
            }
        }

        btnDialBackspace.setOnLongClickListener {
            dialpadBuffer.clear()
            updateDialpadDisplay()
            true
        }

        btnDialCall.setOnClickListener {
            val phone = dialpadBuffer.toString()
            if (phone.isNotBlank()) {
                makeCall(phone)
            } else {
                Toast.makeText(this, "Vui lòng nhập số điện thoại", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateDialpadDisplay() {
        val number = dialpadBuffer.toString()
        txtDialNumber.text = number

        if (number.isNotBlank()) {
            val match = allContacts.firstOrNull {
                it.phone.replace(" ", "").contains(number)
            }
            if (match != null) {
                txtDialMatch.text = "Gợi ý: ${match.name}"
                txtDialMatch.visibility = View.VISIBLE
            } else {
                txtDialMatch.visibility = View.GONE
            }
        } else {
            txtDialMatch.visibility = View.GONE
        }
    }

    private fun loadContacts() {
        allContacts = storage.loadContacts()

        val favorites = allContacts.filter { it.isFavorite }
        favoriteAdapter.updateData(favorites)

        applyFilters()
    }

    private fun applyFilters() {
        val keyword = edtSearch.text.toString().trim().lowercase()

        val filtered = allContacts.filter { contact ->
            val matchesSearch =
                contact.name.lowercase().contains(keyword) ||
                        contact.phone.contains(keyword) ||
                        contact.email.lowercase().contains(keyword)

            val matchesGroup = when (selectedCategory) {
                "Tất cả" -> true
                "Số điện thoại" -> contact.phone.isNotBlank()
                "Yêu thích" -> contact.isFavorite
                else -> contact.groupName == selectedCategory
            }

            matchesSearch && matchesGroup
        }

        contactAdapter.updateData(filtered, isAscending)
        txtContactCount.text = "${filtered.size} liên hệ trên thiết bị"
    }

    private fun openAddScreen() {
        val intent = Intent(this, AddEditContactActivity::class.java)
        formLauncher.launch(intent)
    }

    private fun openEditScreen(contact: Contact) {
        val intent = Intent(this, AddEditContactActivity::class.java).apply {
            putExtra("contact_id", contact.id)
        }
        formLauncher.launch(intent)
    }

    private fun makeCall(phone: String) {
        if (phone.isBlank()) {
            Toast.makeText(this, "Không có số điện thoại", Toast.LENGTH_SHORT).show()
            return
        }
        val cleanPhone = phone.replace(" ", "").replace("-", "")
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$cleanPhone")
        }
        startActivity(intent)
    }

    private fun confirmDelete(contact: Contact) {
        AlertDialog.Builder(this)
            .setTitle("Xóa liên hệ")
            .setMessage("Bạn có chắc muốn xóa ${contact.name} không?")
            .setNegativeButton("Hủy", null)
            .setPositiveButton("Xóa") { _, _ ->
                val contacts = storage.loadContacts()
                contacts.removeAll { it.id == contact.id }
                storage.saveContacts(contacts)
                loadContacts()

                Toast.makeText(this, "Đã xóa liên hệ", Toast.LENGTH_SHORT).show()
            }
            .show()
    }
}
