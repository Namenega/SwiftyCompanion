package com.example.swiftycompanion

import android.content.Intent
import android.os.Bundle
import android.widget.ExpandableListView
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONObject
import java.math.RoundingMode

class StudentProfileActivity : AppCompatActivity()  {

    private lateinit var backIB: ImageButton
    private lateinit var profilePicCIV: CircleImageView
    private lateinit var loginTV : TextView
    private lateinit var expandableListView: ExpandableListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.student_profile_layout)

        // Get the input value from the Intent
        val intentFromOPA = intent
        val login = intent.getStringExtra("login")
        val accessToken = intent.getStringExtra("accessToken")

        // Get JSON object of searched login
        val jsonString = intent.getStringExtra("jsonObject")
        val jsonObject = jsonString?.let { JSONObject(it) }


        //Fill the layout
        backIB = findViewById(R.id.backbtn)
        profilePicCIV = findViewById(R.id.profileImage)
        if (jsonObject != null) {
            Picasso.get().load(jsonObject.getJSONObject("image").getString("link"))
                .into(profilePicCIV)
        }
        loginTV = findViewById(R.id.login)
        loginTV.text = login

        expandableListView = findViewById(R.id.exp_list_view)

        // Value
        val listGroup: ArrayList<String> = ArrayList()
        val listChild: HashMap<String, ArrayList<String>> = HashMap()

        // Loop to create group list
        for (i in 0..3) {

            when (i) {
                // 1st group list - Personal Information
                0 -> {
                    listGroup.add("Personal Information")
                    var arrayList: ArrayList<String> = ArrayList()

                    // Loop to create child elements in 1st group list
                    if (jsonObject != null) {
                        arrayList = createChildFirstGroup(arrayList, jsonObject)
                    }

                    //Put values in child list
                    listChild[listGroup[i]] = arrayList
                }
                // 2nd group list - Cursus Information
                1 -> {
                    listGroup.add("Cursus Information")
                    var arrayList: ArrayList<String> = ArrayList()

                    // Loop to create child elements in 2nd group list
                    if (jsonObject != null) {
                        arrayList = createChildSecondGroup(arrayList, jsonObject)
                    }

                    //Put value in child list
                    listChild[listGroup[i]] = arrayList
                }
                // 3rd group list - Skills Information
                2 -> {
                    listGroup.add("Skills Information")
                    var arrayList: ArrayList<String> = ArrayList()

                    // Loop to create child elements in 2nd group list
                    if (jsonObject != null) {
                        arrayList = createChildThirdGroup(arrayList, jsonObject)
                    }

                    //Put value in child list
                    listChild[listGroup[i]] = arrayList
                }
                // 4th group list - Project Information
                else -> {
                    listGroup.add("Projects Information")
                    var arrayList: ArrayList<String> = ArrayList()

                    // Loop to create child elements in 2nd group list
                    if (jsonObject != null) {
                        arrayList = createChildFourthGroup(arrayList, jsonObject)
                    }

                    //Put value in child list
                    listChild[listGroup[i]] = arrayList
                }
            }
        }

        // Adapter for ExpandableListView
        val adapter = PersonalInfoAdapter(listGroup, listChild)
        expandableListView.setAdapter(adapter)

        // Back button to return to OwnProfileActivity
        backIB.setOnClickListener {
            val myIntent = Intent(this, OwnProfileActivity::class.java)

            myIntent.data = intentFromOPA.data
            myIntent.putExtra("accessToken", accessToken)

            startActivity(myIntent)
        }
    }

    // Get Personal Information
    private fun createChildFirstGroup(arrayList: ArrayList<String>, jsonObject: JSONObject): ArrayList<String> {
        for (j in 0..3) {
            when (j) {
                0 -> arrayList.add("First Name - " + jsonObject.getString("first_name"))
                1 -> arrayList.add("Last Name - " + jsonObject.getString("last_name"))
                2 -> arrayList.add("Email - " + jsonObject.getString("email"))
                else -> arrayList.add("Campus - "
                        + jsonObject.getJSONArray("campus").getJSONObject(0).getString("name"))
            }
        }
        return arrayList
    }

    // Get Cursus Information
    private fun createChildSecondGroup(arrayList: ArrayList<String>, jsonObject: JSONObject): ArrayList<String> {
        for (j in 0..3) {
            when (j) {
                0 ->
                    arrayList.add(jsonObject.getString("kind")
                        .replaceFirstChar { it.uppercase() })
                1 ->
                    arrayList.add("Level - "
                            + jsonObject.getJSONArray("cursus_users").getJSONObject(1).getString("level"))
                2 ->
                    arrayList.add("Piscine - "
                            + jsonObject.getString("pool_month") + " "
                            + jsonObject.getString("pool_year"))
                else ->
                    arrayList.add("Correction points - " + jsonObject.getInt("correction_point"))
            }
        }
        return arrayList
    }

    // Get Skills Information
    private fun createChildThirdGroup(arrayList: ArrayList<String>, jsonObject: JSONObject): ArrayList<String> {

        // Get Skills array from JSON
        val skills = jsonObject.getJSONArray("cursus_users").getJSONObject(1).getJSONArray("skills")

        // Add skills name and level into arrayList
        for (j in 0 until skills.length()) {
            arrayList.add(skills.getJSONObject(j).getString("name")
                    + " - "
                    + skills.getJSONObject(j).getDouble("level").toBigDecimal().setScale(5, RoundingMode.UP).toDouble())
        }
        return arrayList
    }

    // Get Projects Information
    private fun createChildFourthGroup(arrayList: ArrayList<String>, jsonObject: JSONObject): ArrayList<String> {

        // Get Projects array from JSON
        val projects = jsonObject.getJSONArray("projects_users")

        // Add projects and final_mark into arrayList
        for (j in 0 until projects.length()) {
            val final_mark = projects.getJSONObject(j).getString("final_mark")

            if (final_mark != "null")
                arrayList.add(projects.getJSONObject(j).getJSONObject("project").getString("name")
                    + " - " + projects.getJSONObject(j).getDouble("final_mark"))
        }

        arrayList.sorted()

        return arrayList
    }
}
