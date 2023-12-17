package com.example.chatequipemaroc.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatequipemaroc.R
import com.example.chatequipemaroc.adapters.ChatRecyclerAdapter
import com.example.chatequipemaroc.models.Friend
import com.example.chatequipemaroc.models.Message
import com.example.chatequipemaroc.models.User
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase

class ChatActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var currentUser: FirebaseUser?=null

    lateinit var fabSendMessage : FloatingActionButton
    lateinit var editMessage : EditText
    lateinit var rvChatList : RecyclerView
    lateinit var chatRecyclerAdapter : ChatRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        auth= Firebase.auth
        db= Firebase.firestore
        currentUser=auth.currentUser

        rvChatList= findViewById(R.id.rvChatList)
        editMessage = findViewById(R.id.editMessage)
        fabSendMessage = findViewById(R.id.fabSendMessage)

        val userUuid = intent.getStringExtra("friend")!!

        db.collection("users")
            .document(userUuid)
            .get()
            .addOnSuccessListener {result->
                if (result!=null){
                    var user = result.toObject(User::class.java)
                    user?.let {
                        user.uuid=userUuid
                        setUserData(user)
                    }
                }
            }.addOnFailureListener {
                Log.e("ChatActivity","error getting user",it)
            }

    }

    private fun setUserData(user: User) {
        supportActionBar?.title = user.fullname ?: "ChatEquipeMaroc"

        chatRecyclerAdapter = ChatRecyclerAdapter()

        //Message(sender = "Achraf", receiver = "Yassine", text = "Merhaba", timestamp = 123456789, isReceived = false)
        val messages = mutableListOf<Message>()
        rvChatList.apply {
            layoutManager = LinearLayoutManager(this@ChatActivity)
            adapter = chatRecyclerAdapter
        }

        fabSendMessage.setOnClickListener {
            //envoyer le message

            val message = editMessage.text.toString()
            if(message.isNotEmpty()){
                val message =Message(
                    sender = currentUser!!.uid,
                    receiver = user.uuid,
                    text = message,
                    timestamp = System.currentTimeMillis(),
                    isReceived = false
                )
                editMessage.setText("")
                //hide keyboard
                val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(editMessage.windowToken, 0)

                db.collection("messages")
                    .add(message)
                    .addOnSuccessListener {
                        rvChatList.scrollToPosition(messages.size-1)
                    }.addOnFailureListener {
                        Log.e("ChatActivity","error adding message",it)
                    }

                val friend = Friend(
                    "",
                    user.fullname,
                    message.text,
                    timestamp = System.currentTimeMillis(),
                    image = user.image?: ""
                )
                db.collection("users")
                    .document(currentUser!!.uid)
                    .collection("friends")
                    .document(user.uuid)
                    .set(friend)
                    .addOnSuccessListener {
                        Log.e("ChatActivity","friend added")
                    }.addOnFailureListener {
                        Log.e("ChatActivity","error adding friend",it)
                    }
            }
        }

        val sentQuery = db.collection("messages")
            .whereEqualTo("sender",currentUser!!.uid)
            .whereEqualTo("receiver",user.uuid)
            .orderBy("timestamp",Query.Direction.ASCENDING)

        val receivedQuery = db.collection("messages")
            .whereEqualTo("sender",user.uuid)
            .whereEqualTo("receiver",currentUser!!.uid)
            .orderBy("timestamp",Query.Direction.ASCENDING)

        sentQuery.addSnapshotListener { snapshot, exception ->
            if(exception!=null){
                Log.e("ChatActivity","error getting messages",exception)
                return@addSnapshotListener
            }

            for(document in snapshot!!.documents){
                var message = document.toObject(Message::class.java)
                message?.let {
                    Log.d("ChatActivity sent",message.toString())
                    message.isReceived=false
                    if(!messages.contains(message)){
                        messages.add(message)
                    }
                }
            }
            if(messages.isNotEmpty()){
                chatRecyclerAdapter.items=messages.sortedBy { it.timestamp } as MutableList<Message>
                rvChatList.scrollToPosition(messages.size-1)
            }
        }

        receivedQuery.addSnapshotListener { snapshot, exception ->
            if(exception!=null){
                Log.e("ChatActivity","error getting messages",exception)
                return@addSnapshotListener
            }

            for(document in snapshot!!.documents){
                var message = document.toObject(Message::class.java)
                message?.let {
                    Log.d("ChatActivity received",message.toString())
                    message.isReceived=true
                    if(!messages.contains(message)){
                        messages.add(message)
                    }
                }
            }
            if(messages.isNotEmpty()){
                chatRecyclerAdapter.items=messages.sortedBy { it.timestamp } as MutableList<Message>
                rvChatList.scrollToPosition(messages.size-1)
            }
        }
    }
}