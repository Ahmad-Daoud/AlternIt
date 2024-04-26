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
import com.example.alternit.ui.theme.AlternItTheme
import androidx.compose.material3.BottomNavigationItem
import androidx.compose.material3.icons.Icons
import androidx.compose.material3.icons.outlined.Book
import androidx.compose.material3.icons.outlined.Add
import androidx.compose.material3.icons.outlined.Schedule

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AlternItTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    MyApp()
                }
            }
        }
    }
}

@Composable
fun MyApp() {
    Scaffold(
        bottomBar = {
            BottomAppBar(
                modifier = Modifier.fillMaxWidth(),
                content = {
                    BottomNavigationItem(
                        icon = { Icon(painterResource(id = R.drawable.ic_open_book), contentDescription = "Open Book") },
                        label = { Text("Open Book") },
                        selected = false, // Set this according to your logic
                        onClick = { /* Handle click event */ }
                    )
                    BottomNavigationItem(
                        icon = { Icon(painterResource(id = R.drawable.ic_plus_sign), contentDescription = "Add") },
                        label = { Text("Add") },
                        selected = false, // Set this according to your logic
                        onClick = { /* Handle click event */ }
                    )
                    BottomNavigationItem(
                        icon = { Icon(painterResource(id = R.drawable.ic_clock), contentDescription = "Clock Icon") },
                        label = { Text("Clock") },
                        selected = false, // Set this according to your logic
                        onClick = { /* Handle click event */ }
                    )
                }
            )
        }
    ) {
        Greeting("Android")
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AlternItTheme {
        MyApp()
    }
}
