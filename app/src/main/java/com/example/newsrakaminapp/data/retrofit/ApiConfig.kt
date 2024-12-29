/**
 * ApiConfig
 *
 * Kelas konfigurasi untuk menyediakan instance Retrofit yang digunakan untuk melakukan permintaan API.
 * Kelas ini mencakup konfigurasi seperti logging, pengaturan API key, dan timeout koneksi.
 *
 * **Fitur Utama**:
 * 1. Menyediakan instance Retrofit yang sudah terkonfigurasi.
 * 2. Menambahkan API key ke setiap request melalui Interceptor.
 * 3. Mendukung logging untuk mencetak request dan response saat debugging.
 * 4. Mengatur timeout koneksi untuk mencegah aplikasi hang jika server tidak merespons.
 */
package com.example.newsrakaminapp.data.retrofit

import android.util.Log
import com.example.newsrakaminapp.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApiConfig {
    companion object {
        /**
         * Mengembalikan instance `ApiService` yang digunakan untuk berinteraksi dengan API.
         *
         * @return `ApiService` Instance Retrofit yang sudah dikonfigurasi.
         */
        fun getApiService(): ApiService {
            // Interceptor untuk mencatat log HTTP saat debugging
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
            }

            // Interceptor untuk menambahkan API key ke setiap request
            val apiKeyInterceptor = Interceptor { chain ->
                val originalRequest = chain.request()
                val originalUrl = originalRequest.url
                val newUrl = originalUrl.newBuilder()
                    .addQueryParameter("apiKey", BuildConfig.API_KEY) // Menambahkan API key dari BuildConfig
                    .build()
                val newRequest = originalRequest.newBuilder()
                    .url(newUrl)
                    .build()
                chain.proceed(newRequest)
            }

            // Interceptor tambahan untuk mencetak URL dan kode respons
            val debugInterceptor = Interceptor { chain ->
                val request = chain.request()
                Log.d("API_REQUEST", "Request URL: ${request.url}")
                val response = chain.proceed(request)
                Log.d("API_RESPONSE", "Response Code: ${response.code}")
                response
            }

            // Mengonfigurasi OkHttpClient dengan interceptor dan pengaturan timeout
            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(apiKeyInterceptor)
                .addInterceptor(debugInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build()

            // Membangun instance Retrofit
            val retrofit = Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL) // Base URL diambil dari konfigurasi BuildConfig
                .addConverterFactory(GsonConverterFactory.create()) // Konverter untuk parsing JSON menggunakan Gson
                .client(client)
                .build()

            return retrofit.create(ApiService::class.java) // Mengembalikan instance ApiService
        }
    }
}
