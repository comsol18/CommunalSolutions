package com.communal_solutions.www.communalsolutions

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.JsonToken
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_settings.*
import org.json.JSONObject


data class Salad(
        val name: String = "",
        val description: String = "",
        var uuid: String = ""
)

data class Profile(
    // profile info
    val profile_name: String = "",
    val user_name: String = "",
    val cell_number: String = "",
    val email_addres: String = "",
    val status: String = "",
    var uuid: String = ""
)

class SettingsActivity : AppCompatActivity() {

    private var db = FirebaseDatabase.getInstance()
    private val users = db.getReference("users")
    private var cUser = FirebaseAuth.getInstance().currentUser!!
    private var profile_values: MutableList<Profile> = mutableListOf()
    private val profile_texts: ArrayList<EditText> = ArrayList(5)

    // Initializing edittexts
    private fun initValues() {
        val menuListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                profile_values.clear()
                dataSnapshot.children.mapNotNullTo(profile_values) {
                    it.getValue<Profile>(Profile::class.java)
                }

                profile_values.forEach {
                    if (it.uuid == cUser.email!!.hashCode().toString()) {
                        profile_texts[0].setText(it.profile_name)
                        profile_texts[1].setText(it.user_name)
                        profile_texts[2].setText(it.email_addres)
                        profile_texts[3].setText(it.cell_number)
                        profile_texts[4].setText(it.status)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("loadPost:onCancelled ${databaseError.toException()}")
            }
        }
        users.child("users").addValueEventListener(menuListener)
        Toast.makeText(this, "Data Pulled", Toast.LENGTH_LONG).show()
    }

    fun updateProfile(view: View) {
        val email = FirebaseAuth.getInstance().currentUser!!.email.toString()
        val username = email.substringBefore('@', "")
        val editName = findViewById<EditText>(R.id.editDisplayName)
        val editNum = findViewById<EditText>(R.id.editPhoneNum)
        val editStatus = findViewById<EditText>(R.id.editStatus)

        val profile = Profile(editName.text.toString(), username, editNum.text.toString(), email, editStatus.text.toString(), cUser.email!!.hashCode().toString())

        users.child(cUser.email!!.hashCode().toString()).setValue(profile)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        /*
	        define references
	        define current user
         */

        profile_texts.add(findViewById(R.id.editDisplayName))
        profile_texts.add(findViewById(R.id.editUsername))
        profile_texts.add(findViewById(R.id.editPhoneNum))
        profile_texts.add(findViewById(R.id.editEmail))
        profile_texts.add(findViewById(R.id.editStatus))

        initValues()
    }

    override fun onStart() {
        super.onStart()

        /*
            define a ValueEventListener
                onDataChange:
                    if dataSnapshot exists
                        get values
                        use data
                onCancelled:
                    do things
            add listener to reference
            set global listener to the listener defined
         */
    }

    override fun onStop() {
        super.onStop()
        /*
            remove the listener
         */
    }
}

/* Reading example
private fun initSaladMenu(firebaseData: DatabaseReference) {
    val menuListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val editName = findViewById<EditText>(R.id.editDisplayName)
            menu.clear()
            dataSnapshot.children.mapNotNullTo(menu) {
                it.getValue<Salad>(Salad::class.java)
            }
            menu.forEach {
                editName.setText(it.name)
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
            println("loadPost:onCancelled ${databaseError.toException()}")
        }
    }
    firebaseData.child("salads").addListenerForSingleValueEvent(menuListener)
}
// --------------------

// Writing example
fun loadDatabase(firebaseData: DatabaseReference) {
    val availableSalads: List<Salad> = mutableListOf(
            Salad("Gherkin", "Fresh and delicious"),
            Salad("Lettuce", "Easy to prepare"),
            Salad("Tomato", "Boring but healthy"),
            Salad("Zucchini", "Healthy and gross")
    )
    availableSalads.forEach {
        val key = firebaseData.child("salads").push().key
        it.uuid = key.toString()
        //firebaseData.child("salads").child(key.toString()).setValue(it)
    }
    Toast.makeText(this, "Data Pushed", Toast.LENGTH_LONG).show()
}

// -------------------- */
