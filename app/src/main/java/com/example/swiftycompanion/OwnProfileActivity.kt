package com.example.swiftycompanion


import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
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

    private val CLIENT_ID = "u-s4t2ud-dc1df21032f1b7395ef00e3f89ace6cca48cc12b12bd54457ed5c0d032c06c5f"
    private val CLIENT_SECRET = "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"
    private val TOKEN_ENDPOINT = "https://api.intra.42.fr/oauth/token"

    private lateinit var accessToken: String
    private var login: String = "namenega"

    private lateinit var loginTV: TextView
    private lateinit var profileImage: CircleImageView
    private lateinit var searchBtn: Button
    private lateinit var loginET: EditText

    override fun onCreate(savedInstanceState:Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.own_profile_layout)

        val uri = intent.data

        if ((uri != null) && uri.toString().startsWith("swiftycompanionapp://oauth2callback/oauth2callback")) {
            // the activity was started with an authorization code.
            // extract the code from URI and use it to request an access token
            requestAccessToken()
        } else {
            setContentView(R.layout.own_profile_layout)
//            requestAccessToken()
        }

        loginTV = findViewById(R.id.login_textview)
        profileImage = findViewById(R.id.profileImage)
        loginET = findViewById(R.id.login)
        searchBtn = findViewById(R.id.submitSearch);

        Picasso.get().load("https://cdn.intra.42.fr/users/062c898b8689243600d15da94813f130/namenega.png")
            .into(profileImage)

        searchBtn.setOnClickListener {
            login = loginET.text.toString()

            requestLogin(login, accessToken, 1)
        }
    }

    private suspend fun apiRequest(url: String, status: Int): String {
        return withContext(Dispatchers.IO) {
            try {
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url(url)
                    .addHeader("Authorization", ("Bearer " + accessToken))
                    .build()
                val response = client.newCall(request).execute()
                Log.i("RESPONSE", response.toString())
                Log.i("RESPONSE_CODE", response.code.toString())
                if (response.code == 200) {
                    val responseBody = response.peekBody(Long.MAX_VALUE).string()
                    println(responseBody)
                    val jsonObject = JSONObject(responseBody)
                    jsonObject.getString("login")
                } else {
                    Log.i("STATUS", status.toString())
                    if (status == 0) {
                        throw IOException("HTTP error code: ")
                    } else {
                        Log.i("JSON", JSONObject().toString())
                        JSONObject().toString()
                    }
                }
            } catch (e: Exception) {
                throw IOException("Error while getting response from $url: ${e.message}")
            }
        }
    }

    private fun requestAccessToken() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
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

                val response = OkHttpClient().newCall(request).execute()
                val responseBody = response.peekBody(Long.MAX_VALUE).string()

                if (response.isSuccessful && responseBody != null) {
                    val token = JSONObject(responseBody).getString("access_token")
//                    Log.d("ACCESS_TOKEN", accessToken)
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

    private fun requestLogin(login: String, token: String, status: Int) {
        var str: String
        accessToken = token
        Log.i("TOKEN", accessToken)
        CoroutineScope(Dispatchers.Main).launch {
            str = async { apiRequest("https://api.intra.42.fr/v2/users/" + login, status) }.await()

            Log.i("STR", str)

            if (str.isNotEmpty() && str != "{}" && str != "namenega") {
                val intent = Intent(this@OwnProfileActivity, StudentProfileActivity::class.java)

                intent.putExtra("login", login)
                intent.putExtra("accessToken", accessToken)

                startActivity(intent)
            } else {
                if (str != "namenega") {
                    loginET.setBackgroundColor(Color.RED)
                }
            }
        }
    }
}