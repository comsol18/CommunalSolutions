package comcomsol.wixsite.communalsolutions1.communalsolutions

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.auth.api.signin.*
import com.google.firebase.auth.*
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.GoogleAuthProvider
import comcomsol.wixsite.communalsolutions1.communalsolutions.HelperFiles.*
import kotlinx.android.synthetic.main.activity_main.*
import comcomsol.wixsite.communalsolutions1.communalsolutions.Managers.*


class MainActivity : AppCompatActivity() {

    // Class Variables
    private val TAG = "MainActivity"
    private val auth: AuthManager = AuthManager()
    private var signInClient: GoogleSignInClient? = null
    private val RC_SIGN_IN = 9001

    // updateUI function
    private fun updateUI(user: FirebaseUser?) {
        val homeIntent = Intent(this, HomeActivity::class.java)
        if (user != null) {
            startActivity(homeIntent)
        }
    }

    // Google Sign In Code
    fun signInWithGoogle() {
        val signInIntent: Intent = signInClient!!.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            try {
                val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
                val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                eLog(TAG, e.toString())
                Log.w("Warning", "Google sign in failed", e)
                Toast.makeText(this, "Login Failed: onActivityResult", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d("Debug", "firebaseAuthWithGoogle:" + acct.getId())
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.mAuth!!.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("Debug", "signInWithCredential:success")
                        val user = auth.mAuth!!.currentUser
                        updateUI(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        eLog(TAG, task.exception.toString())
                        Toast.makeText(this, "Login Failed: firebaseAuthWithGoogle", Toast.LENGTH_SHORT).show()
                    }
                }
    }
    // End of Google Sign In Code

    // Email Sign In Code
    private fun signInWithEmail() {
        val email: EditText = findViewById(R.id.email)
        val pass: EditText = findViewById(R.id.password)
        val eText = email.text.toString()
        val pText = pass.text.toString()

        if (auth.checkCreds(email, pass)) {
            auth.mAuth!!.signInWithEmailAndPassword(eText, pText)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Registration Completes
                            dLog("Info", "signInWithEmailAndPassword:success")
                            //Toast.makeText(this, "Login Success", Toast.LENGTH_SHORT).show()
                            val user: FirebaseUser? = auth.mAuth!!.currentUser
                            updateUI(user)
                        } else {
                            // Registration Errors
                            Log.w("Warning", "signInWithEmailAndPassword:failure")
                            auth.mAuth!!.createUserWithEmailAndPassword(eText, pText)
                                    .addOnCompleteListener(this) { task ->
                                        if (task.isSuccessful) {
                                            // Registration Completes
                                            dLog("Info", "createUserWithEmail:success")
                                            val user: FirebaseUser? = auth.mAuth!!.currentUser
                                            updateUI(user)
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
    // End of Email Sign In Code

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth.mAuth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        signInClient = GoogleSignIn.getClient(this, gso)
        googleSignIn.setOnClickListener {view: View -> signInWithGoogle() }
        emailSignIn.setOnClickListener {view: View -> signInWithEmail() }
    }

    override fun onStart() {
        super.onStart()
        val currentUser: FirebaseUser? = auth.mAuth!!.currentUser
        updateUI(currentUser)
    }
}
