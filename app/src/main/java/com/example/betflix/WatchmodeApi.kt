package com.example.betflix

import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface WatchmodeApi {
    @GET("v1/list-titles/")
    fun getMovies(
        @Query("apiKey") apiKey: String,
        @Query("types") type: String,
        @Query("limit") limit: Int,
        @Query("page") page: Int
    ): Single<ApiResponse>

    @GET("v1/autocomplete-search/")
    fun searchTitle(
        @Query("apiKey") apiKey: String,
        @Query("search_value") searchValue: String,
        @Query("search_type") searchType: Int = 1
    ): Single<SearchResponse>
}
