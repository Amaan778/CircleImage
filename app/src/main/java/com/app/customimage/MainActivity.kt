package com.app.customimage

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import java.io.File
import com.yalantis.ucrop.UCrop

class MainActivity : AppCompatActivity() {
    private lateinit var imageViewProfile: ImageView
    private lateinit var buttonSelectImage: Button

    companion object {
        private const val REQUEST_IMAGE_PICK = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        imageViewProfile = findViewById(R.id.imageViewProfile)
        buttonSelectImage = findViewById(R.id.buttonSelectImage)

        // Set button click listener
        buttonSelectImage.setOnClickListener {
            pickImageFromGallery()
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK && data != null) {
            val sourceUri = data.data ?: return

            // Define destination URI for the cropped image
            val destinationUri = Uri.fromFile(File(cacheDir, "cropped_image.jpg"))

            // uCrop options to set a circular crop outline
            val options = UCrop.Options().apply {
                setCircleDimmedLayer(true)    // Circular outline
                setShowCropGrid(false)        // Hide the grid
                setShowCropFrame(false)       // Hide the crop frame
            }

            // Start uCrop with defined source and destination URIs
            UCrop.of(sourceUri, destinationUri)
                .withAspectRatio(1f, 1f)     // Square aspect ratio for circular crop
                .withOptions(options)
                .start(this)

        } else if (requestCode == UCrop.REQUEST_CROP && resultCode == Activity.RESULT_OK) {
            val resultUri = UCrop.getOutput(data!!) ?: return

            // Load the cropped image into the ImageView using Glide
            Glide.with(this)
                .load(resultUri)
                .circleCrop() // Optional, for rounded display if ImageView is not circular
                .into(imageViewProfile)
        }
    }
}