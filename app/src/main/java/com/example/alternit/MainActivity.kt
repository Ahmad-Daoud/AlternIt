package com.example.alternit
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import android.util.Log
import com.example.alternit.R
import com.example.alternit.ui.theme.AlternItTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AlternItTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    MyApp(context = applicationContext) // Pass the context here
                }
            }
        }
    }
}

@Composable
fun ContactList(contacts: List<Contact>) {
    LazyColumn {
        items(contacts) { contact ->
            Text(
                text = "${contact.firstName} ${contact.lastName}",
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MyApp(context: Context) {
    var showDialog by remember { mutableStateOf(false) }
    val contactsState = remember { mutableStateOf(emptyList<Contact>()) }

    // Read contacts when MyApp is recomposed
    LaunchedEffect(Unit) {
        refreshContactList(context, contactsState)
    }

    Scaffold(
        bottomBar = {
            BottomAppBar(
                modifier = Modifier.fillMaxWidth(),
                content = {
                    // Empty content, as we're not adding anything to the bottom app bar
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.add_24px),
                    contentDescription = "Add"
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) {
        ContactList(contacts = contactsState.value)

        if (showDialog) {
            AddItemDialog(
                onClose = { showDialog = false },
                context = context,
                onContactAdded = { refreshContactList(context, contactsState) } // Refresh the contact list
            )
        }
    }
}

fun refreshContactList(context: Context, contactsState: MutableState<List<Contact>>) {
    contactsState.value = readContactsFromJson(context)
}



// Data class to represent a contact
data class Contact(
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val company: String
)


// Function to read contacts from JSON file
fun readContactsFromJson(context: Context): List<Contact> {
    val contacts = mutableListOf<Contact>()
    println("start contacts : \n")
    try {
        val jsonString = context.openFileInput("contacts.json").bufferedReader().use { it.readText() }
        Log.d("JSON_CONTENT", jsonString) // Add this line to print JSON content
        val jsonArray = JSONArray(jsonString)
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val contact = Contact(
                firstName = jsonObject.getString("prenom"),
                lastName = jsonObject.getString("nom"),
                phoneNumber = jsonObject.getString("telephone"),
                company = jsonObject.getString("entreprise")
            )
            contacts.add(contact)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    println("End contacts : \n")
    return contacts
}

fun writeContactToJson(context: Context, contactName: String, lastName: String, phoneNumber: String, company: String) {
    val contactObject = JSONObject().apply {
        put("prenom", contactName)
        put("nom", lastName)
        put("telephone", phoneNumber)
        put("entreprise", company)
    }

    try {
        // Read existing contacts
        val existingContacts = readContactsFromJson(context).toMutableList()

        // Add the new contact
        existingContacts.add(Contact(contactName, lastName, phoneNumber, company))

        // Convert the list to JSON array
        val jsonArray = JSONArray(existingContacts.map { contact ->
            JSONObject().apply {
                put("prenom", contact.firstName)
                put("nom", contact.lastName)
                put("telephone", contact.phoneNumber)
                put("entreprise", contact.company)
            }
        })

        // Write the JSON array to file
        val jsonString = jsonArray.toString()
        val fileOutputStream = context.openFileOutput("contacts.json", Context.MODE_PRIVATE)
        fileOutputStream.write(jsonString.toByteArray())
        fileOutputStream.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}



@Composable
fun AddContactDialog(onClose: () -> Unit, context: Context, onContactAdded: () -> Unit) {
    var contactName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var company by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onClose,
        title = { Text("Add Contact") },
        text = {
            Column {
                TextField(
                    value = contactName,
                    onValueChange = { contactName = it },
                    label = { Text("Prénom") },
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                TextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = { Text("Nom") },
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                TextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Numéro de téléphone") },
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                TextField(
                    value = company,
                    onValueChange = { company = it },
                    label = { Text("Entreprise") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    // Handle contact addition here
                    writeContactToJson(context, contactName, lastName, phoneNumber, company)
                    onClose()
                    onContactAdded() // Notify that a contact has been added
                }
            ) {
                Text("Ajout Contact")
            }
        },
        dismissButton = {
            Button(
                onClick = onClose
            ) {
                Text("Quitter")
            }
        }
    )
}

fun AddEventDialog(onClose: () -> Unit) {
    // Functionality for adding event goes here
}


@Composable
fun AddItemDialog(
    onClose: () -> Unit,
    context: Context,
    onContactAdded: () -> Unit // Define onContactAdded parameter here
) {
    var showDialog by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onClose,
        title = { Text("Add Item") },
        text = { /* No additional text */ },
        confirmButton = {
            Button(
                onClick = {
                    // Handle contact button click
                    showDialog = true
                }
            ) {
                Text("Contact")
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    // Handle event button click
                    showDialog = false
                    onClose()
                }
            ) {
                Text("Event")
            }
        }
    )

    if (showDialog) {
        AddContactDialog(
            onClose = {
                showDialog = false
                onClose()
            },
            context = context,
            onContactAdded = onContactAdded // Pass onContactAdded to AddContactDialog
        )
    } else {
        // AddEventDialog can be called similarly when needed
    }
}



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    val context = LocalContext.current
    AlternItTheme { MyApp(context) }
}