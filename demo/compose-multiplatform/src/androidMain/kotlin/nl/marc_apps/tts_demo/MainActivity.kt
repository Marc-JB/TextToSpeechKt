package nl.marc_apps.tts_demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.*
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import nl.marc_apps.tts_demo.resources.*

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()

        super.onCreate(savedInstanceState)

        setContent {
            TtsDemoApp(
                topAppBar = {
                    TopAppBar(
                        title = {
                            Text(stringResource(Res.string.app_name))
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                        )
                    )
                }
            )
        }
    }
}
