package com.example.imageuploader

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import com.example.imageuploader.databinding.ActivityMainBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.net.URI

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var storageRef:StorageReference
    private lateinit var firebaseFirestore: FirebaseFirestore
    private var imageURI:Uri?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        binding=ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initVars()
        registerClicks()
    }
    private fun initVars() {
        storageRef = FirebaseStorage.getInstance().reference.child("Images")
        firebaseFirestore=FirebaseFirestore.getInstance()
    }

    private fun registerClicks() {
        binding.uploadBtn.setOnClickListener {
            uploadImage()
        }
        binding.showAllBtn.setOnClickListener {
            startActivity(Intent(this,ImagesActivity::class.java))
        }
        binding.imageView.setOnClickListener {
            resultLauncher.launch("image/*")
        }
    }
    private val resultLauncher=registerForActivityResult(
        ActivityResultContracts.GetContent()){
        imageURI=it
        binding.imageView.setImageURI(imageURI)
    }

    private fun uploadImage() {
        binding.progressBar.visibility=View.VISIBLE
        storageRef=storageRef.child(System.currentTimeMillis().toString())
        imageURI?.let {
            storageRef.putFile(it).addOnCompleteListener {task->
                if(task.isSuccessful){
                    //here we have successfully uploaded the image with timestamp to cloud firebase storage
                    //now we have to get the URL for that image and add that to cloud firebase firestore

                    storageRef.downloadUrl.addOnSuccessListener {uri->
                        val map=HashMap<String,Any>()
                        map["pic"]=uri.toString()

                        firebaseFirestore.collection("images").add(map).addOnCompleteListener {firestoreTask->
                            if(firestoreTask.isSuccessful){
                                Toast.makeText(this,"Image Uploaded Successfully",Toast.LENGTH_SHORT).show()
                            }
                            else{
                                Toast.makeText(this,firestoreTask.exception?.message,Toast.LENGTH_SHORT).show()
                            }
                            binding.progressBar.visibility=View.GONE
                            binding.imageView.setImageResource(R.drawable.vector)
                        }
                    }
                }
                else{
                    Toast.makeText(this,task.exception?.message,Toast.LENGTH_SHORT).show()
                    binding.progressBar.visibility=View.GONE
                    binding.imageView.setImageResource(R.drawable.vector)
                }
            }
        }
    }
}