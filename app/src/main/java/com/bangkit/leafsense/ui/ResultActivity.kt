package com.bangkit.leafsense.ui

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.leafsense.data.api.ApiConfig
import com.bangkit.leafsense.data.response.PredictResponse
import com.bangkit.leafsense.databinding.ActivityResultBinding
import com.bangkit.leafsense.uriToFile
import com.bangkit.leafsense.reduceFileImage
import com.bumptech.glide.Glide
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Mendapatkan URI gambar dari Intent
        val imageUriString = intent.getStringExtra(EXTRA_IMAGE_URI)
        if (imageUriString != null) {
            val imageUri = Uri.parse(imageUriString)

            Glide.with(this)
                .load(imageUri)
                .into(binding.imageView)

            val file = uriToFile(imageUri, this)
            setupAnalyzeButton(file)
        }

        hidePredictionViews()
    }

    private fun hidePredictionViews() {
        binding.predictionResult.visibility = View.GONE
        binding.predictionDetails.visibility = View.GONE
        binding.cureTextView.visibility = View.GONE
        binding.preventionTextView.visibility = View.GONE
    }

    private fun showPredictionViews() {
        binding.predictionResult.visibility = View.VISIBLE
        binding.predictionDetails.visibility = View.VISIBLE
        binding.cureTextView.visibility = View.VISIBLE
        binding.preventionTextView.visibility = View.VISIBLE
    }

    private fun setupAnalyzeButton(file: File) {
        binding.analyzeButton.setOnClickListener {
            binding.analyzeButton.visibility = View.GONE
            predictDisease(file)
        }
    }

    private fun predictDisease(file: File) {
        // Periksa apakah file berformat JPEG
        if (!file.name.lowercase().endsWith(".jpg") && !file.name.lowercase().endsWith(".jpeg")) {
            Toast.makeText(this, "File harus dalam format JPEG", Toast.LENGTH_SHORT).show()
            binding.analyzeButton.visibility = View.VISIBLE
            return
        }
        // Kompres file jika ukurannya terlalu besar
        val reducedFile = file.reduceFileImage()

        val fileBody = reducedFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imagePart = MultipartBody.Part.createFormData("image", reducedFile.name, fileBody)

        val apiService = ApiConfig.getClassificationApiService()
        val call = apiService.predictLeafDisease(imagePart)

        call.enqueue(object : Callback<PredictResponse> {
            override fun onResponse(call: Call<PredictResponse>, response: Response<PredictResponse>) {
                if (response.isSuccessful) {
                    val data = response.body()?.data
                    if (data != null) {
                        binding.predictionResult.text = if (data.probability != null) {
                            val probabilityPercentage = (data.probability as? Double ?: data.probability.toString().toDouble()) * 100
                            "Result: ${data.result} (${String.format("%.2f", probabilityPercentage)}%)"
                        } else {
                            "Result: ${data.result} (Probability not available)"
                        }
                        binding.predictionDetails.text = "Description: ${data.description}"
                        binding.cureTextView.text = "Cure: ${data.cure}"
                        binding.preventionTextView.text = "Prevention: ${data.prevention}"
                        showPredictionViews()
                    } else {
                        Toast.makeText(this@ResultActivity, "Failed to get prediction", Toast.LENGTH_SHORT).show()
                        binding.analyzeButton.visibility = View.VISIBLE
                    }
                } else {
                    Toast.makeText(this@ResultActivity, "Failed: ${response.message()}", Toast.LENGTH_SHORT).show()
                    binding.analyzeButton.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: Call<PredictResponse>, t: Throwable) {
                Toast.makeText(this@ResultActivity, "Network Error: ${t.message}", Toast.LENGTH_SHORT).show()
                binding.analyzeButton.visibility = View.VISIBLE
            }
        })
    }

    companion object {
        const val EXTRA_IMAGE_URI = "extra_image_uri"
    }
}
