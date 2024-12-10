package nl.marc_apps.tts_demo

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import nl.marc_apps.tts_demo.databinding.ActivityMainXmlBinding

class MainXmlActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(ActivityMainXmlBinding.inflate(layoutInflater).root)
    }
}
