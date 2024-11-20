package com.bangkit.leafsense.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bangkit.leafsense.databinding.ItemArticleBinding
import com.bangkit.leafsense.data.response.DataItem

class ArticlesAdapter(private val articles: List<DataItem>) : RecyclerView.Adapter<ArticlesAdapter.ArticleViewHolder>() {

    inner class ArticleViewHolder(val binding: ItemArticleBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val binding = ItemArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArticleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = articles[position]
        holder.binding.apply {
            titleTextView.text = article.title
            contentTextView.text = article.content

            article.imageUrl?.let {
                Glide.with(imageView.context).load(it).into(imageView)
            }
        }
    }

    override fun getItemCount(): Int = articles.size
}
