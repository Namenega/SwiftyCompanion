package com.example.swiftycompanion

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ExpandableListView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class StudentProfileActivity : AppCompatActivity()  {

    private lateinit var loginTV : TextView
    private lateinit var backIB: ImageButton

    private lateinit var expandableListView: ExpandableListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.student_profile_layout)

        // Get the input value from the Intent
        val login = intent.getStringExtra("login")
        val accessToken = intent.getStringExtra("accessToken")

        // Use the input value as needed
        // For example, you can set the text of a TextView to display the login:
        loginTV = findViewById<TextView>(R.id.login)
        loginTV.setText(login)

        backIB = findViewById(R.id.backbtn)

        expandableListView = findViewById(R.id.exp_list_view)
        val listGroup: ArrayList<String> = ArrayList<String>()
        val listChild: HashMap<String, ArrayList<String>> = HashMap<String, ArrayList<String>>()

        for (i in 0..1) {
            // Add value in group list
            if (i == 0) {
                listGroup.add("Personal Information")
                val arrayList: ArrayList<String> = ArrayList<String>()

                for (j in 0..2) {
                    if (j == 0) {
                        arrayList.add("Firstname")
                    } else if (j == 1) {
                        arrayList.add("Lastname")
                    } else {
                        arrayList.add("Email")
                    }
                }

                //Put value in child list
                listChild.put(listGroup.get(i), arrayList)
            } else if (i == 1) {
                listGroup.add("Cursus Information")
                val arrayList: ArrayList<String> = ArrayList<String>()

                for (j in 0..5) {
                    arrayList.add("Item" + j)
                }

                //Put value in child list
                listChild.put(listGroup.get(i), arrayList)
            }
        }

        val adapter = PersonalInfoAdapter(listGroup, listChild)

        expandableListView.setAdapter(adapter)

        backIB.setOnClickListener {
            val intent = Intent(this, OwnProfileActivity::class.java)

            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            intent.putExtra("accessToken", accessToken)

            startActivity(intent)
        }
    }
}