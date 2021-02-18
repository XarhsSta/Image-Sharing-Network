package com.xarhssta.imagesharingnetwork

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase

class FeedActivity : AppCompatActivity() {

    val auth = FirebaseAuth.getInstance()
    var feedListView: ListView? = null
    var emails : ArrayList<String> = ArrayList()
    var snaps : ArrayList<DataSnapshot> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Your Feed"
        setContentView(R.layout.activity_feed)

        feedListView = findViewById(R.id.feedListView)
        val arrayAdapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,emails)
        feedListView?.adapter = arrayAdapter

        val database = FirebaseDatabase.getInstance("https://image-sharing-network-default-rtdb.europe-west1.firebasedatabase.app/")
        val myRef = database.reference
        myRef.child("users").child(auth.currentUser!!.uid).child("snaps").addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                emails.add(snapshot.child("from").value.toString())
                snaps.add(snapshot)
                arrayAdapter.notifyDataSetChanged()
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {
                var index = 0
                for (snap : DataSnapshot in snaps) {
                    if(snap.key == snapshot.key){
                        snaps.removeAt(index)
                        emails.removeAt(index)
                        arrayAdapter.notifyDataSetChanged()
                    }
                    index ++
                }
            }
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}

        })

        feedListView?.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val snapshot = snaps[position]
            var intent = Intent(this, ViewImageActivity::class.java)

            intent.putExtra("imageName", snapshot.child("imageName").value as String)
            intent.putExtra("imageURL", snapshot.child("imageURL").value as String)
            intent.putExtra("message", snapshot.child("message").value as String)
            intent.putExtra("imageKey", snapshot.key)

            startActivity(intent)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.feed,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item?.itemId == R.id.sendImage) {
            val intent = Intent(this , SendImageActivity::class.java)
            startActivity(intent)
        } else if (item?.itemId == R.id.logOut) {
            auth.signOut()
            finish()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        auth.signOut()
    }
}