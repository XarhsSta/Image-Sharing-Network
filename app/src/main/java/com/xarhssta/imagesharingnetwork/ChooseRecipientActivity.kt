package com.xarhssta.imagesharingnetwork

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase

class ChooseRecipientActivity : AppCompatActivity() {

    var chooseRecipientListView : ListView? = null
    var emails : ArrayList<String> = ArrayList()
    var keys : ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_recepient)

        title = "User List"

        chooseRecipientListView = findViewById(R.id.listView)
        val arrayAdapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,emails)
        chooseRecipientListView?.adapter = arrayAdapter

        val database = FirebaseDatabase.getInstance("https://image-sharing-network-default-rtdb.europe-west1.firebasedatabase.app/")
        val myRef = database.reference
        myRef.child("users").addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val email = snapshot.child("email").value as String
                emails.add(email)
                snapshot.key?.let { keys.add(it) }
                arrayAdapter.notifyDataSetChanged()
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}

        })

        chooseRecipientListView?.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val database = FirebaseDatabase.getInstance("https://image-sharing-network-default-rtdb.europe-west1.firebasedatabase.app/")
            val myRef = database.reference
            val recipient = myRef.child("users").child(keys[position])

            val imageMap : Map <String, String?> = mapOf("from" to FirebaseAuth.getInstance().currentUser!!.email!!,
            "imageName" to intent.getStringExtra("imageName"),
            "imageURL" to intent.getStringExtra("imageURL"),
            "message" to intent.getStringExtra("message"))


            recipient.child("snaps").push().setValue(imageMap)

            val intent = Intent (this, FeedActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
    }
}