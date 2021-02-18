@file:Suppress("DEPRECATION")

package com.xarhssta.imagesharingnetwork

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.lang.Exception
import java.util.*

class SendImageActivity : AppCompatActivity() {

    var sendImageView : ImageView? = null
    var messageEditText : EditText? = null
    val imageName = UUID.randomUUID().toString() + ".jpg"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_image)

        title = "Send Image"

        sendImageView = findViewById(R.id.imageView)
        messageEditText = findViewById(R.id.messageTextView)
    }

    fun getPhoto() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 1)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == 1) {
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getPhoto()
            }
        }
    }

    fun chooseImageClicked (view : View) {
        if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),1)
        } else {
            getPhoto()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val selectedImage = data!!.data

        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImage)
                sendImageView?.setImageBitmap(bitmap)
            } catch (e : Exception) {
                e.printStackTrace()
            }
        }
    }

    fun sendClicked (view : View) {
        sendImageView?.isDrawingCacheEnabled = true
        sendImageView?.buildDrawingCache()
        val bitmap = sendImageView?.drawingCache
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val data = byteArrayOutputStream.toByteArray()

        val uploadTask = FirebaseStorage.getInstance()
            .reference.child("images").child(imageName).putBytes(data)
        uploadTask.addOnFailureListener {
            Toast.makeText(this, "Upload Failed!", Toast.LENGTH_SHORT).show()
        }.addOnSuccessListener { sendImageTask ->
            var downloadUrl = sendImageTask.storage.downloadUrl.addOnCompleteListener() { task ->
                var url = task.result
                Log.i("Url", url.toString())

                val intent = Intent(this, ChooseRecipientActivity::class.java)
                intent.putExtra("imageURL", url.toString())
                intent.putExtra("imageName", imageName)
                intent.putExtra("message", messageEditText?.text.toString())
                startActivity(intent)
            }
        }
    }

}