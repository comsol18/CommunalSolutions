package com.communal_solutions.www.communalsolutions

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.content.pm.PackageManager
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.app.LoaderManager.LoaderCallbacks
import android.content.CursorLoader
import android.content.Loader
import android.database.Cursor
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.EditorInfo

import java.util.ArrayList
import android.Manifest.permission.READ_CONTACTS
import android.content.Intent
import android.util.Log
import android.widget.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*

import kotlinx.android.synthetic.main.activity_main.*

/*
class MainActivity : AppCompatActivity() {

    private var eLogin: EmailLogin? = null
    private var mAuth: FirebaseAuth? = null
    private var gSignInClient: GoogleSignInClient? = null
    private var homeIntent: Intent = Intent(this, HomeActivity::class.java)
    private val RC_SIGN_IN = 9001

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            startActivity(homeIntent)
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        Log.d("D", "firebaseAuthWithGoogle:" + account.getId())

        val credential: AuthCredential = GoogleAuthProvider.getCredential(account.getIdToken(), null)
        mAuth!!.signInWithCredential(credential)
                .addOnCompleteListener(this, OnCompleteListener<AuthResult>() { task ->
                    val user: FirebaseUser?
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("D", "signInWithCredential:success")
                        user = mAuth!!.currentUser
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("W", "signInWithCredential:failure", task.getException())
                        user = null
                        // Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                    }
                    updateUI(user)
                })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("W", "Google sign in failed", e)
            }
        }
    }

    fun signInWithEmail(view: View) {
        if (eLogin != null) {
            eLogin!!.signIn(mAuth!!)
            val user: FirebaseUser? = mAuth!!.currentUser
            updateUI(user)
        } else {
            Toast.makeText(this, "Error: Cannot Login with Email.", Toast.LENGTH_SHORT).show()
        }
    }

    fun signInWithGoogle(view: View) {
        val signInIntent = gSignInClient!!.getSignInIntent()
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mAuth = FirebaseAuth.getInstance()

        // Email/Password Module
        eLogin = EmailLogin()

        // Google Sign-In Module
        val gso: GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        gSignInClient = GoogleSignIn.getClient(this, gso)
    }

    override fun onStart() {
        super.onStart()
        // Get Status of Logged in user if one exists
        val currentUser: FirebaseUser? =
                if (mAuth != null) mAuth!!.currentUser
                else null
        val googleAccount = GoogleSignIn.getLastSignedInAccount(this)

        // Goto home activity if already signed in
        if (currentUser != null || googleAccount != null) startActivity(homeIntent)
    }
}

*/

/**
 * A login screen that offers login via email/password.
 */
class MainActivity : AppCompatActivity(), LoaderCallbacks<Cursor>, View.OnClickListener {
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private var mGoogleSignInClient: GoogleSignInClient? = null
    private var mAuthTask: UserLoginTask? = null
    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Set up the login form.
        populateAutoComplete()
        password.setOnEditorActionListener(TextView.OnEditorActionListener { _, id, _ ->
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptLogin()
                return@OnEditorActionListener true
            }
            false
        })

        email_sign_in_button.setOnClickListener { attemptLogin() }

        // Button listeners
        findViewById<View>(R.id.sign_in_button).setOnClickListener(this)
        //findViewById<View>(R.id.sign_out_button).setOnClickListener(this)
        //findViewById<View>(R.id.disconnect_button).setOnClickListener(this)

        // [START configure_signin]
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        // [END configure_signin]

        // [START build_client]
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        // [END build_client]

        // [START customize_button]
        // Set the dimensions of the sign-in button.
        val signInButton = findViewById<SignInButton>(R.id.sign_in_button)
        signInButton.setSize(SignInButton.SIZE_STANDARD)
        signInButton.setColorScheme(SignInButton.COLOR_LIGHT)
        // [END customize_button]

        mAuth = FirebaseAuth.getInstance()
    }

    public override fun onStart() {
        super.onStart()

        // [START on_start_sign_in]
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        //val account = GoogleSignIn.getLastSignedInAccount(this)
        //updateUI(account)
        val currentUser: FirebaseUser? = mAuth!!.getCurrentUser()
        updateUI(currentUser)
        // [END on_start_sign_in]
    }

    // [START onActivityResult]
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }
    // [END onActivityResult]

    private fun signInWithEmail() {
        val email: AutoCompleteTextView = findViewById(R.id.email)
        val password: EditText = findViewById(R.id.password)
        mAuth!!.createUserWithEmailAndPassword(email.text.toString(), password.text.toString())
        .addOnCompleteListener(this, OnCompleteListener<AuthResult>() {
            @Override
            fun onComplete(task: Task<AuthResult> ) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    Toast.makeText(this, "Login Success", Toast.LENGTH_SHORT).show()
                    var user: FirebaseUser? = mAuth!!.getCurrentUser()
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                    Toast.makeText(this, "Login Failure", Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
        })
    }

    // [START handleSignInResult]
    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult<ApiException>(ApiException::class.java)

            // Signed in successfully, show authenticated UI.
            //updateUI(account)
            firebaseAuthWithGoogle(account)
            val homeIntent: Intent = Intent(this, HomeActivity::class.java)
            //startActivity(homeIntent)
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.statusCode)
            //updateUI(null)
        }

    }
    // [END handleSignInResult]

    private fun firebaseAuthWithGoogle( acct: GoogleSignInAccount ) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId())

        val credential: AuthCredential = GoogleAuthProvider.getCredential(acct.getIdToken(), null)
        mAuth!!.signInWithCredential(credential)
                .addOnCompleteListener(this, OnCompleteListener<AuthResult>() {
                    @Override
                    fun onComplete(task: Task<AuthResult> ) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success")
                            val user: FirebaseUser? = mAuth!!.getCurrentUser()
                            updateUI(user)
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException())
                            //Snackbar.make((R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            updateUI(null)
                        }
                    }
                })
    }

    fun userLoggedIn(view: View) {
        if (FirebaseAuth.getInstance().currentUser == null)
            Toast.makeText(this, "No User Signed In", Toast.LENGTH_SHORT).show()
        else {
            Toast.makeText(this, "User Signed In: Signing Out", Toast.LENGTH_SHORT).show()
            FirebaseAuth.getInstance().signOut()
        }
    }

    // [START signIn]
    private fun signIn() {
        val signInIntent: Intent = mGoogleSignInClient!!.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
    // [END signIn]

    // [START signOut]
    private fun signOut() {
        mGoogleSignInClient!!.signOut()
                .addOnCompleteListener(this) {
                    // [START_EXCLUDE]
                    updateUI(null)
                    // [END_EXCLUDE]
                }
    }
    // [END signOut]

    // [START revokeAccess]
    private fun revokeAccess() {
        mGoogleSignInClient!!.revokeAccess()
                .addOnCompleteListener(this) {
                    // [START_EXCLUDE]
                    updateUI(null)
                    // [END_EXCLUDE]
                }
    }
    // [END revokeAccess]

    private fun updateUI(account: FirebaseUser?) {
        if (account != null) {
            //findViewById<View>(R.id.sign_in_button).visibility = View.GONE
            //findViewById<View>(R.id.sign_out_and_disconnect).visibility = View.VISIBLE

        } else {
            //findViewById<View>(R.id.sign_in_button).visibility = View.VISIBLE
            //findViewById<View>(R.id.sign_out_and_disconnect).visibility = View.GONE
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.sign_in_button -> signIn()
            //R.id.sign_out_button -> signOut()
            //R.id.disconnect_button -> revokeAccess()
        }
    }


    private fun populateAutoComplete() {
        if (!mayRequestContacts()) {
            return
        }

        loaderManager.initLoader(0, null, this)
    }

    private fun mayRequestContacts(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(email, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok,
                            { requestPermissions(arrayOf(READ_CONTACTS), REQUEST_READ_CONTACTS) })
        } else {
            requestPermissions(arrayOf(READ_CONTACTS), REQUEST_READ_CONTACTS)
        }
        return false
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete()
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private fun attemptLogin() {
        if (mAuthTask != null) {
            return
        }

        // Reset errors.
        email.error = null
        password.error = null

        // Store values at the time of the login attempt.
        val emailStr = email.text.toString()
        val passwordStr = password.text.toString()

        var cancel = false
        var focusView: View? = null

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(passwordStr) && !isPasswordValid(passwordStr)) {
            password.error = getString(R.string.error_invalid_password)
            focusView = password
            cancel = true
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(emailStr)) {
            email.error = getString(R.string.error_field_required)
            focusView = email
            cancel = true
        } else if (!isEmailValid(emailStr)) {
            email.error = getString(R.string.error_invalid_email)
            focusView = email
            cancel = true
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView?.requestFocus()
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true)
            mAuthTask = UserLoginTask(emailStr, passwordStr)
            mAuthTask!!.execute(null as Void?)
            signInWithEmail()
            val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                val homeIntent: Intent = Intent(this, HomeActivity::class.java)
                startActivity(homeIntent)
            } else {
                Toast.makeText(this, "Failure to login", Toast.LENGTH_SHORT).show()
                //focusView?.requestFocus()
            }
        }
    }

    private fun isEmailValid(email: String): Boolean {
        //TODO: Replace this with your own logic
        return email.contains("@")
    }

    private fun isPasswordValid(password: String): Boolean {
        //TODO: Replace this with your own logic
        return password.length > 6
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private fun showProgress(show: Boolean) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

        login_form.visibility = if (show) View.GONE else View.VISIBLE
        login_form.animate()
                .setDuration(shortAnimTime)
                .alpha((if (show) 0 else 1).toFloat())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        login_form.visibility = if (show) View.GONE else View.VISIBLE
                    }
                })

        login_progress.visibility = if (show) View.VISIBLE else View.GONE
        login_progress.animate()
                .setDuration(shortAnimTime)
                .alpha((if (show) 1 else 0).toFloat())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        login_progress.visibility = if (show) View.VISIBLE else View.GONE
                    }
                })
    }

    override fun onCreateLoader(i: Int, bundle: Bundle?): Loader<Cursor> {
        return CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE + " = ?", arrayOf(ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE),

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC")
    }

    override fun onLoadFinished(cursorLoader: Loader<Cursor>, cursor: Cursor) {
        val emails = ArrayList<String>()
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS))
            cursor.moveToNext()
        }

        addEmailsToAutoComplete(emails)
    }

    override fun onLoaderReset(cursorLoader: Loader<Cursor>) {

    }

    private fun addEmailsToAutoComplete(emailAddressCollection: List<String>) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        val adapter = ArrayAdapter(this@MainActivity,
                android.R.layout.simple_dropdown_item_1line, emailAddressCollection)

        email.setAdapter(adapter)
    }

    object ProfileQuery {
        val PROJECTION = arrayOf(
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY)
        val ADDRESS = 0
        val IS_PRIMARY = 1
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    inner class UserLoginTask internal constructor(private val mEmail: String, private val mPassword: String) : AsyncTask<Void, Void, Boolean>() {

        override fun doInBackground(vararg params: Void): Boolean? {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000)
            } catch (e: InterruptedException) {
                return false
            }

            return DUMMY_CREDENTIALS
                    .map { it.split(":") }
                    .firstOrNull { it[0] == mEmail }
                    ?.let {
                        // Account exists, return true if the password matches.
                        it[1] == mPassword
                    }
                    ?: true
        }

        override fun onPostExecute(success: Boolean?) {
            mAuthTask = null
            showProgress(false)

            if (success!!) {
                finish()
            } else {
                password.error = getString(R.string.error_incorrect_password)
                password.requestFocus()
            }
        }

        override fun onCancelled() {
            mAuthTask = null
            showProgress(false)
        }
    }

    companion object {

        /**
         * Id to identity READ_CONTACTS permission request.
         */
        private val REQUEST_READ_CONTACTS = 0

        /**
         * A dummy authentication store containing known user names and passwords.
         * TODO: remove after connecting to a real authentication system.
         */
        private val DUMMY_CREDENTIALS = arrayOf("foo@example.com:hello", "bar@example.com:world")
        private val TAG = "SignInActivity"
        private val RC_SIGN_IN = 9001
    }
}
