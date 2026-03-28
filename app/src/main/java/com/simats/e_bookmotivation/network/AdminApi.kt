package com.simats.e_bookmotivation.network

import com.simats.e_bookmotivation.network.models.*
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface AdminApi {

    @GET("api/users/admin/books/")
    suspend fun listBooks(
        @Header("Authorization") token: String
    ): Response<List<AdminBookResponse>>

    @Multipart
    @POST("api/users/admin/books/")
    suspend fun uploadBook(
        @Header("Authorization") token: String,
        @Part("title") title: okhttp3.RequestBody,
        @Part("author") author: okhttp3.RequestBody,
        @Part("description") description: okhttp3.RequestBody,
        @Part("category") category: okhttp3.RequestBody,
        @Part("genre") genre: okhttp3.RequestBody,
        @Part("cover_url") coverUrl: okhttp3.RequestBody,
        @Part("is_premium") isPremium: okhttp3.RequestBody,
        @Part pdf_file: MultipartBody.Part?
    ): Response<AdminBookResponse>

    @DELETE("api/users/admin/books/{book_id}/")
    suspend fun deleteBook(
        @Header("Authorization") token: String,
        @retrofit2.http.Path("book_id") bookId: Int
    ): Response<AdminDeleteResponse>

    @GET("api/users/admin/books/{book_id}/chapters/")
    suspend fun listChapters(
        @Header("Authorization") token: String,
        @retrofit2.http.Path("book_id") bookId: Int
    ): Response<List<AdminChapterResponse>>

    @POST("api/users/admin/books/{book_id}/chapters/")
    suspend fun addChapter(
        @Header("Authorization") token: String,
        @retrofit2.http.Path("book_id") bookId: Int,
        @Body request: AdminChapterRequest
    ): Response<AdminChapterResponse>
    @DELETE("api/users/admin/chapters/{chapter_id}/")
    suspend fun deleteChapter(
        @Header("Authorization") token: String,
        @retrofit2.http.Path("chapter_id") chapterId: Int
    ): Response<AdminDeleteResponse>
}
