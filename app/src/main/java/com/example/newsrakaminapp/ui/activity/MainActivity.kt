/**
 * MainActivity
 *
 * Kelas ini adalah aktivitas utama aplikasi yang bertanggung jawab untuk menampilkan dua jenis berita:
 * - **Top News**: Berita unggulan yang ditampilkan secara horizontal di bagian atas layar.
 * - **All News**: Daftar berita lainnya yang mendukung fitur infinite scrolling.
 *
 * Fitur utama yang disediakan oleh `MainActivity`:
 * 1. Menampilkan daftar berita yang diambil dari API.
 * 2. Mendukung fitur infinite scrolling untuk memuat lebih banyak berita secara dinamis.
 * 3. Memungkinkan pengguna memilih kategori berita melalui tombol kategori.
 * 4. Mengarahkan pengguna ke halaman detail berita ketika sebuah berita dipilih.
 *
 * Komponen utama:
 * - `ViewModel`: Mengelola data dan state aplikasi secara reaktif menggunakan `MainViewModel`.
 * - `RecyclerView`: Menampilkan daftar berita menggunakan adapter `NewsAdapter`.
 * - `View Binding`: Digunakan untuk mengakses elemen UI dengan lebih efisien.
 *
 * Struktur kode ini mencerminkan praktik pengembangan Android modern, seperti penggunaan
 * ViewModel dan RecyclerView dengan adapter.
 */

package com.example.newsrakaminapp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsrakaminapp.R
import com.example.newsrakaminapp.data.response.ArticlesItem
import com.example.newsrakaminapp.databinding.ActivityMainBinding
import com.example.newsrakaminapp.ui.adapter.NewsAdapter
import com.example.newsrakaminapp.viewModel.MainViewModel

class MainActivity : AppCompatActivity() {

    // Menggunakan view binding untuk mengakses elemen UI
    private lateinit var binding: ActivityMainBinding

    // ViewModel untuk mengelola data dan status aplikasi
    private val mainViewModel: MainViewModel by viewModels()

    // Adapter untuk menampilkan daftar berita
    private lateinit var topNewsAdapter: NewsAdapter
    private lateinit var allNewsAdapter: NewsAdapter

    // Query yang digunakan untuk menentukan kategori berita yang sedang aktif
    private var currentQuery: String = "Technology"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Mengatur warna status bar
        window.statusBarColor = ContextCompat.getColor(this, R.color.shimmer_purple)

        // Inisialisasi view binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi UI dan data
        setupRecyclerView()
        setupQueryButtons()
        observeViewModel()
        fetchData()
        setupInfiniteScrolling()
    }

    /**
     * Mengatur tombol kategori berita.
     * Setiap tombol mewakili kategori tertentu dan akan memuat ulang berita sesuai kategori tersebut.
     */
    private fun setupQueryButtons() {
        val buttons = mapOf(
            binding.btnTechnology to "Technology",
            binding.btnSports to "Sports",
            binding.btnHealth to "Health",
            binding.btnFinance to "Finance",
            binding.btnGame to "Game"
        )

        buttons.forEach { (button, query) ->
            button.setOnClickListener {
                setActiveButton(button, buttons.keys)
                currentQuery = query
                mainViewModel.resetAllNews() // Mengosongkan data lama
                mainViewModel.loadMoreNews(query = currentQuery) // Memuat berita baru
            }
        }
    }

    /**
     * Mengatur tampilan tombol kategori yang aktif.
     * Tombol aktif ditandai dengan warna ungu dan teks putih.
     */
    private fun setActiveButton(activeButton: Button, allButtons: Set<Button>) {
        allButtons.forEach { button ->
            if (button == activeButton) {
                button.setBackgroundColor(ContextCompat.getColor(this, R.color.button_purple))
                button.setTextColor(ContextCompat.getColor(this, R.color.white))
            } else {
                button.setBackgroundColor(ContextCompat.getColor(this, R.color.button_grey))
                button.setTextColor(ContextCompat.getColor(this, R.color.black))
            }
        }
    }

    /**
     * Mengatur RecyclerView untuk menampilkan berita.
     * Terdapat dua RecyclerView:
     * - `topNewsAdapter`: Menampilkan berita unggulan secara horizontal.
     * - `allNewsAdapter`: Menampilkan daftar berita lainnya secara vertikal.
     */
    private fun setupRecyclerView() {
        topNewsAdapter = NewsAdapter(
            onItemClick = { navigateToDetail(it) },
            isTopArticle = { true }
        )
        binding.listTopNews.apply {
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = topNewsAdapter
        }

        allNewsAdapter = NewsAdapter(
            onItemClick = { navigateToDetail(it) },
            isTopArticle = { false }
        )
        binding.listAllNews.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = allNewsAdapter
        }
    }

    /**
     * Mengamati perubahan data dari ViewModel.
     * Data yang diamati meliputi:
     * - Berita unggulan (`topNews`).
     * - Daftar berita lainnya (`allNews`).
     * - Status loading (`isLoading`).
     */
    private fun observeViewModel() {
        mainViewModel.topNews.observe(this) { topNews ->
            if (topNews.isNotEmpty()) {
                binding.shimmerTopNews.stopShimmer()
                binding.shimmerTopNews.visibility = View.GONE
                binding.listTopNews.visibility = View.VISIBLE
                topNewsAdapter.submitList(topNews)
            } else {
                binding.shimmerTopNews.startShimmer()
                binding.shimmerTopNews.visibility = View.VISIBLE
                binding.listTopNews.visibility = View.GONE
            }
        }

        mainViewModel.allNews.observe(this) { allNews ->
            if (allNews.isNotEmpty()) {
                binding.shimmerNews.stopShimmer()
                binding.shimmerNews.visibility = View.GONE
                binding.listAllNews.visibility = View.VISIBLE
                allNewsAdapter.submitList(allNews)
            }
        }

        mainViewModel.isLoading.observe(this) { isLoading ->
            binding.shimmerNews.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    /**
     * Memuat data awal, termasuk berita unggulan dan daftar berita lainnya.
     */
    private fun fetchData() {
        mainViewModel.getTopHeadlines()
        mainViewModel.loadMoreNews(query = currentQuery)
    }

    /**
     * Mengatur infinite scrolling untuk daftar berita lainnya.
     * Ketika pengguna menggulir ke akhir daftar, berita baru akan dimuat.
     */
    private fun setupInfiniteScrolling() {
        binding.listAllNews.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                // Memeriksa apakah berada di ujung daftar dan memuat data baru
                if (!mainViewModel.isLoading.value!! && lastVisibleItem + 1 >= totalItemCount) {
                    val lastVisiblePosition = layoutManager.findFirstVisibleItemPosition() // Simpan posisi terakhir

                    mainViewModel.loadMoreNews(query = currentQuery) {
                        // Panggil setelah data berhasil dimuat
                        layoutManager.scrollToPositionWithOffset(lastVisiblePosition, 0)
                    }
                }
            }
        })
    }


    /**
     * Mengarahkan pengguna ke halaman detail berita.
     * @param article Artikel yang dipilih.
     */
    private fun navigateToDetail(article: ArticlesItem) {
        val intent = Intent(this, DetailNewsActivity::class.java).apply {
            putExtra("EXTRA_ARTICLE", article)
        }
        startActivity(intent)
    }
}
