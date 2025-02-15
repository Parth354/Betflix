package com.example.betflix

data class SearchResponse(
    val results: List<SearchResult> = emptyList()
)

data class SearchResult(
    val id: Int,
    val name: String,
    val image_url: String?,
    val type: String,
    val year: Int?
)
