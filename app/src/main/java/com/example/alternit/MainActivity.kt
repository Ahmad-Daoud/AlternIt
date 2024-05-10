package com.example.alternit
import com.example.alternit.ui.theme.AlternItTheme
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
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import android.content.Context
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Checkbox
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.Alignment
import org.json.JSONArray
import org.json.JSONObject


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AlternItTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    MyApp(context = applicationContext)
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

@Composable
fun EventList(events: List<Event>) {
    LazyColumn {
        items(events) { event ->
            Text(
                text = "${event.eventName} - ${event.eventDate}",
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
    val eventsState = remember { mutableStateOf(emptyList<Event>()) }

    LaunchedEffect(Unit) {
        refreshContactList(context, contactsState)
        refreshEventList(context, eventsState)
    }

    Scaffold(
        bottomBar = {
            BottomAppBar(
                modifier = Modifier.fillMaxWidth(),
                content = { }
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
        Column {
            ContactList(contacts = contactsState.value)
            EventList(events = eventsState.value)
        }

        if (showDialog) {
            AddItemDialog(
                onClose = { showDialog = false },
                context = context,
                onContactAdded = { refreshContactList(context, contactsState) },
                onEventAdded = { refreshEventList(context, eventsState) }
            )
        }
    }
}

fun refreshContactList(context: Context, contactsState: MutableState<List<Contact>>) {
    contactsState.value = readContactsFromJson(context)
}
fun refreshEventList(context: Context, eventsState: MutableState<List<Event>>) {
    eventsState.value = readEventsFromJson(context)
}


data class Contact(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val company: String
)
data class Event(
    val id: Int,
    val eventName: String,
    val eventDate: String,
    val eventLocation: String,
    val contactsLinked: List<Int>
)


enum class DialogType {
    Contact,
    Event
}


fun readContactsFromJson(context: Context): List<Contact> {
    val contacts = mutableListOf<Contact>()
    try {
        val jsonString = context.openFileInput("contacts.json").bufferedReader().use { it.readText() }
        val jsonArray = JSONArray(jsonString)
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val contact = Contact(
                id = jsonObject.getInt("id"),
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
    return contacts
}
fun readEventsFromJson(context: Context): List<Event> {
    val events = mutableListOf<Event>()
    try {
        val jsonString = context.openFileInput("events.json").bufferedReader().use { it.readText() }
        val jsonArray = JSONArray(jsonString)
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val event = Event(
                id = jsonObject.getInt("id"),
                eventName = jsonObject.getString("eventName"),
                eventDate = jsonObject.getString("eventDate"),
                eventLocation = jsonObject.getString("eventLocation"),
                contactsLinked = jsonArrayToContactsList(jsonObject.getJSONArray("contactsLinked"))
            )
            events.add(event)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return events
}

private fun jsonArrayToContactsList(jsonArray: JSONArray): List<Int> {
    val contactsList = mutableListOf<Int>()
    for (i in 0 until jsonArray.length()) {
        contactsList.add(jsonArray.getInt(i))
    }
    return contactsList
}

fun getLastContactId(context: Context): Int {
    var lastContactId = 0
    try {
        val jsonString = context.openFileInput("contacts.json").bufferedReader().use { it.readText() }
        val jsonArray = JSONArray(jsonString)
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val contactId = jsonObject.getInt("id")
            if (contactId > lastContactId) {
                lastContactId = contactId
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return lastContactId
}



fun getLastEventId(context: Context): Int {
    var lastEventId = 0
    try {
        val jsonString = context.openFileInput("events.json").bufferedReader().use { it.readText() }
        val jsonArray = JSONArray(jsonString)
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val eventId = jsonObject.getInt("id")
            if (eventId > lastEventId) {
                lastEventId = eventId
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return lastEventId
}

fun writeContactToJson(context: Context, contactName: String, lastName: String, phoneNumber: String, company: String) {
    val contactObject = JSONObject().apply {
        put("prenom", contactName)
        put("nom", lastName)
        put("telephone", phoneNumber)
        put("entreprise", company)
    }

    try {
        val existingContacts = readContactsFromJson(context).toMutableList()
        val contactId = getLastContactId(context) + 1
        existingContacts.add(Contact(contactId ,contactName, lastName, phoneNumber, company))

        val jsonArray = JSONArray(existingContacts.map { contact ->
            JSONObject().apply {
                put("id", contact.id)
                put("prenom", contact.firstName)
                put("nom", contact.lastName)
                put("telephone", contact.phoneNumber)
                put("entreprise", contact.company)
            }
        })
        val jsonString = jsonArray.toString()
        val fileOutputStream = context.openFileOutput("contacts.json", Context.MODE_PRIVATE)
        fileOutputStream.write(jsonString.toByteArray())
        fileOutputStream.close()
    } catch (e: Exception) {
        e.printStackTrace()
        println("Erreur ecriture de contact!")
    }
}

fun writeEventToJson(context: Context, eventName: String, eventDate: String, eventLocation: String, contactsLinked: List<Int>) {
    val eventObject = JSONObject().apply {
        put("eventName", eventName)
        put("eventDate", eventDate)
        put("eventLocation", eventLocation)
        put("contactsLinked", JSONArray(contactsLinked))
    }

    try {
        val existingEvents = readEventsFromJson(context).toMutableList()
        existingEvents.add(Event(getLastEventId(context) + 1, eventName, eventDate, eventLocation, contactsLinked))

        val jsonArray = JSONArray(existingEvents.map { event ->
            JSONObject().apply {
                put("id", event.id)
                put("eventName", event.eventName)
                put("eventDate", event.eventDate)
                put("eventLocation", event.eventLocation)
                put("contactsLinked", JSONArray(event.contactsLinked))
            }
        })

        val jsonString = jsonArray.toString()
        val fileOutputStream = context.openFileOutput("events.json", Context.MODE_PRIVATE)
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
                    writeContactToJson(context, contactName, lastName, phoneNumber, company)
                    onClose() // Close the dialog
                    onContactAdded() // Trigger refresh of contacts list
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

@Composable
fun AddEventDialog(
    onClose: () -> Unit,
    context: Context,
    onEventAdded: () -> Unit
) {
    var eventName by remember { mutableStateOf("") }
    var eventDate by remember { mutableStateOf("") }
    var eventLocation by remember { mutableStateOf("") }
    val selectedContacts = remember { mutableStateOf(mutableListOf<Int>()) }
    val contacts = readContactsFromJson(context) // Retrieve the list of contacts
    AlertDialog(
        onDismissRequest = onClose,
        title = { Text("Ajouter un evenement") },
        text = {
            Column {
                TextField(
                    value = eventName,
                    onValueChange = { eventName = it },
                    label = { Text("Nom") },
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                TextField(
                    value = eventDate,
                    onValueChange = { eventDate = it },
                    label = { Text("Date") },
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                TextField(
                    value = eventLocation,
                    onValueChange = { eventLocation = it },
                    label = { Text("Localisation") }
                )
                Text("Choisir des contacts qui font partie de l'evenement:")
                contacts.forEach { contact ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Checkbox(
                            checked = selectedContacts.value.contains(contact.id),
                            onCheckedChange = {
                                if (it) {
                                    selectedContacts.value.add(contact.id)
                                } else {
                                    selectedContacts.value.remove(contact.id)
                                }
                            }
                        )
                        Text(
                            text = "${contact.firstName} ${contact.lastName}",
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    writeEventToJson(context, eventName, eventDate, eventLocation, selectedContacts.value)
                    onClose()
                    onEventAdded()
                }
            ) {
                Text(
                    text = "Ajouter l'evenement",
                    style = TextStyle(fontSize = 11.sp)
                )
            }
        },
        dismissButton = {
            Button(
                onClick = onClose
            ) {
                Text(
                    text = "Annuler",
                    style = TextStyle(fontSize = 12.sp)
                )
            }
        }
    )
}

@Composable
fun AddItemDialog(
    onClose: () -> Unit,
    context: Context,
    onContactAdded: () -> Unit,
    onEventAdded: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var dialogType by remember { mutableStateOf(DialogType.Contact) }
    AlertDialog(
        onDismissRequest = onClose,
        title = { Text("Ajouter un : ") },
        text = { /* No additional text */ },
        confirmButton = {
            Button(
                onClick = {
                    dialogType = DialogType.Contact
                    showDialog = true
                }
            ) {
                Text("Contact")
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    dialogType = DialogType.Event
                    showDialog = true
                }
            ) {
                Text("Evenement")
            }
        }
    )

    if(showDialog){
        if (dialogType == DialogType.Contact) {
            AddContactDialog(
                onClose = {
                    showDialog = false
                    onClose()
                },
                context = context,
                onContactAdded = onContactAdded
            )
        } else {
            AddEventDialog(
                onClose = {
                    showDialog = false
                    onClose()
                },
                context = context,
                onEventAdded = onEventAdded
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    val context = LocalContext.current
    AlternItTheme { MyApp(context) }
}