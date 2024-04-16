package com.example.imageuploader

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.imageuploader.databinding.ActivityImagesBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.net.URI

class ImagesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityImagesBinding
    private lateinit var firebaseFirestore: FirebaseFirestore
    private var imageURI:URI?=null
    private var mList= mutableListOf<String>()
    private lateinit var adapter: ImagesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding=ActivityImagesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initVars()
        getImages()
    }

    private fun initVars() {
        firebaseFirestore=FirebaseFirestore.getInstance()
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager=LinearLayoutManager(this)
        adapter= ImagesAdapter(mList)
        binding.recyclerView.adapter=adapter

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getImages() {
        firebaseFirestore.collection("images").get().addOnSuccessListener {
            for (i in it) {
                mList.add(i.data["pic"].toString())
            }
            adapter.notifyDataSetChanged()
        }
    }
}