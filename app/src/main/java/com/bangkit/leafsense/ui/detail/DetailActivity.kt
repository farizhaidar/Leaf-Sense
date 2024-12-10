package com.bangkit.leafsense.ui.detail

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bangkit.leafsense.databinding.ActivityDetailBinding
import com.bangkit.leafsense.data.api.ApiConfig
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity() {

    private lateinit var viewModel: DetailViewModel
    private lateinit var binding: ActivityDetailBinding
    private lateinit var articleId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        window.sharedElementEnterTransition = android.transition.TransitionInflater.from(this)
            .inflateTransition(android.R.transition.move)
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(DetailViewModel::class.java)

        viewModel.detailData.observe(this, Observer { article ->
            article?.let {
                binding.tvTitle.text = it.title
                binding.tvContent.text = it.content
                Glide.with(this).load(it.imageUrl).into(binding.ivArticleImage)
                showLoading(false)
            }
        })

        articleId = intent.getStringExtra("ARTICLE_ID") ?: ""

        loadArticleDetail(articleId)
    }

    private fun loadArticleDetail(articleId: String) {
        showLoading(true)
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val response = ApiConfig.getApiService().getArticleDetail(articleId)
                viewModel.setDetailData(response)
            } catch (e: Exception) {
                showLoading(false)
                Toast.makeText(this@DetailActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.loadingAnimation.visibility = View.VISIBLE
        } else {
            binding.loadingAnimation.visibility = View.GONE
        }
    }
}
