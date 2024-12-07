package com.bangkit.leafsense.ui.leaf

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.leafsense.R
import com.bangkit.leafsense.data.api.ApiConfig
import com.bangkit.leafsense.data.response.ArticlesResponse
import com.bangkit.leafsense.data.response.DataItem
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
        setupSearchBar()
        fetchArticles()
    }

    private fun setupRecyclerView() {
        articlesAdapter = ArticlesAdapter(emptyList(), this)
        binding.verticalRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.verticalRecyclerView.adapter = articlesAdapter
    }

    private fun setupSearchBar() {
        val searchBar = binding.searchBar
        val handler = android.os.Handler(mainLooper)
        var searchRunnable: Runnable? = null

        searchBar.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchRunnable?.let { handler.removeCallbacks(it) }

                searchRunnable = Runnable {
                    val query = s.toString()
                    if (query.isNotEmpty()) {
                        searchArticles(query)
                    } else {
                        fetchArticles()
                    }
                }
                handler.postDelayed(searchRunnable!!, 300)
            }

            override fun afterTextChanged(s: android.text.Editable?) {}
        })
    }

    private fun fetchArticles() {
        showLoading(true)
        ApiConfig.getApiService().getArticles().enqueue(object : Callback<ArticlesResponse> {
            override fun onResponse(call: Call<ArticlesResponse>, response: Response<ArticlesResponse>) {
                showLoading(false)
                if (response.isSuccessful) {
                    response.body()?.data?.filterNotNull()?.let { articles ->
                        val filteredArticles = articles.filter { it.plantType == "Teh" }
                        articlesAdapter.updateData(filteredArticles)
                    } ?: showToast("Tidak ada artikel yang ditemukan")
                } else {
                    showToast("Gagal memuat artikel")
                }
            }

            override fun onFailure(call: Call<ArticlesResponse>, t: Throwable) {
                showLoading(false)
                showToast("Error: ${t.message}")
            }
        })
    }

    private fun searchArticles(query: String) {
        showLoading(true)
        ApiConfig.getApiService().getArticles().enqueue(object : Callback<ArticlesResponse> {
            override fun onResponse(call: Call<ArticlesResponse>, response: Response<ArticlesResponse>) {
                showLoading(false)
                if (response.isSuccessful) {
                    response.body()?.data?.filterNotNull()?.let { articles ->
                        val filteredByPlantType = articles.filter { it.plantType == "Teh" }

                        val filteredByQuery = filteredByPlantType.filter {
                            it.title?.contains(query, ignoreCase = true) == true ||
                                    it.content?.contains(query, ignoreCase = true) == true
                        }

                        articlesAdapter.updateData(filteredByQuery)
                    }
                } else {
                    showToast("Gagal memuat artikel")
                }
            }

            override fun onFailure(call: Call<ArticlesResponse>, t: Throwable) {
                showLoading(false)
                showToast("Error: ${t.message}")
            }
        })
    }

    private fun showLoading(isLoading: Boolean) {
        binding.loadingAnimation.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.verticalRecyclerView.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
