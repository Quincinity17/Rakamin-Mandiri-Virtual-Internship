package com.example.newsrakaminapp.ui.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.newsrakaminapp.R
import com.example.newsrakaminapp.data.response.ArticlesItem
import com.example.newsrakaminapp.databinding.ActivityDetailNewsBinding

class DetailNewsActivity : AppCompatActivity() {

    // View binding untuk elemen layout
    private lateinit var binding: ActivityDetailNewsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = ContextCompat.getColor(this, R.color.shimmer_purple)

        // Ambil data dari Intent
        val article = intent.getParcelableExtra<ArticlesItem>("EXTRA_ARTICLE")

        // Tampilkan data di UI
        article?.let {
            binding.newsTitle.text = it.title
            binding.tvAuthor.text = it.author ?: "Unknown Author"
            binding.tvDate.text = it.publishedAt
            binding.newsContent.text = it.content

            Glide.with(binding.root.context)
                .load(article.urlToImage)
                .apply(
                    RequestOptions()
                )
                .into(binding.newsImage)
        }

        binding.backButton.setOnClickListener{
            onBackPressed()
        }
    }
}