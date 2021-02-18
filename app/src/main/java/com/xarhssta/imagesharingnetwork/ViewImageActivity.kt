@file:Suppress("DEPRECATION")

package com.xarhssta.imagesharingnetwork

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

class ViewImageActivity : AppCompatActivity() {
    val auth = FirebaseAuth.getInstance()
    var messageTextView : TextView? = null
    var snapImageView : ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_image)

        messageTextView = findViewById(R.id.viewMessageTextView)
        snapImageView = findViewById(R.id.snapImageView)

        messageTextView?.text = intent.getStringExtra("message")
        Log.i("URL",intent.getStringExtra("imageURL").toString())
        val task = ImageDownloader()
        val myImage: Bitmap
        try {
            myImage = task.execute(intent.getStringExtra("imageURL")).get()
            snapImageView?.setImageBitmap(myImage)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    inner class ImageDownloader : AsyncTask<String, Void, Bitmap>() {
        override fun doInBackground(vararg params: String?): Bitmap? {
            try {
                val url = URL(params[0])
                val connection = url.openConnection() as HttpURLConnection
                connection.connect()
                val inputStream = connection.inputStream
                return BitmapFactory.decodeStream(inputStream)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

        val database = FirebaseDatabase.getInstance("https://image-sharing-network-default-rtdb.europe-west1.firebasedatabase.app/")
        val myRef = database.reference
        myRef.child("users").child(auth.currentUser!!.uid).child("snaps").child(intent.getStringExtra("imageKey")!!).removeValue()

        val storage = FirebaseStorage.getInstance()
        storage.reference.child("images").child(intent.getStringExtra("imageName")!!).delete()
    }
}