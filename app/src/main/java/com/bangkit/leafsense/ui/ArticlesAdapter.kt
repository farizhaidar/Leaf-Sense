package com.bangkit.leafsense.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bangkit.leafsense.databinding.ItemArticleBinding
import com.bangkit.leafsense.data.response.DataItem
import com.bangkit.leafsense.ui.detail.DetailActivity

class ArticlesAdapter(
    private var articles: List<DataItem>,
    private val context: Context
) : RecyclerView.Adapter<ArticlesAdapter.ArticleViewHolder>() {

    inner class ArticleViewHolder(val binding: ItemArticleBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(article: DataItem) {
            binding.apply {
                titleTextView.text = article.title
                contentTextView.text = article.content

                article.imageUrl?.let {
                    Glide.with(imageView.context).load(it).into(imageView)
                }

                root.setOnClickListener {
                    val intent = Intent(context, DetailActivity::class.java)
                    intent.putExtra("ARTICLE_ID", article.id)
                    context.startActivity(intent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val binding = ItemArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArticleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = articles[position]
        holder.bind(article)
    }

    override fun getItemCount(): Int = articles.size

    // Fungsi untuk memperbarui data artikel
    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newArticles: List<DataItem>) {
        articles = newArticles
        notifyDataSetChanged()
    }
}
