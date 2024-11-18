package com.bangkit.leafsense.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.leafsense.R

class ResultActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_IMAGE_URI = "com.bangkit.leafsense.ui.EXTRA_IMAGE_URI"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val imageView: ImageView = findViewById(R.id.resultImageView)

        val imageUriString = intent.getStringExtra(EXTRA_IMAGE_URI)
        val imageUri = Uri.parse(imageUriString)

        imageView.setImageURI(imageUri)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@ResultActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        })

    }
}
