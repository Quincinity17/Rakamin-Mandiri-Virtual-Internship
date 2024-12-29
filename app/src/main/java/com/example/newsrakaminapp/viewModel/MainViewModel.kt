/**
 * MainViewModel
 *
 * ViewModel yang bertanggung jawab untuk mengelola data berita yang ditampilkan di UI.
 * Data yang diambil dari API akan difilter dan disimpan dalam objek LiveData untuk diamati oleh aktivitas atau fragmen.
 *
 * Fitur Utama:
 * - Mengambil daftar berita utama (Top Headlines).
 * - Mendukung pemuatan berita tambahan untuk infinite scrolling.
 * - Memfilter berita berdasarkan kriteria tertentu.
 * - Mengelola state loading untuk menampilkan indikator loading di UI.
 */
package com.example.newsrakaminapp.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsrakaminapp.data.response.ArticlesItem
import com.example.newsrakaminapp.data.retrofit.ApiConfig
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    companion object {
        private const val TAG = "MainViewModel"
    }

    // LiveData untuk menyimpan daftar berita (All News)
    private val _allNews = MutableLiveData<List<ArticlesItem>>()
    val allNews: LiveData<List<ArticlesItem>> get() = _allNews

    // LiveData untuk menyimpan daftar berita utama (Top Headlines)
    private val _topNews = MutableLiveData<List<ArticlesItem>>()
    val topNews: LiveData<List<ArticlesItem>> get() = _topNews

    // LiveData untuk mengelola state loading
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    /**
     * Mengambil daftar berita utama (Top Headlines) berdasarkan negara.
     *
     * @param country Kode negara (default: "us").
     */
    fun getTopHeadlines(country: String = "us") {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = ApiConfig.getApiService().getTopHeadlines(country)
                if (response.isSuccessful) {
                    val articles = response.body()?.articles ?: emptyList()
                    // Filter berita: hanya menampilkan yang memiliki gambar dan bukan [removed]
                    val filteredArticles = articles.filter { article ->
                        article.title != "[removed]" && article.urlToImage != null
                    }
                    _topNews.value = filteredArticles
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching top headlines: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Memuat lebih banyak berita berdasarkan query tertentu.
     * - Digunakan untuk mendukung infinite scrolling.
     *
     * @param query Kata kunci pencarian.
     */
    fun loadMoreNews(query: String, onComplete: () -> Unit = {}) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = ApiConfig.getApiService().getEverything(q = query)
                if (response.isSuccessful) {
                    val articles = response.body()?.articles.orEmpty()
                    val filteredArticles = articles.filter { article ->
                        article.title != "[removed]" && article.urlToImage != null
                    }
                    val currentList = _allNews.value.orEmpty().toMutableList()
                    currentList.addAll(filteredArticles)
                    _allNews.value = currentList
                } else {
                    Log.e("API_ERROR", "Error code: ${response.code()}, message: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching all news: ${e.message}")
            } finally {
                _isLoading.value = false
                onComplete() // Panggil callback jika ada
            }
        }
    }



    /**
     * Mereset daftar berita (All News) menjadi kosong.
     * - Biasanya digunakan saat pengguna mengganti kategori.
     */
    fun resetAllNews() {
        _allNews.value = emptyList()
    }
}
