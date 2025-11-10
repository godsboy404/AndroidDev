package com.example.contact

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.contact.data.Contact
import com.example.contact.data.ContactDatabase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

open class ContactViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = ContactDatabase.getDatabase(app).contactDao()
    open val contacts: StateFlow<List<Contact>> = dao.getAllContacts()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    open fun addContact(name: String, phone: String) {
        viewModelScope.launch {
            dao.insert(Contact(name = name, phone = phone))
        }
    }

    open fun updateContact(contact: Contact) {
        viewModelScope.launch {
            dao.update(contact)
        }
    }

    open fun deleteContact(contact: Contact) {
        viewModelScope.launch {
            dao.delete(contact)
        }
    }
}

