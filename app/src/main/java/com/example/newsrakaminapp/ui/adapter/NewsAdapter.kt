/**
 * NewsAdapter
 *
 * Adapter untuk `RecyclerView` yang bertanggung jawab untuk menampilkan daftar berita dengan dua jenis tampilan:
 * 1. **Top Articles**: Artikel unggulan dengan desain khusus (horizontal layout).
 * 2. **List Articles**: Artikel reguler yang ditampilkan dalam format daftar (vertical layout).
 *
 * @param onItemClick Callback yang dipanggil ketika sebuah item diklik. Umumnya digunakan untuk navigasi ke halaman detail artikel.
 * @param isTopArticle Fungsi untuk menentukan apakah sebuah artikel termasuk kategori `Top Article`.
 */
package com.example.newsrakaminapp.ui.adapter

import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.newsrakaminapp.data.response.ArticlesItem
import com.example.newsrakaminapp.databinding.ItemNewsBinding
import com.example.newsrakaminapp.databinding.ItemTopnewsBinding
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import java.text.SimpleDateFormat
import java.util.TimeZone

class NewsAdapter(
    private val onItemClick: (ArticlesItem) -> Unit,
    private val isTopArticle: (ArticlesItem) -> Boolean
) : ListAdapter<ArticlesItem, RecyclerView.ViewHolder>(DiffCallback) {

    companion object {
        private const val VIEW_TYPE_TOP = 0
        private const val VIEW_TYPE_LIST = 1

        /**
         * DiffCallback
         *
         * Digunakan oleh `ListAdapter` untuk membandingkan elemen dalam daftar, sehingga hanya elemen yang berubah
         * yang akan di-update di RecyclerView, meningkatkan efisiensi.
         */
        private val DiffCallback = object : DiffUtil.ItemCallback<ArticlesItem>() {
            override fun areItemsTheSame(oldItem: ArticlesItem, newItem: ArticlesItem): Boolean {
                // Periksa apakah item memiliki ID atau properti unik yang sama
                return oldItem.title == newItem.title
            }

            override fun areContentsTheSame(oldItem: ArticlesItem, newItem: ArticlesItem): Boolean {
                // Periksa apakah seluruh isi item sama
                return oldItem == newItem
            }
        }

    }

    /**
     * Menentukan tipe view berdasarkan posisi artikel dalam daftar.
     *
     * @param position Posisi artikel dalam daftar.
     * @return Tipe view, `VIEW_TYPE_TOP` atau `VIEW_TYPE_LIST`.
     */
    override fun getItemViewType(position: Int): Int {
        return if (isTopArticle(getItem(position))) VIEW_TYPE_TOP else VIEW_TYPE_LIST
    }

    /**
     * Membuat `ViewHolder` berdasarkan tipe view yang diberikan.
     *
     * @param parent ViewGroup induk tempat ViewHolder akan ditempatkan.
     * @param viewType Tipe view yang menentukan desain item (Top Article atau List Article).
     * @return `RecyclerView.ViewHolder` yang sesuai dengan desain item.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_TOP -> {
                val binding = ItemTopnewsBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                TopArticleViewHolder(binding)
            }
            VIEW_TYPE_LIST -> {
                val binding = ItemNewsBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                ListArticleViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    /**
     * Menghubungkan data artikel dengan ViewHolder berdasarkan posisi.
     *
     * @param holder ViewHolder yang akan diisi dengan data.
     * @param position Posisi artikel dalam daftar.
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val article = getItem(position)
        when (holder) {
            is TopArticleViewHolder -> holder.bind(article)
            is ListArticleViewHolder -> holder.bind(article)
        }
    }

    /**
     * ViewHolder untuk menampilkan artikel unggulan (Top Articles).
     */
    inner class TopArticleViewHolder(private val binding: ItemTopnewsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        /**
         * Mengisi data artikel ke dalam ViewHolder.
         *
         * @param article Artikel yang akan ditampilkan.
         */
        fun bind(article: ArticlesItem) {
            binding.tvTitle.text = article.title
            binding.tvTitle.apply {
                maxLines = 2
                ellipsize = TextUtils.TruncateAt.END
            }
            binding.tvAuthor.text = article.author ?: "Unknown Author"
            binding.tvDate.text = formatRelativeTime(article.publishedAt ?: "Unknown Date")

            Glide.with(binding.root.context)
                .load(article.urlToImage)
                .apply(
                    RequestOptions.bitmapTransform(RoundedCornersTransformation(16, 0))
                )
                .into(binding.imageView)

            binding.root.setOnClickListener {
                onItemClick(article)
            }
        }
    }

    /**
     * ViewHolder untuk menampilkan artikel biasa (List Articles).
     */
    inner class ListArticleViewHolder(private val binding: ItemNewsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        /**
         * Mengisi data artikel ke dalam ViewHolder.
         *
         * @param article Artikel yang akan ditampilkan.
         */
        fun bind(article: ArticlesItem) {
            binding.tvTitle.text = article.title
            binding.tvTitle.apply {
                maxLines = 2
                ellipsize = TextUtils.TruncateAt.END
            }
            binding.tvAuthor.text = article.author ?: "Unknown Author"
            binding.tvDate.text = formatRelativeTime(article.publishedAt ?: "Unknown Date")

            Glide.with(binding.root.context)
                .load(article.urlToImage)
                .apply(RequestOptions())
                .into(binding.imageView)

            binding.root.setOnClickListener {
                onItemClick(article)
            }
        }
    }

    /**
     * Memformat tanggal artikel menjadi representasi waktu relatif.
     *
     * @param publishedAt Tanggal artikel dalam format ISO 8601 (e.g., "2022-10-01T10:15:30Z").
     * @return String yang merepresentasikan waktu relatif (e.g., "Hari ini", "3 hari lalu").
     */
    @SuppressLint("SimpleDateFormat")
    private fun formatRelativeTime(publishedAt: String): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        val date = try {
            sdf.parse(publishedAt)
        } catch (e: Exception) {
            null
        }

        date?.let {
            val now = System.currentTimeMillis()
            val difference = now - it.time
            val days = difference / (1000 * 60 * 60 * 24)
            return when {
                days < 1 -> "Hari ini"
                days in 1..6 -> "$days hari lalu"
                else -> "${days / 7} minggu lalu"
            }
        }
        return "Unknown Date"
    }
}
