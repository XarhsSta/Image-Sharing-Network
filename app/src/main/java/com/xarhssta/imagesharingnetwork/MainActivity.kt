package com.xarhssta.imagesharingnetwork

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {

    var emailEditText : EditText? = null
    var passwordEditText : EditText? = null

    val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        emailEditText = findViewById(R.id.emailTextView)
        passwordEditText = findViewById(R.id.passwordEditText)

        if (auth.currentUser != null) {
            login()
        }
    }

    fun enterClicked (view : View) {

        // Check if the user can login
        auth.signInWithEmailAndPassword(emailEditText?.text.toString(), passwordEditText?.text.toString())
            .addOnCompleteListener(this) { loginTask ->
                if (loginTask.isSuccessful) {
                    Log.i("Sign in","Success")
                    login()
                } else {
                    // Else sign up the user
                    Log.i("Sign in","failure")
                    auth.createUserWithEmailAndPassword(emailEditText?.text.toString(),passwordEditText?.text.toString())
                        .addOnCompleteListener(this) { signUpTask ->
                            if(signUpTask.isSuccessful) {
                                Log.i("Sign Up","Success")
                                //Add to Database
                                val database = FirebaseDatabase.getInstance("https://image-sharing-network-default-rtdb.europe-west1.firebasedatabase.app/")
                                val myRef = database.reference
                                myRef.child("users").child(signUpTask.result?.user!!.uid).child("email").setValue(emailEditText?.text.toString())
                                login()
                            } else {
                                Log.i("Sign up","Failure")
                            }
                        }
                }
            }
    }

    fun login() {
        //Move to next activity
        val intent = Intent(this, FeedActivity::class.java)
        startActivity(intent)
    }
}
