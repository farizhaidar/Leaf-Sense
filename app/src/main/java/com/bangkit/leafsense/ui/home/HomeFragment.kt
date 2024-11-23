package com.bangkit.leafsense.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.leafsense.R
import com.bangkit.leafsense.data.api.ApiConfig
import com.bangkit.leafsense.data.response.ArticlesResponse
import com.bangkit.leafsense.databinding.FragmentHomeBinding
import com.bangkit.leafsense.ui.adapter.ArticlesAdapter
import com.bangkit.leafsense.ui.leaf.CoffeActivity
import com.bangkit.leafsense.ui.leaf.StrawberryActivity
import com.bangkit.leafsense.ui.leaf.TeaActivity
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var articlesAdapter: ArticlesAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentHomeBinding.bind(view)

        val currentUser = FirebaseAuth.getInstance().currentUser
        val userName = currentUser?.displayName ?: "User"

        binding.userText.text = "Halo $userName!"

        binding.verticalRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        setupMenuNavigation()
        fetchArticles()
    }

    private fun setupMenuNavigation() {
        // Navigasi ke TeaActivity
        binding.teaLeavesImage.apply {
            setOnClickListener {
                animateClick { startActivity(Intent(requireContext(), TeaActivity::class.java)) }
            }
        }

        // Navigasi ke CoffeeActivity
        binding.coffeeLeavesImage.apply {
            setOnClickListener {
                animateClick { startActivity(Intent(requireContext(), CoffeActivity::class.java)) }
            }
        }

        // Navigasi ke StrawberryActivity
        binding.strawberryLeavesImage.apply {
            setOnClickListener {
                animateClick { startActivity(Intent(requireContext(), StrawberryActivity::class.java)) }
            }
        }
    }

    private fun fetchArticles() {
        ApiConfig.getApiService().getArticles().enqueue(object : Callback<ArticlesResponse> {
            override fun onResponse(call: Call<ArticlesResponse>, response: Response<ArticlesResponse>) {
                if (response.isSuccessful) {
                    response.body()?.data?.let { articles ->
                        val filteredArticles = articles.filterNotNull()
                        articlesAdapter = ArticlesAdapter(filteredArticles, requireContext())
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

    private fun View.animateClick(onAnimationEnd: () -> Unit) {
        this.animate()
            .scaleX(0.9f)
            .scaleY(0.9f)
            .setDuration(100)
            .withEndAction {
                this.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(100)
                    .withEndAction { onAnimationEnd() }
                    .start()
            }
            .start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
