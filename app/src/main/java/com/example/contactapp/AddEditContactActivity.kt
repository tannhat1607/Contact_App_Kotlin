package com.example.contactapp


import android.app.Activity
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class AddEditContactActivity : AppCompatActivity() {

    private lateinit var storage: ContactStorage
    private var contactId: Long = -1L

    private lateinit var edtName: EditText
    private lateinit var edtPhone: EditText
    private lateinit var edtEmail: EditText
    private lateinit var spinnerGroup: Spinner
    private lateinit var chkFavorite: CheckBox

    private val groups = listOf(
        "Gia đình",
        "Bạn bè",
        "Công việc",
        "Khác"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_contact)

        storage = ContactStorage(this)

        edtName = findViewById(R.id.edtName)
        edtPhone = findViewById(R.id.edtPhone)
        edtEmail = findViewById(R.id.edtEmail)
        spinnerGroup = findViewById(R.id.spinnerGroup)
        chkFavorite = findViewById(R.id.chkFavorite)

        val title = findViewById<TextView>(R.id.txtFormTitle)
        val btnSave = findViewById<Button>(R.id.btnSave)
        val btnCancel = findViewById<Button>(R.id.btnCancel)

        spinnerGroup.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            groups
        )

        contactId = intent.getLongExtra("contact_id", -1L)

        if (contactId != -1L) {
            val contact = storage.loadContacts()
                .find { it.id == contactId }

            if (contact == null) {
                Toast.makeText(
                    this,
                    "Không tìm thấy liên hệ",
                    Toast.LENGTH_SHORT
                ).show()

                finish()
                return
            }

            title.text = "Chỉnh sửa liên hệ"
            edtName.setText(contact.name)
            edtPhone.setText(contact.phone)
            edtEmail.setText(contact.email)
            chkFavorite.isChecked = contact.isFavorite

            val index = groups.indexOf(contact.groupName)
            spinnerGroup.setSelection(
                if (index >= 0) index else groups.lastIndex
            )
        } else {
            title.text = "Thêm liên hệ"
        }

        btnSave.setOnClickListener {
            saveContact()
        }

        btnCancel.setOnClickListener {
            finish()
        }
    }

    private fun saveContact() {
        val name = edtName.text.toString().trim()
        val phone = edtPhone.text.toString().trim()
        val email = edtEmail.text.toString().trim()

        if (name.isBlank()) {
            edtName.error = "Vui lòng nhập họ tên"
            edtName.requestFocus()
            return
        }

        if (phone.isBlank()) {
            edtPhone.error = "Vui lòng nhập số điện thoại"
            edtPhone.requestFocus()
            return
        }

        val contacts = storage.loadContacts()

        val contact = Contact(
            id = if (contactId == -1L) {
                System.currentTimeMillis()
            } else {
                contactId
            },
            name = name,
            phone = phone,
            email = email,
            groupName = spinnerGroup.selectedItem.toString(),
            isFavorite = chkFavorite.isChecked
        )

        if (contactId == -1L) {
            contacts.add(contact)
        } else {
            val index = contacts.indexOfFirst {
                it.id == contactId
            }

            if (index == -1) {
                Toast.makeText(
                    this,
                    "Liên hệ không còn tồn tại",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

            contacts[index] = contact
        }

        storage.saveContacts(contacts)

        setResult(Activity.RESULT_OK)
        finish()
    }
}
