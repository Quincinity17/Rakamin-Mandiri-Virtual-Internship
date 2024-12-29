/**
 * Data Models untuk API Respons Berita
 *
 * Kelas-kelas berikut digunakan untuk merepresentasikan data yang diterima dari API
 * respons berita. Setiap data di-annotate dengan `@SerializedName` agar sesuai dengan
 * key yang digunakan dalam respons JSON. Semua kelas menerapkan `Parcelable`
 * untuk mempermudah pengiriman data antar komponen Android.
 */
package com.example.newsrakaminapp.data.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * NewsResponse
 *
 * Model utama untuk respons API berita. Berisi informasi tentang jumlah hasil,
 * status permintaan, dan daftar artikel berita.
 *
 * @property totalResults Total jumlah artikel berita yang ditemukan.
 * @property articles Daftar artikel berita.
 * @property status Status respons API, biasanya "ok" untuk permintaan berhasil.
 */
@Parcelize
data class NewsResponse(

	@field:SerializedName("totalResults")
	val totalResults: Int,

	@field:SerializedName("articles")
	val articles: List<ArticlesItem>,

	@field:SerializedName("status")
	val status: String
) : Parcelable

/**
 * ArticlesItem
 *
 * Model untuk merepresentasikan satu artikel berita.
 *
 * @property publishedAt Tanggal publikasi artikel dalam format ISO 8601.
 * @property author Nama penulis artikel (jika tersedia).
 * @property urlToImage URL gambar terkait artikel.
 * @property description Deskripsi singkat tentang artikel.
 * @property source Sumber berita (lihat model `Source`).
 * @property title Judul artikel.
 * @property url URL lengkap untuk membaca artikel.
 * @property content Isi artikel dalam bentuk teks (bisa null atau sebagian).
 */
@Parcelize
data class ArticlesItem(

	@field:SerializedName("publishedAt")
	val publishedAt: String?,

	@field:SerializedName("author")
	val author: String?,

	@field:SerializedName("urlToImage")
	val urlToImage: String?,

	@field:SerializedName("description")
	val description: String?,

	@field:SerializedName("source")
	val source: Source?,

	@field:SerializedName("title")
	val title: String?,

	@field:SerializedName("url")
	val url: String?,

	@field:SerializedName("content")
	val content: String?
) : Parcelable

/**
 * Source
 *
 * Model untuk merepresentasikan sumber berita.
 *
 * @property name Nama sumber berita (misalnya, "BBC News").
 * @property id ID sumber berita (bisa null jika tidak tersedia).
 */
@Parcelize
data class Source(

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("id")
	val id: String?
) : Parcelable
