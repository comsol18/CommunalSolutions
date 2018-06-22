package com.communal_solutions.www.communalsolutions

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import android.support.design.widget.Snackbar
import android.support.v4.app.FragmentActivity
import android.widget.Button
import com.google.android.gms.common.SignInButton
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.AuthCredential
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Thread.sleep


class MainActivity : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null
    private var signInClient: GoogleSignInClient? = null

    private fun updateUI(user: FirebaseUser?) {
        val homeIntent: Intent = Intent(this, HomeActivity::class.java)
        if (user != null) {
            startActivity(homeIntent)
        }
    }

    private fun checkValidEmail(eText: String): Boolean {
        val email: EditText = findViewById(R.id.email)

        // list of most common email addresses
        val validEmails: Array<String> = arrayOf("aol.com", "att.net", "comcast.net", "facebook.com", "gmail.com", "gmx.com", "googlemail.com",
		"google.com", "hotmail.com", "hotmail.co.uk", "mac.com", "me.com", "mail.com", "msn.com",
		"live.com", "sbcglobal.net", "verizon.net", "yahoo.com", "yahoo.co.uk", "email.com",
		"fastmail.fm", "games.com", "gmx.net", "hush.com", "hushmail.com", "icloud.com",
		"iname.com", "inbox.com", "lavabit.com", "love.com", "outlook.com", "pobox.com",
		"protonmail.com", "rocketmail.com", "safe-mail.net", "wow.com", "ygm.com", "ymail.com",
		"zoho.com", "yandex.com", "bellsouth.net", "charter.net", "cox.net", "earthlink.net",
		"juno.com", "btinternet.com", "virginmedia.com", "blueyonder.co.uk", "freeserve.co.uk",
		"live.co.uk", "ntlworld.com", "o2.co.uk", "orange.net", "sky.com", "talktalk.co.uk",
		"tiscali.co.uk", "virgin.net", "wanadoo.co.uk", "bt.com", "sina.com", "qq.com",
		"naver.com", "hanmail.net", "daum.net", "nate.com", "yahoo.co.jp", "yahoo.co.kr",
		"yahoo.co.id", "yahoo.co.in", "yahoo.com.sg", "yahoo.com.ph", "hotmail.fr", "live.fr",
		"laposte.net", "yahoo.fr", "wanadoo.fr", "orange.fr", "gmx.fr", "sfr.fr", "neuf.fr",
		"free.fr", "gmx.de", "hotmail.de", "live.de", "online.de", "t-online.de", "web.de",
		"yahoo.de", "libero.it", "virgilio.it", "hotmail.it", "aol.it", "tiscali.it", "alice.it",
		"live.it", "yahoo.it", "email.it", "tin.it", "poste.it", "teletu.it", "mail.ru",
		"rambler.ru", "yandex.ru", "ya.ru", "list.ru", "hotmail.be", "live.be", "skynet.be",
		"voo.be", "tvcablenet.be", "telenet.be", "hotmail.com.ar", "live.com.ar", "yahoo.com.ar",
		"fibertel.com.ar", "speedy.com.ar", "arnet.com.ar", "yahoo.com.mx", "live.com.mx",
		"hotmail.es", "hotmail.com.mx", "prodigy.net.mx", "yahoo.com.br", "hotmail.com.br",
		"outlook.com.br", "uol.com.br", "bol.com.br", "terra.com.br", "ig.com.br",
		"itelefonica.com.br", "r7.com", "zipmail.com.br", "globo.com", "globomail.com", "oi.com.br")

        // Check if field is empty
        if (TextUtils.isEmpty(eText)) {
            email.error = "Field must not be empty."
            return false
        } // check if text entered is in email format
        else if (eText.contains("@")) {
            // validate a university email if possible else check other email types
            if (eText.contains(".edu")) return true
        } // Text is not in email format
        else {
            email.error = "Email must contain the '@' symbol."
            return false
        }

        // check if email is one of the most common types after confirming @ exists else not a valid email address
        // since these emails will cover most people, temporary spam emails can be avoided
        for (emailType in validEmails) {
            // if the email matches, return true confirming it is a valid email
            if (eText.contains(emailType)) return true
        }

        // if after all checks, default to an invalid email
        email.error = "Please use an email from a more common domain."
        return false
    }

    private fun checkValidPassword(pText: String): Boolean {
        val pass: EditText = findViewById(R.id.password)

        when {
            // Checks that the field isn't left empty
            TextUtils.isEmpty(pText) -> {
                pass.error = "Field cannot be empty."
                return false
            } // Checks that the password given is long enough
            pText.length < 6 -> {
                pass.error = "Password must be at least 6 characters in length."
                return false
            } // Checks password requirements
            else -> {
                val reqs = arrayOf(false, false, false)

                // Searches password for alphanumeric characters
                for (x in pText) {
                    when (x) {
                        in '0'..'9' -> reqs[0] = true
                        in 'a'..'z' -> reqs[1] = true
                        in 'A'..'Z' -> reqs[2] = true
                    }
                }

                // Checks that all requirements were met and gives an error otherwise
                for (req in reqs) {
                    if (!req) {
                        pass.error = "Password must contain upper and lower case\ncharacters and numbers."
                        return false
                    }
                }

                // Returns true when requirements met
                return true
            }
        }
    }

    fun checkCreds(eText: String, pText: String): Boolean {
        // Check to see if email and password are valid
        if (!checkValidEmail(eText)) return false
        if (!checkValidPassword(pText)) return false
        return true
    }


    fun signInWithGoogle() {
        val RC_SIGN_IN = 9001
        val signInIntent: Intent = signInClient!!.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    fun signInWithEmail() {
        val email: EditText = findViewById(R.id.email)
        val pass: EditText = findViewById(R.id.password)
        val eText = email.text.toString()
        val pText = pass.text.toString()

        if (checkCreds(eText, pText) && mAuth != null) {
            mAuth!!.signInWithEmailAndPassword(eText, pText)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Registration Completes
                            Log.d("Info", "signInWithEmailAndPassword:success")
                            //Toast.makeText(this, "Login Success", Toast.LENGTH_SHORT).show()
                            val user: FirebaseUser? = mAuth!!.currentUser
                            updateUI( user)
                        } else {
                            // Registration Errors
                            Log.w("Warning", "signInWithEmailAndPassword:failure")
                            mAuth!!.createUserWithEmailAndPassword(eText, pText)
                                    .addOnCompleteListener(this) { task ->
                                        if (task.isSuccessful) {
                                            // Registration Completes
                                            Log.d("Info", "createUserWithEmail:success")
                                            val user: FirebaseUser? = mAuth!!.currentUser
                                            updateUI( user)
                                        } else {
                                            // Registration Errors
                                            Log.w("Warning", "createUserWithEmail:failure")
                                            Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                        }
                    }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val RC_SIGN_IN = 9001
        if (requestCode == RC_SIGN_IN) {
            try {
                val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
                val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                Log.w("Warning", "Google sign in failed", e)
                Toast.makeText(this, "Login Failed: onActivityResult", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d("Debug", "firebaseAuthWithGoogle:" + acct.getId())
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        mAuth!!.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("Debug", "signInWithCredential:success")
                        val user = mAuth!!.currentUser
                        updateUI(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("Warning", "signInWithCredential:failure", task.exception)
                        updateUI(null)
                        // Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show()
                        Toast.makeText(this, "Login Failed: firebaseAuthWithGoogle", Toast.LENGTH_SHORT).show()
                    }
                }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        signInClient = GoogleSignIn.getClient(this, gso)

        findViewById<SignInButton>(R.id.googleSignIn).setOnClickListener {view: View -> signInWithGoogle()}
        findViewById<Button>(R.id.emailSignIn).setOnClickListener {view: View -> signInWithEmail() }
    }

    override fun onStart() {
        super.onStart()
        val currentUser: FirebaseUser? = mAuth!!.currentUser
        updateUI(currentUser)
    }
}