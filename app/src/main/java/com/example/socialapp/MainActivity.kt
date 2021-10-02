package com.example.socialapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.socialapp.daos.PostDao
import com.example.socialapp.databinding.ActivityMainBinding
import com.example.socialapp.models.Post
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity(), IPostAdapter {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter:PostAdapter
    private lateinit var postDao: PostDao
    private lateinit var bottomSheetDialog : BottomSheetDialog

    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.setting.setOnClickListener {
            bottomSheetDialog = BottomSheetDialog(this,R.style.BottomSheetDialogTheme)
            val view = layoutInflater.inflate(R.layout.logout_dialog, null)

            val btnClose = view.findViewById<ImageView>(R.id.closeButton)
            btnClose.setOnClickListener {
                bottomSheetDialog.dismiss()
            }

            val signout = view.findViewById<Button>(R.id.signOutButton)
            signout.setOnClickListener{
                val auth = com.google.firebase.ktx.Firebase.auth
                auth.signOut()
                val signInIntent=
                    android.content.Intent(this, com.example.socialapp.SignInActivity::class.java)
                startActivity(signInIntent)
                finish()
            }

            bottomSheetDialog.setContentView(view)
            bottomSheetDialog.show()
        }

        binding.fab.setOnClickListener {
            val intent = Intent(this,CreatePostActivity::class.java)
            startActivity(intent)
        }
        SetUpRecyclerView()
    }


    private fun SetUpRecyclerView() {
        postDao= PostDao()
        val postCollections=postDao.postCollection
        val query=postCollections.orderBy("createdAt", Query.Direction.DESCENDING)
        val recyclerViewOption=FirestoreRecyclerOptions.Builder<Post>().setQuery(query,Post::class.java).build()
        adapter= PostAdapter(recyclerViewOption,this)

        binding.recyclerView.adapter=adapter
        binding.recyclerView.layoutManager=LinearLayoutManager(this)
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    override fun onLikeClicked(postId: String) {
      postDao.updateLikes(postId)
    }


}