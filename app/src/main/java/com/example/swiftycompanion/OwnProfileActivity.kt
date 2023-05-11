package com.example.swiftycompanion


import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException

class OwnProfileActivity : AppCompatActivity() {

    //! TODO : change how it works
    private val CLIENT_ID = "u-s4t2ud-dc1df21032f1b7395ef00e3f89ace6cca48cc12b12bd54457ed5c0d032c06c5f"
    private val CLIENT_SECRET = "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"
    private val TOKEN_ENDPOINT = "https://api.intra.42.fr/oauth/token"

    // Declare variable to store access token
    private var accessToken: String? = null
    private var uri: Uri? = null
    private lateinit var login: String

    // Declare variable to get elements from current layout
    private lateinit var loginTV: TextView
    private lateinit var profileImage: CircleImageView
    private lateinit var searchBtn: Button
    private lateinit var loginET: EditText

    // Starting function for Activity
    override fun onCreate(savedInstanceState:Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.own_profile_layout)

        // Get data from previous activity (MainActivity)
        uri = intent.data

        // Get the code
        val result = uri?.getBooleanQueryParameter("code", true)

        // If the code from 42 is ok, with stay, else, we go back to MainActivity
        if (result == true && uri?.getQueryParameter("code").toString() != "null") {
            val authCode = uri?.getQueryParameter("code").toString()
        } else if (result == false || uri?.getQueryParameter("code").toString() == "null") {

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // Get access token from StudentProfileActivity
        accessToken = intent.getStringExtra("accessToken")

        // Request access token with code from callback
        if ((uri != null) && uri.toString().startsWith("swiftycompanionapp://oauth2callback/oauth2callback") && accessToken == null) {
            requestAccessToken()
        } else {
            setContentView(R.layout.own_profile_layout)
        }


        // Get elements from current layout
        profileImage    = findViewById(R.id.profileImage)
        loginTV         = findViewById(R.id.login_textview)
        loginET         = findViewById(R.id.login)
        searchBtn       = findViewById(R.id.submitSearch)

        // Get profile picture from Intra
        Picasso.get().load("https://cdn.intra.42.fr/users/062c898b8689243600d15da94813f130/namenega.png")
            .into(profileImage)

        // OnClickListener for 'Search' button
        searchBtn.setOnClickListener {
            login = loginET.text.toString()

            // Request login to search for and go next Activity
            accessToken?.let { it1 -> requestLogin(login, it1, 1) }
        }
    }

    // Make request to get Login value from JSON
    private suspend fun apiRequest(url: String, status: Int): JSONObject {
        return withContext(Dispatchers.IO) {
            try {

                // Create a client for request
                val client = OkHttpClient()

                // Create request
                val request = Request.Builder()
                    .url(url)
                    .addHeader("Authorization", ("Bearer " + accessToken))
                    .build()

                // Get response from request
                val response = client.newCall(request).execute()

                // Response = 200 --> Get response body
                // Isolate 'login' value from JSON and return it
                if (response.code == 200) {
                    val responseBody = response.peekBody(Long.MAX_VALUE).string()
//                    val jsonObject = JSONObject(responseBody)
                    JSONObject(responseBody)
//                    jsonObject.getString("login")
                }
                // Else, return empty JSON '{}'
                else {
                    if (status == 0) {
                        throw IOException("HTTP error code: ")
                    } else {
                        JSONObject()
                    }
                }
            } catch (e: Exception) {
                throw IOException("Error while getting response from $url: ${e.message}")
            }
        }
    }

    // Request Access Token
    private fun requestAccessToken() {
        // Start Routing IO at launch
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Create request
                val request = Request.Builder()
                    .url(TOKEN_ENDPOINT)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .post(
                        FormBody.Builder()
                            .add("grant_type", "client_credentials")
                            .add("client_id", CLIENT_ID)
                            .add("client_secret", CLIENT_SECRET)
                            .build()
                    )
                    .build()

                // Get response from request
                val response = OkHttpClient().newCall(request).execute()

                // Get response body
                val responseBody = response.peekBody(Long.MAX_VALUE).string()

                // If response, get token and request Login
                if (response.isSuccessful) {
                    val token = JSONObject(responseBody).getString("access_token")
                    withContext(Dispatchers.Main) {
                        requestLogin("namenega", token, 0)
                    }
                } else {
                    Log.e("ACCESS_TOKEN", "Failed to get access token")
                }
            } catch (e: Exception) {
                Log.e("ACCESS_TOKEN", "Error: ${e.message}")
            }
        }
    }

    // Request Login from JSON
    private fun requestLogin(login: String, token: String, status: Int) {

        // Declare string and get token
        accessToken = token

        // Start Routing Main at launch
        CoroutineScope(Dispatchers.Main).launch {

            // Async on apiRequest() to get JSON Object
            val obj = async { apiRequest("https://api.intra.42.fr/v2/users/" + login, status) }.await()

            // If login is not empty and is not empty JSON and not myself
            // create Intent and go next Activity (StudentProfileActivity)
            if (obj.toString().isNotEmpty() && obj.toString() != "{}" && obj.getString("login") != "namenega") {

                // Intent
                val studIntent = Intent(this@OwnProfileActivity, StudentProfileActivity::class.java)

                // Add data from Main, login, access token and JSONObject to intent
                studIntent.data = intent.data
                studIntent.putExtra("accessToken", accessToken)
                studIntent.putExtra("login", login)
                studIntent.putExtra("jsonObject", obj.toString())

                // Start new activity (StudentProfileActivity)
                startActivity(studIntent)
            } else {

                // If student does not exist, color the EditText in Red
                /*if (str == "Namenega") {
                    //recommencer api request login.lowercase()
                }*/
                if (obj.toString().isEmpty() || obj.toString() == "{}" || obj.getString("login") != "namenega") {
                    loginET.setBackgroundColor(Color.RED)
                }

            }
        }
    }
}