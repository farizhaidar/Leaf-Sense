package com.bangkit.leafsense.ui.leaf

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.leafsense.data.api.ApiConfig
import com.bangkit.leafsense.data.response.ArticlesResponse
import com.bangkit.leafsense.databinding.ActivityTeaBinding
import com.bangkit.leafsense.ui.adapter.ArticlesAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TeaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTeaBinding
    private lateinit var articlesAdapter: ArticlesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        fetchArticles()
    }

    private fun setupRecyclerView() {
        binding.verticalRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun fetchArticles() {
        showLoading(true)
        ApiConfig.getApiService().getArticles().enqueue(object : Callback<ArticlesResponse> {
            override fun onResponse(call: Call<ArticlesResponse>, response: Response<ArticlesResponse>) {
                showLoading(false)
                if (response.isSuccessful) {
                    response.body()?.data?.let { articles ->
                        val filteredArticles = articles.filterNotNull().filter { it.plantType == "Teh" }
                        if (filteredArticles.isNotEmpty()) {
                            articlesAdapter = ArticlesAdapter(filteredArticles, this@TeaActivity)
                            binding.verticalRecyclerView.adapter = articlesAdapter
                        } else {
                            Toast.makeText(this@TeaActivity, "No articles found for Teh", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this@TeaActivity, "Failed to load articles", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ArticlesResponse>, t: Throwable) {
                showLoading(false)
                Toast.makeText(this@TeaActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.loadingAnimation.visibility = View.VISIBLE
            binding.verticalRecyclerView.visibility = View.GONE
        } else {
            binding.loadingAnimation.visibility = View.GONE
            binding.verticalRecyclerView.visibility = View.VISIBLE
        }
    }
}
