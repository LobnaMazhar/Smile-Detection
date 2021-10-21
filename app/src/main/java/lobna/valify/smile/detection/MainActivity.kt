package lobna.valify.smile.detection

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import lobna.smile.detection.ui.SmileActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startActivity(Intent(this, SmileActivity::class.java))
    }
}