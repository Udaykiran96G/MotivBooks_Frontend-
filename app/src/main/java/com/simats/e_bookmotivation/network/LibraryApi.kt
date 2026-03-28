package com.simats.e_bookmotivation.network

import com.simats.e_bookmotivation.network.models.BookResponse
import com.simats.e_bookmotivation.network.models.UserBookResponse
import com.simats.e_bookmotivation.network.models.ChapterResponse
import retrofit2.Response
import retrofit2.http.*

interface LibraryApi {
    @GET("api/users/library/")
    suspend fun getLibrary(
        @Header("Authorization") token: String,
        @Query("category") category: String? = null,
        @Query("sort") sort: String? = null,
        @Query("author") author: String? = null,
        @Query("year_min") yearMin: String? = null,
        @Query("year_max") yearMax: String? = null,
        @Query("rating") rating: String? = null,
        @Query("language") language: String? = null,
        @Query("tags") tags: String? = null,
        @Query("recently_added") recentlyAdded: String? = null
    ): Response<List<BookResponse>>

    @GET("api/users/library/completed/")
    suspend fun getCompletedBooks(
        @Header("Authorization") token: String
    ): Response<List<UserBookResponse>>

    @GET("api/users/books/{book_id}/")
    suspend fun getBookDetails(
        @Header("Authorization") token: String,
        @retrofit2.http.Path("book_id") bookId: Int
    ): Response<BookResponse>

    @GET("api/users/books/{book_id}/chapters/")
    suspend fun getChapters(
        @Header("Authorization") token: String,
        @retrofit2.http.Path("book_id") bookId: Int
    ): Response<List<ChapterResponse>>
}
