package com.bangkit.leafsense.ui.result

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
    private var retryCount = 0
    private val maxRetry = 3 // Batas maksimal percobaan ulang

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageUriString = intent.getStringExtra(EXTRA_IMAGE_URI)
        if (imageUriString != null) {
            val imageUri = Uri.parse(imageUriString)

            Glide.with(this)
                .load(imageUri)
                .into(binding.imageView)

            val file = uriToFile(imageUri, this)
            predictDisease(file)
        }

        hidePredictionViews()
    }

    private fun hidePredictionViews() {
        binding.predictionResult.visibility = View.GONE
        binding.cardPredictionDetails.visibility = View.GONE
        binding.cardCure.visibility = View.GONE
        binding.cardPrevention.visibility = View.GONE
    }

    private fun predictDisease(file: File) {
        if (!file.name.lowercase().endsWith(".jpg") && !file.name.lowercase().endsWith(".jpeg")) {
            Toast.makeText(this, "File harus dalam format JPEG", Toast.LENGTH_SHORT).show()
            return
        }

        showLoading()

        val reducedFile = file.reduceFileImage()

        val fileBody = reducedFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imagePart = MultipartBody.Part.createFormData("image", reducedFile.name, fileBody)

        val apiService = ApiConfig.getClassificationApiService()
        val call = apiService.predictLeafDisease(imagePart)

        call.enqueue(object : Callback<PredictResponse> {
            override fun onResponse(call: Call<PredictResponse>, response: Response<PredictResponse>) {
                hideLoading()

                if (response.isSuccessful) {
                    val data = response.body()?.data
                    if (data != null) {
                        binding.predictionResult.text = if (data.probability != null) {
                            val probabilityPercentage = (data.probability as? Double
                                ?: data.probability.toString().toDouble()) * 100
                            "Hasil: ${data.result} (${String.format("%.2f", probabilityPercentage)}%)"
                        } else {
                            "Result: ${data.result} (Probability not available)"
                        }
                        binding.predictionDetails.text = "${data.description}"
                        binding.cureTextView.text = "${data.cure}"
                        binding.preventionTextView.text = "${data.prevention}"
                        showPredictionViews()
                        retryCount = 0 // Reset retry count on success
                    } else {
                        handleRetry(file, "Failed to get prediction.")
                    }
                } else {
                    handleRetry(file, "Prediksi gagal, silakan coba lagi: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<PredictResponse>, t: Throwable) {
                handleRetry(file, "Network Error: ${t.message}")
                hideLoading()

            }
        })
    }

    private fun handleRetry(file: File, errorMessage: String) {
        if (retryCount < maxRetry) {
            retryCount++
            Toast.makeText(this, "$errorMessage. Mengulang (${retryCount}/${maxRetry})", Toast.LENGTH_SHORT).show()
            predictDisease(file) // Coba lagi
        } else {
            Toast.makeText(this, "Gagal setelah $maxRetry percobaan. Harap coba lagi nanti.", Toast.LENGTH_LONG).show()
        }
    }

    private fun showPredictionViews() {
        val viewsToAnimate = listOf(
            binding.predictionResult,
            binding.cardPredictionDetails,
            binding.cardCure,
            binding.cardPrevention
        )

        for ((index, view) in viewsToAnimate.withIndex()) {
            view.visibility = View.VISIBLE
            view.alpha = 0f
            view.animate()
                .alpha(1f)
                .setStartDelay((index * 200).toLong())
                .setDuration(500)
                .start()
        }
    }

    private fun showLoading() {
        binding.loadingAnimation.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        binding.loadingAnimation.visibility = View.GONE
    }

    companion object {
        const val EXTRA_IMAGE_URI = "extra_image_uri"
    }
}
