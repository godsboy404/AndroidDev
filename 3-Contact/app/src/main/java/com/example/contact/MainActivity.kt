package com.example.contact

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.contact.ui.theme.ContactTheme
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.contact.ContactViewModel
import com.example.contact.data.Contact
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val contactViewModel: ContactViewModel by viewModels()
        setContent {
            ContactTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ContactScreen(
                        modifier = Modifier.padding(innerPadding),
                        viewModel = contactViewModel
                    )
                }
            }
        }
    }
}

@Composable
fun ContactScreen(modifier: Modifier = Modifier, viewModel: ContactViewModel) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var search by remember { mutableStateOf("") }
    var editingContact by remember { mutableStateOf<Contact?>(null) }
    var editName by remember { mutableStateOf("") }
    var editPhone by remember { mutableStateOf("") }
    val contacts by viewModel.contacts.collectAsState()
    Column(modifier = modifier.padding(24.dp)) {
        Text(
            text = "通讯录",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        OutlinedTextField(
            value = search,
            onValueChange = { search = it },
            label = { Text("搜索联系人") },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        )
        Spacer(modifier = Modifier.height(30.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("姓名") },
                modifier = Modifier.weight(1f),
                shape = MaterialTheme.shapes.medium
            )
            Spacer(modifier = Modifier.width(12.dp))
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("电话") },
                modifier = Modifier.weight(1f),
                shape = MaterialTheme.shapes.medium
            )
            Spacer(modifier = Modifier.width(12.dp))
            Button(
                onClick = {
                    if (name.isNotBlank() && phone.isNotBlank()) {
                        viewModel.addContact(name, phone)
                        name = ""
                        phone = ""
                    }
                },
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.height(48.dp)
            ) {
                Text("添加")
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        val filteredContacts = if (search.isBlank()) contacts else contacts.filter {
            it.name.contains(search) || it.phone.contains(search)
        }
        if (filteredContacts.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().padding(32.dp)) {
                Text(text = "暂无联系人", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn {
                items(filteredContacts) { contact ->
                    ContactItem(
                        contact = contact,
                        onDelete = { viewModel.deleteContact(contact) },
                        onEdit = {
                            editingContact = contact
                            editName = contact.name
                            editPhone = contact.phone
                        }
                    )
                    Divider(modifier = Modifier.padding(horizontal = 8.dp), thickness = 0.5.dp)
                }
            }
        }
        if (editingContact != null) {
            Dialog(onDismissRequest = { editingContact = null }) {
                Card(modifier = Modifier.padding(16.dp), shape = MaterialTheme.shapes.large, elevation = CardDefaults.cardElevation(8.dp)) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(text = "编辑联系人", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 12.dp))
                        OutlinedTextField(
                            value = editName,
                            onValueChange = { editName = it },
                            label = { Text("姓名") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = editPhone,
                            onValueChange = { editPhone = it },
                            label = { Text("电话") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Row {
                            Button(onClick = {
                                editingContact?.let {
                                    viewModel.updateContact(it.copy(name = editName, phone = editPhone))
                                }
                                editingContact = null
                            }, shape = MaterialTheme.shapes.medium) {
                                Text("保存")
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            OutlinedButton(onClick = { editingContact = null }, shape = MaterialTheme.shapes.medium) {
                                Text("取消")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ContactItem(contact: Contact, onDelete: () -> Unit, onEdit: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 2.dp),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(modifier = Modifier.padding(18.dp), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = contact.name, style = MaterialTheme.typography.titleMedium)
                Text(text = contact.phone, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
            }
            Button(onClick = onEdit, shape = MaterialTheme.shapes.medium) {
                Text("编辑")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = onDelete, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error), shape = MaterialTheme.shapes.medium) {
                Text("删除")
            }
        }
    }
}

class FakeContactViewModel : ContactViewModel(Application()) {
    override val contacts: StateFlow<List<Contact>> = MutableStateFlow(
        listOf(
            Contact(id = 1, name = "桦 原味", phone = "04000721"),
            Contact(id = 2, name = "高杆 灯", phone = "95959292")
        )
    )
    override fun addContact(name: String, phone: String) {}
    override fun updateContact(contact: Contact) {}
    override fun deleteContact(contact: Contact) {}
}

// preview currently not working, no time to fix LMAO ©ø^ ƒ˚^¨®ß´¬ƒ^^
@Preview(showBackground = true)
@Composable
fun ContactScreenPreview() {
    ContactTheme {
        ContactScreen(modifier = Modifier, viewModel = FakeContactViewModel())
    }
}
