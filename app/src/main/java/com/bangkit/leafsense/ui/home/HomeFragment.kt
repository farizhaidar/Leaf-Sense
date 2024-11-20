package com.bangkit.leafsense.ui.home

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.leafsense.R
import com.bangkit.leafsense.databinding.FragmentHomeBinding
import com.bangkit.leafsense.data.api.ApiConfig
import com.bangkit.leafsense.data.response.ArticlesResponse
import com.bangkit.leafsense.ui.adapter.ArticlesAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment(R.layout.fragment_home) {

    // Declare the ViewBinding object
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // Declare the adapter for RecyclerView
    private lateinit var articlesAdapter: ArticlesAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentHomeBinding.bind(view)

        val sharedPreferences = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val userName = sharedPreferences.getString("userName", "User")

        binding.userText.text = "Halo $userName!"

        binding.verticalRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        fetchArticles()
    }

    private fun fetchArticles() {
        ApiConfig.getApiService().getArticles().enqueue(object : Callback<ArticlesResponse> {
            override fun onResponse(call: Call<ArticlesResponse>, response: Response<ArticlesResponse>) {
                if (response.isSuccessful) {
                    response.body()?.data?.let { articles ->
                        val filteredArticles = articles.filterNotNull()

                        articlesAdapter = ArticlesAdapter(filteredArticles)
                        binding.verticalRecyclerView.adapter = articlesAdapter
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed to load articles", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ArticlesResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
