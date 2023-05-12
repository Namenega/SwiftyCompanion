package com.example.swiftycompanion

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts

class MainActivity : AppCompatActivity() {

    // Get Button from layout
    private lateinit var loginButton: Button

    // Uri to the 42 API
    private val baseUri = "https://api.intra.42.fr/oauth/authorize?client_id=u-s4t2ud-dc1df21032f1b7395ef00e3f89ace6cca48cc12b12bd54457ed5c0d032c06c5f&redirect_uri=swiftycompanionapp%3A%2F%2Foauth2callback%2Foauth2callback&response_type=code"

    // Launch the Oauth2 portal to 42
    private val authLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { _ -> }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loginButton = findViewById(R.id.loginbtn)

        loginButton.setOnClickListener {
            val authUri = Uri.parse(baseUri)
            val intent = Intent(Intent.ACTION_VIEW, authUri)
//            intent.putExtra("code", 3000)
//            setResult(Activity.RESULT_OK, intent)
//            finish()
            authLauncher.launch(intent)
        }
    }

//    override fun onStart() {
//        // registerForActivityResult() should be call here
//        super.onStart()
//    }
}