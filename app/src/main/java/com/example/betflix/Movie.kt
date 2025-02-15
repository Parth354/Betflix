package com.example.betflix

data class ApiResponse(
    val titles: List<Movie>
)

data class Movie(
    val id: Int,
    val title: String?,
    val year: String?,
    val poster: String?,
    val type: String?
)

fun Movie.mergeWith(searchResult: SearchResult): Movie {
    return this.copy(
        title = if (searchResult.name.isNotBlank()) searchResult.name else (this.title ?: "Unknown Title"),
        year = searchResult.year?.toString() ?: this.year ?: "N/A",
        poster = searchResult.image_url ?: this.poster,
        type = searchResult.type // using search result’s type
    )
}
