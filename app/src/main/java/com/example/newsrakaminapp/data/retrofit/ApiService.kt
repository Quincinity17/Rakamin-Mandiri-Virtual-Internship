/**
 * ApiService
 *
 * Interface untuk mendefinisikan endpoint API yang digunakan dalam aplikasi.
 * Setiap fungsi merepresentasikan satu permintaan HTTP dengan spesifikasi endpoint, metode, dan parameter query.
 *
 * **Fitur Utama**:
 * 1. Mendukung pengambilan semua berita berdasarkan kata kunci dan kriteria tertentu.
 * 2. Mendukung pengambilan berita utama (Top Headlines) berdasarkan negara.
 * 3. Menggunakan `suspend` untuk mendukung pemanggilan asinkron dengan coroutine.
 *
 * Semua permintaan akan mengembalikan respons dalam bentuk `Response<NewsResponse>`.
 */
package com.example.newsrakaminapp.data.retrofit

import com.example.newsrakaminapp.BuildConfig.API_KEY
import com.example.newsrakaminapp.data.response.NewsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    /**
     * Mendapatkan daftar berita berdasarkan kata kunci.
     *
     * @param q Kata kunci pencarian untuk berita (default: kosong).
     * @param sortBy Metode pengurutan berita, nilai default adalah `publishedAt` untuk mengurutkan berdasarkan waktu publikasi.
     * @param apiKey API Key untuk autentikasi, secara default diambil dari `BuildConfig.API_KEY`.
     * @return Response dengan daftar berita dalam bentuk `NewsResponse`.
     */
    @GET("everything")
    suspend fun getEverything(
        @Query("q") q: String? = "",
        @Query("sortBy") sortBy: String? = "publishedAt",
        @Query("apiKey") apiKey: String? = API_KEY
    ): Response<NewsResponse>

    /**
     * Mendapatkan daftar berita utama (Top Headlines) berdasarkan negara.
     *
     * @param country Kode negara untuk filter berita (default: "us").
     * @return Response dengan daftar berita utama dalam bentuk `NewsResponse`.
     */
    @GET("top-headlines")
    suspend fun getTopHeadlines(
        @Query("country") country: String? = "us"
    ): Response<NewsResponse>
}
